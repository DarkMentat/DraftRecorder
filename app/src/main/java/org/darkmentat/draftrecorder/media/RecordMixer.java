package org.darkmentat.draftrecorder.media;

import org.darkmentat.draftrecorder.domain.MusicComposition;

import java.util.ArrayList;
import java.util.Collection;

public class RecordMixer {

  private static class RecordList extends ArrayList<MusicComposition.Record> {}

  private short[] mergeChunks(short[] chunk1, short[] chunk2){

    if(chunk1 == null && chunk2 == null)
      return null;

    if(chunk1 == null)
      return chunk2;

    if(chunk2 == null)
      return chunk1;


    short[] basic = chunk1.length > chunk2.length ? chunk1 : chunk2;
    short[] mixin = chunk1.length <= chunk2.length ? chunk1 : chunk2;

    for(int i = 0; i < basic.length; i++){

      short mixinValue = i < mixin.length ?  mixin[i] : 0;

      float mixed = basic[i] / 16384.0f + mixinValue / 16384.0f;

      // reduce the volume a bit:
      mixed *= 0.8;
      // hard clipping
      if (mixed > 1.0f) mixed = 1.0f;
      if (mixed < -1.0f) mixed = -1.0f;

      basic[i] = (short)(mixed * 16384.0f);
    }

    return basic;
  }
  private short[] getMixedChunk(RecordList[] tracks){

    int trackCount = tracks.length;

    if(mDecoders == null)
      mDecoders = new RecordDecoder[trackCount];

    short[] mixedChunk = null;

    for(int i = 0; i < trackCount; i++){

      short[] chunk = null;

      if(mDecoders[i] == null && !tracks[i].isEmpty()){
        mDecoders[i] = new RecordDecoder(tracks[i].get(0));
        mDecoders[i].startRecordReading();
        mDecoders[i].seekToRecordStart();
      }

      if(mDecoders[i] == null){
        continue;
      }

      if(mDecoders[i] != null){
        chunk = mDecoders[i].isEnd() ? null : mDecoders[i].readRecordChunkShorts();
      }

      if(chunk == null) {
        mDecoders[i].stopRecordReading();
        mDecoders[i] = null;
        tracks[i].remove(0);
      }

      mixedChunk = mergeChunks(mixedChunk, chunk);
    }

    return mixedChunk;
  }

  private final RecordList[] mTracksWithRecords;
  private RecordDecoder[] mDecoders;

  public RecordMixer(MusicComposition composition) {
    int tracks = composition.getTrackCount();

    mTracksWithRecords = new RecordList[tracks];

    for(int i = 0; i < mTracksWithRecords.length; i++){
      mTracksWithRecords[i] = new RecordList();
    }

    MusicComposition.Region region = composition.getRegions().get(0);

    Collection<MusicComposition.Track> tracksInRegion = region.getTracks().values();

    int i = 0;
    for(MusicComposition.Track track : tracksInRegion){

      mTracksWithRecords[i].addAll(track.getRecords());

      i++;
    }
  }

  public short[] readChunk(){
    return getMixedChunk(mTracksWithRecords);
  }
  public void releaseAll(){

    if(mDecoders == null)
      return;

    for(RecordDecoder decoder : mDecoders){
      if(decoder != null){
        decoder.stopRecordReading();
      }
    }
  }
}
