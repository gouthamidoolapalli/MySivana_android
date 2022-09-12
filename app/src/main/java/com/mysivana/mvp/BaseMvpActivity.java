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
package com.mysivana.mvp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.ui.DashboardUserActivity;
import com.mysivana.ui.LoginActivity;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.RaiseTicket;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

public abstract class BaseMvpActivity<T extends BaseMvpPresenter> extends AppCompatActivity implements BaseMvpView {

    private Dialog mProgressDialog;
    protected T mPresenter;
    public static int errorCounter = 0;
    public SubmitTicket submitTicket;
    public FirebaseAnalytics mFirebaseAnalytics;
    public static final int REQUEST_GPS_SETTINGS = 233;

    @Nullable
    @BindView(R.id.toolbar)
    public Toolbar mToolBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.onActivityCreateSetTheme(this);
        setContentView(setContentLayoutId());
        mPresenter = createPresenter();
        mPresenter.attachView(this);
        ButterKnife.bind(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setTitle("");
        }
        init();
    }


    public FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    /**
     * For ActionBar with back button enabled
     */

    public void setSupportActionBar() {
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_toolbar_arrow);
        }
    }

    /**
     * Used to prompt GPS settings dialog
     */
    public void promptGPSDialog() {
        UIUtils.showCustomDialog(this, UIUtils.getStringFromResource(this, R.string.location_service), UIUtils.getStringFromResource(this, R.string.gps_not_enabled),
                UIUtils.getStringFromResource(this, R.string.go_to_location_settings), UIUtils.getStringFromResource(this, R.string.cancel), positiveCallback,
                negativeCallback);
    }

    /**
     * positive callback for GPS Dialog
     */
    public UIUtils.DialogButtonClickListener positiveCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(myIntent, REQUEST_GPS_SETTINGS);
        }
    };
    /**
     * negative callback for GPS Dialog
     */
    UIUtils.DialogButtonClickListener negativeCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            UIUtils.showToast(BaseMvpActivity.this, UIUtils.getStringFromResource(BaseMvpActivity.this, R.string.gps_not_enabled_error));


        }
    };

    protected abstract int setContentLayoutId();

    protected abstract void init();

    protected abstract T createPresenter();

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    public void showError(String error) {
        showSomethingWrongDialog(error);
    }

    public void showSomethingWrongDialog(String error) {

        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Light_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_no_internet);
        dialog.setCancelable(false);

        TextView wrongText = dialog.findViewById(R.id.txt_no_internet1);
        if (error != null && !TextUtils.isEmpty(error)) {
            wrongText.setText(error);
        } else {
            wrongText.setText(getString(R.string.try_again));
        }

        Button ok = dialog.findViewById(R.id.btn_retry);
        ok.setText("OK");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(getContext() instanceof DashboardUserActivity)) {
                    onBackPressed();
                }
                dialog.dismiss();
            }
        });


        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        if (!((Activity) getContext()).isFinishing()) {
            dialog.show();
        }
    }


    @Override
    public void showError(int stringResId) {
        showSomethingWrongDialog("");
    }

    @Override
    public void showMessage(int srtResId) {
        showSomethingWrongDialog(getString(srtResId));
    }

    @Override
    public void showMessage(final String message) {
        showSomethingWrongDialog(message);
    }

    public void logOutFromApp() {
        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Light_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_no_internet);
        dialog.setCancelable(false);

        TextView wrongText = dialog.findViewById(R.id.txt_no_internet1);

        wrongText.setText(getString(R.string.log_out));
        Button ok = dialog.findViewById(R.id.btn_retry);
        ok.setText("OK");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Activity activity = (Activity) getContext();
                SharedPrefsUtils.clear(activity);
                CommonUtils.startActivity(activity, LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
                activity.finish();

            }
        });


        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        if (!((Activity) getContext()).isFinishing()) {
            dialog.show();
        }
    }

    /**
     * load progress bar
     */
    @Override
    public void showLoading() {
        hideLoading();
        if (mProgressDialog == null)
            mProgressDialog = CommonUtils.showLoadingDialog(this.getContext());
        mProgressDialog.show();
    }

    /**
     * hide progress bar
     */
    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onUnSubscribe();
        }
        if (submitTicket != null) {
            submitTicket.cancel(true);
            submitTicket = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SubmitTicket extends AsyncTask {
        Context context;
        String errorDescription;

        public SubmitTicket(Context context, String errorDescription) {
            this.context = context;
            this.errorDescription = errorDescription;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if (SharedPrefsUtils.getLoggedUserObject(context) != null) {
                RaiseTicket.submitTicket(errorDescription, SharedPrefsUtils.getLoggedUserObject(context).getValue().getEmail());
            } else {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideLoading();
        }
    }

    public void addClickHereInString(Context context, int resID, ClickableSpan clickableSpan, MSTextView textView) {
        String thisLink = context.getString(R.string.click_here);
        String yourString = context.getString(resID, thisLink);
        int thisLinkIndex = yourString.indexOf(thisLink);
        SpannableString spannableString = new SpannableString(yourString);
        spannableString.setSpan(clickableSpan,
                thisLinkIndex, thisLinkIndex + thisLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
