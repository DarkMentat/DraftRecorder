package org.darkmentat.draftrecorder.domain;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.Map;

@EBean public class FakeMusicCompositionRepository implements MusicCompositionRepository {

  private Map<Integer, MusicComposition> mMusicCompositions = new HashMap<>(15);
  {
//    mMusicCompositions.put(0, new MusicComposition(0, "Test1", 120));
//    mMusicCompositions.put(1, new MusicComposition(1, "Test2", 140));
//    mMusicCompositions.put(2, new MusicComposition(2, "Test3", 180));
//    mMusicCompositions.put(3, new MusicComposition(3, "Test4 123 123 123", 60));
//    mMusicCompositions.put(4, new MusicComposition(4, "Test5", 120));
//    mMusicCompositions.put(5, new MusicComposition(5, "Test6", 80));
  }

  @Override public MusicComposition getMusicCompositionsWithName(String name) {
    return new MusicComposition(0, name);
  }

  @Override public MusicComposition[] getAllMusicCompositions() {
    return mMusicCompositions.values().toArray(new MusicComposition[mMusicCompositions.size()]);
  }

  @Override public void saveMusicComposition(MusicComposition musicComposition) {
    mMusicCompositions.put(musicComposition.getId(), musicComposition);
  }
}
