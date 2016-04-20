package org.darkmentat.draftrecorder.ui.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.media.Metronome;
import org.darkmentat.draftrecorder.media.Player;
import org.darkmentat.draftrecorder.media.Recorder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@EActivity(R.layout.activity_capture_sound)
public class CaptureSoundActivity extends AppCompatActivity {

  @ViewById(R.id.bpm) EditText mBpmText;  //todo replace with NumberPicker
  @ViewById(R.id.size_beats) EditText mSizeBeatsText;  //todo replace with NumberPicker
  @ViewById(R.id.size_length) EditText mSizeLengthText;  //todo replace with NumberPicker

  @ViewById(R.id.metronome_leds) LinearLayout mMetronomeLeds;

  @ViewById(R.id.start_capture) Button mStartCapture;
  @ViewById(R.id.stop_capture) Button mStopCapture;
  @ViewById(R.id.play_sound) Button mPlaySound;
  @ViewById(R.id.stop_sound) Button mStopSound;

  Metronome mMetronome;
  Recorder mRecorder;
  Player mPlayer;

  private int mBpm;
  private int mBeats;
  private int mBeatLength;

  Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      setMetronomeLedTick(msg.what, msg.obj == Metronome.TOCK);
    }
  };


  @Bean
  public void setMetronome(Metronome metronome){
    if(mMetronome == null) mMetronome = metronome;

    mMetronome.setHandler(mHandler);
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

  @AfterViews
  protected void onLoad(){
    setMetronomeConfig();
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
    resetMetronomeLeds();
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
    mBpm = Integer.parseInt(mBpmText.getText().toString()); //todo replace with NumberPicker
    mBeats = Integer.parseInt(mSizeBeatsText.getText().toString()); //todo replace with NumberPicker
    mBeatLength = Integer.parseInt(mSizeLengthText.getText().toString()); //todo replace with NumberPicker

    mMetronome.setBpm(mBpm);
    mMetronome.setBeat(mBeats);
    mMetronome.setBeatLength(mBeatLength);

    setMetronomeLeds(mBeats);
  }
  private void setMetronomeLeds(int count){

    int oldCount = mMetronomeLeds.getChildCount();

    for(int i = count; i < oldCount; i++){
      mMetronomeLeds.removeView(mMetronomeLeds.findViewWithTag(i + 1));
    }

    for(int i = oldCount; i < count; i++){
      View led = new View(this);
      led.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
      led.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_grey));
      led.setTag(i + 1);
      mMetronomeLeds.addView(led);
    }
  }
  private void setMetronomeLedTick(int index, boolean tock){
    int prev = index - 1 > 0 ? index - 1 : mBeats;

    int newColor = tock ? R.drawable.circle_orange : R.drawable.circle_yellow;

    mMetronomeLeds.findViewWithTag(prev).setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_grey));
    mMetronomeLeds.findViewWithTag(index).setBackgroundDrawable(getResources().getDrawable(newColor));
  }
  private void resetMetronomeLeds(){
    int count = mMetronomeLeds.getChildCount();

    for(int i = 0; i < count; i++){
      mMetronomeLeds.getChildAt(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_grey));
    }
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
