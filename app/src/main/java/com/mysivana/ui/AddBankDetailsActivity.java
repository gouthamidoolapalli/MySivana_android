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
 * MAY 07 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 * <p>
 * ========================================================================
 */
package com.mysivana.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.EditTextDebounce;
import com.mysivana.custom.MSEditText;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.BankDetails;
import com.mysivana.mvp.model.BankNameResponse;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.ViewBankDetailsResponse;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.CryptoUtil;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddBankDetailsActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.screen_title)
    MSTextView mScreenTitle;
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
    @BindView(R.id.et_upi_addr)
    MSEditText mEtUpiAddr;
    @BindView(R.id.root_layout)
    CoordinatorLayout mRootLayout;
    @BindView(R.id.save_btn)
    Button mSaveBtn;
    @BindView(R.id.edit_details_btn)
    ImageView mEditBtn;
    @BindView(R.id.error_text)
    TextView mErrorText;
    @BindView(R.id.details_layout)
    LinearLayout mDetailsLayout;

    private BankDetails bankDetails;
    private UserResponse.Value value;
    private static final int GET_BANK_DETAILS = 21;
    private static final int SAVE_BANK_DETAILS = 22;
    private static final int GET_BANK_NAME = 23;
    private String ifscCode, bankName, bankBranch;

    private BankNameResponse bankNameResponse;


    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_add_bank_details;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_BANK_DETAILS, null);

        setSupportActionBar();
        value = SharedPrefsUtils.getLoggedUserObject(this).getValue();
        if (TextUtils.isEmpty(value.getBankDetailsAvailable()) ||
                (!TextUtils.isEmpty(value.getBankDetailsAvailable()) &&
                        value.getBankDetailsAvailable().equalsIgnoreCase("0"))) {
            //add bank details
            mScreenTitle.setText(UIUtils.getStringFromResource(AddBankDetailsActivity.this, R.string.add_bank_details));
            mEtAccNum.setEnabled(true);
            mEtIfscCode.setEnabled(true);
            mEtUpiAddr.setEnabled(true);
//            mEtAccountType.setEnabled(true);
            mSaveBtn.setVisibility(View.VISIBLE);
            mEditBtn.setVisibility(View.GONE);
        } else {
            if (NetworkUtil.getConnectivityStatus(this)) {
                mErrorText.setVisibility(View.INVISIBLE);
                if (NetworkUtil.getConnectivityStatus(AddBankDetailsActivity.this))
                    mPresenter.getBankDetails(value.getUserId(), value.getUserApiKey(), GET_BANK_DETAILS);
                else
                    showMessage(getString(R.string.internet_check));
            } else {
                mEtAccNum.setEnabled(false);
                mEtIfscCode.setEnabled(false);
                mEtUpiAddr.setEnabled(false);
//                mEtAccountType.setEnabled(false);
                mEtAccNum.setFocusable(false);
                mEtIfscCode.setFocusable(false);
                mEtUpiAddr.setFocusable(false);
//                mEtAccountType.setFocusable(false);
                mSaveBtn.setVisibility(View.GONE);
                mEditBtn.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.internet_connection_unavailable));
            }
        }


        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log Analytics
                UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(AddBankDetailsActivity.this);
                Bundle bun = new Bundle();
                bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_EDIT_BANK_DETAILS, bun);

                mEtIfscCode.setFocusable(true);
                mEtAccNum.setFocusable(true);
                mEtUpiAddr.setFocusable(true);
//                mEtAccountType.setFocusable(true);
                mEtIfscCode.setFocusableInTouchMode(true);
                mEtAccNum.setFocusableInTouchMode(true);
                mEtUpiAddr.setFocusableInTouchMode(true);
//                mEtAccountType.setFocusableInTouchMode(true);
                mEtIfscCode.setEnabled(true);
                mEtAccNum.setEnabled(true);
