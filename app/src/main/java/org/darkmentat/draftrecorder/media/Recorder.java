package org.darkmentat.draftrecorder.media;

import android.media.MediaRecorder;
import android.os.Environment;

import org.androidannotations.annotations.EBean;

import java.io.File;

@EBean
public class Recorder {

  public static final String NEW_SOUND_FILE = "newsound.mp3";

  private MediaRecorder mMediaRecorder;

  private int mBpm;
  private int mBeats;
  private int mBeatLength;

  public void recordStart(int bpm, int beats, int beatLength) {
    try {
      releaseRecorder();

//      File outFile = new File(NEW_SOUND_FILE);
//      if (outFile.exists()) {
//        //noinspection ResultOfMethodCallIgnored
//        outFile.delete();
//      }

      mBpm = bpm;
      mBeats = beats;
      mBeatLength = beatLength;

      mMediaRecorder = new MediaRecorder();
      mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
      mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
      mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + NEW_SOUND_FILE);
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
  public void releaseRecorder() {
    if (mMediaRecorder != null) {
      mMediaRecorder.release();
      mMediaRecorder = null;
    }
  }

  public void saveFile(String name){
    String fileName = name + " " + mBpm + " " + mBeats + " " + mBeatLength + ".mp3";

    File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), NEW_SOUND_FILE);
    File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);

    //noinspection ResultOfMethodCallIgnored
    from.renameTo(to);
  }
}
