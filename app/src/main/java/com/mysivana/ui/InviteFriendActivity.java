package com.mysivana.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.mysivana.R;
import com.mysivana.custom.MSButton;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteFriendActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {


    public static final String QUERY_PARAM_INVITE_CODE = "inviteCode";
    private static final String DYNAMIC_LINK_DOMAIN = "mysivana.page.link";
    private static final String TAG = InviteFriendActivity.class.getCanonicalName();

    @BindView(R.id.screen_title)
    MSTextView screenTitle;
    @BindView(R.id.tv_invite_code)
    MSTextView tvInviteCode;
    @BindView(R.id.btn_confirm_payment)
    MSButton btnConfirmPayment;
    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;
    String inviteCode;
    UserResponse userResponse;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_invite_friend;
    }

    @Override
    protected void init() {
        setSupportActionBar();
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_INVITE_FIREND, null);

        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        inviteCode = userResponse.getValue().getReferralCode();
        tvInviteCode.setText(inviteCode);


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

    public void OnClickContestWork(View v) {
        Bundle bun = new Bundle();
        bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_INVITE_RULES, bun);

        CommonUtils.startActivity(this, InviteRulesActivity.class);
    }

    public void OnClickShareInvite(View v) {
        Bundle bun = new Bundle();
        bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        bun.putString(FirebaseConstants.INVITE_CODE, userResponse.getValue().getReferralCode());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_INVITE_FRIENDS, bun);

        Uri myUri = UIUtils.createShareUri(this, inviteCode);
        Uri dynamicLinkUri = UIUtils.createDynamicUri(myUri);
        UIUtils.shortenLink(this, dynamicLinkUri);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
