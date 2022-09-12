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
package com.mysivana;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.DashboardMerchantActivity;
import com.mysivana.ui.DashboardUserActivity;
import com.mysivana.ui.WelcomeActivity;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.SharedPrefsUtils;

import static com.mysivana.ui.InviteFriendActivity.QUERY_PARAM_INVITE_CODE;
import static com.mysivana.util.AppConstants.USER_TYPE_MERCHANT;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2000L;
    String inviteCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            if (inviteCode == null || TextUtils.isEmpty(inviteCode))
                                inviteCode = deepLink.getQueryParameter(QUERY_PARAM_INVITE_CODE);
                            SharedPrefsUtils.saveReferrerCode(SplashActivity.this, inviteCode);
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR: ", "getDynamicLink:onFailure", e);
                    }
                });
        final UserResponse loggedUserObject = SharedPrefsUtils.getLoggedUserObject(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loggedUserObject != null) {
                    if (loggedUserObject.getValue().getUserType().equalsIgnoreCase(USER_TYPE_MERCHANT)) {
                        CommonUtils.startActivity(SplashActivity.this, DashboardMerchantActivity.class);
                    } else {
                        CommonUtils.startActivity(SplashActivity.this, DashboardUserActivity.class);
                    }
                } else {
                    CommonUtils.startActivity(SplashActivity.this, WelcomeActivity.class);
                }
                finish();
            }
        }, SPLASH_DURATION);
    }
}
