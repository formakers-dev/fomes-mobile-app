package com.formakers.fomes.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.formakers.fomes.common.network.vo.BetaTestRequest;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;

import java.util.ArrayList;
import java.util.List;

public class BetaTestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BetaTestListAdapterContract.Model, BetaTestListAdapterContract.View {

    private static final String TAG = "BetaTestListAdapter";

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
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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
}
