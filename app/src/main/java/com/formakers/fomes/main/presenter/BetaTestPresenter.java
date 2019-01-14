package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.RequestService;
import com.formakers.fomes.common.network.vo.BetaTestRequest;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.scope.BetaTestFragmentScope;
import com.formakers.fomes.model.User;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@BetaTestFragmentScope
public class BetaTestPresenter implements BetaTestContract.Presenter {

    public static String TAG = "BetaTestPresenter";

    private BetaTestListAdapterContract.Model betaTestListAdapterModel;

    private BetaTestContract.View view;
    private RequestService requestService;
    private UserDAO userDAO;
    private User user;

    @Inject
    public BetaTestPresenter(BetaTestContract.View view, RequestService requestService, UserDAO userDAO) {
        this.view = view;
        this.requestService = requestService;
        this.userDAO = userDAO;
    }

    @Override
    public void setAdapterModel(BetaTestListAdapterContract.Model adapterModel) {
        this.betaTestListAdapterModel = adapterModel;
    }

    @Override
    public void load() {
        userDAO.getUserInfo()
                .observeOn(Schedulers.io())
                .flatMap(user -> {
                    this.user = user;
                    return requestService.getFeedbackRequest();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> view.showLoading())
                .doAfterTerminate(() -> view.hideLoading())
                .subscribe(requests -> {
                    betaTestListAdapterModel.addAll(requests);
                    view.refreshBetaTestList();
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public String getSurveyURL(int position) {
        return ((BetaTestRequest) betaTestListAdapterModel.getItem(position)).getAction()
                + user.getEmail();
    }


}
