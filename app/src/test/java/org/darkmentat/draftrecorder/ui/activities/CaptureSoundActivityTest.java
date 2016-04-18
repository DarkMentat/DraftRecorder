package org.darkmentat.draftrecorder.ui.activities;

import android.os.Build;

import org.darkmentat.draftrecorder.BuildConfig;
import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.media.Player;
import org.darkmentat.draftrecorder.media.Recorder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Any;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
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

    verify(recorder, atLeastOnce()).setFileName(anyString());

    shadowOf(activity).findViewById(R.id.start_capture).performClick();
    verify(recorder).recordStart();

    shadowOf(activity).findViewById(R.id.stop_capture).performClick();
    verify(recorder).recordStop();
  }
  @Test public void testPlayerUsed() {
    Player player = mock(Player.class);

    ActivityController<CaptureSoundActivity_> captureActivityController = Robolectric.buildActivity(CaptureSoundActivity_.class);
    captureActivityController.get().setPlayer(player);
    CaptureSoundActivity activity = captureActivityController.create().start().resume().visible().get();

    verify(player, atLeastOnce()).setFileName(anyString());

    shadowOf(activity).findViewById(R.id.play_sound).performClick();
    verify(player).playStart();

    shadowOf(activity).findViewById(R.id.stop_sound).performClick();
    verify(player).playStop();
  }
}