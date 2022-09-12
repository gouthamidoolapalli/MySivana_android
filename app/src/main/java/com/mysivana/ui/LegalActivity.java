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
package com.mysivana.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

public class LegalActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_legal;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_LEGAL,null);
        setSupportActionBar();
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    public void onClickLegal(View view) {
        UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        switch (view.getId()) {
            case R.id.tv_copyright:
                //log Analytics
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_COPYRIGHT,b);

                showDailog(UIUtils.getStringFromResource(LegalActivity.this, R.string.copyright), "file:///android_asset/html/copy.html");
                break;
            case R.id.tv_terms:
                //log Analytics
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_TERMS,bundle);

                showDailog(UIUtils.getStringFromResource(LegalActivity.this, R.string.terms_conditions), "file:///android_asset/html/terms.html");
                break;
            case R.id.tv_privacy:
                //log Analytics
                Bundle bun = new Bundle();
                bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_PRIVACY,bun);

                showDailog(UIUtils.getStringFromResource(LegalActivity.this, R.string.privacy_policy), "file:///android_asset/html/privacy.html");
                break;

            case R.id.tv_aml_privacy:
                //log Analytics
                Bundle _bun = new Bundle();
                _bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_PRIVACY,_bun);

                showDailog(UIUtils.getStringFromResource(LegalActivity.this, R.string.aml_privacy_policy), "file:///android_asset/html/aml.html");
                break;
        }
    }


    /**
     * Show the html files in the dialog for copyrights, terms, privacy
     *
     * @param stringFromResource title of the dialog
     * @param url                url to be loaded
     */
    private void showDailog(String stringFromResource, String url) {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_legal_section);
        ImageView mClose = dialog.findViewById(R.id.iv_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        TextView titleHeader = dialog.findViewById(R.id.header_title);
        titleHeader.setText(stringFromResource);

        WebView webView = dialog.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewController());
        webView.loadUrl(url);

        webView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                WebView.HitTestResult hr = ((WebView) v).getHitTestResult();

                Log.i("CLICK", "getExtra = " + hr.getExtra() + "\t\t Type=" + hr.getType());
                String extras = hr.getExtra();
                int type = hr.getType();
                if (extras != null && !TextUtils.isEmpty(extras) && type == 4) {
                    dialog.dismiss();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{extras});
                    i.putExtra(Intent.EXTRA_SUBJECT, "");
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {

                        UIUtils.showToast(LegalActivity.this, "There are no email clients installed.");
                    }
                }
                return false;
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
           showLoading();

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
           hideLoading();
        }
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {

    }

    @Override
    public void onDataFailure(Throwable e) {

    }
}
