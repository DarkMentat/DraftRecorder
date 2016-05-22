package org.darkmentat.draftrecorder.utils;

public final class AudioUtils {
  public static int calculateAudioLength(int samplesCount, int sampleRate, int channelCount) {
    return ((samplesCount / channelCount) * 1000) / sampleRate;
  }
  public static int getIndexOfSecond(float second, int sampleRate, int channelCount){
    return (int) (second * sampleRate * channelCount);
  }
}
