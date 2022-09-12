package com.mysivana.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyBitcoinsActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {


    @BindView(R.id.web_view)
    WebView webView;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_buy_bitcoins;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_BUY_BITCOINS, null);
        setSupportActionBar();
        String url = "https://localbitcoins.com/";
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
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{extras});
                    i.putExtra(Intent.EXTRA_SUBJECT, "");
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (ActivityNotFoundException ex) {

                        UIUtils.showToast(BuyBitcoinsActivity.this, "There are no email clients installed.");
                    }
                }
                return false;
            }
        });
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
