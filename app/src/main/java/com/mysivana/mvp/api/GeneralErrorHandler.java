/**
 * Copyright MySivana LLC
 *
 * (C) Copyright MySivana LLC   All rights reserved.
 *
 * NOTICE:  All information contained herein or attendant hereto is,
 *          and remains, the property of MySivana LLC.  Many of the
 *          intellectual and technical concepts contained herein are
 *          proprietary to MySivana LLC. Any dissemination of this
 *          information or reproduction of this material is strictly
 *          forbidden unless prior written permission is obtained
 *          from MySivana LLC.
 *
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 * Revision History
 * ========================================================================
 * DATE             : PROGRAMMER  : DESCRIPTION
 * ========================================================================
 * JUNE 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.mvp.api;

import android.text.TextUtils;

import com.mysivana.R;
import com.mysivana.mvp.BaseMvpView;

import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
import retrofit2.adapter.rxjava.HttpException;

public class GeneralErrorHandler
        implements Consumer<Throwable> {

    private WeakReference<BaseMvpView> mViewReference;

    private onFailure onFailure;

    public GeneralErrorHandler(onFailure onFailure) {
        this.onFailure = onFailure;
        mViewReference = new WeakReference<>(onFailure.view);
    }


    @Override
    public void accept(Throwable throwable) {
        boolean isNetwork = false;
        ErrorBody errorBody = null;
        if (isNetworkError(throwable)) {
            isNetwork = true;
            showMessage(R.string.internet_check);
        } else if (throwable instanceof HttpException) {
            errorBody = ErrorBody.parseError(((HttpException) throwable).response());
            if (errorBody != null) {
                handleError(errorBody);
            }
        }
        onFailure.show(throwable, errorBody, isNetwork);
    }


    private boolean isNetworkError(Throwable throwable)

    {
        return throwable instanceof SocketException ||
                throwable instanceof UnknownHostException ||
                throwable instanceof SocketTimeoutException;
    }

    private void handleError(ErrorBody errorBody) {
        if (errorBody.code != ErrorBody.UNKNOWN_ERROR) {
            showErrorIfRequired(R.string.server_error);
        }
    }

    private void showErrorIfRequired(int strResId) {
        if (onFailure.mShowError) {
            mViewReference.get().showError(strResId);
        }
    }

    private void showErrorIfRequired(String error) {
        if (onFailure.mShowError && !TextUtils.isEmpty(error)) {
            mViewReference.get().showError(error);
        }
    }

    private void showMessage(int strResId) {
        mViewReference.get().showMessage(strResId);
    }

    private void showMessage(String error) {
        if (!error.isEmpty()) {
            mViewReference.get().showError(error);
        }
    }

    public static class onFailure {


        public BaseMvpView view = null;
        public boolean mShowError = false;

        public onFailure(BaseMvpView baseMvpView, boolean mShowError) {
            this.view = baseMvpView;
            this.mShowError = mShowError;
        }

        public GeneralErrorHandler build() {
            return new GeneralErrorHandler(this);
        }

        private void show(Throwable throwable, ErrorBody errorBody, boolean isNetwork) {
            view.showMessage(throwable.getMessage());
        }
    }

}