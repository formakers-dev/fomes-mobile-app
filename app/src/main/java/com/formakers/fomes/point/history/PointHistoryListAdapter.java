package com.formakers.fomes.point.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.R;
import com.formakers.fomes.common.model.FomesPoint;
import com.formakers.fomes.common.util.DateUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PointHistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements PointHistoryListAdapterContract.View, PointHistoryListAdapterContract.Model {

    private List<FomesPoint> pointHistoryList = new ArrayList<>();

    private Context context;

    private PointHistoryContract.Presenter presenter;

    public PointHistoryListAdapter(PointHistoryContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_point_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FomesPoint item = pointHistoryList.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.descriptionTextView.setText(item.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.YYYY_DOT_MM_DOT_DD, Locale.getDefault());
        viewHolder.dateTextView.setText(dateFormat.format(item.getDate()));

        viewHolder.pointTextView.setText(String.format("%s P", NumberFormat.getInstance().format(item.getPoint())));

        viewHolder.pointTextView.setTextColor(item.getType() == FomesPoint.TYPE_SAVE ?
                context.getResources().getColor(R.color.colorPrimary) :
                context.getResources().getColor(R.color.fomes_white_alpha_87));

        if(item.getStatus() == FomesPoint.STATUS_REQUEST &&
                item.getType() == FomesPoint.TYPE_EXCHANGE) {
            viewHolder.pointStatusTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.pointStatusTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return pointHistoryList.size();
    }

    @Override
    public FomesPoint getItem(int position) {
        return pointHistoryList.get(position);
    }

    @Override
    public List<FomesPoint> getAllItems() {
        return pointHistoryList;
    }

    @Override
    public void add(FomesPoint item) {
        pointHistoryList.add(item);
    }

    @Override
    public void addAll(List<FomesPoint> items) {
        pointHistoryList.addAll(items);
    }

    @Override
    public void remove(int position) {
        pointHistoryList.remove(position);
    }

    @Override
    public void clear() {
        pointHistoryList.clear();
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void refresh(int position) {
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionTextView;
        TextView dateTextView;
        TextView pointTextView;
        TextView pointStatusTextView;

        ViewHolder(View itemView) {
            super(itemView);

            descriptionTextView = itemView.findViewById(R.id.description);
            dateTextView = itemView.findViewById(R.id.date);
            pointTextView = itemView.findViewById(R.id.point);
            pointStatusTextView = itemView.findViewById(R.id.point_status);
        }
    }
}
