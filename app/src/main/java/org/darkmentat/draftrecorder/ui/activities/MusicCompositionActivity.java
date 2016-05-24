package org.darkmentat.draftrecorder.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.JsonFileMusicCompositionRepository;
import org.darkmentat.draftrecorder.domain.MusicComposition;
import org.darkmentat.draftrecorder.domain.MusicComposition.Record;
import org.darkmentat.draftrecorder.domain.MusicComposition.Region;
import org.darkmentat.draftrecorder.domain.MusicComposition.Track;
import org.darkmentat.draftrecorder.domain.MusicCompositionRepository;
import org.darkmentat.draftrecorder.media.Player;
import org.darkmentat.draftrecorder.ui.views.WaveformView;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static org.darkmentat.draftrecorder.ui.activities.CutRecordActivity.EXTRA_LAST_SECOND;
import static org.darkmentat.draftrecorder.ui.activities.CutRecordActivity.EXTRA_RECORD;
import static org.darkmentat.draftrecorder.ui.activities.CutRecordActivity.EXTRA_START_CUT_SECONDS;

@EActivity(R.layout.activity_music_composition) @OptionsMenu(R.menu.menu_music_composition)
public class MusicCompositionActivity extends AppCompatActivity implements Player.PlayerListener {

  public static final int REQUEST_NEW_RECORD = 1;
  public static final int REQUEST_CUT_RECORD = 2;

  public static final String EXTRA_BPM = "BPM";
  public static final String EXTRA_BEATS = "BEATS";
  public static final String EXTRA_BEAT_LENGTH = "BEAT_LENGTH";
  public static final String EXTRA_RECORD_FILE = "RECORD_FILE";

  public static final String EXTRA_COMPOSITION_NAME = "COMPOSITION_NAME";

  @Extra(EXTRA_COMPOSITION_NAME) String mCompositionName = "";

  private MusicComposition mMusicComposition;

  private Player mPlayer;
  private MusicCompositionRepository mMusicCompositionRepository;

  private WaveformView mLastSelectedRecordView = null;
  private LinearLayout mLastSelectedRegionView = null;
  private LinearLayout mLastSelectedTrackView = null;

  @ViewById(R.id.fab) FloatingActionButton mFab;
  @ViewById(R.id.toolbar) Toolbar mToolbar;
  @ViewById(R.id.region_container) LinearLayout mRegionContainer;


  @Bean(JsonFileMusicCompositionRepository.class)
  public void setMusicCompositionRepository(MusicCompositionRepository repository){
    if(mMusicCompositionRepository == null) mMusicCompositionRepository = repository;
  }
  @Bean
  public void setPlayer(Player player){
    if(mPlayer == null) mPlayer = player;

    mPlayer.setPlayerListener(this);
  }

  @AfterViews void bindActionBar() {
    setSupportActionBar(mToolbar);
  }
  @AfterViews void loadMusicComposition(){

    mMusicComposition = mMusicCompositionRepository.getMusicCompositionsWithName(mCompositionName);

    if(mMusicComposition == null){
      mMusicComposition = new MusicComposition(0, mCompositionName);
      mMusicComposition.addRegion(new MusicComposition.Region());

      mMusicCompositionRepository.saveMusicComposition(mMusicComposition);
    }

    mRegionContainer.post(this::bindCompositionToLayout);
  }
  private void bindCompositionToLayout(){
    List<Region> regions = mMusicComposition.getRegions();

    for(Region region : regions){

      LinearLayout regionView = createRegionView(region);

      Collection<Track> tracks = region.getTracks().values();

      for(Track track : tracks){
        LinearLayout trackView = createTrackView(regionView, track);

        List<Record> records = track.getRecords();

        for(Record record : records){
          createRecordView(trackView, record);
        }
      }
    }

    mLastSelectedRegionView = (LinearLayout) mRegionContainer.getChildAt(0);
  }

  @OptionsItem(R.id.action_add_track) void onAddTrack(){
    mLastSelectedRegionView = (LinearLayout) mRegionContainer.getChildAt(0);

    Region region = (Region) mLastSelectedRegionView.getTag();
    Track track = new Track();
    region.addTrack(track);

    mMusicCompositionRepository.saveMusicComposition(mMusicComposition);

    createTrackView(mLastSelectedRegionView, track);
  }

