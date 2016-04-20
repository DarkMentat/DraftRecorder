package org.darkmentat.draftrecorder.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioGenerator {

  private AudioTrack mAudioTrack;

  public AudioGenerator(int sampleRate) {
    mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
        sampleRate, AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT, sampleRate,
        AudioTrack.MODE_STREAM);
  }

  public double[] getSineWave(int samples,int sampleRate,double frequencyOfTone){
    double[] sample = new double[samples];
    for (int i = 0; i < samples; i++) {
      sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequencyOfTone));
    }
    return sample;
  }

  public byte[] get16BitPcm(double[] samples) {
    byte[] generatedSound = new byte[2 * samples.length];
    int index = 0;
    for (double sample : samples) {
      // scale to maximum amplitude
      short maxSample = (short) ((sample * Short.MAX_VALUE));
      // in 16 bit wav PCM, first byte is the low order byte
      generatedSound[index++] = (byte) (maxSample & 0x00ff);
      generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);

    }
    return generatedSound;
  }

  public void createPlayer(){
    mAudioTrack.play();
  }

  public void writeSound(double[] samples) {
    byte[] generatedSnd = get16BitPcm(samples);
    mAudioTrack.write(generatedSnd, 0, generatedSnd.length);
  }

  public void stopAudioTrack() {
    mAudioTrack.stop();
  }

  public void releaseAudioTrack() {
    mAudioTrack.release();
  }
}