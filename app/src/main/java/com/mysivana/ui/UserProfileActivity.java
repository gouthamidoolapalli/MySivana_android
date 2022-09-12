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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.CircleImageView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.post.PostCreateUser;
import com.mysivana.mvp.model.post.PostUserLogin;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.DeviceInfo;
import com.mysivana.util.FileHelper;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.GetProfileImage;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PathUtils;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.mysivana.util.AppConstants.USER_TYPE_MERCHANT;

public class UserProfileActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    private static final int REQUEST_ADD_BANK_DETAILS = 11;

    @BindView(R.id.view_business)
    LinearLayout mBusinessLayout;
    @BindView(R.id.view_bank)
    LinearLayout mBankLayout;
    @BindView(R.id.tv_business_name)
    EditText mBusinessName;
    @BindView(R.id.tv_business_address)
    EditText mBusinessAddress;
    @BindView(R.id.tv_full_name)
    EditText mFullName;
    @BindView(R.id.tv_customer_email)
    EditText mEmail;
    @BindView(R.id.tv_phone)
    EditText mPhone;
    @BindView(R.id.tv_password)
    EditText mPassword;
    @BindView(R.id.tv_bank_info)
    EditText mBankInfo;
    @BindView(R.id.bank_details_layout)
    LinearLayout bankDetailsLayout;
    @BindView(R.id.add_bank_details)
    TextView addBankDetails;
    @BindView(R.id.change_profile_img)
    TextView changeProfileImg;
    @BindView(R.id.profile_img)
    CircleImageView mProfileImg;
    @BindView(R.id.phone_verify_text)
    TextView mPhoneVerifyText;
    @BindView(R.id.email_verify_text)
    TextView mEmailVerifyText;
    @BindView(R.id.details_layout)
    LinearLayout detailsLayout;
    @BindView(R.id.bank_balance_layout)
    LinearLayout mBankBalanceLayout;
    @BindView(R.id.tv_bank_balance)
    EditText mTVBankBalance;
    @BindView(R.id.tv_siva_tokens)
    EditText mSivaTokesnTV;

    private PostCreateUser postCreateUser;
    public static final String CHANGE_PASSWORD = "Change Password";
    private String mIMEINumber;
    public static int REQUEST_CAMERA = 4;
    private static int REQUEST_GALLERY = 5;
    private UserResponse userResponse;
    private static final int GET_PROFILE_REQUEST = 301;
    private static final int VERIFY_EMAIL_REQUEST = 302;
    private static final int UPLOAD_IMAGE_REQUEST = 303;
    public static final String PROFILE_PIC_CHANGED = "PROFILE_PIC_CHANGED";
    private boolean isPictureChanged = false;
    private Bitmap bitmap;
    private Uri fileUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_user_profile;
    }

    /**
     * request for read phone permission
     */
    public void requestReadPhonePermissions() {
        PermissionUtil.checkPermission(UserProfileActivity.this, Manifest.permission.READ_PHONE_STATE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onResult(boolean success) {
                if (success) {
                    mIMEINumber = DeviceInfo.getIMEI(UserProfileActivity.this);
                    loadDetails();
                } else {
                    loadDetails();
                }
            }
        });
    }

    /**
     * request for SD card permission to access gallery images from SD card
     */
    public void requestSDCardPermissions() {
        if (!PermissionUtil.hasPermission(UserProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionUtil.checkPermission(UserProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        showDialogForSelection();
                    }
                }
            });
        } else {
            showDialogForSelection();
        }
    }

    @Override
    protected void init() {

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_USER_PROFILE, null);
        setSupportActionBar();
        mBusinessName.setEnabled(false);
        mBusinessAddress.setEnabled(false);
        mFullName.setEnabled(false);
        mEmail.setEnabled(false);
        mPassword.setEnabled(false);
        mBankInfo.setEnabled(false);
        mPhone.setEnabled(false);
        mTVBankBalance.setEnabled(false);
        mProfileImg.setImageResource(R.drawable.ic_user_avatar);
        detailsLayout.setVisibility(View.GONE);
        mBankBalanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowBalanceDetails(v);
            }
        });
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        requestReadPhonePermissions();

    }


    /**
     * Render details on UI
     */
    private void loadDetails() {
        renderDetails();
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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }


    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        hideLoading();
        switch (apiRequestCode) {
            case GET_PROFILE_REQUEST:
                UserResponse userResponseObj = (UserResponse) object;
                int code = userResponseObj.getErrorCode();
                switch (code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        UserResponse.Value value = userResponseObj.getValue();
                        value.setOsVersion(DeviceInfo.getOSVersion());
                        value.setApiVersion(DeviceInfo.getAppVersion(this));
                        value.setDeviceBrand(DeviceInfo.getBuildBrand());
                        value.setDeviceId(mIMEINumber);
                        SharedPrefsUtils.saveLoggedUserObject(this, userResponseObj);
                        renderDetails();

                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToast(UserProfileActivity.this, userResponse.getErrorDescription());
                        break;
                }
                break;
            case VERIFY_EMAIL_REQUEST:
                GenericResponse genericResponse = (GenericResponse) object;
                int responseCode = genericResponse.getErrorCode();
                switch (responseCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (genericResponse.getValue() != null)
                            UIUtils.showToast(UserProfileActivity.this, genericResponse.getValue());

                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToast(UserProfileActivity.this, genericResponse.getErrorDescription());
                        break;
                }
                break;
            case UPLOAD_IMAGE_REQUEST:
                GenericResponse response = (GenericResponse) object;
                int responseErrorCode = response.getErrorCode();
                switch (responseErrorCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        isPictureChanged = true;
                        userResponse.getValue().setImageUrl(response.getValue());
                        SharedPrefsUtils.saveLoggedUserObject(UserProfileActivity.this, userResponse);
                        mProfileImg.setImageBitmap(bitmap);

                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToast(UserProfileActivity.this, response.getErrorDescription());
                        break;
                }
                break;
        }
    }

    private void renderDetails() {

        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        detailsLayout.setVisibility(View.VISIBLE);
        postCreateUser = new PostCreateUser();
        postCreateUser.setUserID(String.valueOf(userResponse.getValue().getUserId()));
        postCreateUser.setUserApiKey(userResponse.getValue().getUserApiKey());
        postCreateUser.setEmail(userResponse.getValue().getEmail());
        postCreateUser.setPhoneNumber(userResponse.getValue().getPhoneNumber());
        postCreateUser.setCountryCode(userResponse.getValue().getCountryCode());
        postCreateUser.setOsVersion(DeviceInfo.getOSVersion());
        postCreateUser.setApiVersion(DeviceInfo.getAppVersion(this));
        postCreateUser.setDeviceBrand(DeviceInfo.getBuildBrand());
        postCreateUser.setDeviceId(mIMEINumber);
        postCreateUser.setActivityType(AppConstants.ActivityType.CHANGE_PASSWORD);
        if (TextUtils.isEmpty(userResponse.getValue().getBankDetailsAvailable()) ||
                (!TextUtils.isEmpty(userResponse.getValue().getBankDetailsAvailable()) &&
                        userResponse.getValue().getBankDetailsAvailable().equalsIgnoreCase("0"))) {
            addBankDetails.setVisibility(View.VISIBLE);
            bankDetailsLayout.setVisibility(View.GONE);
        } else {
            addBankDetails.setVisibility(View.GONE);
            bankDetailsLayout.setVisibility(View.VISIBLE);
        }
        if (userResponse.getValue().getUserType() != null &&
                userResponse.getValue().getUserType().equalsIgnoreCase(USER_TYPE_MERCHANT)) {
            mBusinessLayout.setVisibility(View.VISIBLE);
            mBusinessName.setText(userResponse.getValue().getBusinessName());
            mBusinessAddress.setText(userResponse.getValue().getAddress());
            float bankBalance = userResponse.getValue().getBankBalance();
            if (bankBalance == 0.0) {
                mTVBankBalance.setText("INR 0.00");
            } else {
                mTVBankBalance.setText("INR " + String.format("%.2f", bankBalance));
            }
        } else {
            mBusinessLayout.setVisibility(View.GONE);
            mBankBalanceLayout.setVisibility(View.GONE);

        }

        String token = userResponse.getValue().getSivaToken();
        if (token != null && !token.isEmpty())
            mSivaTokesnTV.setText(token + "");
        else
            mSivaTokesnTV.setText("0");

        mFullName.setText(userResponse.getValue().getFullName());
        mEmail.setText(userResponse.getValue().getEmail());
        String countryCode = userResponse.getValue()
                .getCountryCode();
        String phoneNumber = (countryCode != null && !countryCode.isEmpty() && countryCode.equalsIgnoreCase("+1")) ?
                UIUtils.formatUSNumber(userResponse.getValue().getPhoneNumber()) : userResponse.getValue().getPhoneNumber();
        mPhone.setText(countryCode + " " + phoneNumber);

        String emailVerified = userResponse.getValue().getIsEmailVerified();
        if (emailVerified != null && !emailVerified.isEmpty() && emailVerified.equalsIgnoreCase("1"))
            mEmailVerifyText.setText(getString(R.string.verified));
        else {
            String s = getString(R.string.click_here_to_verify);
            SpannableString spannableString = new SpannableString(s);
            spannableString.setSpan(
                    new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            //log Analytics
                            Bundle b = new Bundle();
                            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                            mFirebaseAnalytics.logEvent(FirebaseConstants.VERIFY_EMAIL, b);

                            UIUtils.showCustomDialog(UserProfileActivity.this, UIUtils.getStringFromResource(UserProfileActivity.this, R.string.warning_verify_email), UIUtils.getStringFromResource(UserProfileActivity.this, R.string.verify_message), UIUtils.getStringFromResource(UserProfileActivity.this, R.string.proceed),
                                    UIUtils.getStringFromResource(UserProfileActivity.this, R.string.cancel), positiveCallback, negativeCallback);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(getResources().getColor(R.color.error));
                            ds.setUnderlineText(true);
                        }
                    }, 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            mEmailVerifyText.setText(spannableString);

            mEmailVerifyText.setMovementMethod(LinkMovementMethod.getInstance());
        }

        String phoneVerified = userResponse.getValue().getIsPhoneVerified();
        if (phoneVerified != null && !phoneVerified.isEmpty() && phoneVerified.equalsIgnoreCase("1"))
            mPhoneVerifyText.setText(getString(R.string.verified));
        else
            mPhoneVerifyText.setText(getString(R.string.click_here_to_verify));

        String imageUrl = userResponse.getValue().getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {

            if (FileHelper.isProfilePictureAvailable(UserProfileActivity.this, userResponse.getValue().getUserId())) {
                Bitmap bm = FileHelper.getProfilePicture(UserProfileActivity.this, userResponse.getValue().getUserId());
                mProfileImg.setImageBitmap(bm);
            } else {
                new GetProfileImage(this, userResponse.getValue().getImageUrl(), mProfileImg, userResponse.getValue().getUserId()).execute();
            }
//            Picasso.with(UserProfileActivity.this).load(userResponse.getValue().getImageUrl()).resize(80, 80)
//                    .placeholder(R.drawable.ic_user_avatar).into(mProfileImg);
        } else {
            mProfileImg.setImageResource(R.drawable.ic_user_avatar);
        }

    }

    UIUtils.DialogButtonClickListener positiveCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            showLoading();
            mPresenter.verifyEmail(userResponse.getValue().getUserId(), userResponse.getValue().getUserApiKey(), VERIFY_EMAIL_REQUEST);
        }
    };
    UIUtils.DialogButtonClickListener negativeCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
        }
    };

    @Override
    public void onDataFailure(Throwable e) {
        e.printStackTrace();
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
            showMessage(getString(R.string.something_wrong_try_again));
        } else if (e instanceof IOException) {
            //called when there's network problem
            showMessage(getString(R.string.something_wrong_try_again));
        } else {
            //other messages
            showMessage(e.getMessage());
        }
    }


    public void onEditPasswordClick(View view) {

        //log Analytics
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_CHANGE_PWD, b);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BundleParams.CREATE_USER, postCreateUser);
        bundle.putString(AppConstants.NAVIGATE_FROM, CHANGE_PASSWORD);
        CommonUtils.startActivity(UserProfileActivity.this, NewPasswordActivity.class, bundle);
    }

    public void onBankInfoClicked(View view) {
        //log Analytics
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_VIEW_BANK_INFO, b);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BundleParams.CREATE_USER, postCreateUser);
        CommonUtils.startActivityForResult(UserProfileActivity.this, AddBankDetailsActivity.class, bundle, REQUEST_ADD_BANK_DETAILS);
    }

    public void onShowBalanceDetails(View view) {
        //log Analytics
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_VIEW_BALANCE, b);

        CommonUtils.startActivity(UserProfileActivity.this, WalletBalanceActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_BANK_DETAILS) {
                loadDetails();
            } else if (requestCode == REQUEST_CAMERA) {
                showLoading();
                isPictureChanged = true;
                getCapturedImage(data);
            } else if (requestCode == REQUEST_GALLERY) {
                showLoading();
                isPictureChanged = true;
                getGalleryImage(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Access the image from gallery and sets as profile picture
     *
     * @param data Intent object which has the details of selected image
     */
    private void getGalleryImage(Intent data) {
        if (data != null) {
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (bm.getByteCount() > 2000) {
                    bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                } else {
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                }
                byte myImageBytes[] = baos.toByteArray();
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), myImageBytes);
                MultipartBody.Part body = MultipartBody.Part.createFormData("user_dp", "IMG_" + userResponse.getValue().getUserId() + ".jpg", requestFile);
                RequestBody requestUserID = RequestBody.create(MediaType.parse("text/plain"), userResponse.getValue().getUserId());
                RequestBody requestUserAPI = RequestBody.create(MediaType.parse("text/plain"), userResponse.getValue().getUserApiKey());
                showLoading();
                bitmap = FileHelper.modifyOrientation(bm, PathUtils.getPath(UserProfileActivity.this, data.getData()));
                FileHelper.saveProfilePicture(this, bitmap, userResponse.getValue().getUserId());
                mPresenter.uploadImage(body, requestUserAPI, requestUserID, UPLOAD_IMAGE_REQUEST);
//                mProfileImg.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Access the captured image and set as profile picture
     *
     * @param data Intent object which has the details of selected image
     */
    private void getCapturedImage(final Intent data) {
        PermissionUtil.checkPermission(UserProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
            @Override
            public void onResult(boolean success) {


                try {
                    Bitmap bm = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (bm.getByteCount() > 2000) {
                        bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    } else {
                        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    }
                    byte myImageBytes[] = baos.toByteArray();
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), myImageBytes);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("user_dp", "IMG_" + userResponse.getValue().getUserId() + ".jpg", requestFile);
                    RequestBody requestUserID = RequestBody.create(MediaType.parse("text/plain"), userResponse.getValue().getUserId());
                    RequestBody requestUserAPI = RequestBody.create(MediaType.parse("text/plain"), userResponse.getValue().getUserApiKey());
                    showLoading();
                    bitmap = bm;
                    FileHelper.saveProfilePicture(UserProfileActivity.this, bitmap, userResponse.getValue().getUserId());
                    mPresenter.uploadImage(body, requestUserAPI, requestUserID, UPLOAD_IMAGE_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onChangeProfilePicture(View v) {
        //log Analytics
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_CHANGE_DP, b);

        if (NetworkUtil.getConnectivityStatus(this))
            requestSDCardPermissions();
        else
            showMessage(getString(R.string.internet_check));
    }

    /**
     * Show user with selection of images from various places in device.
     */
    private void showDialogForSelection() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_camera_chooser);

        ImageView mCancel = dialog.findViewById(R.id.cancel_btn);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView mCameraTxt = dialog.findViewById(R.id.camera_text);
        mCameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PermissionUtil.checkPermission(UserProfileActivity.this, Manifest.permission.CAMERA, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                    @Override
                    public void onResult(boolean success) {
                        openCamera();
                    }
                });

            }
        });

        TextView mGalleryTxt = dialog.findViewById(R.id.gallery_text);
        mGalleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGallery();
            }
        });


        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * forces the app to open gallery
     */
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_GALLERY);
    }

    /**
     * forces the app to open camera
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (!isPictureChanged)
            super.onBackPressed();
        else {
            Intent i = new Intent();
            i.putExtra(PROFILE_PIC_CHANGED, isPictureChanged);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    /**
     * redirects to leaderboard
     * @param v view
     */
    public void onShowLeaderBoard(View v){
        CommonUtils.startActivity(this, LeaderboardActivity.class);
    }
}
