package org.darkmentat.draftrecorder.ui.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;

@EActivity(R.layout.activity_record) @OptionsMenu(R.menu.menu_record)
public class RecordActivity extends AppCompatActivity {

  @ViewById(R.id.toolbar) Toolbar mToolbar;
  @ViewById(R.id.track_container) LinearLayout mTrackContainer;

  @AfterViews protected void bindActionBar() {
    setSupportActionBar(mToolbar);
  }

  @OptionsItem(R.id.action_add_track)
  protected void onAddTrack(){
    LinearLayout track = new LinearLayout(this);
    track.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT){{setMargins(0,5,0,5);}});
    track.setMinimumHeight(100);
    track.setMinimumWidth(mTrackContainer.getRootView().getRootView().getMeasuredWidth());
    track.setOrientation(HORIZONTAL);
    track.setBackgroundColor(Color.LTGRAY);

    mTrackContainer.addView(track);
  }
}
