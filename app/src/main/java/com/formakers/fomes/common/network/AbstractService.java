package com.formakers.fomes.common.network;

import android.util.Log;

import retrofit2.adapter.rxjava.HttpException;

abstract class AbstractService {
    protected abstract String getTag();

    public void logError(Throwable error) {
        if (error instanceof HttpException) {
            Log.e(getTag(), String.valueOf(((HttpException) error).code()));
        } else {
            Log.e(getTag(), error.getMessage());
        }
    }
}
