package org.darkmentat.draftrecorder.ui.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.androidannotations.annotations.*;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.FakeRecordRepository;
import org.darkmentat.draftrecorder.domain.RecordRepository;
import org.darkmentat.draftrecorder.ui.adapters.RecordsAdapter;

@EActivity(R.layout.activity_record_list) @OptionsMenu(R.menu.menu_record_list)
public class RecordListActivity extends AppCompatActivity {

  @ViewById(R.id.toolbar) Toolbar mToolbar;
  @ViewById(R.id.fab) FloatingActionButton mFab;
  @ViewById(R.id.list_records) RecyclerView mListRecords;

  RecordRepository mRecordRepository;
  RecordsAdapter mRecordAdapter;

  @Bean(FakeRecordRepository.class)
  public void setRecordRepository(RecordRepository repository){
    if(mRecordRepository == null) mRecordRepository = repository;
  }
  @Bean
  public void setRecordsAdapter(RecordsAdapter adapter){
    if(mRecordAdapter == null) mRecordAdapter = adapter;
  }

  @AfterViews protected void bindActionBar() {
    setSupportActionBar(mToolbar);
  }
  @AfterViews protected void setListRecords(){
    mRecordAdapter.setItems(mRecordRepository.getAllRecords());

    mListRecords.setHasFixedSize(true);
    mListRecords.setLayoutManager(new LinearLayoutManager(this));
    mListRecords.setAdapter(mRecordAdapter);
  }

  @Click(R.id.fab)
  protected void onFab(){
    RecordActivity_.intent(this).start();
  }

  @OptionsItem(R.id.action_settings)
  protected void onSettings(){
    Toast.makeText(RecordListActivity.this, "Settings", Toast.LENGTH_SHORT).show();
  }
}
