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
package com.mysivana.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.mysivana.R;
import com.mysivana.ui.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.fabric.sdk.android.Fabric.TAG;

/**
 * Handles all UI utilities like keyboard show/hide, toast msg, show dialog, format date, etc.
 */
public class UIUtils {
    public static final String QUERY_PARAM_INVITE_CODE = "inviteCode";
    private static final String DYNAMIC_LINK_DOMAIN = "mysivana.page.link";


    /**
     * To hide keyboard explicitly
     *
     * @param context
     * @param view
     */
    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Convert the density independent pixels to pixels
     *
     * @param dp      - density independent pixels
     * @param context
     * @return returns in pixels
     */
    public static int dpToPx(float dp, Context context) {
        return dpToPx(dp, context.getResources());
    }

    /**
     * Convert the density independent pixels to pixels from resources
     *
     * @param dp
     * @param resources
     * @return returns in pixels
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    /**
     * Format the date given template
     *
     * @param transactionDate - input parameter
     * @return return the date
     */
    public static Date formatTransactionDate(String transactionDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date = null;
        try {
            date = simpleDateFormat.parse(transactionDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Show dialog the positive and negative buttons
     *
     * @param context
     * @param title            - title of dialog
     * @param message          - message to display in dialog
     * @param positiveButton
     * @param negativeButton
     * @param positiveCallback - listener
     * @param negativeCallback - listener
     * @return
     */
    public static Dialog showCustomDialog(final Context context, String title, String message, String positiveButton, final String negativeButton, final DialogButtonClickListener positiveCallback, final DialogButtonClickListener negativeCallback) {
        final Dialog dialog = new Dialog(context, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_message);
        Button mProceed = dialog.findViewById(R.id.proceed_btn);
        mProceed.setText(positiveButton);
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveCallback.onClickButton(dialog, v);
            }
        });
        TextView mDesc = dialog.findViewById(R.id.warning_msg);
        mDesc.setText(message);
        ImageView mCancel = dialog.findViewById(R.id.cancel_btn);
        if (negativeButton == null) {
            mCancel.setVisibility(View.GONE);
        } else {
            mCancel.setVisibility(View.VISIBLE);
        }
        mCancel.setTag(negativeButton);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeCallback.onClickButton(dialog, v);
            }
        });

        TextView mTitle = dialog.findViewById(R.id.header_title);
        mTitle.setText(title);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        return dialog;
    }

    public interface DialogButtonClickListener {

        public void onClickButton(Dialog d, View v);


    }

    public static Dialog showCustomDialogForMoneyRequest(final Context context, String title, String message, String positiveButton1, String positiveButton2, final DialogButtonClickListener positiveCallback1, final DialogButtonClickListener positiveCallback2, final DialogButtonClickListener negativeCallback) {
        final Dialog dialog = new Dialog(context, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_message);
        Button mProceed1 = dialog.findViewById(R.id.proceed_btn);
        mProceed1.setText(positiveButton1);
        mProceed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveCallback1.onClickButton(dialog, v);
            }
        });
        Button mProceed2 = dialog.findViewById(R.id.proceed_btn_1);
        if (positiveButton2 != null && !positiveButton2.isEmpty()) {
            mProceed2.setVisibility(View.VISIBLE);
            mProceed2.setText(positiveButton2);
            mProceed2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positiveCallback2.onClickButton(dialog, v);
                }
            });
        } else {
            mProceed2.setVisibility(View.GONE);
        }
        TextView mDesc = dialog.findViewById(R.id.warning_msg);
        mDesc.setText(message);
        ImageView mCancel = dialog.findViewById(R.id.cancel_btn);
        if (negativeCallback != null) {
            mCancel.setVisibility(View.VISIBLE);
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    negativeCallback.onClickButton(dialog, v);
                }
            });
        }else{
            mCancel.setVisibility(View.GONE);
        }


        TextView mTitle = dialog.findViewById(R.id.header_title);
        mTitle.setText(title);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        return dialog;
    }

    public static Dialog showCustomDialogForConfirmation(final Context context, String title, String message, String amount, String name, String phone, String email, String positiveButton, final String negativeButton, final DialogButtonClickListener positiveCallback, final DialogButtonClickListener negativeCallback) {
        final Dialog dialog = new Dialog(context, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_message_confirmation);
        Button mProceed = dialog.findViewById(R.id.proceed_btn);
        mProceed.setText(positiveButton);
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveCallback.onClickButton(dialog, v);
            }
        });
        TextView mDesc = dialog.findViewById(R.id.warning_msg);
        mDesc.setText(message);
        TextView amountTV = dialog.findViewById(R.id.amount_tv);
        amountTV.setText(amount);
        TextView nameTV = dialog.findViewById(R.id.name_tv);
        nameTV.setText(name);
        TextView phoneTV = dialog.findViewById(R.id.phone_tv);
        phoneTV.setText(phone);
        TextView emailTV = dialog.findViewById(R.id.email_tv);
        emailTV.setText(email);


        ImageView mCancel = dialog.findViewById(R.id.cancel_btn);
        if (negativeButton == null) {
            mCancel.setVisibility(View.GONE);
        } else {
            mCancel.setVisibility(View.VISIBLE);
        }
        mCancel.setTag(negativeButton);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeCallback.onClickButton(dialog, v);
            }
        });

        TextView mTitle = dialog.findViewById(R.id.header_title);
        mTitle.setText(title);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        return dialog;
    }

    /**
     * Gets String from Resource value
     *
     * @param context
     * @param resID   - input resourceID
     * @return - returns String value
     */
    public static String getStringFromResource(Context context, int resID) {
        return context.getResources().getString(resID);
    }

    /**
     * Copy the value to clipboard
     *
     * @param context
     * @param text    - input text parameter to copy
     * @return - returns true if copied to clipboard
     */
    public static boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText(
                                "bitcoinaddress", text);
                clipboard.setPrimaryClip(clip);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check the Logged User userApiKey with userApiKey from every response
     *
     * @param context
     * @param userApiKey - input userApiKey from api response
     * @return - returns true Logged User userApiKey match with api response key
     */
    public static boolean isUserApiKeyValid(Context context, String userApiKey) {
        if (!SharedPrefsUtils.getUserApiKey(context).equalsIgnoreCase(userApiKey)) {
            SharedPrefsUtils.saveUserApiKey(context, userApiKey);
            return false;
        }
        return true;
    }


    /**
     * Show the Toast message
     *
     * @param context
     * @param message - input message
     */
    public static void showToast(Context context, String message) {
        boolean reLogin = false;
        if (message.contains("401")) {
            reLogin = true;
            message = context.getString(R.string.account_relogin);
        }
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_toast, null);
        TextView tv = view.findViewById(R.id.toast_msg);
        tv.setText(message);
        toast.setView(view);
        toast.show();
        if (reLogin && context instanceof Activity) {
            Activity activity = (Activity) context;
            SharedPrefsUtils.clear(activity);
            CommonUtils.startActivity(activity, LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
            activity.finish();
        }
    }

    /**
     * Show the Toast message
     *
     * @param context
     * @param message - input message
     */
    public static void showToastOnDashboard(Context context, String message) {
        boolean reLogin = false;
        if (message.contains("401")) {
            reLogin = true;
            message = context.getString(R.string.account_relogin);
        }
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 16, 50);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_toast_dashboard, null);
        LinearLayout ll = view.findViewById(R.id.parent);
        TextView tv = view.findViewById(R.id.toast_msg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, (int) context.getResources().getDimension(R.dimen.dimen_16));
        ll.setLayoutParams(lp);
        tv.setText(message);
        toast.setView(view);
        toast.show();
        if (reLogin && context instanceof Activity) {
            Activity activity = (Activity) context;
            SharedPrefsUtils.clear(activity);
            CommonUtils.startActivity(activity, LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
            activity.finish();
        }
    }

    /**
     * To find the distance from current location LatLng with merchant LatLng
     *
     * @param lat1 - current location latitude
     * @param lon1 - current location longitude
     * @param lat2 - Merchant location latitude
     * @param lon2 - Merchant location longitude
     * @return - returns distance between the co-ordinates
     */
    public static double findDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }


    /**
     * Converts the degrees to radians
     *
     * @param deg - input degrees value
     * @return - returns in radians
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Converts the radians to degrees
     *
     * @param rad - input radians
     * @return - returns in degrees
     */
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static String formatUSNumber(String text) {
        String val = text;
        String a = "";
        String b = "";
        String c = "";
        if (val != null && val.length() > 0) {
            val = val.replace("-", "");
            if (val.length() >= 3) {
                a = val.substring(0, 3);
            } else if (val.length() < 3) {
                a = val.substring(0, val.length());
            }
            if (val.length() >= 6) {
                b = val.substring(3, 6);
                c = val.substring(6, val.length());
            } else if (val.length() > 3 && val.length() < 6) {
                b = val.substring(3, val.length());
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (a != null && a.length() > 0) {
                stringBuffer.append(a);
                if (a.length() == 3) {
                    stringBuffer.append("-");
                }
            }
            if (b != null && b.length() > 0) {
                stringBuffer.append(b);
                if (b.length() == 3) {
                    stringBuffer.append("-");
                }
            }
            if (c != null && c.length() > 0) {
                stringBuffer.append(c);
            }
            return stringBuffer.toString();
        } else {
            return "";
        }
    }

    public static String formatIndianNumber(String text) {
        if (text != null && text.length() > 0)
            return text.replace("-", "");
        else
            return "";
    }

    public static String captializeNames(String name) {
        StringBuilder result = new StringBuilder(name.length());
        String words[] = name.split("\\ ");
        for (int i = 0; i < words.length; i++) {
            result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(" ");

        }
        return result.toString();
    }

    public static void shortenLink(final Activity activity, Uri linkUri) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(linkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {

                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            String msg = "Hey, check out new MySivana app which lets you transact or cash crypto-currency from local stores. Download from here: " + shortLink;
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                            sendIntent.setType("text/plain");
                            activity.startActivity(sendIntent);
                        } else {
                            Log.e(TAG, "error found");
                        }
                    }
                });
    }

    public static void shortenLink(final Activity activity, Uri linkUri, final String requestString) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(linkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {

                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            String msg = requestString + "\n Check out new MySivana app which lets you transact or cash crypto-currency from local stores. Download from here: " + shortLink;
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                            sendIntent.setType("text/plain");
                            activity.startActivity(sendIntent);
                        } else {
                            Log.e(TAG, "error found");
                        }
                    }
                });
    }

    public static Uri createDynamicUri(Uri myUri) {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(myUri)
                .setDynamicLinkDomain(DYNAMIC_LINK_DOMAIN)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.mysivana.iosApp") //Bundle ID
                                .setAppStoreId("1436466871") //app ID
                                .setMinimumVersion("10.0") //minimum iOS version to be supported
                                .build())
                .buildDynamicLink();
        return dynamicLink.getUri();
    }

    public static Uri createShareUri(Activity activity, String inviteCode) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(activity.getString(R.string.config_scheme)) // "https"
                .authority(activity.getString(R.string.config_host)) // "mysivana.com"
                .appendPath(activity.getString(R.string.config_path)) // "invites"
                .appendQueryParameter(QUERY_PARAM_INVITE_CODE, inviteCode);
        return builder.build();


    }


    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        String sTheme = SharedPrefsUtils.getStringPreference(activity, AppConstants.BundleParams.THEME);
        if (TextUtils.isEmpty(sTheme)) {
            sTheme = AppConstants.CryptoCodes.BTC;
        }
        switch (sTheme) {
            default:
            case AppConstants.CryptoCodes.BTC:
                activity.setTheme(R.style.ThemeBitCoin);
                break;
            case AppConstants.CryptoCodes.ETH:
                activity.setTheme(R.style.ThemeEthereum);
                break;

        }
    }

    public static String getCryptoCode(Activity activity) {
        String crytoCode = SharedPrefsUtils.getStringPreference(activity, AppConstants.BundleParams.THEME);
        return crytoCode != null ? crytoCode : AppConstants.CryptoCodes.BTC;
    }

}

