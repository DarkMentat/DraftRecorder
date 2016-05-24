package org.darkmentat.draftrecorder.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RecyclerViewAdapterBase<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>> {

  protected List<T> Items = new ArrayList<>();

  @Override
  public int getItemCount() {
    return Items.size();
  }

  @Override
  public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewWrapper<>(onCreateItemView(parent, viewType));
  }

  protected abstract V onCreateItemView(ViewGroup parent, int viewType);

  public void setItems(T[] items){
    Items.clear();
    Collections.addAll(Items, items);

    notifyDataSetChanged();
  }
  public void addItems(T[] items){
    int indexFirstNewElement = Items.size();

    Collections.addAll(Items, items);

    notifyItemRangeInserted(indexFirstNewElement, items.length);
  }
  public void deleteItem(T item){
    int index = Items.indexOf(item);
    Items.remove(item);
    notifyItemRemoved(index);
  }
}