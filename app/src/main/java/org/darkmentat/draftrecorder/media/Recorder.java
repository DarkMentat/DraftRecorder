package org.darkmentat.draftrecorder.media;

import android.media.MediaRecorder;
import android.os.Environment;

import org.androidannotations.annotations.EBean;

import java.io.File;

@EBean
public class Recorder {

  private String mFileName;
  private MediaRecorder mMediaRecorder;

  public void setFileName(String fileName){
    mFileName = fileName;
  }
  public void recordStart() {
    try {
      releaseRecorder();

      File outFile = new File(mFileName);
      if (outFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        outFile.delete();
      }

      mMediaRecorder = new MediaRecorder();
      mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
      mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
      mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mFileName);
      mMediaRecorder.prepare();
      mMediaRecorder.start();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void recordStop() {
    if (mMediaRecorder != null) {
      mMediaRecorder.stop();
    }
  }
  private void releaseRecorder() {
    if (mMediaRecorder != null) {
      mMediaRecorder.release();
      mMediaRecorder = null;
    }
  }
}
