package org.darkmentat.draftrecorder.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;

import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.MusicComposition;
import org.darkmentat.draftrecorder.ui.activities.MusicCompositionActivity;
import org.darkmentat.draftrecorder.ui.activities.MusicCompositionActivity_;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static org.darkmentat.draftrecorder.ui.adapters.MusicCompositionsAdapter_.MusicCompositionItemView_;

@EBean
public class MusicCompositionsAdapter extends RecyclerViewAdapterBase<MusicComposition, MusicCompositionsAdapter.MusicCompositionItemView> {

  @EViewGroup(R.layout.item_music_composition)
  public static class MusicCompositionItemView extends CardView {

    @ViewById(R.id.music_composition_name) TextView Name;

    public MusicCompositionItemView(Context context) {
      super(context);

      setLayoutParams(new CardView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    public void bind(MusicComposition musicComposition) {
      Name.setText(musicComposition.getName());
    }
  }

  @RootContext Context mContext;

  @Override protected MusicCompositionItemView onCreateItemView(ViewGroup parent, int viewType) {
    return MusicCompositionItemView_.build(mContext);
  }
  @Override public void onBindViewHolder(ViewWrapper<MusicCompositionItemView> viewHolder, int position) {
    final MusicCompositionItemView view = viewHolder.getView();
    final MusicComposition musicComposition = Items.get(position);

    view.bind(musicComposition);
    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        MusicCompositionActivity_.intent(mContext)
            .extra(MusicCompositionActivity.EXTRA_COMPOSITION_NAME, musicComposition.getName())
            .start();
      }
    });
  }
}