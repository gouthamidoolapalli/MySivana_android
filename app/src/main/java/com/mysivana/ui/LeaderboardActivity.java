package com.mysivana.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSButton;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.ReferralBoardResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.adapters.LeaderBoardAdapter;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderboardActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {


    private static final String TAG = LeaderboardActivity.class.getCanonicalName();


    LinearLayoutManager mLayoutManager;
    int myPosition = -1;
    @BindView(R.id.screen_title)
    MSTextView screenTitle;
    @BindView(R.id.winner_rcv)
    RecyclerView winnerRcv;
    @BindView(R.id.winner_rank_layout)
    FrameLayout winnerRankLayout;
    @BindView(R.id.winner_rank_3)
    MSTextView winnerRank3;
    @BindView(R.id.winner_rank)
    MSTextView winnerRank;
    @BindView(R.id.winner_img)
    ImageView winnerImg;
    @BindView(R.id.winner_name_tv)
    MSTextView winnerNameTv;
    @BindView(R.id.winner_points)
    MSTextView winnerPoints;
    @BindView(R.id.my_position_view)
    RelativeLayout myPositionView;
    @BindView(R.id.btn_refer_friends)
    MSButton btnReferFriends;
    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;
    @BindView(R.id.txt_contest_rules)
    MSTextView contestRulesTxt;
    @BindView(R.id.participants_count)
    MSTextView participantsCount;


    UserResponse userResponse;
    LeaderBoardAdapter adapter;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_leaderboard;
    }

    @Override
    protected void init() {
        setSupportActionBar();
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_LEADER_BOARD, null);

        userResponse = SharedPrefsUtils.getLoggedUserObject(this);


        mLayoutManager = new LinearLayoutManager(this);
        winnerRcv.setLayoutManager(mLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(winnerRcv.getContext(),
                mLayoutManager.getOrientation());
        winnerRcv.addItemDecoration(mDividerItemDecoration);
        adapter = new LeaderBoardAdapter(this);
        winnerRcv.setAdapter(adapter);
        winnerRcv.addOnScrollListener(mScrollListener);


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Bundle bun = new Bundle();
                bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_INVITE_RULES, bun);

                CommonUtils.startActivity(LeaderboardActivity.this, InviteRulesActivity.class);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#0000FF"));
                ds.setUnderlineText(true);
            }
        };
        String thisLink = getString(R.string.click_here);
        String yourString = getString(R.string.know_contest_rules, thisLink);
        int thisLinkIndex = yourString.indexOf(thisLink);
        SpannableString spannableString = new SpannableString(yourString);
        spannableString.setSpan(clickableSpan,
                thisLinkIndex, thisLinkIndex + thisLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        contestRulesTxt.setText(spannableString, TextView.BufferType.SPANNABLE);
        contestRulesTxt.setMovementMethod(LinkMovementMethod.getInstance());

        mPresenter.getReferralBoardList(userResponse.getValue().getUserId(), userResponse.getValue().getUserApiKey());


    }

    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition() + 1;

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            if (dy > 0) {
                // Scrolled from Downwards
                if ((myPosition <= lastVisibleItemPosition || totalItemCount <= visibleItemCount) && myPosition != -1) {
                    myPositionView.setVisibility(View.GONE);
                    lp.bottomMargin = 0;
                    winnerRcv.setLayoutParams(lp);
                } else {
                    myPositionView.setVisibility(View.VISIBLE);
                    int height = myPositionView.getMeasuredHeight();
                    lp.bottomMargin = height;
                    winnerRcv.setLayoutParams(lp);
                }

            } else if (dy < 0) {
                // Scrolled from Upwards

                if ((lastVisibleItemPosition <= myPosition || totalItemCount < visibleItemCount) || myPosition == -1) {
                    myPositionView.setVisibility(View.VISIBLE);
                    int height = myPositionView.getMeasuredHeight();
                    lp.bottomMargin = height;
                    winnerRcv.setLayoutParams(lp);
                } else {
                    myPositionView.setVisibility(View.GONE);
                    lp.bottomMargin = 0;
                    winnerRcv.setLayoutParams(lp);
                }

            } else {
                // No Vertical Scrolled

                if (myPosition <= lastVisibleItemPosition && myPosition != -1) {
                    myPositionView.setVisibility(View.GONE);
                    lp.bottomMargin = 0;
                    winnerRcv.setLayoutParams(lp);
                } else {
                    myPositionView.setVisibility(View.VISIBLE);
                    int height = myPositionView.getMeasuredHeight();
                    lp.bottomMargin = height;
                    winnerRcv.setLayoutParams(lp);
                }

            }
        }
    };

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        ReferralBoardResponse referralBoardResponse = (ReferralBoardResponse) object;
        int code = referralBoardResponse.getHttpStatusCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                if(referralBoardResponse.getValue()==null)
                    return;
                int totalUsers = referralBoardResponse.getValue().getTotalUsers();
                participantsCount.setText("" + totalUsers);
                adapter.setMyLeaderBoardList(referralBoardResponse.getValue().getUserScores());
                ReferralBoardResponse.UserScore userScore = referralBoardResponse.getValue().getMyScore();
                if (userScore.getMysivanaScore() > 0) {
                    myPosition = userScore.getUserRank();
                    winnerPoints.setText(userScore.getMysivanaScore() + "");
                } else {
                    myPosition = -1;
                    winnerPoints.setText("0");
                }

                adapter.setMyRank(myPosition);

                if (myPosition == -1)
                    myPositionView.setVisibility(View.VISIBLE);
                else {
                    int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition() + 1;
                    if (myPosition <= lastVisibleItemPosition) {
                        myPositionView.setVisibility(View.GONE);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        int height = 0;
                        lp.bottomMargin = height;
                        winnerRcv.setLayoutParams(lp);
                    } else {
                        myPositionView.setVisibility(View.VISIBLE);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        int height = myPositionView.getMeasuredHeight();
                        lp.bottomMargin = height;
                        winnerRcv.setLayoutParams(lp);
                    }
                }
                if (userScore.getFullName() != null && !TextUtils.isEmpty(userScore.getFullName())) {
                    winnerNameTv.setText(userScore.getFullName());
                } else {
                    winnerNameTv.setText("");
                }

                if (userScore.getProfileUrl() != null && !TextUtils.isEmpty(userScore.getProfileUrl())) {
                    Picasso.with(LeaderboardActivity.this).load(userScore.getProfileUrl()).resize(80, 80)
                            .placeholder(R.drawable.ic_user_avatar).into(winnerImg);
                } else {
                    winnerImg.setImageResource(R.drawable.ic_user_avatar);
                }
                if (myPosition <= 2 && myPosition > -1) {
                    winnerRankLayout.setVisibility(View.VISIBLE);
                    winnerRank.setVisibility(View.INVISIBLE);
                } else {
                    winnerRankLayout.setVisibility(View.INVISIBLE);
                    winnerRank.setVisibility(View.VISIBLE);
                }
                if (myPosition > -1) {
                    winnerRank3.setText((myPosition) + "");
                    winnerRank.setText((myPosition) + "");
                } else {
                    winnerRank3.setText("--");
                    winnerRank.setText("--");
                }
                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                if (!TextUtils.isEmpty(referralBoardResponse.getErrorDescription()))
                    UIUtils.showToast(this, referralBoardResponse.getErrorDescription());
                break;
        }
    }

    @Override
    public void onDataFailure(Throwable e) {
        if (e instanceof HttpException) {
            //HTTP exceptions of non 200 type.
            int code = ((HttpException) e).code();
            if (code == 500) {
                if (errorCounter >= 5) {
                    submitTicket = new SubmitTicket(this, code + ", " + ((HttpException) e).response().toString());
                    submitTicket.execute(0);
                    errorCounter = 0;
                } else {
                    showMessage(getString(R.string.internal_server_error));
                    errorCounter++;
                }

            } else if (code == 401) {
                //Bad credentials error
                logOutFromApp();
            }
        } else if (e instanceof SocketTimeoutException) {
            //connection establishment failure exception (might have slow internet or server unavailable)
            showMessage(getString(R.string.try_again));
        } else if (e instanceof IOException) {
            //called when there's network problem
            showMessage(getString(R.string.internet_check));
        } else {
            //other messages
            showMessage(e.getMessage());
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    public void OnClickShareInvite(View v) {
        Bundle bun = new Bundle();
        bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        bun.putString(FirebaseConstants.INVITE_CODE, userResponse.getValue().getReferralCode());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_INVITE_FRIENDS, bun);

        Uri myUri = UIUtils.createShareUri(this, SharedPrefsUtils.getLoggedUserObject(this).getValue().getReferralCode());
        Uri dynamicLinkUri = UIUtils.createDynamicUri(myUri);
        UIUtils.shortenLink(this, dynamicLinkUri);
    }
}
