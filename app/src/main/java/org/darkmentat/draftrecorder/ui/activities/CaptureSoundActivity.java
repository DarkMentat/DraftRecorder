package org.darkmentat.draftrecorder.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.media.Player;
import org.darkmentat.draftrecorder.media.Recorder;
import org.darkmentat.draftrecorder.ui.adapters.RecordsAdapter;

import static android.view.View.*;

@EActivity(R.layout.activity_capture_sound)
public class CaptureSoundActivity extends AppCompatActivity {
  @ViewById(R.id.start_capture) Button mStartCapture;
  @ViewById(R.id.stop_capture) Button mStopCapture;
  @ViewById(R.id.play_sound) Button mPlaySound;
  @ViewById(R.id.stop_sound) Button mStopSound;

  Recorder mRecorder;
  Player mPlayer;

  @Bean
  public void setPlayer(Player player){
    if(mPlayer == null) mPlayer = player;

    mPlayer.setFileName("test_mic.mp3");
  }
  @Bean
  public void setRecorder(Recorder recorder){
    if(mRecorder == null) mRecorder = recorder;

    mRecorder.setFileName("test_mic.mp3");
  }

  @Click(R.id.start_capture)
  protected void onStartCapture(){
    mRecorder.recordStart();
    switchToCapturing();
  }
  @Click(R.id.stop_capture)
  protected void onStopCapture(){
    mRecorder.recordStop();
    switchToCaptured();
  }
  @Click(R.id.play_sound)
  protected void onPlaySound(){
    mPlayer.playStart();
    switchToPlaying();
  }
  @Click(R.id.stop_sound)
  protected void onStopSound(){
    mPlayer.playStop();
    switchToEmpty();
  }

  private void switchToCapturing(){
    mStartCapture.setVisibility(GONE);
    mStopCapture.setVisibility(VISIBLE);
    mPlaySound.setVisibility(GONE);
    mStopSound.setVisibility(GONE);
  }
  private void switchToCaptured(){
    mStartCapture.setVisibility(GONE);
    mStopCapture.setVisibility(GONE);
    mPlaySound.setVisibility(VISIBLE);
    mStopSound.setVisibility(GONE);
  }
  private void switchToPlaying(){
    mStartCapture.setVisibility(GONE);
    mStopCapture.setVisibility(GONE);
    mPlaySound.setVisibility(GONE);
    mStopSound.setVisibility(VISIBLE);
  }
  private void switchToEmpty(){
    mStartCapture.setVisibility(VISIBLE);
    mStopCapture.setVisibility(GONE);
    mPlaySound.setVisibility(GONE);
    mStopSound.setVisibility(GONE);
  }
}
