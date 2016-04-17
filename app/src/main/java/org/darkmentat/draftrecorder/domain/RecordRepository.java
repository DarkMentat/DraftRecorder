package org.darkmentat.draftrecorder.domain;

import org.androidannotations.annotations.EBean;


public interface RecordRepository {
  Record[] getAllRecords();
  void saveRecord(Record record);
}
