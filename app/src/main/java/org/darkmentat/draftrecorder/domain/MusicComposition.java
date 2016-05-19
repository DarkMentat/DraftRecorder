package org.darkmentat.draftrecorder.domain;

import com.google.gson.annotations.Expose;

import org.darkmentat.draftrecorder.media.RecordDecoder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicComposition {

  public static class Region {
    private int mBpm;
    private int mBeats;
    private int mBeatLength;

    public Region() {
      this(-1,-1,-1);
    }
    public Region(int bpm, int beats, int beatLength) {
      mBpm = bpm;
      mBeats = beats;
      mBeatLength = beatLength;
    }

    private Map<Integer, Track> mTracks = new HashMap<>();

    public void addTrack(int position, Track track){
      mTracks.put(position, track);
    }
    public void addTrack(Track track) {
      mTracks.put(mTracks.size(), track);
    }
    public void removeTrack(int position){
      mTracks.remove(position);
    }
    public Map<Integer, Track> getTracks(){
      return mTracks;
    }

    public int getBpm() {
      return mBpm;
    }
    public void setBpm(int bpm) {
      mBpm = bpm;
    }

    public int getBeats() {
      return mBeats;
    }
    public void setBeats(int beats) {
      mBeats = beats;
    }

    public int getBeatLength() {
      return mBeatLength;
    }
    public void setBeatLength(int beatLength) {
      mBeatLength = beatLength;
    }

    public boolean hasSomeRecord(){
      for(Track track : mTracks.values()){
        if(track.hasSomeRecord())
          return true;
      }

      return false;
    }
  }

  public static class Track {

    private List<Record> mRecords = new ArrayList<>();

    public void addRecord(Record record){
      mRecords.add(record);
    }
    public void removeRecord(Record record){
      mRecords.remove(record);
    }
    public List<Record> getRecords() {
      return mRecords;
    }
    public boolean hasSomeRecord(){
      return !mRecords.isEmpty();
    }
  }

  public static class Record {

    @Expose private long mDuration = 0;  //microseconds
    @Expose private int mSampleRate = 0;
    @Expose private short[] mSamples = null;

    private File mFile;
    private String mFileName;

    public Record(File file) {
      mFile = file;

      mFileName = mFile.getName();
    }

    public File getFile() {
      return mFile;
    }

    private void readData(){
      RecordDecoder decoder = new RecordDecoder(this);

      decoder.startRecordReading();

      mDuration = decoder.getDuration();
      mSampleRate = decoder.getSampleRate();

      List<Short> shorts = new ArrayList<>();

      short[] arr;

      while(true){
        arr = decoder.readRecordChunkShorts();

        if(arr == null)
          break;

        for(short i : arr){
          shorts.add(i);
        }
      }

      mSamples = new short[shorts.size()];
      int i = 0;
      for(Short aShort : shorts){
        mSamples[i++] = aShort;
      }

      decoder.stopRecordReading();
    }

    public long getDuration(){
      if(mDuration ==  0)
        readData();

      return mDuration;
    }
    public int getSampleRate(){
      if(mSampleRate == 0)
        readData();

      return mSampleRate;
    }
    public short[] getSamples(){
      if(mSamples == null)
        readData();

      return mSamples;
    }

    public int getBpm(){
      if(mFileName == null){
        mFileName = mFile.getName();
      }

      String[] split = mFileName.split("(\\s|\\.)");
      return Integer.valueOf(split[split.length - 4]);
    }
    public int getBeats(){
      if(mFileName == null){
        mFileName = mFile.getName();
      }

      String[] split = mFileName.split("(\\s|\\.)");
      return Integer.valueOf(split[split.length - 3]);
    }
    public int getBeatLength(){
      if(mFileName == null){
        mFileName = mFile.getName();
      }

      String[] split = mFileName.split("(\\s|\\.)");
      return Integer.valueOf(split[split.length - 2]);
    }
  }

  private int mId = -1;
  private String mName = "";

  private List<Region> mRegions = new ArrayList<>();

  public MusicComposition(int id, String name) {
    mId = id;
    mName = name;
  }

  public int getId() {
    return mId;
  }

  public String getName() {
    return mName;
  }
  public void setName(String name) {
    mName = name;
  }

  public void addRegion(Region region){
    mRegions.add(region);
  }
  public void removeRegion(Region region){
    mRegions.remove(region);
  }
  public List<Region> getRegions(){
    return mRegions;
  }
  public int getTrackCount(){
    int tracks = -1;

    for(Region region : mRegions){
      int regionTracks = region.getTracks().size();

      if(regionTracks > tracks)
        tracks = regionTracks;
    }

    return tracks;
  }
}
