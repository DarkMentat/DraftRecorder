package org.darkmentat.draftrecorder.media;

import android.os.Handler;
import android.os.Message;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;


/*
  Thanks to: https://github.com/MasterEx/BeatKeeper
             http://masterex.github.com/archive/2012/05/28/android-audio-synthesis.html

  Usage:

  onStartStop(){
    mMetronome.setBeat(4);
    mMetronome.setBpm(120);
    mMetronome.setSound(659);
    mMetronome.setBeatSound(523);

    if(mMetronomeRunning){
      mMetronome.stop();
    }else{
      mMetronome.start();
    }

    mMetronomeRunning = !mMetronomeRunning;
  }
  onDestroy(){
    mMetronome.release();
  }

*/

@EBean
public class Metronome {

  private final static int TICK = 1000; // samples of tick

  private double mBpm;
  private int mBeat;
  private double mBeatSound;
  private double mSound;

  private boolean mPlay = true;

  private double[] soundTickArray;
  private double[] soundTockArray;
  private double[] silenceSoundArray;

  private AudioGenerator mAudioGenerator = new AudioGenerator(8000);

  private Handler mHandler;


  public Metronome() {
    setBeat(4);
    setBpm(120);
    setSound(659);
    setBeatSound(523);
  }

  public void calcSilence() {
    int silence = (int) (((60 / mBpm) * 8000) - TICK);

    soundTickArray = new double[TICK];
    soundTockArray = new double[TICK];
    silenceSoundArray = new double[silence];

    double[] tick = mAudioGenerator.getSineWave(TICK, 8000, mBeatSound);
    double[] tock = mAudioGenerator.getSineWave(TICK, 8000, mSound);

    for(int i = 0; i< TICK; i++) {
      soundTickArray[i] = tick[i];
      soundTockArray[i] = tock[i];
    }

    for(int i = 0; i< silence; i++)
      silenceSoundArray[i] = 0;
  }

  @Background public void start() {
    mPlay = true;
    mAudioGenerator.createPlayer();

    int currentBeat = 1;

    calcSilence();
    do {
      Message msg = Message.obtain(mHandler, currentBeat, "Tick");

      if(currentBeat == 1)
        mAudioGenerator.writeSound(soundTockArray);
      else
        mAudioGenerator.writeSound(soundTickArray);

      mAudioGenerator.writeSound(silenceSoundArray);

      if(mHandler != null)
        mHandler.sendMessage(msg);

      currentBeat++;
      if(currentBeat > mBeat)
        currentBeat = 1;
    } while(mPlay);
  }
  @Background public void stop() {
    mPlay = false;
    mAudioGenerator.stopAudioTrack();
  }
  public void release(){
    mAudioGenerator.releaseAudioTrack();
    mHandler = null;
  }

  public void setHandler(Handler handler){
    mHandler = handler;
  }

  public void setBpm(int bpm) {
    this.mBpm = bpm;
  }
  public void setBeat(int beat) {
    this.mBeat = beat;
  }
  public void setBeatSound(double sound) {
    this.mBeatSound = sound;
  }
  public void setSound(double sound) {
    this.mSound = sound;
  }
}
