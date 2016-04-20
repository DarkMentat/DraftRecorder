package org.darkmentat.draftrecorder.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.media.Metronome;
import org.darkmentat.draftrecorder.media.Player;
import org.darkmentat.draftrecorder.media.Recorder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@EActivity(R.layout.activity_capture_sound)
public class CaptureSoundActivity extends AppCompatActivity {

  @ViewById(R.id.bpm) EditText mBpm;  //todo replace with NumberPicker
  @ViewById(R.id.size_beats) EditText mSizeBeats;  //todo replace with NumberPicker
  @ViewById(R.id.size_length) EditText mSizeLength;  //todo replace with NumberPicker

  @ViewById(R.id.start_capture) Button mStartCapture;
  @ViewById(R.id.stop_capture) Button mStopCapture;
  @ViewById(R.id.play_sound) Button mPlaySound;
  @ViewById(R.id.stop_sound) Button mStopSound;

  Metronome mMetronome;
  Recorder mRecorder;
  Player mPlayer;

  @Bean
  public void setMetronome(Metronome metronome){
    if(mMetronome == null) mMetronome = metronome;
  }
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
    setMetronomeConfig();
    mMetronome.start();
    switchToCapturing();
  }
  @Click(R.id.stop_capture)
  protected void onStopCapture(){
    mRecorder.recordStop();
    mMetronome.stop();
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

  private void setMetronomeConfig(){
    mMetronome.setBpm(Integer.parseInt(mBpm.getText().toString())); //todo replace with NumberPicker
    mMetronome.setBeat(Integer.parseInt(mSizeBeats.getText().toString())); //todo replace with NumberPicker
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
