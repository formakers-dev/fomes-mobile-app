package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.scope.BetaTestFragmentScope;
import com.formakers.fomes.model.User;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@BetaTestFragmentScope
public class BetaTestPresenter implements BetaTestContract.Presenter {

    public static String TAG = "BetaTestPresenter";

    private BetaTestListAdapterContract.Model betaTestListAdapterModel;

    private BetaTestContract.View view;
    private BetaTestService betaTestService;
    private UserDAO userDAO;
    private User user;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public BetaTestPresenter(BetaTestContract.View view, BetaTestService betaTestService, UserDAO userDAO) {
        this.view = view;
        this.betaTestService = betaTestService;
        this.userDAO = userDAO;
    }

    @Override
    public void setAdapterModel(BetaTestListAdapterContract.Model adapterModel) {
        this.betaTestListAdapterModel = adapterModel;
    }

    @Override
    public void load() {
        compositeSubscription.add(
                userDAO.getUserInfo()
                        .observeOn(Schedulers.io())
                        .flatMap(user -> {
                            this.user = user;
                            return betaTestService.getBetaTestList();
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(() -> view.showLoading())
                        .doAfterTerminate(() -> view.hideLoading())
                        .subscribe(betaTests -> {
                            betaTestListAdapterModel.addAll(betaTests);
                            view.refreshBetaTestList();
                        }, e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public BetaTest getBetaTestItem(int position) {
        return (BetaTest) this.betaTestListAdapterModel.getItem(position);
    }

    @Override
    public String getUserEmail() {
        return this.user.getEmail();
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }
}
