package org.darkmentat.draftrecorder.ui.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
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
  private ViewGroup mLastSelectedRegionView = null;
  private ViewGroup mLastSelectedTrackView = null;

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

      ViewGroup regionView = createRegionView(region);

      Collection<Track> tracks = region.getTracks().values();

      for(Track track : tracks){
        ViewGroup trackView = createTrackView(regionView, track);

        List<Record> records = track.getRecords();

        for(Record record : records){
          createRecordView(trackView, record);
        }
      }
    }

    mLastSelectedRegionView = (ViewGroup) mRegionContainer.getChildAt(0);
  }

  @Click(R.id.add_region) @OptionsItem(R.id.action_add_region) void onAddRegion(){

    Region region = new Region();
    createRegionView(region);
    mMusicComposition.addRegion(region);

    mMusicCompositionRepository.saveMusicComposition(mMusicComposition);

  }

  private ViewGroup createRegionView(Region region) {
    CardView regionView = (CardView) getLayoutInflater().inflate(R.layout.view_region, null);
    regionView.setMinimumWidth(mRegionContainer.getRootView().getMeasuredWidth()-54);
    regionView.setTag(region);

    regionView.findViewById(R.id.menu).setOnClickListener(v -> showRegionViewPopUp(v, regionView));
    mRegionContainer.addView(regionView);

    mLastSelectedRegionView = regionView;

    return regionView;
  }
  private boolean showRegionViewPopUp(View view, ViewGroup regionView){
    PopupMenu popupMenu = new PopupMenu(this, view);
    popupMenu.inflate(R.menu.menu_region_view);

    popupMenu.setOnMenuItemClickListener(item -> {
      switch (item.getItemId()){

        case R.id.action_add_track:
          Region region = (Region) regionView.getTag();
          Track track = new Track();
          region.addTrack(track);
          createTrackView(regionView, track);

          mMusicCompositionRepository.saveMusicComposition(mMusicComposition);

          return true;

        case R.id.action_delete:

          region = ((Region) regionView.getTag());
          mMusicComposition.removeRegion(region);
          mRegionContainer.removeView(regionView);

          mMusicCompositionRepository.saveMusicComposition(mMusicComposition);
          return true;

        default:
          return false;
      }
    });
    popupMenu.show();

    return true;
  }
  private ViewGroup createTrackView(final ViewGroup regionView, final Track track){
    final ViewGroup trackView = (ViewGroup) getLayoutInflater().inflate(R.layout.view_track, null);
    trackView.setMinimumWidth(regionView.getRootView().getMeasuredWidth()-54);
    trackView.findViewById(R.id.add_record).setOnClickListener(v -> {
      mLastSelectedTrackView = trackView;

      Region region = (Region) regionView.getTag();

      startCaptureSoundActivity(region);
    });
    trackView.findViewById(R.id.remove_track).setOnClickListener(v -> {
      Region region = (Region) regionView.getTag();

      region.removeTrack(track);
      ((ViewGroup) regionView.findViewById(R.id.tracks)).removeView(trackView);

      mMusicCompositionRepository.saveMusicComposition(mMusicComposition);
    });

    trackView.setTag(track);

    ((ViewGroup) regionView.findViewById(R.id.tracks)).addView(trackView);

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

  private void createRecordView(ViewGroup trackView, Record record){
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

    recordView.setOnLongClickListener(v -> showRecordViewPopUp(trackView, recordView, record));

    trackView.addView(recordView, 0);
  }

  private boolean showRecordViewPopUp(ViewGroup trackView, WaveformView recordView, Record record){
    PopupMenu popupMenu = new PopupMenu(this, recordView);
    popupMenu.inflate(R.menu.menu_record_view);

    popupMenu.setOnMenuItemClickListener(item -> {
      switch (item.getItemId()){
        case R.id.action_cut_record:
          mLastSelectedRecordView = recordView;
          CutRecordActivity_.intent(this)
              .extra(EXTRA_RECORD, record)
              .startForResult(REQUEST_CUT_RECORD);
          return true;
        case R.id.action_delete:
          Track track = (Track) trackView.getTag();

          track.removeRecord(record);
          trackView.removeView(recordView);

          mMusicCompositionRepository.saveMusicComposition(mMusicComposition);
          return true;
        default:
          return false;
      }
    });
    popupMenu.show();

    return true;
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
