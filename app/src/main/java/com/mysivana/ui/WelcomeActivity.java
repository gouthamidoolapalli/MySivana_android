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
package com.mysivana.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mysivana.R;
import com.mysivana.custom.MSCircleIndicator;
import com.mysivana.ui.adapters.WelcomePagerAdapter;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.vp_welcome)
    ViewPager mViewPager;
    @BindView(R.id.btn_prev)
    TextView mPrevious;
    @BindView(R.id.btn_next)
    TextView mNext;
    private FirebaseAnalytics mFirebaseAnalytics;
    private WelcomePagerAdapter pagerAdapter;
    private int currentPage;
    private int totalPages = 3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_TOUR,null);
        init();
    }

    private void init() {
        pagerAdapter = new WelcomePagerAdapter(WelcomeActivity.this, null);
        mViewPager.setAdapter(pagerAdapter);
        MSCircleIndicator indicator = findViewById(R.id.ci_welcome);
        indicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPage = position;
            if (currentPage == 0) {
                mPrevious.setAlpha(0.5f);
                mNext.setAlpha(1f);
            } else if (currentPage == totalPages - 1) {
                mNext.setAlpha(0.5f);
                mPrevious.setAlpha(1f);
            } else {
                mPrevious.setAlpha(1f);
                mNext.setAlpha(1f);
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void onClickOption(View v) {
        switch (v.getId()) {
            case R.id.btn_prev:
                if (currentPage != 0) {
                    mViewPager.setCurrentItem(currentPage - 1, true);
                    mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_PREV_TOUR,new Bundle());
                }
                break;
            case R.id.btn_skip:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SKIP_TOUR,new Bundle());
                CommonUtils.startActivity(WelcomeActivity.this, LoginActivity.class);
                finish();
                break;
            case R.id.btn_next:
                if (currentPage != totalPages - 1) {
                    mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_NEXT_TOUR,new Bundle());
                    mViewPager.setCurrentItem(currentPage + 1, true);
                }
                break;
        }
    }
}
