package org.darkmentat.draftrecorder.ui.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.androidannotations.annotations.*;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.FakeMusicCompositionRepository;
import org.darkmentat.draftrecorder.domain.MusicCompositionRepository;
import org.darkmentat.draftrecorder.ui.adapters.MusicCompositionsAdapter;

@EActivity(R.layout.activity_music_composition_list) @OptionsMenu(R.menu.menu_music_composition_list)
public class MusicCompositionListActivity extends AppCompatActivity {

  @ViewById(R.id.toolbar) Toolbar mToolbar;
  @ViewById(R.id.fab) FloatingActionButton mFab;
  @ViewById(R.id.list_records) RecyclerView mListRecords;

  MusicCompositionRepository mMusicCompositionRepository;
  MusicCompositionsAdapter mMusicCompositionsAdapter;

  @Bean(FakeMusicCompositionRepository.class)
  public void setMusicCompositionRepository(MusicCompositionRepository repository){
    if(mMusicCompositionRepository == null) mMusicCompositionRepository = repository;
  }
  @Bean
  public void setMusicCompositionsAdapter(MusicCompositionsAdapter adapter){
    if(mMusicCompositionsAdapter == null) mMusicCompositionsAdapter = adapter;
  }

  @AfterViews protected void bindActionBar() {
    setSupportActionBar(mToolbar);
  }
  @AfterViews protected void setListRecords(){
    mMusicCompositionsAdapter.setItems(mMusicCompositionRepository.getAllMusicCompositions());

    mListRecords.setHasFixedSize(true);
    mListRecords.setLayoutManager(new LinearLayoutManager(this));
    mListRecords.setAdapter(mMusicCompositionsAdapter);
  }

  @Click(R.id.fab)
  protected void onFab(){
    MusicCompositionActivity_.intent(this).start();
  }

  @OptionsItem(R.id.action_settings)
  protected void onSettings(){
    Toast.makeText(MusicCompositionListActivity.this, "Settings", Toast.LENGTH_SHORT).show();
  }
}
