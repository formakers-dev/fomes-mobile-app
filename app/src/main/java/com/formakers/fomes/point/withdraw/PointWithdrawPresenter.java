package com.formakers.fomes.point.withdraw;

import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@PointWithdrawDagger.Scope
public class PointWithdrawPresenter implements PointWithdrawContract.Presenter {
    public static final String TAG = "PointWithdrawPresenter";

    private PointWithdrawContract.View view;
    private PointService pointService;

    @Inject
    public PointWithdrawPresenter(PointWithdrawContract.View view, PointService pointService) {
        this.view = view;
        this.pointService = pointService;
    }

    @Override
    public void bindAvailablePoint() {
        this.pointService.getAvailablePoint()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(point -> {
                    view.setAvailablePoint(point);

                    int maxWithdrawCount = (int)(point / 5000l);
                    view.setMaxWithdrawCount(maxWithdrawCount);
                    view.setInputComponentsEnabled(maxWithdrawCount > 0);
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

}
