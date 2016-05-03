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
    mMetronome.setBeatLength(4);
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

  public static final String TICK = "Tick";
  public static final String TOCK = "Tock";

  private final static int TICK_SAMPLES = 1000; // samples of tick

  private double mBpm;
  private int mBeat;
  private double mBeatSound;
  private double mSound;
  private double mVolume = 1.0;

  private boolean mPlay = true;

  private double[] soundTickArray;
  private double[] soundTockArray;
  private double[] silenceSoundArray;

  private AudioGenerator mAudioGenerator = new AudioGenerator(8000);

  private Handler mHandler;
  private int mBeatLength;


  public Metronome() {
    setBeat(4);
    setBeatLength(4);
    setBpm(120);
    setSound(659);
    setBeatSound(523);
  }

  private void calcSilence() {
    int silence = (int) (((60 / (mBpm * mBeatLength / 4)) * 8000) - TICK_SAMPLES);

    soundTickArray = new double[TICK_SAMPLES];
    soundTockArray = new double[TICK_SAMPLES];
    silenceSoundArray = new double[silence];

    double[] tick = mAudioGenerator.getSineWave(TICK_SAMPLES, 8000, mBeatSound, mVolume);
    double[] tock = mAudioGenerator.getSineWave(TICK_SAMPLES, 8000, mSound, mVolume);

    for(int i = 0; i< TICK_SAMPLES; i++) {
      soundTickArray[i] = tick[i];
      soundTockArray[i] = tock[i];
    }

    for(int i = 0; i< silence; i++)
      silenceSoundArray[i] = 0;
  }
  @SuppressWarnings("RedundantIfStatement")
  private boolean isBeatTock(int beat){
    if(beat == 1) return true;

    if(mBeatLength >= 16 && mBeat % 4 == 0 && beat % (mBeat/4) == 1)
      return true;

    if(mBeatLength >= 8 && mBeat % 2 == 0 && beat % (mBeat/2) == 1)
      return true;

    return false;
  }

  @Background public void start() {
    mPlay = true;
    mAudioGenerator.createPlayer();

    int currentBeat = 1;

    calcSilence();
    do {
      Message msg = Message.obtain(mHandler, currentBeat, isBeatTock(currentBeat)? TOCK : TICK);

      if(isBeatTock(currentBeat))
        mAudioGenerator.writeSound(soundTockArray);
      else
        mAudioGenerator.writeSound(soundTickArray);

      mAudioGenerator.writeSound(silenceSoundArray);

      if(mHandler != null && mPlay)
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
  public void setBeatLength(int beatLength) {
    mBeatLength = beatLength;
  }
  public void setVolume(double volume) {
    if(volume > 1.0)
      volume = 1.0;

    if(volume < 0)
      volume = 0;

    mVolume = volume;
  }
}
