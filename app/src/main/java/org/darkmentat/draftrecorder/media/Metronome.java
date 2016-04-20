package org.darkmentat.draftrecorder.media;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

/*

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

*/


@EBean
public class Metronome {

  private double mBpm;
  private int mBeat;
  private int mSilence;

  private double mBeatSound;
  private double mSound;
  private final int mTick = 1000; // samples of tick

  private boolean mPlay = true;

  private AudioGenerator mAudioGenerator = new AudioGenerator(8000);

  public Metronome() {
    setBeat(4);
    setBpm(120);
    setSound(659);
    setBeatSound(523);
  }

  public void calcSilence() {
    mSilence = (int) (((60/ mBpm)*8000)- mTick);
  }

  @Background public void start() {
    mPlay = true;
    mAudioGenerator.createPlayer();

    calcSilence();
    double[] tick = mAudioGenerator.getSineWave(this.mTick, 8000, mBeatSound);
    double[] tock = mAudioGenerator.getSineWave(this.mTick, 8000, mSound);
    double silence = 0;
    double[] sound = new double[8000];
    int t = 0,s = 0,b = 0;
    do {
      for(int i = 0; i<sound.length&& mPlay; i++) {
        if(t<this.mTick) {
          if(b == 0)
            sound[i] = tock[t];
          else
            sound[i] = tick[t];
          t++;
        } else {
          sound[i] = silence;
          s++;
          if(s >= this.mSilence) {
            t = 0;
            s = 0;
            b++;
            if(b > (this.mBeat -1))
              b = 0;
          }
        }
      }
      mAudioGenerator.writeSound(sound);
    } while(mPlay);
  }
  @Background public void stop() {
    mPlay = false;
    mAudioGenerator.destroyAudioTrack();
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
