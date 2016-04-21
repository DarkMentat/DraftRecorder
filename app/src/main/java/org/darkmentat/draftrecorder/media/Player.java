package org.darkmentat.draftrecorder.media;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import org.androidannotations.annotations.EBean;

import java.lang.ref.WeakReference;

@EBean
public class Player {

  public interface PlayerListener {
    void onPlayingStop();
  }

  private WeakReference<PlayerListener> mPlayerListener;

  private String mFileName;
  private MediaPlayer mMediaPlayer;

  public void setFileName(String fileName){
    mFileName = fileName;
  }
  public void playStart() {
    try {
      releasePlayer();

      mMediaPlayer = new MediaPlayer();
      mMediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mFileName);
      mMediaPlayer.prepare();
      mMediaPlayer.start();

      mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
          PlayerListener listener = mPlayerListener.get();

          if(listener != null){
            listener.onPlayingStop();
          }
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void playStop() {
    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
      mMediaPlayer.stop();

      PlayerListener listener = mPlayerListener.get();

      if(listener != null){
        listener.onPlayingStop();
      }
    }
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
