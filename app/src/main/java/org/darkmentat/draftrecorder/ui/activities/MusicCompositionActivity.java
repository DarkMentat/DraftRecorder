package org.darkmentat.draftrecorder.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.MusicComposition;
import org.darkmentat.draftrecorder.domain.MusicComposition.Record;
import org.darkmentat.draftrecorder.domain.MusicComposition.Region;
import org.darkmentat.draftrecorder.domain.MusicComposition.Track;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

@EActivity(R.layout.activity_music_composition) @OptionsMenu(R.menu.menu_music_composition)
public class MusicCompositionActivity extends AppCompatActivity {

  public static final int REQUEST_NEW_RECORD = 1;

  public static final String EXTRA_BPM = "BPM";
  public static final String EXTRA_BEATS = "BEATS";
  public static final String EXTRA_BEAT_LENGTH = "BEAT_LENGTH";
  public static final String EXTRA_RECORD_FILE = "RECORD_FILE";

  public static final String EXTRA_COMPOSITION_NAME = "COMPOSITION_NAME";

  @Extra(EXTRA_COMPOSITION_NAME) String mCompositionName = "";

  private MusicComposition mMusicComposition = new MusicComposition(0, mCompositionName);
  {
    mMusicComposition.addRegion(new MusicComposition.Region());
  }

  @ViewById(R.id.toolbar) Toolbar mToolbar;
  @ViewById(R.id.region_container) LinearLayout mRegionContainer;

  @AfterViews void bindActionBar() {
    setSupportActionBar(mToolbar);
  }
  @AfterViews void loadMusicComposition(){
    List<Region> regions = mMusicComposition.getRegions();

    for(Region region : regions){

      LinearLayout regionView = createRegionView(region);

      Collection<Track> tracks = region.getTracks().values();

      for(Track track : tracks){
        createTrackView(regionView, track);
      }
    }
  }

  @OptionsItem(R.id.action_add_track) void onAddTrack(){
    createTrackView((LinearLayout) mRegionContainer.getChildAt(0), new Track());
  }

  private LinearLayout createRegionView(Region region) {
    LinearLayout regionView = new LinearLayout(this);
    regionView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT){{setMargins(0,5,0,5);}});
    regionView.setOrientation(VERTICAL);
    regionView.setTag(region);

    mRegionContainer.addView(regionView);

    return regionView;
  }
  private void createTrackView(final LinearLayout regionView, final Track track){
    final LinearLayout trackView = new LinearLayout(this);
    trackView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT){{setMargins(0,5,0,5);}});
    trackView.setMinimumHeight(100);
    trackView.setMinimumWidth(regionView.getRootView().getRootView().getMeasuredWidth());
    trackView.setOrientation(HORIZONTAL);
    trackView.setGravity(CENTER_VERTICAL);
    trackView.setPadding(5,5,5,5);
    trackView.setBackgroundColor(Color.LTGRAY);

    trackView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        if(((Region) regionView.getTag()).hasSomeRecord()){
          CaptureSoundActivity_.intent(MusicCompositionActivity.this)
              .extra(EXTRA_COMPOSITION_NAME, mCompositionName)
              .startForResult(REQUEST_NEW_RECORD);
        } else {
          CaptureSoundActivity_.intent(MusicCompositionActivity.this)
              .extra(EXTRA_COMPOSITION_NAME, mCompositionName)
              .extra(EXTRA_BPM, ((Region) regionView.getTag()).getBpm())
              .extra(EXTRA_BEATS, ((Region) regionView.getTag()).getBeats())
              .extra(EXTRA_BEAT_LENGTH, ((Region) regionView.getTag()).getBeatLength())
              .startForResult(REQUEST_NEW_RECORD);
        }
        return true;
      }
    });

    trackView.setTag(track);

    regionView.addView(trackView);
  }
  private void createRecordView(LinearLayout trackView, Record record){
    View recordView = new View(this);
    recordView.setLayoutParams(new LinearLayout.LayoutParams(170, 90){{setMargins(0,0,5,0);}});
    recordView.setBackgroundColor(Color.DKGRAY);
    recordView.setTag(record);

    trackView.addView(recordView);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(requestCode != REQUEST_NEW_RECORD)
      return;

    if(resultCode != RESULT_OK)
      return;

    Region region = (Region) mRegionContainer.getChildAt(0).getTag();
    Track track = (Track) ((LinearLayout) mRegionContainer.getChildAt(0)).getChildAt(0).getTag();

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

    createRecordView((LinearLayout) ((LinearLayout) mRegionContainer.getChildAt(0)).getChildAt(0), record);
  }
}
