package org.darkmentat.draftrecorder.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.darkmentat.draftrecorder.R;

import static org.darkmentat.draftrecorder.ui.activities.MusicCompositionActivity.EXTRA_NOTE_TEXT;


@EActivity(R.layout.activity_text_note)
public class TextNoteActivity extends AppCompatActivity {

  @Extra(EXTRA_NOTE_TEXT) String mNoteText = "";

  @ViewById(R.id.note) EditText mNoteEditText;
  @ViewById(R.id.save_note) Button mSaveNote;

  @AfterViews void loadNoteText(){
    mNoteEditText.setText(mNoteText);
  }

  @Click(R.id.save_note) void saveNote(){
    setResult(RESULT_OK, new Intent(){{
      putExtra(EXTRA_NOTE_TEXT, mNoteEditText.getText().toString());
    }});

    finish();
  }
}
