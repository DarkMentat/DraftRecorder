package org.darkmentat.draftrecorder;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;


import org.darkmentat.draftrecorder.domain.Record;
import org.darkmentat.draftrecorder.domain.RecordRepository;
import org.darkmentat.draftrecorder.ui.activities.RecordsActivity;
import org.darkmentat.draftrecorder.ui.activities.RecordsActivity_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.android.recyclerview.v7.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.KITKAT)
public class RecordsActivityTest {

  @Test public void testRecordsListLoaded() {
    RecordRepository recordRepository = mock(RecordRepository.class);
    when(recordRepository.getAllRecords()).thenReturn(new Record[]{
        new Record(0, "Test1", 80),
        new Record(0, "Test2", 120),
        new Record(0, "Test3", 80)
    });

    ActivityController<RecordsActivity_> recordsActivityController = Robolectric.buildActivity(RecordsActivity_.class);
    recordsActivityController.get().setRecordRepository(recordRepository);
    RecordsActivity activity = recordsActivityController.create().start().resume().visible().get();

    RecyclerView listRecords = (RecyclerView) shadowOf(activity).findViewById(R.id.list_records);

    assertThat(listRecords).hasChildCount(3);
    assertThat((TextView)(listRecords.getChildAt(0).findViewById(R.id.record_name))).containsText("Test1");
    assertThat((TextView)(listRecords.getChildAt(1).findViewById(R.id.record_name))).containsText("Test2");
    assertThat((TextView)(listRecords.getChildAt(2).findViewById(R.id.record_name))).containsText("Test3");
  }
}
