package org.darkmentat.draftrecorder.ui.activities;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;


import org.darkmentat.draftrecorder.BuildConfig;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.MusicComposition;
import org.darkmentat.draftrecorder.domain.MusicCompositionRepository;
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
public class MusicCompositionListActivityTest {

//  @Test public void testRecordsListLoaded() {
//    MusicCompositionRepository recordRepository = mock(MusicCompositionRepository.class);
//    when(recordRepository.getAllMusicCompositions()).thenReturn(new MusicComposition[]{
//        new MusicComposition(0, "Test1", 80),
//        new MusicComposition(0, "Test2", 120),
//        new MusicComposition(0, "Test3", 80)
//    });
//
//    ActivityController<MusicCompositionListActivity_> recordsActivityController = Robolectric.buildActivity(MusicCompositionListActivity_.class);
//    recordsActivityController.get().setMusicCompositionRepository(recordRepository);
//    MusicCompositionListActivity activity = recordsActivityController.create().start().resume().visible().get();
//
//    RecyclerView listMusicCompositions = (RecyclerView) shadowOf(activity).findViewById(R.id.list_records);
//
//    assertThat(listMusicCompositions).hasChildCount(3);
//    assertThat((TextView)(listMusicCompositions.getChildAt(0).findViewById(R.id.music_composition_name))).containsText("Test1");
//    assertThat((TextView)(listMusicCompositions.getChildAt(1).findViewById(R.id.music_composition_name))).containsText("Test2");
//    assertThat((TextView)(listMusicCompositions.getChildAt(2).findViewById(R.id.music_composition_name))).containsText("Test3");
//  }
}
