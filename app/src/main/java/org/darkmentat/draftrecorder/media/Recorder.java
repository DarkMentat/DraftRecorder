package org.darkmentat.draftrecorder.media;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.File;

@EBean
public class Recorder {

  public static final String NEW_SOUND_FILE = "newsound.mp3";

  private MediaRecorder mMediaRecorder;

  @RootContext Context mContext;

  private int mBpm;
  private int mBeats;
  private int mBeatLength;

  public String getTempRecordFile(){
    return mContext.getExternalFilesDir(null).getAbsolutePath() + "/" + NEW_SOUND_FILE;
  }
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
      mMediaRecorder.setOutputFile(getTempRecordFile());
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

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public File saveFile(String compositionName, String name){
    String fileName = name + " " + mBpm + " " + mBeats + " " + mBeatLength + ".mp3";

    File home = mContext.getExternalFilesDir(null);
    File dir = home;

    if(!compositionName.isEmpty()){
      dir = new File(dir, compositionName);
      dir.mkdir();
    }

    File from = new File(home, NEW_SOUND_FILE);
    File to = new File(dir, fileName);

    from.renameTo(to);

    return to;
  }
}
