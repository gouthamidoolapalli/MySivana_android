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

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mysivana.BuildConfig;
import com.mysivana.R;
import com.mysivana.custom.CircleImageView;
import com.mysivana.custom.EditTextDebounce;
import com.mysivana.custom.MSEditText;
import com.mysivana.custom.ProgressWheel;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.api.APISettings;
import com.mysivana.mvp.model.BankDetails;
import com.mysivana.mvp.model.BankNameResponse;
import com.mysivana.mvp.model.CryptoMenuResponse;
import com.mysivana.mvp.model.CryptoResponse;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.ReceivePaymentRequest;
import com.mysivana.mvp.model.ReceivePaymentResponse;
import com.mysivana.mvp.model.StartPaymentRequest;
import com.mysivana.mvp.model.StartPaymentResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.post.PostCryptoValue;
import com.mysivana.mvp.model.post.PostUserLogin;
import com.mysivana.ui.adapters.CryptoMenuAdapter;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.DeviceInfo;
import com.mysivana.util.FileHelper;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.GetProfileImage;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.OnCryptoMenuClickListener;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.SingleShotLocationProvider;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

import static com.mysivana.util.AppConstants.DEPOSIT_BANK;
import static com.mysivana.util.AppConstants.DEPOSIT_CASH;
import static com.mysivana.util.AppConstants.INTENT_ACTION_NEW_NOTIFICATION;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_COUNTRY_CODE;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_EMAIL;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_FULL_NAME;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_PHONE_NUMBER;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_AMOUNT;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_DEPOSIT_TYPE;
import static com.mysivana.util.AppConstants.Notifications.TRANSACTION_REQUEST_ID;
import static com.mysivana.util.FirebaseConstants.USR_TYPE_USER;

public class DashboardUserActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    private static final int API_CRYPTO_REQUEST = 10;
    private static final int API_START_PAYMENT = 11;
    private static final int API_RECEIVE_PAYMENT = 12;
    private static final int API_COMPLETE_PAYMENT = 13;
    private static final int API_CRYPTO_MENU_REQUEST = 14;
    private static final int VERIFY_EMAIL_REQUEST = 302;
    private static final int GET_PROFILE_REQUEST = 301;
    private static final int SAVE_BANK_DETAILS = 22;
    private static final int GET_BANK_NAME = 23;
    private static final int REQUEST_PAYMENT_COMPLETED = 122;
    private static final int REQUEST_PROFILE_PICTURE = 123;
    private static final int REQUEST_FCM_REGISTRATION = 124;
    public static final String DASHBOARD_PAYMENT_COMPLETED = "DashboardPaymentCompleted";


    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.tv_nav_profile)
    TextView mNavProfileName;
    @BindView(R.id.tv_merchant_listing)
    TextView mNavMerchantListing;
    @BindView(R.id.tv_transaction_lookup)
    TextView mNavTransactionLookup;
    @BindView(R.id.tv_business_name)
    TextView mBusinessName;
    @BindView(R.id.et_rupee_value)
    EditText mRupeeValue;
    @BindView(R.id.et_fullname)
    EditText mFullName;
    @BindView(R.id.et_phone)
    EditText mPhone;
    @BindView(R.id.et_email)
    EditText mEmail;
    @BindView(R.id.tv_btc_value)
    TextView mBTCValue;
    @BindView(R.id.buy_here)
    TextView buyHere;
    @BindView(R.id.btc_address_layout)
    CardView mBitcoinAddressLayout;
    @BindView(R.id.dashboard_main)
    CardView mDashboardMain;
    @BindView(R.id.bank_details_layout)
    CardView mBankDetailsLayout;
    @BindView(R.id.phone_email_layout)
    LinearLayout mPhoneEmailLayout;
    @BindView(R.id.iv_btc_qr)
    ImageView mBitcoinQR;
    @BindView(R.id.tv_btc_address)
    TextView mBitcoinAddress;
    @BindView(R.id.pw_timer)
    ProgressWheel mTimerView;
    @BindView(R.id.tv_payment_confirm)
    TextView mTVConfirmPayment;
    @BindView(R.id.btn_confirm_payment)
    Button mConfirmPayment;
    @BindView(R.id.btn_payment_completed)
    Button mPaymentComplete;
    @BindView(R.id.btn_save_bank_details)
    Button mSaveBankDetails;
    @BindView(R.id.profile_img)
    CircleImageView mProfileImg;
    @BindView(R.id.tv_version_name)
    TextView mVersionName;
    @BindView(R.id.balance_layout)
    LinearLayout mBalanceLayout;

    //Bank Details View
    @BindView(R.id.et_bank_name)
    MSEditText mEtBankName;
    @BindView(R.id.et_bank_branch)
    MSEditText mETBankBranch;
    @BindView(R.id.et_acc_num)
    MSEditText mEtAccNum;
    @BindView(R.id.et_ifsc_code)
    MSEditText mEtIfscCode;
    //    @BindView(R.id.et_account_type)
