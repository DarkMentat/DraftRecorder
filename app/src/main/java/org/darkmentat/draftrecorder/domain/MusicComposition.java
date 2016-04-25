package org.darkmentat.draftrecorder.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicComposition {

  public static class Region {
    private int mBpm;
    private int mBeats;
    private int mBeatLength;

    public Region(int bpm, int beats, int beatLength) {
      mBpm = bpm;
      mBeats = beats;
      mBeatLength = beatLength;
    }

    private Map<Integer, Track> mTracks = new HashMap<>();

    public void addTrack(int position, Track track){
      mTracks.put(position, track);
    }
    public void removeTrack(int position){
      mTracks.remove(position);
    }
    public Map<Integer, Track> getTracks(){
      return mTracks;
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

  }

  public static class Record {


  }

  private int mId;
  private String mName;

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
}
