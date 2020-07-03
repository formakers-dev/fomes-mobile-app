package com.formakers.fomes.common.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.formakers.fomes.common.util.Log;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseFragment extends Fragment {

    protected Context context;
    Unbinder unbinder;
    final CompositeSubscription compositeSubscription = new CompositeSubscription();

    protected void addCompositeSubscription(@NonNull final Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        compositeSubscription.clear();

        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    protected boolean isNotAvailableWidget() {
        return context == null
                || !this.isVisible()    // isAdded로 할지 고민이 되는군...
                || this.isDetached()
                || this.isRemoving();
    }

    public void logError(Throwable error) {
        if (error instanceof HttpException) {
            Log.e(getTag(), String.valueOf(((HttpException) error).code()));
        } else {
            Log.e(getTag(), error.getMessage());
        }
    }

    protected Animation getFadeInAnimation(long durationMills) {
        Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(durationMills);
        return in;
    }
}
