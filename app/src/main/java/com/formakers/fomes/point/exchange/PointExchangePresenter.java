package com.formakers.fomes.point.exchange;

import com.formakers.fomes.common.model.FomesPoint;
import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.util.StringFormatUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@PointExchangeDagger.Scope
public class PointExchangePresenter implements PointExchangeContract.Presenter {
    public static final String TAG = "PointExchangePresenter";

    private static final long BASE_EXCHANGE_POINT = 5000L;

    private PointExchangeContract.View view;
    private PointService pointService;
    private int maxExchangeCount = 0;

    @Inject
    public PointExchangePresenter(PointExchangeContract.View view, PointService pointService) {
        this.view = view;
        this.pointService = pointService;
    }

    @Override
    public void bindAvailablePoint() {
        this.pointService.getAvailablePoint()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(point -> {
                    view.setAvailablePoint(point);

                    maxExchangeCount = (int)(point / BASE_EXCHANGE_POINT);
                    view.setMaxExchangeCount(maxExchangeCount);
                    view.setInputComponentsEnabled(maxExchangeCount > 0);
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public boolean isAvailableToExchange(int currentExchangeCount, String phoneNumber) {
        boolean isMatched = StringFormatUtil.verifyPhoneNumberFormat(phoneNumber);

        return isMatched && currentExchangeCount > 0 && currentExchangeCount <= maxExchangeCount;
    }

    @Override
    public void exchange(int exchangeCount, String phoneNumber) {
        FomesPoint fomesPoint = new FomesPoint().setPoint(BASE_EXCHANGE_POINT * exchangeCount)
                .setDescription("문화상품권 " + BASE_EXCHANGE_POINT + "원권 " + exchangeCount + "장 교환")
                .setPhoneNumber(phoneNumber);

        this.pointService.requestExchange(fomesPoint)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    this.view.showToast("교환 신청이 완료되었습니다!");
                    this.view.successfullyFinish();
                }, e -> {
                    Log.e(TAG, String.valueOf(e));
                    this.view.showToast("교환 신청이 실패하였습니다. 다시 시도해주세요 🙏");
                });
    }

}
