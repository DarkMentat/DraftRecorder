package org.darkmentat.draftrecorder.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;

import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.domain.Record;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static org.darkmentat.draftrecorder.ui.adapters.RecordsAdapter_.RecordItemView_;

@EBean
public class RecordsAdapter extends RecyclerViewAdapterBase<Record, RecordsAdapter.RecordItemView> {

  @EViewGroup(R.layout.item_record)
  public static class RecordItemView extends CardView {

    @ViewById(R.id.record_name) TextView Name;

    public RecordItemView(Context context) {
      super(context);

      setLayoutParams(new CardView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    public void bind(Record record) {
      Name.setText(record.getName());
    }
  }

  @RootContext Context mContext;

  @Override protected RecordItemView onCreateItemView(ViewGroup parent, int viewType) {
    return RecordItemView_.build(mContext);
  }
  @Override public void onBindViewHolder(ViewWrapper<RecordItemView> viewHolder, int position) {
    RecordItemView view = viewHolder.getView();
    Record record = Items.get(position);

    view.bind(record);
  }
}