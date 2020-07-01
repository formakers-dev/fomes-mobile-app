package com.formakers.fomes.point;

import javax.inject.Inject;

@PointHistoryDagger.Scope
public class PointHistoryPresenter implements PointHistoryContract.Presenter {

    private PointHistoryContract.View view;

    @Inject
    public PointHistoryPresenter(PointHistoryContract.View view) {
        this.view = view;
    }

}