//                mEtAccountType.setEnabled(true);
                mEtUpiAddr.setEnabled(true);
                mSaveBtn.setVisibility(View.VISIBLE);
            }
        });
        EditTextDebounce.create(mEtIfscCode).watch(new EditTextDebounce.DebounceCallback() {
            @Override
            public void onFinished(@NonNull String result) {
                if ((ifscCode != null && !ifscCode.isEmpty() && !mEtIfscCode.getText().toString().equalsIgnoreCase(ifscCode)
                        && mScreenTitle.getText().toString().equalsIgnoreCase(getString(R.string.view_bank_details))) || (ifscCode == null && !mEtIfscCode.getText().toString().isEmpty())) {
                    //do the API call for showing bank name while edit/ adding
                    BankDetails bankDetails = new BankDetails();
                    bankDetails.setUserId(Integer.parseInt(value.getUserId()));
                    bankDetails.setUserApiKey(value.getUserApiKey());
                    bankDetails.setBankCode(mEtIfscCode.getText().toString());
                    if (NetworkUtil.getConnectivityStatus(AddBankDetailsActivity.this)) {
                        if (mEtIfscCode.getText().length() == 11) {
                            if (CommonUtils.isIFSCCode(mEtIfscCode.getText().toString())) {
                                mErrorText.setVisibility(View.INVISIBLE);
                                mPresenter.getBankName(bankDetails, GET_BANK_NAME);
                            } else {
                                mErrorText.setVisibility(View.VISIBLE);
                                mErrorText.setText(getString(R.string.validation_bankcode_proper));
                            }
                        }
                    } else
                        showMessage(getString(R.string.internet_check));
                } else if (ifscCode != null && mEtIfscCode.getText().toString().equalsIgnoreCase(ifscCode)) {
                    mEtBankName.setText(bankName);
                }
            }
        }, 3000);
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        hideLoading();
        mErrorText.setVisibility(View.INVISIBLE);
        switch (apiRequestCode) {
            case GET_BANK_DETAILS:

                ViewBankDetailsResponse response = (ViewBankDetailsResponse) object;
                int code = response.getErrorCode();
                switch (code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (response.getValue() != null && response.getValue() instanceof BankDetails) {

                            BankDetails details = (BankDetails) response.getValue();
                            bankDetails = new BankDetails();
                            bankDetails.setBankAddress(details.getBankAddress());
                            bankDetails.setBankCity(details.getBankCity());
                            if (TextUtils.isEmpty(details.getAccountNumber())) {
                                return;
                            }
                            //view bank details
                            mScreenTitle.setText(UIUtils.getStringFromResource(AddBankDetailsActivity.this, R.string.view_bank_details));
                            mEtAccNum.setEnabled(false);
                            mEtIfscCode.setEnabled(false);
                            mEtUpiAddr.setEnabled(false);
//                            mEtAccountType.setEnabled(false);
                            mEtAccNum.setFocusable(false);
                            mEtIfscCode.setFocusable(false);
                            mEtUpiAddr.setFocusable(false);
//                            mEtAccountType.setFocusable(false);
                            mSaveBtn.setVisibility(View.GONE);
                            mEditBtn.setVisibility(View.VISIBLE);

                            mEtAccNum.append(details.getAccountNumber());
                            bankName = details.getBankName();
                            mEtBankName.setText(bankName);
                            bankBranch = details.getBankBranch();
                            mETBankBranch.setText(bankBranch);
                            mEtUpiAddr.setText(details.getUpiAddress());
//                            mEtAccountType.setText(details.getAccountType());

                            ifscCode = details.getBankCode();
                            mEtIfscCode.setText(ifscCode);


                        }

                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        mErrorText.setVisibility(View.VISIBLE);
                        mErrorText.setText(response.getErrorDescription());
                        break;
                }

                break;
            case SAVE_BANK_DETAILS:
                GenericResponse _response = (GenericResponse) object;
                int _code = _response.getErrorCode();
                switch (_code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:

                        SharedPrefsUtils.saveBankDetailsDone(this);
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        mErrorText.setVisibility(View.VISIBLE);
                        mErrorText.setText(_response.getErrorDescription());
                        break;
                }

                break;

            case GET_BANK_NAME:
                bankNameResponse = (BankNameResponse) object;
                int errorCode = bankNameResponse.getErrorCode();
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
                        mErrorText.setVisibility(View.VISIBLE);
                        mErrorText.setText(bankNameResponse.getErrorDescription());
                        break;
                }
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

            } else if (code == 401) {
                //Bad credentials error
                logOutFromApp();
            }
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    /**
     * Checks for all possible user level validation, if any failures appropriate
     * message is displayed to the user and if all fine then take the user to dialog
     * box to key in the password to submit request to server for processing.
     *
     * @param view
     */
    public void onClickSave(View view) {
        if (TextUtils.isEmpty(mEtBankName.getText().toString()) &&
                TextUtils.isEmpty(mEtAccNum.getText().toString()) &&
                TextUtils.isEmpty(mEtIfscCode.getText().toString())) {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.validation_blank));
            return;
        } else if (TextUtils.isEmpty(mEtAccNum.getText().toString())) {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.validation_accountnumber));
            return;
        } else if (TextUtils.isEmpty(mEtBankName.getText().toString())) {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.validation_bankname));
            return;
        } else if (TextUtils.isEmpty(mEtIfscCode.getText().toString()) || mEtIfscCode.getText().length() != 11) {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.validation_bankcode));
            return;
        }
