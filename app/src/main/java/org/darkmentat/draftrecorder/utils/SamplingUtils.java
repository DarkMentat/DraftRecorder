package org.darkmentat.draftrecorder.utils;

import java.util.Arrays;

public final class SamplingUtils {
  public static short[][] getExtremes(short[] data, int sampleSize, int startIndex, int endIndex) {

    if(endIndex > data.length)
      endIndex = data.length;

    short[][] newData = new short[sampleSize][];
    int groupSize = (endIndex-startIndex) / sampleSize;

    for (int i = 0; i < sampleSize; i++) {

      int from = startIndex + i * groupSize;
      int to = Math.min(startIndex + (i + 1) * groupSize, endIndex);

      short[] group = Arrays.copyOfRange(data, from, to);

      // Fin min & max values
      short min = Short.MAX_VALUE, max = Short.MIN_VALUE;
      for (short a : group) {
        min = (short) Math.min(min, a);
        max = (short) Math.max(max, a);
      }
      newData[i] = new short[] { max, min };
    }

    return newData;
  }
}
