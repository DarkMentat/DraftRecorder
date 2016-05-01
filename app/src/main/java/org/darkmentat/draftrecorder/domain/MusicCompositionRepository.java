package org.darkmentat.draftrecorder.domain;


public interface MusicCompositionRepository {
  MusicComposition getMusicCompositionsWithName(String name);
  MusicComposition[] getAllMusicCompositions();
  void saveMusicComposition(MusicComposition musicComposition);
}
