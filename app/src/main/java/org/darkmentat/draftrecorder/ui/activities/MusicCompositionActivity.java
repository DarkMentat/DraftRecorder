package org.darkmentat.draftrecorder.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.MusicComposition;
import org.darkmentat.draftrecorder.domain.MusicComposition.Region;
import org.darkmentat.draftrecorder.domain.MusicComposition.Track;

import java.util.Collection;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

@EActivity(R.layout.activity_music_composition) @OptionsMenu(R.menu.menu_music_composition)
public class MusicCompositionActivity extends AppCompatActivity {

  public static final int REQUEST_NEW_RECORD = 1;

  private MusicComposition mMusicComposition = new MusicComposition(0, "TestComposition");
  {
    mMusicComposition.addRegion(new MusicComposition.Region(120, 4, 4));
  }

  @ViewById(R.id.toolbar) Toolbar mToolbar;
  @ViewById(R.id.region_container) LinearLayout mRegionContainer;

  @AfterViews protected void bindActionBar() {
    setSupportActionBar(mToolbar);
  }
  @AfterViews protected void loadMusicComposition(){
    List<Region> regions = mMusicComposition.getRegions();

    for(Region region : regions){

      LinearLayout regionView = createRegionView(region);

      Collection<Track> tracks = region.getTracks().values();

      for(Track track : tracks){
        createTrackView(regionView, track);
      }
    }
  }

  @OptionsItem(R.id.action_add_track)
  protected void onAddTrack(){
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
  private void createTrackView(LinearLayout regionView, Track track){
    LinearLayout trackView = new LinearLayout(this);
    trackView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT){{setMargins(0,5,0,5);}});
    trackView.setMinimumHeight(100);
    trackView.setMinimumWidth(regionView.getRootView().getRootView().getMeasuredWidth());
    trackView.setOrientation(HORIZONTAL);
    trackView.setBackgroundColor(Color.LTGRAY);

    trackView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        CaptureSoundActivity_.intent(MusicCompositionActivity.this).startForResult(REQUEST_NEW_RECORD);
        return true;
      }
    });

    trackView.setTag(track);

    regionView.addView(trackView);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(requestCode != REQUEST_NEW_RECORD)
      return;

    if(resultCode != RESULT_OK)
      return;

  }
}
