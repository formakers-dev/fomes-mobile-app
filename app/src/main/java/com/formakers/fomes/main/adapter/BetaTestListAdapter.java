package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.BetaTestRequest;
import com.formakers.fomes.common.view.adapter.listener.OnRecyclerItemClickListener;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;

import java.util.ArrayList;
import java.util.List;

public class BetaTestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BetaTestListAdapterContract.Model, BetaTestListAdapterContract.View {

    private static final String TAG = "BetaTestListAdapter";

    private Context context;
    private OnRecyclerItemClickListener itemClickListener;

    private List<BetaTestRequest> betaTestRequests = new ArrayList<>();

    private BetaTestContract.Presenter presenter;

    /**
     * View
     */

    @Override
    public void setPresenter(BetaTestContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BetaTestRequest item = betaTestRequests.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tempTextView.setText(String.valueOf(item));
        viewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));
    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * Model
     */

    @Override
    public int getItemCount() {
        return betaTestRequests.size();
    }

    @Override
    public Object getItem(int position) {
        return betaTestRequests.get(position);
    }

    @Override
    public List<BetaTestRequest> getAllItems() {
        return betaTestRequests;
    }

    @Override
    public void add(BetaTestRequest item) {
        betaTestRequests.add(item);
    }

    @Override
    public void addAll(List<BetaTestRequest> items) {
        betaTestRequests.addAll(items);
    }

    @Override
    public void clear() {
        betaTestRequests.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tempTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            tempTextView = itemView.findViewById(R.id.betatest_temp_textview);
        }
    }
}
