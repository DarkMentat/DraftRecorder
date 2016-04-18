package org.darkmentat.draftrecorder.media;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import org.androidannotations.annotations.EBean;

@EBean
public class Player {

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

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void playStop() {
    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
    }
  }
  private void releasePlayer() {
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }
}
