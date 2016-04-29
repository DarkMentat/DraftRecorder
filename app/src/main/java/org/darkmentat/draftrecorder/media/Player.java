package org.darkmentat.draftrecorder.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.darkmentat.draftrecorder.domain.MusicComposition;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

@EBean
public class Player {

  public interface PlayerListener {
    void onPlayingStop();
  }

  private static AudioTrack getAudioTrack(){
    int minSize = AudioTrack.getMinBufferSize( 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    return new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
  }

  private WeakReference<PlayerListener> mPlayerListener;

  private MediaPlayer mMediaPlayer;

  private boolean mStop = false;

  @Background
  public void playStart(MusicComposition composition) {
    AudioTrack audioTrack = getAudioTrack();

    audioTrack.play();

    MusicComposition.Record record = composition.getRegions().get(0).getTracks().get(0).getRecords().get(0);

    RecordDecoder recordDecoder = new RecordDecoder(record);

    recordDecoder.startRecordReading();

    while (!mStop) {
      byte[] chunk = recordDecoder.readRecordChunk();

      if(chunk == null)
        break;

      audioTrack.write(chunk,0,chunk.length);
    }

    recordDecoder.stopRecordReading();

    audioTrack.flush();
    audioTrack.release();

    notifyListenerStop();
  }

  public void playStart(String file) {
    try {
      releasePlayer();

      mMediaPlayer = new MediaPlayer();
      mMediaPlayer.setDataSource(file);
      mMediaPlayer.prepare();
      mMediaPlayer.start();

      mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mp) {
          notifyListenerStop();
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void playStop() {
    mStop = true;

    if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
      mMediaPlayer.stop();

      notifyListenerStop();
    }
  }

  private void notifyListenerStop() {
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
