package org.darkmentat.draftrecorder.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.darkmentat.draftrecorder.domain.MusicComposition;

import java.lang.ref.WeakReference;

import static org.darkmentat.draftrecorder.media.Player.PlayerState.PLAYING;
import static org.darkmentat.draftrecorder.media.Player.PlayerState.STOPPED;

@EBean
public class Player {

  public interface PlayerListener {
    void onPlayingStop();
  }

  public enum PlayerState {PLAYING, STOPPED}

  private static AudioTrack getAudioTrack(){
    int minSize = AudioTrack.getMinBufferSize( 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    return new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
  }

  private WeakReference<PlayerListener> mPlayerListener;

  private MediaPlayer mMediaPlayer;

  private PlayerState mState = STOPPED;

  private boolean mStop = false;

  @Background
  public void playStart(MusicComposition composition) {
    mStop = false;

    AudioTrack audioTrack = getAudioTrack();

    audioTrack.play();

    mState = PLAYING;

    RecordMixer mixer = new RecordMixer(composition);

    while (!mStop) {

      byte[] chunk = mixer.readChunk();

      if(chunk == null)
        break;

      audioTrack.write(chunk,0,chunk.length);
    }

    mixer.releaseAll();

    audioTrack.flush();
    audioTrack.release();

    mState = STOPPED;
    notifyListenerStop();
  }

  public void playStart(String file) {
    try {
      releasePlayer();

      mMediaPlayer = new MediaPlayer();
      mMediaPlayer.setDataSource(file);
      mMediaPlayer.prepare();
      mMediaPlayer.start();
      mState = PLAYING;

      mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mp) {
          mState = STOPPED;
          notifyListenerStop();
        }
      });

    } catch (Exception e) {
      mState = STOPPED;
      notifyListenerStop();
      e.printStackTrace();
    }
  }
  public void playStop() {
    mStop = true;
    mState = STOPPED;

    if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
      mMediaPlayer.stop();
    }

    notifyListenerStop();
  }

  public PlayerState getPlayerState(){
    return mState;
  }

  @UiThread void notifyListenerStop() {
    if(mPlayerListener == null)
      return;

    PlayerListener listener = mPlayerListener.get();

    if(listener == null)
      return;

    listener.onPlayingStop();
  }

  public void releasePlayer() {
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  public void setPlayerListener(PlayerListener playerListener) {
    mPlayerListener = new WeakReference<>(playerListener);
  }
}
