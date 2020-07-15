package com.formakers.fomes.point.withdraw;

import com.formakers.fomes.common.model.FomesPoint;
import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@PointWithdrawDagger.Scope
public class PointWithdrawPresenter implements PointWithdrawContract.Presenter {
    public static final String TAG = "PointWithdrawPresenter";

    private static final long BASE_WITHDRAW_POINT = 5000L;

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

                    int maxWithdrawCount = (int)(point / BASE_WITHDRAW_POINT);
                    view.setMaxWithdrawCount(maxWithdrawCount);
                    view.setInputComponentsEnabled(maxWithdrawCount > 0);
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void withdraw(int withdrawCount, String phoneNumber) {
        FomesPoint fomesPoint = new FomesPoint().setPoint(BASE_WITHDRAW_POINT * withdrawCount)
                .setDescription(BASE_WITHDRAW_POINT + "원권 " + withdrawCount + "장 교환")
                .setPhoneNumber(phoneNumber);

        this.pointService.requestWithdraw(fomesPoint)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    this.view.showToast("교환 신청이 완료되었습니다!");
                    this.view.finish();
                }, e -> {
                    Log.e(TAG, String.valueOf(e));
                    this.view.showToast("교환 신청이 실패하였습니다. 다시 시도해주세요 🙏");
                });
    }

}