  private LinearLayout createRegionView(Region region) {
    LinearLayout regionView = new LinearLayout(this);
    regionView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT){{setMargins(0,5,0,5);}});
    regionView.setOrientation(VERTICAL);
    regionView.setTag(region);

    mRegionContainer.addView(regionView);

    return regionView;
  }
  private LinearLayout createTrackView(final LinearLayout regionView, final Track track){
    final LinearLayout trackView = new LinearLayout(this);
    trackView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT){{setMargins(0,5,0,5);}});
    trackView.setMinimumHeight(100);
    trackView.setMinimumWidth(regionView.getRootView().getRootView().getMeasuredWidth());
    trackView.setOrientation(HORIZONTAL);
    trackView.setGravity(CENTER_VERTICAL);
    trackView.setPadding(5,5,5,5);
    trackView.setBackgroundColor(Color.LTGRAY);

    trackView.setOnLongClickListener(v -> {
      mLastSelectedTrackView = trackView;

      Region region = (Region) regionView.getTag();

      startCaptureSoundActivity(region);
      return true;
    });

    trackView.setTag(track);

    regionView.addView(trackView);

    return trackView;
  }

  private void startCaptureSoundActivity(Region region) {

    CaptureSoundActivity_.IntentBuilder_ intent = CaptureSoundActivity_
        .intent(MusicCompositionActivity.this);

    if(!region.hasSomeRecord()){
      intent.extra(EXTRA_COMPOSITION_NAME, mCompositionName);
    } else {
      intent.extra(EXTRA_COMPOSITION_NAME, mCompositionName)
            .extra(EXTRA_BPM, region.getBpm())
            .extra(EXTRA_BEATS, region.getBeats())
            .extra(EXTRA_BEAT_LENGTH, region.getBeatLength());
    }



    intent.startForResult(REQUEST_NEW_RECORD);
  }

  private void createRecordView(LinearLayout trackView, Record record){
    WaveformView recordView = new WaveformView(this);
    recordView.setLayoutParams(new LinearLayout.LayoutParams((int) (record.getCutDurationSeconds() * 100), 180){{setMargins(0,0,5,0);}});
    //recordView.setBackgroundColor(Color.DKGRAY);
    recordView.setTag(record);

    recordView.setChannels(1);
    recordView.setSampleRate(record.getSampleRate());
    recordView.setShowCutEnds(false);
    recordView.setStartCutSeconds(record.getStartFromSecond());
    recordView.setLastSecond(record.getLastSecond());
    recordView.setSamples(record.getSamples());
    recordView.setTempo(record.getBpm(), record.getBeats(), record.getBeatLength());

    recordView.setOnLongClickListener(v -> {
          mLastSelectedRecordView = recordView;
          CutRecordActivity_.intent(this)
              .extra(EXTRA_RECORD, record)
              .startForResult(REQUEST_CUT_RECORD);
          return true;
        }
    );

    trackView.addView(recordView);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(requestCode == REQUEST_CUT_RECORD && resultCode == RESULT_OK){

      Record record = (Record) mLastSelectedRecordView.getTag();

      float startCut = data.getFloatExtra(EXTRA_START_CUT_SECONDS, 0.0f);
      float endCut = data.getFloatExtra(EXTRA_LAST_SECOND, -1f);

      record.setStartFromSecond(startCut);
      record.setLastSecond(endCut);

      mLastSelectedRecordView.setStartCutSeconds(record.getStartFromSecond());
      mLastSelectedRecordView.setLastSecond(record.getLastSecond());
      mLastSelectedRecordView.setLayoutParams(new LinearLayout.LayoutParams((int) (record.getCutDurationSeconds() * 100), 180){{setMargins(0,0,5,0);}});

      mMusicCompositionRepository.saveMusicComposition(mMusicComposition);

      return;
    }

    if(requestCode != REQUEST_NEW_RECORD)
      return;

    if(resultCode != RESULT_OK)
      return;

    Region region = (Region) mLastSelectedRegionView.getTag();
    Track track = (Track) mLastSelectedTrackView.getTag();

    if(!region.hasSomeRecord()){
      int bpm = data.getIntExtra(EXTRA_BPM, -1);
      int beats = data.getIntExtra(EXTRA_BEATS, -1);
      int beatLength = data.getIntExtra(EXTRA_BEAT_LENGTH, -1);

      region.setBpm(bpm);
      region.setBeats(beats);
      region.setBeatLength(beatLength);
    }

    File file = (File) data.getSerializableExtra(EXTRA_RECORD_FILE);

    Record record = new Record(file);
    track.addRecord(record);

    createRecordView(mLastSelectedTrackView, record);

    mMusicCompositionRepository.saveMusicComposition(mMusicComposition);
  }

  @Click(R.id.fab) void onPlay(){

    if(mPlayer.getPlayerState() == Player.PlayerState.STOPPED){
      mPlayer.playStart(mMusicComposition, null);
    } else {
      mPlayer.playStop();
    }

    mFab.setImageResource(android.R.drawable.ic_media_pause);
  }

  @Override public void onPlayingStop() {
    mFab.setImageResource(android.R.drawable.ic_media_play);
  }
}
