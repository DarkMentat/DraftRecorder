package org.darkmentat.draftrecorder.domain;

import android.content.Context;

import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@EBean public class JsonFileMusicCompositionRepository implements MusicCompositionRepository {

  @RootContext Context mContext;

  private Gson mGson = new Gson();

  @Override public MusicComposition getMusicCompositionsWithName(String name) {

    MusicComposition res = null;

    File home = mContext.getExternalFilesDir(null);
    File file = new File(home, name);

    if(file.exists() && file.isDirectory()){

      File compositionJson = new File(file, "composition.json");
      if(compositionJson.exists()){
        try{

          Reader reader = new FileReader(compositionJson);

          res = mGson.fromJson(reader, MusicComposition.class);

          reader.close();
        }catch(IOException e){
          e.printStackTrace();
        }
      }
    }

    return res;
  }
  @Override public MusicComposition[] getAllMusicCompositions() {

    List<MusicComposition> musicCompositions = new ArrayList<>();

    try{
      File home = mContext.getExternalFilesDir(null);

      File[] files = home.listFiles();

      for(File file : files){

        if(file.isDirectory()){

          File compositionJson = new File(file, "composition.json");
          if(compositionJson.exists()){
            Reader reader = new FileReader(compositionJson);

            musicCompositions.add(mGson.fromJson(reader, MusicComposition.class));

            reader.close();
          }
        }
      }
    }catch(IOException e){
      e.printStackTrace();
    }

    return musicCompositions.toArray(new MusicComposition[musicCompositions.size()]);
  }
  @Override public void saveMusicComposition(MusicComposition musicComposition) {

    try{
      File home = mContext.getExternalFilesDir(null);
      File dir = new File(home, musicComposition.getName());
      dir.mkdir();

      File compositionJson = new File(dir, "composition.json");

      Writer writer = new FileWriter(compositionJson, false);

      mGson.toJson(musicComposition, MusicComposition.class, writer);

      writer.flush();
      writer.close();

    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
