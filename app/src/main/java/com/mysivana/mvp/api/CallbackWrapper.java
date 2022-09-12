/**
 * Copyright MySivana LLC
 * <p>
 * (C) Copyright MySivana LLC   All rights reserved.
 * <p>
 * NOTICE:  All information contained herein or attendant hereto is,
 * and remains, the property of MySivana LLC.  Many of the
 * intellectual and technical concepts contained herein are
 * proprietary to MySivana LLC. Any dissemination of this
 * information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained
 * from MySivana LLC.
 * <p>
 * ------------------------------------------------------------------------
 * <p>
 * ========================================================================
 * Revision History
 * ========================================================================
 * DATE             : PROGRAMMER  : DESCRIPTION
 * ========================================================================
 * JUNE 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 * <p>
 * ========================================================================
 */
package com.mysivana.mvp.api;

import com.mysivana.mvp.BaseMvpView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

public abstract class CallbackWrapper<T> extends DisposableObserver<T> {
    //BaseMvpView is just a reference of a View in MVP
    private WeakReference<BaseMvpView> weakReference;

    public CallbackWrapper(BaseMvpView view) {
        this.weakReference = new WeakReference<>(view);
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(Throwable e);

    @Override
    public void onNext(T t) {
        BaseMvpView view = weakReference.get();
        if (view != null)
            view.hideLoading();
        onSuccess(t);
    }


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        BaseMvpView view = weakReference.get();
        onFailure(e);
        if (view != null)
            view.hideLoading();
    }

    @Override
    public void onComplete() {
        BaseMvpView view = weakReference.get();
    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}