//    MSEditText mEtAccountType;
    @BindView(R.id.et_payee_name)
    MSEditText mEtPayeeName;
    @BindView(R.id.iv_deposit)
    ImageView mIvDeposit;
    @BindView(R.id.ll_deposit)
    LinearLayout mllDeposit;
    @BindView(R.id.tv_siva_tokens)
    TextView mSivaTokensTV;

    //CrytoMenu
    @BindView(R.id.reveal_layout)
    LinearLayout revealLayout;
    @BindView(R.id.rv_crypto_menu)
    RecyclerView mRvCryptoMenu;

    TextView notificationCountTV;

    private CryptoMenuAdapter cryptoMenuAdapter;
    private BankNameResponse bankNameResponse;

    private Dialog dialog;
    private CryptoResponse cryptoResponse;
    private UserResponse userResponse;
    private StartPaymentResponse paymentResponse;
    private ReceivePaymentResponse receivePaymentResponse;
    private CountDownTimer paymentTimer;

    private String enteredRupee;
    private String enteredEmail;
    private String enteredPhone;
    private String enteredName;
    private int selectedMenuId = -1;
    private Handler profileImageHandler;
    private StartPaymentRequest startPaymentRequest;
    private boolean isTimerRunning;
    private boolean shouldLoadNewImage = true;
    private String mIMEINumber;
    private String receiverId = "0";
    private String bankDetailsAvailable;
    private String receiverBankId;
    private String fcmToken;
    private String theme;
    private String requestedAmount, receiverEmail, receiverName, receiverPhone, receiverCountryCode, transactionRequestId, requestedPaymentType;
    int repeatCounter = 0;


    /**
     * request for read phone permission
     */
    public void requestReadPhonePermissions() {
        PermissionUtil.checkPermission(DashboardUserActivity.this, Manifest.permission.READ_PHONE_STATE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onResult(boolean success) {
                if (success) {
                    mIMEINumber = DeviceInfo.getIMEI(DashboardUserActivity.this);
                    fcmToken = SharedPrefsUtils.getFCMToken(DashboardUserActivity.this);
                    String registerToken = FirebaseInstanceId.getInstance().getToken();
                    System.out.println("registered token is::::" + registerToken);
                    if (fcmToken == null || fcmToken.isEmpty() || (fcmToken != null && !fcmToken.equals(registerToken))) {
                        registerFCMTokenToServer();
                    }
                }
            }
        });
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_dashboard;
    }

    @Override
    protected void init() {
        revealLayout.setVisibility(View.INVISIBLE);
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_DASHBOARD_USER, null);
        mFirebaseAnalytics.setUserProperty(USR_TYPE_USER, SharedPrefsUtils.getLoggedUserObject(this).getValue().getUserType());
        mRvCryptoMenu.setLayoutManager(new LinearLayoutManager(this));
        mRvCryptoMenu.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        cryptoMenuAdapter = new CryptoMenuAdapter(onCryptoMenuClickListener);
        mRvCryptoMenu.setAdapter(cryptoMenuAdapter);
        setSupportActionBar();


        theme = SharedPrefsUtils.getStringPreference(DashboardUserActivity.this, AppConstants.BundleParams.THEME);
        if (TextUtils.isEmpty(theme)) {
            theme = AppConstants.CryptoCodes.BTC;
            SharedPrefsUtils.setStringPreference(DashboardUserActivity.this, AppConstants.BundleParams.THEME, theme);

        }

        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        requestReadPhonePermissions();
        //FCM Token registration with server


        profileImageHandler = new Handler();
        String imageUrl = userResponse.getValue().getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (FileHelper.isProfilePictureAvailable(DashboardUserActivity.this, userResponse.getValue().getUserId())) {
                Bitmap bm = FileHelper.getProfilePicture(DashboardUserActivity.this, userResponse.getValue().getUserId());
                mProfileImg.setImageBitmap(bm);
            } else {
                new GetProfileImage(DashboardUserActivity.this, userResponse.getValue().getImageUrl(), mProfileImg, userResponse.getValue().getUserId()).execute();
            }
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (selectedMenuId != -1) {
                    navigateToScreen(selectedMenuId);
                    selectedMenuId = -1;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mVersionName.setText("v" + BuildConfig.VERSION_NAME);
                userResponse = SharedPrefsUtils.getLoggedUserObject(DashboardUserActivity.this);
                String token = userResponse.getValue().getSivaToken();
                if (token != null && !token.isEmpty())
                    mSivaTokensTV.setText(token + "");
                else
                    mSivaTokensTV.setText("0");
                String imageUrl = userResponse.getValue().getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    profileImageHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (FileHelper.isProfilePictureAvailable(DashboardUserActivity.this, userResponse.getValue().getUserId())) {
                                Bitmap bm = FileHelper.getProfilePicture(DashboardUserActivity.this, userResponse.getValue().getUserId());
                                mProfileImg.setImageBitmap(bm);
                            } else {
                                new GetProfileImage(DashboardUserActivity.this, userResponse.getValue().getImageUrl(), mProfileImg, userResponse.getValue().getUserId()).execute();
                            }

                        }
                    }, 300);
                }
            }
        };
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_circle); //set your own
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavProfileName.setText(userResponse.getValue().getFullName());
        mllDeposit.setVisibility(View.VISIBLE);
        mBalanceLayout.setVisibility(View.GONE);
        mIvDeposit.setTag(DEPOSIT_BANK);
        mEtPayeeName.setEnabled(false);
        mEmail.setText(userResponse.getValue().getEmail());
        mPhone.setText(userResponse.getValue().getPhoneNumber());
        mFullName.setText(userResponse.getValue().getFullName());
        mNavMerchantListing.setVisibility(View.VISIBLE);
        mNavTransactionLookup.setVisibility(View.GONE);
        if (SingleShotLocationProvider.isGPSEnabled(this)) {
            requestPermissions();

        } else {
            promptGPSDialog();
        }


        EditTextDebounce.create(mRupeeValue).watch(new EditTextDebounce.DebounceCallback() {
            @Override
            public void onFinished(@NonNull String result) {
                double rupee = 0;
                if (!TextUtils.isEmpty(result)) {
                    rupee = Double.parseDouble(result);
                } else {
                    cryptoResponse = null;
                    mBTCValue.setText("");
                }
                if (rupee > 0 && rupee <= 49999) {
                    PostCryptoValue postCryptoValue = new PostCryptoValue();
                    postCryptoValue.setInr(rupee);
                    postCryptoValue.setCryptoCode(UIUtils.getCryptoCode(DashboardUserActivity.this));
                    postCryptoValue.setUserApiKey(SharedPrefsUtils.getUserApiKey(DashboardUserActivity.this));
                    postCryptoValue.setUserId(String.valueOf(userResponse.getValue().getUserId()));
                    if (NetworkUtil.getConnectivityStatus(DashboardUserActivity.this)) {
                        mPresenter.getCryptoValue(postCryptoValue, API_CRYPTO_REQUEST);
                    } else {
                        showMessage(getString(R.string.internet_check));
                    }
                } else if (rupee > 49999) {
                    UIUtils.showToastOnDashboard(DashboardUserActivity.this, getString(R.string.cannot_initiate));
                }
            }
        }, 1500);

        //navigation from push notification
        Intent intent = getIntent();
        if (intent != null) {
            receiverName = intent.getStringExtra(RECEIVER_FULL_NAME);
            if (receiverName != null && !receiverName.isEmpty()) {
                mFullName.setText(receiverName);
            }
            receiverEmail = intent.getStringExtra(RECEIVER_EMAIL);
            if (receiverEmail != null && !receiverEmail.isEmpty()) {
                mEmail.setText(receiverEmail);
            }
            receiverPhone = intent.getStringExtra(RECEIVER_PHONE_NUMBER);
            receiverCountryCode = intent.getStringExtra(RECEIVER_COUNTRY_CODE);
            if (receiverPhone != null && !receiverPhone.isEmpty()) {
                mPhone.setText(receiverPhone);
            }
            requestedAmount = intent.getStringExtra(REQUESTED_AMOUNT);
            if (requestedAmount != null && !requestedAmount.isEmpty()) {
                mRupeeValue.append(requestedAmount);
            }
            transactionRequestId = intent.getStringExtra(TRANSACTION_REQUEST_ID);
            requestedPaymentType = intent.getStringExtra(REQUESTED_DEPOSIT_TYPE);
            if (requestedPaymentType != null && !requestedPaymentType.isEmpty()) {
                if (requestedPaymentType.equalsIgnoreCase(DEPOSIT_BANK)) {
                    mIvDeposit.setTag(DEPOSIT_BANK);
                    mIvDeposit.setImageResource(R.drawable.ic_credit_card);
                } else {
                    mIvDeposit.setTag(DEPOSIT_CASH);
                    mIvDeposit.setImageResource(R.drawable.ic_money);
                }
            }

        }


        EditTextDebounce.create(mEtIfscCode).watch(new EditTextDebounce.DebounceCallback() {
            @Override
            public void onFinished(@NonNull String result) {
                BankDetails bankDetails = new BankDetails();
                bankDetails.setUserId(Integer.parseInt(userResponse.getValue().getUserId()));
                bankDetails.setUserApiKey(userResponse.getValue().getUserApiKey());
                bankDetails.setBankCode(mEtIfscCode.getText().toString());
                if (NetworkUtil.getConnectivityStatus(DashboardUserActivity.this)) {
                    if (mEtIfscCode.getText().length() == 11) {
                        if (CommonUtils.isIFSCCode(mEtIfscCode.getText().toString())) {
                            mPresenter.getBankName(bankDetails, GET_BANK_NAME);
                        } else {
                            UIUtils.showToast(DashboardUserActivity.this, getString(R.string.validation_bankcode_proper));
                        }
                    }
                } else
                    showMessage(getString(R.string.internet_check));

            }
        }, 3000);
        setBuyHere();
        if (NetworkUtil.getConnectivityStatus(this)) {
            mPresenter.getCryptoMenuList(SharedPrefsUtils.getLoggedUserObject(DashboardUserActivity.this).getValue().getUserId(),
                    SharedPrefsUtils.getLoggedUserObject(DashboardUserActivity.this).getValue().getUserApiKey(),
                    API_CRYPTO_MENU_REQUEST);
        } else {
            CryptoMenuResponse response = FileHelper.readMenuList(this);
            cryptoMenuAdapter.updateMenu(response.getValue());
        }

    }

    private void registerFCMTokenToServer() {
        mPresenter.registerFCMToken(userResponse.getValue().getUserId(), userResponse.getValue().getUserApiKey(), FirebaseInstanceId.getInstance().getToken(), mIMEINumber, REQUEST_FCM_REGISTRATION);
    }

    OnCryptoMenuClickListener onCryptoMenuClickListener = new OnCryptoMenuClickListener() {
        @Override
        public void onItemClick(CryptoMenuResponse.Value item) {
            int cx = revealLayout.getRight();
            int cy = revealLayout.getTop();
            makeEffect(revealLayout, cx, cy);
            theme = SharedPrefsUtils.getStringPreference(DashboardUserActivity.this, AppConstants.BundleParams.THEME);
            if (TextUtils.isEmpty(theme)) {
                theme = AppConstants.CryptoCodes.BTC;
            }
            if (!item.getCryptoCode().equalsIgnoreCase(theme)) {
                SharedPrefsUtils.setStringPreference(DashboardUserActivity.this, AppConstants.BundleParams.THEME, item.getCryptoCode());
                recreate();
            }
        }
    };


    public void onClickNotifications(View v) {
        CommonUtils.startActivity(DashboardUserActivity.this, NotificationsActivity.class);
    }

    private void setBuyHere() {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                CommonUtils.startActivity(DashboardUserActivity.this, BuyBitcoinsActivity.class);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        String thisLink = getString(R.string.click_here);
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.buy_currency_link, typedValue, true);
        String yourString = getString(typedValue.resourceId, thisLink);
        int thisLinkIndex = yourString.indexOf(thisLink);
        SpannableString spannableString = new SpannableString(yourString);
        spannableString.setSpan(clickableSpan,
                thisLinkIndex, thisLinkIndex + thisLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        buyHere.setText(spannableString, TextView.BufferType.SPANNABLE);
        buyHere.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void requestPermissions() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        checkFineLocationPermission();
                    }
                }

            });
        } else {
            setBusinessNameAsLocation();
        }
    }

    private void checkFineLocationPermission() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        setBusinessNameAsLocation();
                    }
                }
            });
        } else {
            setBusinessNameAsLocation();
        }
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    public void onClickSaveBankDetails(View v) {
        if (validateBankDetails()) {
            saveBankDetails();
        }
    }

    public void onClickPayment(View v) {
        if (validatePayment()) {
            String rupee = mRupeeValue.getText().toString();
            String email = mEmail.getText().toString();
            String phone = mPhone.getText().toString();
            String name = mFullName.getText().toString();
            String text = this.getString(R.string.confirm_your_details);
            if (mIvDeposit.getTag().toString().equalsIgnoreCase(DEPOSIT_BANK)) {
                text = this.getString(R.string.confirm_your_details_1);
            }

            //Log Analytics
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_PHONE, phone);
            b.putString(FirebaseConstants.PARAM_EMAIL, email);
            mFirebaseAnalytics.logEvent(FirebaseConstants.USR_CONFIRM_PAYMENT, b);

            UIUtils.showCustomDialogForConfirmation(this, UIUtils.getStringFromResource(this, R.string.confirm_payment), text, "INR " + rupee, UIUtils.captializeNames(name), phone, email,
                    UIUtils.getStringFromResource(this, R.string.proceed), UIUtils.getStringFromResource(this, R.string.cancel), proceedCallback,
                    cancelCallback);

        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    /**
     * positive callback for payment confirmation
     */
    UIUtils.DialogButtonClickListener proceedCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();

            //Analytics log
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_PHONE, enteredPhone);
            b.putString(FirebaseConstants.PARAM_EMAIL, enteredEmail);
            mFirebaseAnalytics.logEvent(FirebaseConstants.USR_PROCEED_PAYMENT, b);

            if (TextUtils.isEmpty(receiverId) || receiverId.equalsIgnoreCase("0")) {
                startPayment();
                return;
            }
            // user opt for bank deposit check bank details available or not
            if (mIvDeposit.getTag().toString().equalsIgnoreCase(DEPOSIT_BANK) && (TextUtils.isEmpty(bankDetailsAvailable) || bankDetailsAvailable.equalsIgnoreCase("0"))) {
                // add back details if user opt for bank deposit
                mEtPayeeName.setText(mFullName.getText().toString());
                mBankDetailsLayout.setVisibility(View.VISIBLE);
                mSaveBankDetails.setVisibility(View.VISIBLE);
                mConfirmPayment.setVisibility(View.GONE);
                mDashboardMain.setVisibility(View.GONE);
                return;
            }
            startPayment();
        }
    };
    /**
     * negative callback for payment confirmation
     */
    UIUtils.DialogButtonClickListener cancelCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
        }
    };

    private void startPayment() {
        enteredRupee = mRupeeValue.getText().toString();
        enteredEmail = mEmail.getText().toString();
        enteredPhone = mPhone.getText().toString();
        enteredName = mFullName.getText().toString();
        startPaymentRequest = new StartPaymentRequest();
        startPaymentRequest.setFiatValue(enteredRupee);
        startPaymentRequest.setEmail(enteredEmail);
        startPaymentRequest.setCryptoCode(UIUtils.getCryptoCode(this));
        startPaymentRequest.setPhoneNumber(enteredPhone);
        startPaymentRequest.setFullName(enteredName);
        startPaymentRequest.setUserId(Integer.parseInt(userResponse.getValue().getUserId()));
        startPaymentRequest.setUserApiKey(userResponse.getValue().getUserApiKey());
        startPaymentRequest.setUserType(userResponse.getValue().getUserType());
        startPaymentRequest.setPaymentType(mIvDeposit.getTag().toString());
        startPaymentRequest.setReceiverId(receiverId);
        // user opt for bank deposit check bank details available or not
        if ((receiverId != null && !receiverId.equalsIgnoreCase("0")) && (TextUtils.isEmpty(bankDetailsAvailable) || bankDetailsAvailable.equalsIgnoreCase("0")) && mIvDeposit.getTag().toString().equalsIgnoreCase(DEPOSIT_BANK)) {
            // add back details if user opt for bank deposit
            mEtPayeeName.setText(mFullName.getText().toString());
            mBankDetailsLayout.setVisibility(View.VISIBLE);
            mSaveBankDetails.setVisibility(View.VISIBLE);
            mConfirmPayment.setVisibility(View.GONE);
            mDashboardMain.setVisibility(View.GONE);
            return;
        }
        if (NetworkUtil.getConnectivityStatus(DashboardUserActivity.this)) {
            mPresenter.startPayment(startPaymentRequest, API_START_PAYMENT);
        } else {
            showMessage(getString(R.string.internet_check));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem alertMenuItem = menu.findItem(R.id.action_notifications);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        notificationCountTV = (TextView) rootView.findViewById(R.id.notification_count);
        if (SharedPrefsUtils.getNotificationCount(this) > 0) {
            notificationCountTV.setVisibility(View.VISIBLE);
            notificationCountTV.setText(SharedPrefsUtils.getNotificationCount(this) + "");
        } else {
            notificationCountTV.setVisibility(View.GONE);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (paymentTimer != null && isTimerRunning) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                int cx = revealLayout.getRight();
                int cy = revealLayout.getTop();
                makeEffect(revealLayout, cx, cy);
                return true;
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean hidden = true;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void makeEffect(final LinearLayout layout, int cx, int cy) {
        int radius = Math.max(layout.getWidth(), layout.getHeight());
        if (hidden) {
            layout.setVisibility(View.VISIBLE);
            hidden = false;
        } else {
            layout.setVisibility(View.INVISIBLE);
            hidden = true;
        }
    }


    private boolean validatePayment() {
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        String rupee = mRupeeValue.getText().toString();
        String email = mEmail.getText().toString();
        String phone = mPhone.getText().toString();
        String name = mFullName.getText().toString();

        if (TextUtils.isEmpty(rupee)) {
            UIUtils.showToastOnDashboard(DashboardUserActivity.this, getString(R.string.rupee_validation));
            return false;
        } else if (Integer.parseInt(rupee) > 49999) {
            UIUtils.showToastOnDashboard(DashboardUserActivity.this, getString(R.string.cannot_initiate));
            return false;
        } else if (TextUtils.isEmpty(name)) {
            UIUtils.showToastOnDashboard(DashboardUserActivity.this, getString(R.string.my_name_validation));
            return false;
        } else if (TextUtils.isEmpty(email) || (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            UIUtils.showToastOnDashboard(DashboardUserActivity.this, getString(R.string.email_validation_message));
            return false;
        } else if (TextUtils.isEmpty(phone) || (!TextUtils.isEmpty(phone) && !CommonUtils.isPhoneNumber(phone)) || phone.length() < 10) {
            UIUtils.showToastOnDashboard(DashboardUserActivity.this, getString(R.string.phone_validation_message));
            return false;
        } else if (!TextUtils.isEmpty(userResponse.getValue().getIsEmailVerified()) && userResponse.getValue().getIsEmailVerified().equalsIgnoreCase("0")) {
            if (NetworkUtil.getConnectivityStatus(this)) {
                PostUserLogin postUserLogin = new PostUserLogin();
                postUserLogin.setEmail(userResponse.getValue().getEmail());
                postUserLogin.setPhoneNumber(userResponse.getValue().getPhoneNumber());
                postUserLogin.setOsVersion(DeviceInfo.getOSVersion());
                postUserLogin.setApiVersion(DeviceInfo.getAppVersion(this));
                postUserLogin.setDeviceBrand(DeviceInfo.getBuildBrand());
                postUserLogin.setDeviceId(mIMEINumber);
                postUserLogin.setCountryCode(userResponse.getValue().getCountryCode());
                postUserLogin.setUserId(userResponse.getValue().getUserId());
                postUserLogin.setUserApiKey(userResponse.getValue().getUserApiKey());
                mPresenter.viewMyProfile(postUserLogin, GET_PROFILE_REQUEST);
            }

            return false;
        }

        return true;
    }

    private boolean validateBankDetails() {
        if (mBankDetailsLayout.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(mEtBankName.getText().toString()) &&
                    TextUtils.isEmpty(mEtAccNum.getText().toString()) &&
                    TextUtils.isEmpty(mEtIfscCode.getText().toString())) {
                UIUtils.showToastOnDashboard(this, getString(R.string.validation_blank));
                return false;
            } else if (TextUtils.isEmpty(mEtAccNum.getText().toString())) {
                UIUtils.showToastOnDashboard(this, getString(R.string.validation_accountnumber));
                return false;
            } else if (TextUtils.isEmpty(mEtBankName.getText().toString())) {
                UIUtils.showToastOnDashboard(this, getString(R.string.validation_bankname));
                return false;
            } else if (TextUtils.isEmpty(mEtIfscCode.getText().toString()) || mEtIfscCode.getText().length() != 11) {
                UIUtils.showToastOnDashboard(this, getString(R.string.validation_bankcode));
                return false;
            }
//            else if (TextUtils.isEmpty(mEtAccountType.getText().toString())) {
//                UIUtils.showToastOnDashboard(this, getString(R.string.validation_acctype));
//                return false;
//            } else if (!mEtAccountType.getText().toString().equalsIgnoreCase(getString(R.string.account_type_savings)) &&
//                    !mEtAccountType.getText().toString().equalsIgnoreCase(getString(R.string.account_type_current))) {
//                UIUtils.showToastOnDashboard(this, getString(R.string.validation_acctype_notvalid));
//                return false;
//            }
        }
        return true;
    }

    /**
     * Shows email verification dialog if email isn't verified
     */
    private void showEmailVerificationDialog() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_bank_details);
        TextInputLayout mTILPassword = dialog.findViewById(R.id.til_password);
        mTILPassword.setVisibility(View.GONE);
        Button mProceed = dialog.findViewById(R.id.proceed_btn);
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Analytics Log
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                b.putString(FirebaseConstants.PARAM_EMAIL, userResponse.getValue().getEmail());
                mFirebaseAnalytics.logEvent(FirebaseConstants.VERIFY_EMAIL, b);

                dialog.dismiss();
                mPresenter.verifyEmail(userResponse.getValue().getUserId(), userResponse.getValue().getUserApiKey(), VERIFY_EMAIL_REQUEST);

            }
        });
        TextView mDesc = dialog.findViewById(R.id.warning_msg);
        mDesc.setText(getString(R.string.email_verification_msg));
        ImageView mCancel = dialog.findViewById(R.id.cancel_btn);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView mTitle = dialog.findViewById(R.id.header_title);
        mTitle.setText(getString(R.string.warning_verify_email));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * method to set the bankdetails to model to calling server api
     */
    private void saveBankDetails() {
        BankDetails bankDetails = new BankDetails();
        bankDetails.setUserId(Integer.parseInt(userResponse.getValue().getUserId()));
        bankDetails.setUserApiKey(userResponse.getValue().getUserApiKey());
        bankDetails.setPassword("");
        bankDetails.setReceiverId(receiverId);
        bankDetails.setTransactionFlag(true);
        if (bankNameResponse != null) {
            bankDetails.setBankAddress(bankNameResponse.getValue().getBankAddress());
            bankDetails.setBankCity(bankNameResponse.getValue().getBankCity());
        }

        String bankName = mEtBankName.getText().toString();
        bankDetails.setBankName(bankName);

        String bankBranch = mETBankBranch.getText().toString();
        bankDetails.setBankBranch(bankBranch);

        String accNum = mEtAccNum.getText().toString();
        bankDetails.setAccountNumber(accNum);

        String ifscCode = mEtIfscCode.getText().toString();
        bankDetails.setBankCode(ifscCode);

//        String accountType = mEtAccountType.getText().toString();
//        bankDetails.setAccountType(accountType);

        bankDetails.setUpiAddress("");

        if (NetworkUtil.getConnectivityStatus(this)) {

            //Analytics log
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.SAVE_BANK_DETAILS_TRANS, b);

            mPresenter.saveBankDetails(bankDetails, SAVE_BANK_DETAILS);
        } else
            showMessage(getString(R.string.internet_check));
    }

    /**
     * clears data from all the textboxes for bank details screen
     */
    private void clearBankDetails() {
        mEtIfscCode.setText("");
        mEtBankName.setText("");
        mETBankBranch.setText("");
        mEtAccNum.setText("");
//        mEtAccountType.setText("");

    }

    /**
     * Show user with selection of deposit from dialog.
     */
    private void showDialogForDeposit() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_deposit_choose);

        ImageView mCancel = dialog.findViewById(R.id.cancel_btn);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView mTvBank = dialog.findViewById(R.id.tv_bank);
        mTvBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                mIvDeposit.setTag(DEPOSIT_BANK);
                mIvDeposit.setImageResource(R.drawable.ic_credit_card);


                //Analytics log
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                b.putString(FirebaseConstants.PARAM_TRANS_TYPE, mIvDeposit.getTag().toString());
                mFirebaseAnalytics.logEvent(FirebaseConstants.TRANSACTION_TYPE, b);
            }
        });

        TextView mTvCash = dialog.findViewById(R.id.tv_cash);
        mTvCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mIvDeposit.setTag(DEPOSIT_CASH);
                mIvDeposit.setImageResource(R.drawable.ic_money);
                //Analytics log
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                b.putString(FirebaseConstants.PARAM_TRANS_TYPE, mIvDeposit.getTag().toString());
                mFirebaseAnalytics.logEvent(FirebaseConstants.TRANSACTION_TYPE, b);
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @OnClick(R.id.ll_deposit)
    public void OnClickDeposit() {
        showDialogForDeposit();
    }


    public void onClickPaymentComplete(View v) {

        if (receivePaymentResponse != null &&
                receivePaymentResponse.getStatus().equalsIgnoreCase(APISettings.RESPONSE_STATUS_SUCCESS)) {

            //Analytics log
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.USR_PAYMENT_COMPLETE, b);


            Bundle bundle = new Bundle();
            ReceivePaymentRequest value = receivePaymentResponse.getValue();
            bundle.putString(TransactionReceiptActivity.TRANSACTION_CODE, value.getTransactionCode());
            bundle.putBoolean(DASHBOARD_PAYMENT_COMPLETED, true);
            CommonUtils.startActivityForResult(DashboardUserActivity.this, TransactionReceiptActivity.class, bundle, REQUEST_PAYMENT_COMPLETED);
        }
    }

    /**
     * clears data from all the textboxes
     */
    private void clearData() {
        mConfirmPayment.setVisibility(View.VISIBLE);
        mPaymentComplete.setVisibility(View.GONE);
        mRupeeValue.setEnabled(true);
        mllDeposit.setEnabled(true);
        mEmail.setEnabled(true);
        mPhone.setEnabled(true);
        mFullName.setEnabled(true);
        mPhoneEmailLayout.setVisibility(View.VISIBLE);
        mBitcoinAddressLayout.setVisibility(View.GONE);
        mRupeeValue.setText("");
        mEmail.setText(userResponse.getValue().getEmail());
        mPhone.setText(userResponse.getValue().getPhoneNumber());
        mFullName.setText(userResponse.getValue().getFullName());
        mBTCValue.setText("");
    }


    public void onClickBTCValue(View v) {
        //Analytics log
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.BTC_BREAK_UP, b);

        String btcValue = mBTCValue.getText().toString();
        if (!TextUtils.isEmpty(btcValue)) {
            showTransactionDialog();
        }
    }

    public void onClickCopy(View v) {

        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.COPY_BTC_ADDRESS, b);

        boolean copyBitcoinAddress = UIUtils.copyToClipboard(this, paymentResponse.getValue().getCryptoAddress());
        if (copyBitcoinAddress) {
            UIUtils.showToast(this, "Crypto Address is copied to clipboard.");
        }
    }


    /**
     * Location is set using auto-update location while typing
     */
    public void setBusinessNameAsLocation() {
        SingleShotLocationProvider.requestSingleUpdate(DashboardUserActivity.this, new SingleShotLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                Geocoder gcd = new Geocoder(DashboardUserActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(location.latitude, location.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        mBusinessName.setText(addresses.get(0).getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * Shows the transaction details dialog with detailed amount, tax and total BTC
     */
    private void showTransactionDialog() {
        dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_transaction);
        ImageView mClose = dialog.findViewById(R.id.iv_close);
        mClose.setOnClickListener(dialogClickActionListener);
        Button mOK = dialog.findViewById(R.id.btn_ok);
        mOK.setOnClickListener(dialogClickActionListener);

        TextView mCustName = dialog.findViewById(R.id.tv_customer_name);
        TextView mCustEmail = dialog.findViewById(R.id.tv_customer_email);
        TextView mCustPhone = dialog.findViewById(R.id.tv_customer_phone);
        TextView mBTCValue = dialog.findViewById(R.id.tv_btc_resp);
        TextView mINRValue = dialog.findViewById(R.id.tv_amount_inr);
        TextView mServiceCharge = dialog.findViewById(R.id.tv_service_charge);
        TextView mTotalBTCPay = dialog.findViewById(R.id.tv_total_pay);
        LinearLayout headerLayout = dialog.findViewById(R.id.header);
        LinearLayout detailsLayout = dialog.findViewById(R.id.details_layout);

        if (cryptoResponse != null) {
            String fullName = mFullName.getText().toString();
            if (fullName.isEmpty()) {
                mCustName.setVisibility(View.GONE);
            } else {
                mCustName.setVisibility(View.VISIBLE);
                mCustName.setText(fullName);
            }
            String email = mEmail.getText().toString();
            if (email.isEmpty()) {
                mCustEmail.setVisibility(View.GONE);
            } else {
                mCustEmail.setText(email);
                mCustEmail.setVisibility(View.VISIBLE);
            }
            String phoneNum = mPhone.getText().toString();
            if (phoneNum.isEmpty()) {
                mCustPhone.setVisibility(View.GONE);
            } else {
                mCustPhone.setVisibility(View.VISIBLE);
                mCustPhone.setText(getString(R.string.format_phone_IN, phoneNum));
            }
            if (fullName.isEmpty() && email.isEmpty() && phoneNum.isEmpty()) {
                headerLayout.setBackgroundResource(R.drawable.app_toolbar_bg1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DashboardUserActivity.this.getResources().getDimensionPixelOffset(R.dimen.dimen_60));
                headerLayout.setLayoutParams(params);
                detailsLayout.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                headerLayout.setLayoutParams(params);
                headerLayout.setBackgroundResource(R.drawable.app_popup_bg1);
                detailsLayout.setVisibility(View.VISIBLE);
            }
            mINRValue.setText(String.valueOf(cryptoResponse.getValue().getInr()));
            mBTCValue.setText(String.format("%.8f", cryptoResponse.getValue().getCryptoValue()));
            mServiceCharge.setText(String.format("%.8f", cryptoResponse.getValue().getServiceChargeInCrypto()));
            mTotalBTCPay.setText(String.format("%.8f", cryptoResponse.getValue().getTotalCrypto()));
        }


        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    View.OnClickListener dialogClickActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    };


    public void onMenuClick(View v) {
        selectedMenuId = v.getId();
        drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * Side menu navigation details are added to this method
     *
     * @param selectedMenuId ID of the list item which is selected
     */
    private void navigateToScreen(int selectedMenuId) {
        switch (selectedMenuId) {
            case R.id.profile_click:
            case R.id.profile_img:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_PROFILE, new Bundle());
                CommonUtils.startActivityForResult(this, UserProfileActivity.class, REQUEST_PROFILE_PICTURE);
                break;
            case R.id.tv_your_trasactions:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_TRANSACTIONS, new Bundle());
                CommonUtils.startActivity(this, TransactionListActivity.class);
                break;
            case R.id.tv_merchant_listing:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_MERCHANT_LIST, new Bundle());
                CommonUtils.startActivity(this, MerchantListingActivity.class);
                break;
            case R.id.tv_legal:
            case R.id.tv_version_name:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_LEGAL, new Bundle());
                CommonUtils.startActivity(this, LegalActivity.class);
                break;
            case R.id.tv_customer_care:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_HELP, new Bundle());
                CommonUtils.startActivity(this, HelpActivity.class);
                break;
            case R.id.tv_signout:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_SIGN_OUT, new Bundle());
                SharedPrefsUtils.clear(this);
                CommonUtils.clearNotifications(this);
                CommonUtils.startActivity(this, LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
                finish();
                break;
            case R.id.balance_layout:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_BALANCE, new Bundle());
                CommonUtils.startActivity(this, WalletBalanceActivity.class);
                break;

            case R.id.tv_invite:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_INVITE, new Bundle());
                CommonUtils.startActivity(this, InviteFriendActivity.class);
                break;

            case R.id.tv_leaderboard:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_LEADERBOARD, new Bundle());
                CommonUtils.startActivity(this, LeaderboardActivity.class);
                break;

            case R.id.tv_request_money:
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_DRAWER_REQUEST_MONEY, new Bundle());
                CommonUtils.startActivity(this, RequestMoneyActivity.class);
                break;
        }

    }


    /**
     * Starts payment after all details are filled in the fields
     */
    public void initiatePayment() {
        mllDeposit.setEnabled(false);
        mConfirmPayment.setVisibility(View.GONE);
        mRupeeValue.setEnabled(false);
        mEmail.setEnabled(false);
        mPhone.setEnabled(false);
        mFullName.setEnabled(false);
        mPhoneEmailLayout.setVisibility(View.GONE);
        mBitcoinAddressLayout.setVisibility(View.VISIBLE);
        String cryptoAddress = paymentResponse.getValue().getCryptoAddress();
        mBitcoinAddress.setText(cryptoAddress);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix;
            if (UIUtils.getCryptoCode(DashboardUserActivity.this).equalsIgnoreCase(AppConstants.CryptoCodes.BTC)) {
                bitMatrix = multiFormatWriter.encode("bitcoin:" + cryptoAddress + "?amount=" + paymentResponse.getValue().getTotalCrypto(), BarcodeFormat.QR_CODE, 200, 200);
            } else {
                bitMatrix = multiFormatWriter.encode("ethereum:" + cryptoAddress + "?amount=" + paymentResponse.getValue().getTotalCrypto(), BarcodeFormat.QR_CODE, 200, 200);
            }
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            mBitcoinQR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        startTimer();
    }

    /**
     * Starts timer for 15 minutes after the payment is initiated and waits for server to send a response of received payment
     */
    private void startTimer() {
        isTimerRunning = true;
        final long milliDiff = 15 * 60 * 1000;
        paymentTimer = new CountDownTimer(milliDiff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                mTimerView.setText("" + time);
                int percentage = (int) (360 - (millisUntilFinished / (float) milliDiff) * 360);
                mTimerView.setProgress(360 - percentage);
                // execute receive payment api call for every minute to get transaction code
                if (percentage > 0 && percentage % (float) 24 == 0.0 && repeatCounter != percentage) {
                    repeatCounter = percentage;
                    ReceivePaymentRequest request = new ReceivePaymentRequest();
                    StartPaymentResponse.Value value = paymentResponse.getValue();
                    request.setChargeCode(value.getChargeCode());
                    request.setCryptoAddress(value.getCryptoAddress());
                    request.setCryptoValue(value.getCryptoValue());
                    request.setCryptoCode(UIUtils.getCryptoCode(DashboardUserActivity.this));
                    request.setReceiverId(value.getReceiverId());
                    request.setReceiverBankId(receiverBankId);
                    request.setServiceChargeInCrypto(value.getServiceChargeInCrypto());
                    request.setTotalCrypto(value.getTotalCrypto());
                    request.setCurrency(value.getCurrency());
                    request.setPaymentType(mIvDeposit.getTag().toString());
                    request.setUserId(Integer.parseInt(userResponse.getValue().getUserId()));
                    request.setUserApiKey(userResponse.getValue().getUserApiKey());
                    request.setFiatValue(value.getFiatValue());
                    request.setFullName(enteredName);
                    request.setCountryCode(userResponse.getValue().getCountryCode());
                    request.setEmail(enteredEmail);
                    request.setPhoneNumber(enteredPhone);
                    request.setUserType(userResponse.getValue().getUserType());
                    if (transactionRequestId != null && !transactionRequestId.isEmpty())
                        request.setTransactionRequestId(transactionRequestId);

                    if (NetworkUtil.getConnectivityStatus(DashboardUserActivity.this)) {
                        mPresenter.receivePayment(request, API_RECEIVE_PAYMENT);
                    } else {
                        showMessage(getString(R.string.internet_check));
                    }

                }
//                createNotification(getString(R.string.notification_transaction_time, time));
//                notificationManager.notify(UNIQUE_NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                repeatCounter = 0;
                mTimerView.setText("0");
                mTimerView.setProgress(0);
                if (!receivePaymentResponse.getStatus().equalsIgnoreCase(APISettings.RESPONSE_STATUS_SUCCESS)) {
                    //Log Analytics
                    Bundle b = new Bundle();
                    b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                    mFirebaseAnalytics.logEvent(FirebaseConstants.PAYMENT_NOT_RECEIVED, b);

                    UIUtils.showCustomDialog(DashboardUserActivity.this, UIUtils.getStringFromResource(DashboardUserActivity.this, R.string.transaction_error), UIUtils.getStringFromResource(DashboardUserActivity.this, R.string.transaction_code_generation_error), "OK", null, new UIUtils.DialogButtonClickListener() {

                                @Override
                                public void onClickButton(Dialog d, View v) {
                                    d.dismiss();
                                    Intent intent = getIntent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                            , null);
                    clearData();
                    clearBankDetails();
                }
            }
        }.start();

    }


    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        if (!hidden) {
            makeEffect(revealLayout, revealLayout.getRight(), revealLayout.getTop());
        }
        switch (apiRequestCode) {

            case REQUEST_FCM_REGISTRATION:
                GenericResponse registrationResponse = (GenericResponse) object;
                int errorCode1 = registrationResponse.getErrorCode();
                switch (errorCode1) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        fcmToken = FirebaseInstanceId.getInstance().getToken();
                        SharedPrefsUtils.saveFCMToken(DashboardUserActivity.this, fcmToken);
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        //Bad credentials error
                        logOutFromApp();

                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToastOnDashboard(this, registrationResponse.getErrorDescription());
                        break;
                }
                break;
            case API_CRYPTO_MENU_REQUEST:
                CryptoMenuResponse response = (CryptoMenuResponse) object;
                cryptoMenuAdapter.updateMenu(response.getValue());
                FileHelper.saveMenuList(this, response);
                break;
            case API_CRYPTO_REQUEST:
                cryptoResponse = (CryptoResponse) object;
                int code = cryptoResponse.getErrorCode();
                switch (code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (cryptoResponse.getValue() != null && !UIUtils.isUserApiKeyValid(this, cryptoResponse.getValue().getUserApiKey())) {
                            userResponse = SharedPrefsUtils.getLoggedUserObject(this);
                        }
                        mBTCValue.setText(String.format("%.8f", cryptoResponse.getValue().getTotalCrypto()));
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        //Bad credentials error
                        logOutFromApp();

                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToastOnDashboard(this, cryptoResponse.getErrorDescription());
                        break;
                }
                break;

            case API_START_PAYMENT:
                paymentResponse = (StartPaymentResponse) object;
                int errorCode = paymentResponse.getErrorCode();
                switch (errorCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (!UIUtils.isUserApiKeyValid(this, paymentResponse.getValue().getUserApiKey())) {
                            userResponse = SharedPrefsUtils.getLoggedUserObject(this);
                        }

                        if (cryptoResponse != null) {
                            String btcValue = paymentResponse.getValue().getCryptoValue();
                            if (btcValue != null && !btcValue.isEmpty())
                                cryptoResponse.getValue().setCryptoValue(Double.parseDouble(btcValue));
                            String serviceChargeInBtc = paymentResponse.getValue().getServiceChargeInCrypto();
                            if (serviceChargeInBtc != null && !serviceChargeInBtc.isEmpty())
                                cryptoResponse.getValue().setServiceChargeInCrypto(Double.parseDouble(serviceChargeInBtc));
                            String totalBTC = paymentResponse.getValue().getTotalCrypto();
                            if (totalBTC != null && !totalBTC.isEmpty())
                                cryptoResponse.getValue().setTotalCrypto(Double.parseDouble(totalBTC));
                        }

                        // first attempt api call to get receiver id from start payment
                        if (TextUtils.isEmpty(paymentResponse.getValue().getChargeCode()) ||
                                paymentResponse.getValue().getChargeCode().length() == 0) {
                            receiverId = String.valueOf(paymentResponse.getValue().getReceiverId());
                            bankDetailsAvailable = paymentResponse.getValue().getBankDetailsAvailable();
                            // user opt for bank deposit check bank details available or not
                            if ((TextUtils.isEmpty(bankDetailsAvailable) || bankDetailsAvailable.equalsIgnoreCase("0")) && mIvDeposit.getTag().toString().equalsIgnoreCase(DEPOSIT_BANK)) {
                                // add back details if user opt for bank deposit
                                mEtPayeeName.setText(mFullName.getText().toString());
                                mBankDetailsLayout.setVisibility(View.VISIBLE);
                                mSaveBankDetails.setVisibility(View.VISIBLE);
                                mConfirmPayment.setVisibility(View.GONE);
                                mDashboardMain.setVisibility(View.GONE);
                            } else {
                                // passing receiver id to second attempt
                                // second attempt api call to get charge code
                                startPayment();
                            }
                            return;
                        }
                        mBankDetailsLayout.setVisibility(View.GONE);
                        mSaveBankDetails.setVisibility(View.GONE);
                        mDashboardMain.setVisibility(View.VISIBLE);
                        mConfirmPayment.setVisibility(View.VISIBLE);
                        mBTCValue.setText(String.format("%.8f", Double.parseDouble(paymentResponse.getValue().getTotalCrypto())));
                        initiatePayment();
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        //Bad credentials error
                        logOutFromApp();

                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToastOnDashboard(this, paymentResponse.getErrorDescription());
                        break;
                }

                break;

            case API_RECEIVE_PAYMENT:
                receivePaymentResponse = (ReceivePaymentResponse) object;
                int mErrorCode = receivePaymentResponse.getErrorCode();
                switch (mErrorCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (!UIUtils.isUserApiKeyValid(this, receivePaymentResponse.getValue().getUserApiKey())) {
                            userResponse = SharedPrefsUtils.getLoggedUserObject(this);
                        }
                        isTimerRunning = false;
                        mTVConfirmPayment.setText(getString(R.string.payment_completed));
                        mTimerView.setText("0");
                        mTimerView.setProgress(0);
                        paymentTimer.cancel();
                        if (mIvDeposit.getTag().toString().equalsIgnoreCase(DEPOSIT_CASH)) {
                            mPaymentComplete.setVisibility(View.VISIBLE);
                            mPaymentComplete.setText(UIUtils.getStringFromResource(DashboardUserActivity.this, R.string.show_receipt));
                        } else {
                            // call complete payment api
                            ReceivePaymentRequest receivePaymentRequest = receivePaymentResponse.getValue();
                            receivePaymentRequest.setSenderId(Integer.parseInt(userResponse.getValue().getUserId()));
                            receivePaymentRequest.setPaymentType(mIvDeposit.getTag().toString());
                            receivePaymentRequest.setReceiverBankId(receiverBankId);
                            receivePaymentRequest.setUserType(AppConstants.USER_TYPE_SENDER_RECEIVER);
                            receivePaymentRequest.setCryptoCode(UIUtils.getCryptoCode(DashboardUserActivity.this));
                            mPresenter.completePayment(receivePaymentRequest, API_COMPLETE_PAYMENT);
                        }
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        //Bad credentials error
                        logOutFromApp();

                        break;
                }
                break;
            case API_COMPLETE_PAYMENT:
                ReceivePaymentResponse receivePaymentResponse = (ReceivePaymentResponse) object;
                code = receivePaymentResponse.getErrorCode();
                switch (code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        mPaymentComplete.setVisibility(View.VISIBLE);
                        mPaymentComplete.setText(UIUtils.getStringFromResource(DashboardUserActivity.this, R.string.show_receipt));

                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        if (!TextUtils.isEmpty(receivePaymentResponse.getErrorDescription()))
                            UIUtils.showToast(this, receivePaymentResponse.getErrorDescription());
                        break;
                }
                break;
            case VERIFY_EMAIL_REQUEST:
                GenericResponse genericResponse = (GenericResponse) object;
                int responseCode = genericResponse.getErrorCode();
                switch (responseCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (genericResponse.getValue() != null)
                            UIUtils.showToastOnDashboard(DashboardUserActivity.this, genericResponse.getValue());
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToastOnDashboard(DashboardUserActivity.this, genericResponse.getErrorDescription());
                        break;
                }
                break;
            case GET_PROFILE_REQUEST:
                UserResponse userResponseObj = (UserResponse) object;
                int mCode = userResponseObj.getErrorCode();
                switch (mCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        UserResponse.Value value = userResponseObj.getValue();
                        value.setOsVersion(DeviceInfo.getOSVersion());
                        value.setApiVersion(DeviceInfo.getAppVersion(this));
                        value.setDeviceBrand(DeviceInfo.getBuildBrand());
                        value.setDeviceId(mIMEINumber);
                        SharedPrefsUtils.saveLoggedUserObject(this, userResponseObj);
                        String isEmailVerified = value.getIsEmailVerified();
                        if (!TextUtils.isEmpty(isEmailVerified) && isEmailVerified.equalsIgnoreCase("0")) {
                            showEmailVerificationDialog();
                        } else {
                            String rupee = mRupeeValue.getText().toString();
                            String email = mEmail.getText().toString();
                            String phone = mPhone.getText().toString();
                            String name = mFullName.getText().toString();
                            UIUtils.showCustomDialogForConfirmation(this, UIUtils.getStringFromResource(this, R.string.confirm_payment), this.getString(R.string.confirm_your_details), "INR " + rupee, UIUtils.captializeNames(name), phone, email,
                                    UIUtils.getStringFromResource(this, R.string.proceed), UIUtils.getStringFromResource(this, R.string.cancel), proceedCallback,
                                    cancelCallback);
                        }

                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToast(DashboardUserActivity.this, userResponse.getErrorDescription());
                        break;
                }
                break;
            case GET_BANK_NAME:
                bankNameResponse = (BankNameResponse) object;
                errorCode = bankNameResponse.getErrorCode();
                switch (errorCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        mEtBankName.setText(bankNameResponse.getValue().getBankName());
                        mETBankBranch.setText(bankNameResponse.getValue().getBankBranch());
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        mEtBankName.setText("");
                        mETBankBranch.setText("");
                        UIUtils.showToastOnDashboard(this, bankNameResponse.getErrorDescription());
                        break;
                }
                break;
            case SAVE_BANK_DETAILS:
                GenericResponse _response = (GenericResponse) object;
                int _code = _response.getErrorCode();
                switch (_code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        // second attempt startPayment api call after user adding bank details
                        bankDetailsAvailable = "1";
                        receiverBankId = _response.getValue();
                        startPayment();
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToastOnDashboard(this, _response.getErrorDescription());
                        break;
                }
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (paymentTimer != null && isTimerRunning) {
                moveTaskToBack(true);
            } else {
                if (mBankDetailsLayout.getVisibility() == View.VISIBLE) {
                    mDashboardMain.setVisibility(View.VISIBLE);
                    mBankDetailsLayout.setVisibility(View.GONE);
                    mSaveBankDetails.setVisibility(View.GONE);
                    mConfirmPayment.setVisibility(View.VISIBLE);
                    clearBankDetails();
                    return;
                }
                super.onBackPressed();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PAYMENT_COMPLETED:
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                    break;
                case REQUEST_PROFILE_PICTURE:
                    shouldLoadNewImage = data.getBooleanExtra(UserProfileActivity.PROFILE_PIC_CHANGED, false);
                    break;
            }
        } else if (requestCode == REQUEST_GPS_SETTINGS) {
            requestPermissions();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(DashboardUserActivity.this, requestCode, permissions, grantResults);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("DepositType", mIvDeposit.getTag().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String myTag = savedInstanceState.getString("DepositType");
            if (myTag != null && !myTag.isEmpty() && myTag.equalsIgnoreCase(DEPOSIT_CASH)) {
                mIvDeposit.setTag(DEPOSIT_CASH);
                mIvDeposit.setImageResource(R.drawable.ic_money);
            } else {
                mIvDeposit.setTag(DEPOSIT_BANK);
                mIvDeposit.setImageResource(R.drawable.ic_credit_card);
            }
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_NEW_NOTIFICATION.equalsIgnoreCase(intent.getAction())) {
                loadNotificationsCount();
            }
        }
    };


    private void loadNotificationsCount() {
        if (notificationCountTV != null) {
            if (SharedPrefsUtils.getNotificationCount(this) > 0) {
                notificationCountTV.setVisibility(View.VISIBLE);
                notificationCountTV.setText(SharedPrefsUtils.getNotificationCount(this) + "");
            } else {
                notificationCountTV.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadNotificationsCount();
        registerNotificationReceiver();
    }

    @Override
    protected void onStop() {
        unregisterNotificationReceiver();
        super.onStop();
    }

    private void registerNotificationReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(INTENT_ACTION_NEW_NOTIFICATION);
            registerReceiver(receiver, filter);
        } catch (Exception e) {
        }
    }

    private void unregisterNotificationReceiver() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }
}
