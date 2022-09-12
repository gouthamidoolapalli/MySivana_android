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
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.custom.PhoneInputLayout;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.PlaceData;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.post.PostCreateUser;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.CryptoUtil;
import com.mysivana.util.DeviceInfo;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

import static com.mysivana.util.AppConstants.USER_TYPE_MERCHANT;
import static com.mysivana.util.AppConstants.USER_TYPE_SENDER_RECEIVER;

public class CreateAccountActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    private static final int PICK_LOCATION = 11;
    private static final int REQUEST_SIGNUP = 12;

    @BindView(R.id.view_merchant)
    LinearLayout mMerchantTab;
    @BindView(R.id.view_sender)
    LinearLayout mCustomerTab;
    @BindView(R.id.view_business)
    LinearLayout mBusinessLayout;
    @BindView(R.id.et_phone)
    PhoneInputLayout mETPhone;

    @BindView(R.id.et_businessname)
    EditText mBusinessName;
    @BindView(R.id.et_location)
    EditText mLocation;
    @BindView(R.id.et_fullname)
    EditText mFullName;
    @BindView(R.id.et_email)
    EditText mEmail;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.et_confirmpassword)
    EditText mConfirmPassword;
    @BindView(R.id.error_text)
    MSTextView mErrorText;

    private PlaceData placeData;

    private String userType = USER_TYPE_SENDER_RECEIVER;
    private PostCreateUser postCreateUser;
    private int selectedTab = 1;
    private String mIMEINumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_create_account;
    }

    @Override
    protected void init() {
        setSupportActionBar();
        mCustomerTab.setSelected(true);
        mLocation.setEnabled(false);

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_SIGN_UP, null);

        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        UIUtils.showCustomDialogForMoneyRequest(CreateAccountActivity.this, getString(R.string.want_to_be_merchant), getString(R.string.merchant_description), getString(R.string.proceed), getString(R.string.cancel), new UIUtils.DialogButtonClickListener() {

                    @Override
                    public void onClickButton(Dialog d, View v) {
                        d.dismiss();
                        setMerchantSettings();
                    }
                }
                , new UIUtils.DialogButtonClickListener() {
                    @Override
                    public void onClickButton(Dialog d, View v) {
                        d.dismiss();
                        setUserSettings();
                    }
                },null);
        requestPermissions();
    }

    public void requestPermissions() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            PermissionUtil.checkPermission(CreateAccountActivity.this, Manifest.permission.READ_PHONE_STATE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        mIMEINumber = DeviceInfo.getIMEI(CreateAccountActivity.this);
                    }
                }
            });
        } else {
            mIMEINumber = DeviceInfo.getIMEI(CreateAccountActivity.this);
        }
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }


    public void onClickSignIn(View view) {
        finish();
    }

    public void onClickSignUp(View view) {
        mErrorText.setVisibility(View.INVISIBLE);
        String businessName = mBusinessName.getText().toString();
        String location = mLocation.getText().toString();
        String fullname = mFullName.getText().toString();
        String email = mEmail.getText().toString().trim();
        String phone = UIUtils.formatIndianNumber(mETPhone.getRawInput());
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();

        if ((userType.equalsIgnoreCase(USER_TYPE_MERCHANT) && !TextUtils.isEmpty(businessName) && !TextUtils.isEmpty(location) && !TextUtils.isEmpty(fullname) &&
                !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(password))
                || (!userType.equalsIgnoreCase(USER_TYPE_MERCHANT) && !TextUtils.isEmpty(fullname) &&
                !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(password))) {
            if (mIMEINumber == null || mIMEINumber.isEmpty()) {
                requestPermissions();
                return;
            } else {
                mErrorText.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(businessName) && selectedTab == 2) {
                    mErrorText.setText(getString(R.string.name_validation));
                    return;
                } else if (TextUtils.isEmpty(location) && selectedTab == 2) {
                    mErrorText.setText(getString(R.string.location_validation));
                    return;
                } else if (TextUtils.isEmpty(fullname)) {
                    mErrorText.setText(getString(R.string.my_name_validation));
                    return;
                } else if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mErrorText.setText(getString(R.string.email_validation_message));
                    return;
                } else if (TextUtils.isEmpty(password) || password.length() < 6) {
                    mErrorText.setText(getString(R.string.validation_password_min_char));
                    return;
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    mErrorText.setText(getString(R.string.validation_cnfrm_pwd));
                    return;
                } else if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
                    mErrorText.setText(getString(R.string.validation_confirm_password));
                    return;
                } else if (TextUtils.isEmpty(phone) || (!TextUtils.isEmpty(phone) && !CommonUtils.isPhoneNumber(phone)) || phone.length() < 10) {
                    mErrorText.setText(getString(R.string.phone_validation_message));
                    return;
                }
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_EMAIL, email);
                b.putString(FirebaseConstants.PARAM_PHONE, mETPhone.getCountryCode() + phone);
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SIGN_UP_DETAILS, b);


                if (userType.equalsIgnoreCase(USER_TYPE_MERCHANT)) {
                    postCreateUser = new PostCreateUser();
                    postCreateUser.setBusinessName(businessName);
                    postCreateUser.setAddress(placeData.getAddress());
                    postCreateUser.setCountry(placeData.getCountry());
                    postCreateUser.setState(placeData.getState());
                    postCreateUser.setCity(placeData.getCity());
                    postCreateUser.setPincode(placeData.getPostalCode());
                    postCreateUser.setLatitude(String.valueOf(placeData.getLatLng().latitude));
                    postCreateUser.setLongitude(String.valueOf(placeData.getLatLng().longitude));
                } else {
                    postCreateUser = new PostCreateUser();
                }
                postCreateUser.setFullName(fullname);
                postCreateUser.setEmail(email);
                postCreateUser.setPhoneNumber(phone);
                postCreateUser.setCountryCode(mETPhone.getCountryCode());
                postCreateUser.setActivityType(AppConstants.ActivityType.SIGN_UP);
                postCreateUser.setUserType(userType);
                postCreateUser.setOsVersion(DeviceInfo.getOSVersion());
                postCreateUser.setApiVersion(DeviceInfo.getAppVersion(this));
                postCreateUser.setDeviceBrand(DeviceInfo.getBuildBrand());
                postCreateUser.setDeviceId(mIMEINumber);
                postCreateUser.setPassword(CryptoUtil.encryptWithMD5(password));
                String referralCode = SharedPrefsUtils.getReferrerCode(this);
                if (referralCode != null && !TextUtils.isEmpty(referralCode))
                    postCreateUser.setReferredByCode(referralCode);
                if (NetworkUtil.getConnectivityStatus(this)) {
                    mErrorText.setVisibility(View.INVISIBLE);
                    mPresenter.checkUserExists(email, phone, AppConstants.ActivityType.SIGN_UP);
                } else {
                    mErrorText.setVisibility(View.VISIBLE);
                    mErrorText.setText(getString(R.string.internet_connection_unavailable));
                }

            }
        } else {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.all_fields_mandatory));
            return;
        }
    }

    public void onClickPickLocation(View view) {
        CommonUtils.startActivityForResult(this, PickLocationActivity.class, PICK_LOCATION);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        UserResponse genericResponse = (UserResponse) object;
        int code = genericResponse.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:

                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.BundleParams.CREATE_USER, postCreateUser);
                CommonUtils.startActivityForResult(this, OTPVerifyActivity.class, bundle, REQUEST_SIGNUP);
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                mErrorText.setText(genericResponse.getErrorDescription());
                mErrorText.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onDataFailure(Throwable e) {
        mErrorText.setVisibility(View.VISIBLE);

        if (e instanceof HttpException) {
            //HTTP exceptions of non 200 type.
            int code = ((HttpException) e).code();
            if (code == 500) {
                if (errorCounter >= 5) {
                    submitTicket = new SubmitTicket(this, code + ", " + ((HttpException) e).response().toString());
                    submitTicket.execute(0);
                    errorCounter = 0;
                } else {
                    mErrorText.setText(getString(R.string.internal_server_error));
                    errorCounter++;
                }
                return;

            } else if (code == 401) {
                //Bad credentials error
                logOutFromApp();
                return;
            }
            mErrorText.setText(getString(R.string.internal_server_error));
        } else if (e instanceof SocketTimeoutException) {
            //connection establishment failure exception (might have slow internet or server unavailable)
            mErrorText.setText(getString(R.string.something_wrong_try_again));
        } else if (e instanceof IOException) {
            //called when there's network problem
            mErrorText.setText(getString(R.string.internet_connection_unavailable));
        } else {
            //other messages
            mErrorText.setText(e.getMessage());
        }
    }

    /**
     * Tab click is handled here
     *
     * @param v view of the tab
     */
    public void onTabClick(View v) {
        switch (v.getId()) {
            case R.id.view_merchant:
                setMerchantSettings();
                break;
            case R.id.view_sender:
                setUserSettings();
                break;
        }

    }

    private void setUserSettings() {
        userType = USER_TYPE_SENDER_RECEIVER;
        mMerchantTab.setSelected(false);
        mCustomerTab.setSelected(true);
        mBusinessLayout.setVisibility(View.GONE);
        mFullName.requestFocus();
        selectedTab = 1;
        clearData();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SING_UP_USER, new Bundle());
    }

    private void setMerchantSettings() {
        userType = USER_TYPE_MERCHANT;
        mMerchantTab.setSelected(true);
        mCustomerTab.setSelected(false);
        mBusinessLayout.setVisibility(View.VISIBLE);
        mBusinessName.requestFocus();
        selectedTab = 2;
        clearData();

        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SIGN_UP_MERCHANT, new Bundle());
    }

    private void clearData() {
        mErrorText.setVisibility(View.INVISIBLE);
        mBusinessName.setText("");
        mLocation.setText("");
        mFullName.setText("");
        mEmail.setText("");
        mETPhone.getEditText().setText("");
        mPassword.setText("");
        mConfirmPassword.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                placeData = data.getParcelableExtra(AppConstants.BundleParams.PLACE);
                mLocation.setText(placeData.getAddress());
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(placeData.getLatLng().latitude, placeData.getLatLng().longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && addresses.size() > 0) {
                    String premises = addresses.get(0).getPremises();
                    String subThoroughfare = addresses.get(0).getSubThoroughfare();
                    String subLocality = addresses.get(0).getSubLocality();
                    placeData.setSubAddress(premises != null ? premises + ", " : "" +
                            subThoroughfare != null ? subThoroughfare + "," : "" +
                            subLocality != null ? subLocality : "");
                    placeData.setCity(addresses.get(0).getLocality());
                    placeData.setState(addresses.get(0).getAdminArea());
                    placeData.setPostalCode(addresses.get(0).getPostalCode());
                    placeData.setCountry(addresses.get(0).getCountryName());
                }
            }
        } else if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                String error = data.getStringExtra(AppConstants.BundleParams.ERROR);
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(error);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(CreateAccountActivity.this, requestCode, permissions, grantResults);
    }
}
