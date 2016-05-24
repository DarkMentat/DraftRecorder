package org.darkmentat.draftrecorder.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.MusicComposition;
import org.darkmentat.draftrecorder.ui.views.WaveformView;

@EActivity(R.layout.activity_cut_record)
public class CutRecordActivity extends AppCompatActivity {

  public static final String EXTRA_RECORD = "EXTRA_RECORD";
  public static final String EXTRA_START_CUT_SECONDS = "EXTRA_START_CUT_SECONDS";
  public static final String EXTRA_LAST_SECOND = "EXTRA_LAST_SECOND";

  @Extra(EXTRA_RECORD) MusicComposition.Record mRecord = null;

  @ViewById(R.id.scroll_view) HorizontalScrollView mScrollView;
  @ViewById(R.id.root_container) FrameLayout mRootLayout;
  @ViewById(R.id.save_record) Button mSaveRecord;
  @ViewById(R.id.enable_change_grid) Button mEnableGridChange;
  @ViewById(R.id.disable_change_grid) Button mDisableGridChange;

  private WaveformView mRecordView;

  @AfterViews void loadRecordWaveformView(){
    if(mRecord == null)
      return;

    mRecord.getSamples();

    mRecordView = new WaveformView(this);
    mRecordView.setLayoutParams(new LinearLayout.LayoutParams((int) (mRecord.getCutDurationSeconds() * 400), 300){{setMargins(0,0,5,0);}});

    mRecordView.setStartCutSeconds(mRecord.getStartFromSecond());
    mRecordView.setLastSecond(mRecord.getLastSecond());
    mRecordView.setShowCutEnds(true);
    mRecordView.setChannels(1);
    mRecordView.setSampleRate(mRecord.getSampleRate());
    mRecordView.setSamples(mRecord.getSamples());
    mRecordView.setTempo(mRecord.getBpm(), mRecord.getBeats(), mRecord.getBeatLength());

    mRootLayout.addView(mRecordView);
  }

  @Click(R.id.save_record) void onSaveRecord(){

    setResult(RESULT_OK, new Intent(){{
      putExtra(EXTRA_START_CUT_SECONDS, mRecordView.getStartCutSeconds());
      putExtra(EXTRA_LAST_SECOND, mRecordView.getLastSecond());
    }});

    finish();
  }

  @Click(R.id.enable_change_grid) void enableGridChange(){
    mEnableGridChange.setVisibility(View.GONE);
    mDisableGridChange.setVisibility(View.VISIBLE);

    mRecordView.setCanChangeTempoGrid(true);
    mScrollView.setOnTouchListener((v, event) -> mRecordView.onTouchEvent(event));
  }
  @Click(R.id.disable_change_grid) void disableGridChange(){
    mEnableGridChange.setVisibility(View.VISIBLE);
    mDisableGridChange.setVisibility(View.GONE);

    mRecordView.setCanChangeTempoGrid(false);
    mScrollView.setOnTouchListener(null);
  }
}
