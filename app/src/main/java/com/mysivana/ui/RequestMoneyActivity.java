package com.mysivana.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSButton;
import com.mysivana.custom.MSEditText;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.Country;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.adapters.CountriesAdapter;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.Countries;
import com.mysivana.util.DeviceInfo;
import com.mysivana.util.FileHelper;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PathUtils;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.SingleShotLocationProvider;
import com.mysivana.util.UIUtils;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.mysivana.util.AppConstants.DEPOSIT_BANK;
import static com.mysivana.util.AppConstants.DEPOSIT_CASH;
import static com.mysivana.util.AppConstants.USER_TYPE_MERCHANT;

public class RequestMoneyActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {
    private static final int SELECT_PHONE_NUMBER = 12;
    public static final int REQUEST_CAMERA = 4;
    private static final int REQUEST_GALLERY = 5;
    public static final int REQUEST_DOCUMENT = 6;

    @BindView(R.id.tv_business_name)
    MSTextView tvBusinessName;
    @BindView(R.id.et_rupee_value)
    MSEditText etRupeeValue;
    @BindView(R.id.et_phone)
    MSEditText etPhone;
    @BindView(R.id.et_notes)
    MSEditText etNotes;
    @BindView(R.id.phone_email_layout)
    LinearLayout phoneEmailLayout;
    @BindView(R.id.btn_pay_cash)
    MSButton btnPayCash;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.attachment_text)
    MSTextView attachmentText;
    @BindView(R.id.iv_deposit)
    ImageView mIvDeposit;
    @BindView(R.id.ll_deposit)
    LinearLayout llDeposit;

    private UserResponse userResponse;
    private Country mCountry;
    private Uri uri;
    private RequestBody requestUserID, requestUserAPI, requestNotes, requestAmount, requestPhoneNum, requestCountryCode, requestDepositType;
    private MultipartBody.Part body;
    private String mimeType;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_request_money;
    }


    @Override
    protected void init() {
        setSupportActionBar();
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_REQUEST_MONEY, null);
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);


        mSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
        final CountriesAdapter adapter = new CountriesAdapter(getContext(), Countries.COUNTRIES);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCountry = adapter.getItem(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCountry = null;
            }
        });

        mIvDeposit.setTag(DEPOSIT_BANK);
        llDeposit.setEnabled(true);
        if (userResponse.getValue().getUserType().equalsIgnoreCase(AppConstants.USER_TYPE_MERCHANT)) {
            tvBusinessName.setText(userResponse.getValue().getBusinessName());
        } else {
            if (SingleShotLocationProvider.isGPSEnabled(this)) {
                requestPermissions();

            } else {
                promptGPSDialog();
            }
        }
    }


    private void hideKeyboard() {
        ((InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etPhone.getWindowToken(), 0);
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

    /**
     * Location is set using auto-update location while typing
     */
    public void setBusinessNameAsLocation() {
        SingleShotLocationProvider.requestSingleUpdate(RequestMoneyActivity.this, new SingleShotLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                Geocoder gcd = new Geocoder(RequestMoneyActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(location.latitude, location.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        tvBusinessName.setText(addresses.get(0).getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        GenericResponse response = (GenericResponse) object;
        int code = response.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                UIUtils.showCustomDialogForMoneyRequest(this, UIUtils.getStringFromResource(this, R.string.request_initiated), UIUtils.getStringFromResource(this, R.string.request_money_processed),
                        UIUtils.getStringFromResource(this, R.string.contact_now), UIUtils.getStringFromResource(this, R.string.share_now), contactCallback, shareCallback,
                        negativeCallback);
                break;
            case AppConstants.USER_DOES_NOT_EXIST:
                UIUtils.showCustomDialogForMoneyRequest(this, UIUtils.getStringFromResource(this, R.string.user_not_exists_heading), UIUtils.getStringFromResource(this, R.string.user_is_not_my_sivana_user),
                        UIUtils.getStringFromResource(this, R.string.share_now), null, shareCallback, null,
                        negativeCallback);
                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                if (response.getValue() != null)
                    UIUtils.showToastOnDashboard(this, response.getValue());
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

    public void OnClickContacts(View v) {
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_OPEN_CONTACTS, b);

        if (etPhone.isFocused()) {
            UIUtils.hideKeyboard(this, etPhone);
        } else {
            UIUtils.hideKeyboard(this, etRupeeValue);
        }

        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, SELECT_PHONE_NUMBER);
    }


    private boolean validateTextFields() {
        if (etRupeeValue.getText().toString().isEmpty()) {
            UIUtils.showToastOnDashboard(this, getString(R.string.rupee_validation));
            return false;
        } else if (Integer.parseInt(etRupeeValue.getText().toString()) > 49999) {
            UIUtils.showToastOnDashboard(this, getString(R.string.cannot_initiate));
            return false;
        } else if (etPhone.getText().toString().isEmpty() || etPhone.getText().toString().length() < 10) {
            UIUtils.showToastOnDashboard(this, getString(R.string.phone_validation_message));
            return false;
        } else if (etPhone.getText().toString().equalsIgnoreCase(userResponse.getValue().getPhoneNumber())) {
            UIUtils.showToastOnDashboard(this, getString(R.string.cannot_raise_request));
            return false;
        }
        return true;
    }


    UIUtils.DialogButtonClickListener contactCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            //log Analytics
            UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(RequestMoneyActivity.this);
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_CALL, b);

            if (!PermissionUtil.hasPermission(RequestMoneyActivity.this, android.Manifest.permission.CALL_PHONE)) {
                PermissionUtil.checkPermission(RequestMoneyActivity.this, android.Manifest.permission.CALL_PHONE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            callSender(etPhone.getText().toString());
                        }
                    }
                });
            } else {
                callSender(etPhone.getText().toString());
            }
        }
    };
    UIUtils.DialogButtonClickListener shareCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();

            Bundle bun = new Bundle();
            bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_INVITE_FRIENDS, bun);

            Uri myUri = UIUtils.createShareUri(RequestMoneyActivity.this, userResponse.getValue().getReferralCode());
            Uri dynamicLinkUri = UIUtils.createDynamicUri(myUri);
            UIUtils.shortenLink(RequestMoneyActivity.this, dynamicLinkUri, "Our MySivana user, " + userResponse.getValue().getFullName() + " has requested money of " + etRupeeValue.getText().toString() + " INR.");

        }
    };
    UIUtils.DialogButtonClickListener negativeCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            clearData();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case SELECT_PHONE_NUMBER:
                    // Get the URI and query the content provider for the phone number
                    Uri contactUri = data.getData();
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
                            null, null, null);

                    // If the cursor returned is valid, get the phone number
                    if (cursor != null && cursor.moveToFirst()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String number = cursor.getString(numberIndex).replace(" ", "");
                        number = number.replaceAll("[^0-9]", "");
                        int numberLength = number.length();
                        if (numberLength > 10 && (number.startsWith("1") || number.startsWith("91"))) {
                            if (number.startsWith("91")) {
                                mSpinner.setSelection(0);
                            } else {
                                mSpinner.setSelection(1);
                            }
                            int val = numberLength % 10;
                            String str_getMOBILE = number.substring(val);
                            etPhone.setText(str_getMOBILE);
                        } else if (numberLength == 10) {
                            etPhone.setText(number);
                        } else {
                            UIUtils.showToast(RequestMoneyActivity.this, "Invalid number, please select another number");
                        }


                    }

                    cursor.close();
                    break;

                case REQUEST_CAMERA:
