package com.info.qrcodecamera;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tunabaranurut on 26.06.2018.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private static String TAG = ResultsAdapter.class.getSimpleName();

    public static int ROW = 0;
    public static int TITLE = 1;

    private Context context;

    private List<QrResult> items;
    private List<QrResult> itemsCopy;

    public class ResultsViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout wrapperLayout;

        public TextView nameTv;
        public TextView countTv;

        public ResultsViewHolder(View view, int viewType) {
            super(view);

            nameTv = view.findViewById(R.id.name_tv);
            countTv = view.findViewById(R.id.count_tv);
        }
    }

    public ResultsAdapter(Context context, List<QrResult> items) {
        this.context = context;
        this.items = items;

        itemsCopy = new LinkedList<>();
        this.itemsCopy.addAll(items);
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_row, parent, false);
        return new ResultsViewHolder(itemView,viewType);
    }

    @Override
    public void onBindViewHolder(final ResultsViewHolder holder, final int position) {
        final QrResult item = items.get(position);

        holder.nameTv.setText(item.getName());
        holder.countTv.setText(""+  item.getQuantity());
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void update(List<QrResult> objects){
        items.clear();
        itemsCopy.clear();
        items.addAll(objects);
        itemsCopy.addAll(objects);
        notifyDataSetChanged();
    }

    public List<QrResult> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Object getItem(int index){
        if(index >= items.size()){
            return null;
        }else{
            return items.get(index);
        }
    }

    public void removeItem(int position) {
        items.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(QrResult item, int position) {
        items.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}