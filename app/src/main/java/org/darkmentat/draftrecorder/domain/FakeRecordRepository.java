package org.darkmentat.draftrecorder.domain;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.Map;

@EBean public class FakeRecordRepository implements RecordRepository {

  private Map<Integer, Record> mRecords = new HashMap<>(15);
  {
    mRecords.put(0, new Record(0, "Test1", 120));
    mRecords.put(1, new Record(1, "Test2", 140));
    mRecords.put(2, new Record(2, "Test3", 180));
    mRecords.put(3, new Record(3, "Test4 123 123 123", 60));
    mRecords.put(4, new Record(4, "Test5", 120));
    mRecords.put(5, new Record(5, "Test6", 80));
  }

  @Override public Record[] getAllRecords() {
    return mRecords.values().toArray(new Record[mRecords.size()]);
  }

  @Override public void saveRecord(Record record) {
    mRecords.put(record.getId(), record);
  }
}