//        else if (TextUtils.isEmpty(mEtAccountType.getText().toString())) {
//            mErrorText.setVisibility(View.VISIBLE);
//            mErrorText.setText(getString(R.string.validation_acctype));
//            return;
//        } else if (!mEtAccountType.getText().toString().equalsIgnoreCase(getString(R.string.account_type_savings)) &&
//                !mEtAccountType.getText().toString().equalsIgnoreCase(getString(R.string.account_type_current))) {
//            mErrorText.setVisibility(View.VISIBLE);
//            mErrorText.setText(getString(R.string.validation_acctype_notvalid));
//            return;
//        }

        //log Analytics
        UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        Bundle bun = new Bundle();
        bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SAVE_BANK_DETAILS, bun);

        // calling show dialog for entering password with custom title
        showDialog(UIUtils.getStringFromResource(AddBankDetailsActivity.this, R.string.add_bank_details));
    }

    /**
     * Show confirmation dialog to confirm the bank details using password.
     *
     * @param stringFromResource
     */
    private void showDialog(String stringFromResource) {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_bank_details);
        final EditText password = dialog.findViewById(R.id.et_password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final TextView errorText = dialog.findViewById(R.id.error_text);
        Button proceed = dialog.findViewById(R.id.proceed_btn);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().isEmpty()) {

                    //log Analytics
                    UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(AddBankDetailsActivity.this);
                    Bundle bun = new Bundle();
                    bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                    mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_CONFIRM_PASSWORD, bun);

                    sendBankDetails(password.getText().toString());
                    dialog.dismiss();
                } else {
                    errorText.setText(getString(R.string.dialog_window_warning));
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });
        ImageView cancel = dialog.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView title = dialog.findViewById(R.id.header_title);
        title.setText(stringFromResource);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * method to set the bankdetails to model to calling server api
     * take password as input from dialog box and rest will be taken from the
     * add / update bank details screen
     *
     * @param password
     */
    private void sendBankDetails(String password) {
        if(bankDetails == null){
            bankDetails = new BankDetails();
        }
        bankDetails.setUserId(Integer.parseInt(value.getUserId()));
        bankDetails.setUserApiKey(value.getUserApiKey());
        bankDetails.setTransactionFlag(false);
        if (bankNameResponse != null) {
            bankDetails.setBankAddress(bankNameResponse.getValue().getBankAddress());
            bankDetails.setBankCity(bankNameResponse.getValue().getBankCity());
        }
        bankDetails.setPassword(CryptoUtil.encryptWithMD5(password));

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

        String upiAddr = mEtUpiAddr.getText().toString();
        bankDetails.setUpiAddress(upiAddr);
        if (NetworkUtil.getConnectivityStatus(this))
            mPresenter.saveBankDetails(bankDetails, SAVE_BANK_DETAILS);
        else
            showMessage(getString(R.string.internet_check));
    }


}