//                    showLoading();
                    getCapturedImage(data);
                    break;

                case REQUEST_GALLERY:
//                    showLoading();
                    getGalleryImage(data);
                    break;

                case REQUEST_DOCUMENT:
//                    showLoading();
                    getFileDocument(data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onClickCashRequest(View v) {
        if (validateTextFields()) {
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_REQUEST_CASH, b);
            requestUserID = RequestBody.create(MediaType.parse("text/plain"), userResponse.getValue().getUserId());
            requestUserAPI = RequestBody.create(MediaType.parse("text/plain"), userResponse.getValue().getUserApiKey());
            requestAmount = RequestBody.create(MediaType.parse("text/plain"), etRupeeValue.getText().toString());
            requestPhoneNum = RequestBody.create(MediaType.parse("text/plain"), etPhone.getText().toString());
            requestCountryCode = RequestBody.create(MediaType.parse("text/plain"), "" + mCountry.getDialCode());
            requestNotes = RequestBody.create(MediaType.parse("text/plain"), etNotes.getText().toString());
            requestDepositType = RequestBody.create(MediaType.parse("text/plain"),mIvDeposit.getTag().toString());
            if (attachmentText.getText().toString().isEmpty()) {
                byte[] bytesArrayDoc = new byte[4];
                body = MultipartBody.Part.createFormData("request_doc", "REQUEST_DOC_NULL", RequestBody.create(MediaType.parse("application/*"), bytesArrayDoc));
            }

            mPresenter.sendMoneyRequest(body, requestUserID, requestUserAPI, requestAmount, requestPhoneNum, requestCountryCode, requestNotes, requestDepositType);
        }

    }


    private void getFileDocument(Intent data) {
        if (data != null) {


            setPathOfDoc(data);
            File file = null;
            FileInputStream fileInputStream = null;
            byte[] bytesArrayDoc = null;

            try {
                if (!attachmentText.getText().toString().isEmpty()) {
                    file = new File(attachmentText.getText().toString());
                    if (file.length() <= 1024 * 1024 * 3) {
                        bytesArrayDoc = new byte[(int) file.length()];

                        //read file into bytes[]
                        fileInputStream = new FileInputStream(file);
                        fileInputStream.read(bytesArrayDoc);

                        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytesArrayDoc);
                        String fileName = file.getName().replace(" ", "_");
                        body = MultipartBody.Part.createFormData("request_doc", "REQUEST_DOC_" + fileName, requestFile);
                    } else {
                        UIUtils.showToast(this, "You cannot select a file beyond 3MB");
                    }

                } else {
                    UIUtils.showToast(this, "Cannot attach this file, please select another one");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


        }

    }

    private void setPathOfDoc(Intent data) {
        try {
            uri = data.getData();
            InputStream iStream = getContentResolver().openInputStream(uri);

            if (iStream != null) {
                try {
                    mimeType = getContentResolver().getType(uri);
                    String path = PathUtils.getPath(RequestMoneyActivity.this, uri);
                    attachmentText.setText(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Access the image from gallery and sets as profile picture
     *
     * @param data Intent object which has the details of selected image
     */
    private void getGalleryImage(Intent data) {
        if (data != null) {

            setPathOfDoc(data);
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (bm.getByteCount() > 2000) {
                    bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                } else {
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                }
                byte myImageBytes[] = baos.toByteArray();
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), myImageBytes);
                if (!attachmentText.getText().toString().isEmpty()) {
                    File file = new File(attachmentText.getText().toString());
                    String fileName = file.getName().replace(" ", "_");
                    body = MultipartBody.Part.createFormData("request_doc", "REQUEST_DOC_" + fileName, requestFile);
                } else {
                    UIUtils.showToast(this, "Cannot attach this image, please select another one");
                }

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
        PermissionUtil.checkPermission(RequestMoneyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
            @Override
            public void onResult(boolean success) {


                try {

                    attachmentText.setText("REQUEST_DOC_" + System.currentTimeMillis() + ".jpg");
                    Bitmap bm = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (bm.getByteCount() > 2000) {
                        bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    } else {
                        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    }
                    byte myImageBytes[] = baos.toByteArray();
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), myImageBytes);
                    File file = new File(attachmentText.getText().toString());
                    body = MultipartBody.Part.createFormData("request_doc", "REQUEST_DOC_" + file.getName(), requestFile);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Calls to the desired phone number
     */
    @SuppressLint("MissingPermission")
    public void callSender(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void clearData() {
        etRupeeValue.setText("");
        etPhone.setText("");
        etNotes.setText("");
        attachmentText.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }


    public void onClickAttachments(View v) {
        //log Analytics
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_ATTACHMENTS, b);

        if (NetworkUtil.getConnectivityStatus(this))
            requestSDCardPermissions();
        else
            showMessage(getString(R.string.internet_check));
    }

    /**
     * request for SD card permission to access gallery images from SD card
     */
    public void requestSDCardPermissions() {
        if (!PermissionUtil.hasPermission(RequestMoneyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionUtil.checkPermission(RequestMoneyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
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

        TextView headingText = dialog.findViewById((R.id.header_title));
        headingText.setText("SELECT FROM");

        TextView mCameraTxt = dialog.findViewById(R.id.camera_text);
        mCameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PermissionUtil.checkPermission(RequestMoneyActivity.this, Manifest.permission.CAMERA, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
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

        LinearLayout requestMoneyChooser = dialog.findViewById(R.id.request_money_chooser);
        requestMoneyChooser.setVisibility(View.VISIBLE);

        TextView mDocumentText = dialog.findViewById(R.id.document_text);
        mDocumentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openDocument();
            }


        });


        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * Forces the app to open Documents
     */
    private void openDocument() {
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Document"), REQUEST_DOCUMENT);
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
}
