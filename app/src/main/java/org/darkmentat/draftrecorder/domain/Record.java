package org.darkmentat.draftrecorder.domain;

public class Record {
  private int mId;
  private String mName;
  private int mBps;

  public Record(int mId, String mName, int mBps) {
    this.mId = mId;
    this.mName = mName;
    this.mBps = mBps;
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

  public int getBps() {
    return mBps;
  }
  public void setBps(int bps) {
    mBps = bps;
  }
}
