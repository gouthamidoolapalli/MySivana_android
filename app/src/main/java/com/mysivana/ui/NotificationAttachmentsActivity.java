package com.mysivana.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mysivana.R;
import com.mysivana.custom.MSEditText;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.ui.adapters.NotificationsAdapter;
import com.mysivana.util.FirebaseConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAttachmentsActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_attachments;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_ATTACHMENTS_SCREEN, null);

        setSupportActionBar();

        Intent i = getIntent();
//        String notes = i.getStringExtra(NotificationsAdapter.NOTES_KEY);
        String attachmentUrl = i.getStringExtra(NotificationsAdapter.ATTACHMENT_KEY);
        String contentType = i.getStringExtra(NotificationsAdapter.CONTENT_TYPE);
        if(contentType != null && !contentType.contains("image")) {
            attachmentUrl = "https://docs.google.com/viewer?url=".concat(attachmentUrl);

        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new NotificationAttachmentsActivity.WebViewController());
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(attachmentUrl);

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
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {

    }

    @Override
    public void onDataFailure(Throwable e) {

    }


}
