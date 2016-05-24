package org.darkmentat.draftrecorder.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    @ViewById(R.id.menu) ImageButton Menu;

    private Context mContext;
    private MusicComposition mMusicComposition;
    private MusicCompositionsAdapter mAdapter;

    public MusicCompositionItemView(Context context) {
      super(context);
      mContext = context;

      setLayoutParams(new CardView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    public void bind(MusicComposition musicComposition) {
      mMusicComposition = musicComposition;
      Name.setText(musicComposition.getName());
      Menu.setOnClickListener(this::showPopupMenu);
    }
    private void showPopupMenu(View v) {
      PopupMenu popupMenu = new PopupMenu(mContext, v);
      popupMenu.inflate(R.menu.menu_music_composition_item);

      popupMenu.setOnMenuItemClickListener(item -> {
        switch (item.getItemId()){
          case R.id.action_delete:
            mAdapter.deleteItem(mMusicComposition);
            return true;
          default:
            return false;
        }
      });
      popupMenu.show();
    }

    public void setOwnerAdapter(MusicCompositionsAdapter adapter) {
      mAdapter = adapter;
    }
  }

  @RootContext Context mContext;

  @Override protected MusicCompositionItemView onCreateItemView(ViewGroup parent, int viewType) {
    return MusicCompositionItemView_.build(mContext);
  }
  @Override public void onBindViewHolder(ViewWrapper<MusicCompositionItemView> viewHolder, int position) {
    final MusicCompositionItemView view = viewHolder.getView();
    final MusicComposition musicComposition = Items.get(position);

    view.setOwnerAdapter(this);
    view.bind(musicComposition);
    view.setOnClickListener(v ->
      MusicCompositionActivity_.intent(mContext)
        .extra(MusicCompositionActivity.EXTRA_COMPOSITION_NAME, musicComposition.getName())
        .start());
  }
}