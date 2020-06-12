package com.formakers.fomes.common.view;

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
    public void onDestroyView() {
        compositeSubscription.clear();

        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
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
