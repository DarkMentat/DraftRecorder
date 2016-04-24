package org.darkmentat.draftrecorder.ui.activities;

import android.os.Build;

import org.darkmentat.draftrecorder.BuildConfig;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.media.Player;
import org.darkmentat.draftrecorder.media.Recorder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.KITKAT)
public class CaptureSoundActivityTest {

  @Test public void testRecorderUsed() {
    Recorder recorder = mock(Recorder.class);

    ActivityController<CaptureSoundActivity_> captureActivityController = Robolectric.buildActivity(CaptureSoundActivity_.class);
    captureActivityController.get().setRecorder(recorder);
    CaptureSoundActivity activity = captureActivityController.create().start().resume().visible().get();

    //verify(recorder, atLeastOnce()).setNewRecordInfo(anyString());

    assertThat(shadowOf(activity).findViewById(R.id.start_capture)).isVisible();
    assertThat(shadowOf(activity).findViewById(R.id.stop_capture)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.play_sound)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.stop_sound)).isGone();

    shadowOf(activity).findViewById(R.id.start_capture).performClick();
    //verify(recorder).recordStart();

    assertThat(shadowOf(activity).findViewById(R.id.start_capture)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.stop_capture)).isVisible();
    assertThat(shadowOf(activity).findViewById(R.id.play_sound)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.stop_sound)).isGone();

    shadowOf(activity).findViewById(R.id.stop_capture).performClick();
    verify(recorder).recordStop();

    assertThat(shadowOf(activity).findViewById(R.id.start_capture)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.stop_capture)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.play_sound)).isVisible();
    assertThat(shadowOf(activity).findViewById(R.id.stop_sound)).isGone();
  }
  @Test public void testPlayerUsed() {
    Player player = mock(Player.class);

    ActivityController<CaptureSoundActivity_> captureActivityController = Robolectric.buildActivity(CaptureSoundActivity_.class);
    captureActivityController.get().setPlayer(player);
    CaptureSoundActivity activity = captureActivityController.create().start().resume().visible().get();

    verify(player, atLeastOnce()).setFileName(anyString());

    shadowOf(activity).findViewById(R.id.start_capture).performClick();
    shadowOf(activity).findViewById(R.id.stop_capture).performClick();

    shadowOf(activity).findViewById(R.id.play_sound).performClick();
    verify(player).playStart();

    assertThat(shadowOf(activity).findViewById(R.id.start_capture)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.stop_capture)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.play_sound)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.stop_sound)).isVisible();

    shadowOf(activity).findViewById(R.id.stop_sound).performClick();
    verify(player).playStop();

    assertThat(shadowOf(activity).findViewById(R.id.start_capture)).isVisible();
    assertThat(shadowOf(activity).findViewById(R.id.stop_capture)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.play_sound)).isGone();
    assertThat(shadowOf(activity).findViewById(R.id.stop_sound)).isGone();
  }
}