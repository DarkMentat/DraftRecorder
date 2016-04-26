package org.darkmentat.draftrecorder.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.*;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.FakeMusicCompositionRepository;
import org.darkmentat.draftrecorder.domain.MusicComposition;
import org.darkmentat.draftrecorder.domain.MusicCompositionRepository;
import org.darkmentat.draftrecorder.ui.adapters.MusicCompositionsAdapter;

import static org.darkmentat.draftrecorder.ui.activities.MusicCompositionActivity.EXTRA_BEATS;
import static org.darkmentat.draftrecorder.ui.activities.MusicCompositionActivity.EXTRA_BEAT_LENGTH;
import static org.darkmentat.draftrecorder.ui.activities.MusicCompositionActivity.EXTRA_BPM;

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

  @AfterViews void bindActionBar() {
    setSupportActionBar(mToolbar);
  }
  @AfterViews void setListRecords(){
    mMusicCompositionsAdapter.setItems(mMusicCompositionRepository.getAllMusicCompositions());

    mListRecords.setHasFixedSize(true);
    mListRecords.setLayoutManager(new LinearLayoutManager(this));
    mListRecords.setAdapter(mMusicCompositionsAdapter);
  }

  @Click(R.id.fab) void onFab(){
    createNewCompositionDialog().show();
  }

  @OptionsItem(R.id.action_settings) void onSettings(){
    Toast.makeText(MusicCompositionListActivity.this, "Settings", Toast.LENGTH_SHORT).show();
  }

  private Dialog createNewCompositionDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("New music composition");
    builder.setMessage("Enter composition name:");

    final EditText input = new EditText(this);
    input.setId(0);
    builder.setView(input);

    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int whichButton) {
        MusicCompositionActivity_.intent(MusicCompositionListActivity.this)
            .extra(MusicCompositionActivity.EXTRA_COMPOSITION_NAME, input.getText().toString())
            .start();
      }
    });

    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
      }
    });

    return builder.create();
  }
}
