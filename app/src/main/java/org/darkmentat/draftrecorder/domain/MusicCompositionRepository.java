package org.darkmentat.draftrecorder.domain;


public interface MusicCompositionRepository {
  MusicComposition[] getAllMusicCompositions();
  void saveMusicComposition(MusicComposition musicComposition);
}
