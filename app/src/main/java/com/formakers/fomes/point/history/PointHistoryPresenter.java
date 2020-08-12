package com.formakers.fomes.point.history;

import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PointHistoryDagger.Scope
public class PointHistoryPresenter implements PointHistoryContract.Presenter {
    public static final String TAG = "PointHistoryPresenter";

    private PointHistoryContract.View view;
    private PointService pointService;

    private PointHistoryListAdapterContract.Model adapterModel;

    @Inject
    public PointHistoryPresenter(PointHistoryContract.View view, PointService pointService) {
        this.view = view;
        this.pointService = pointService;
    }

    @Override
    public void setAdapterModel(PointHistoryListAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void bindAvailablePoint() {
        this.pointService.getAvailablePoint()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(point -> view.setAvailablePoint(point), e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void bindTotalPoint() {
        this.pointService.getAccumulatedPoint()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(point -> view.setTotalPoint(point), e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void bindHistory() {
        this.pointService.getPointHistory()
                .observeOn(Schedulers.io())
                .toSortedList((p1, p2) -> p2.getDate().compareTo(p1.getDate()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> this.view.showLoading())
                .doAfterTerminate(() -> this.view.hideLoading())
                .subscribe(points -> {
                    if (points.isEmpty()) {
                        this.view.showEmpty();
                    } else {
                        this.adapterModel.addAll(points);
                        this.view.refreshHistory();
                    }
                }, e -> Log.e(TAG, String.valueOf(e)));
    }
}
