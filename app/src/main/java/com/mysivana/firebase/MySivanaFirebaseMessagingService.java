package com.mysivana.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mysivana.R;
import com.mysivana.SplashActivity;
import com.mysivana.ui.DashboardMerchantActivity;
import com.mysivana.ui.DashboardUserActivity;
import com.mysivana.ui.NotificationsActivity;
import com.mysivana.ui.RequestMoneyActivity;
import com.mysivana.ui.TransactionReceiptActivity;
import com.mysivana.util.AppConstants;
import com.mysivana.util.SharedPrefsUtils;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mysivana.ui.TransactionLookUpActivity.IS_FROM_LOOK_UP;
import static com.mysivana.ui.TransactionReceiptActivity.TRANSACTION_CODE;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_COUNTRY_CODE;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_EMAIL;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_FULL_NAME;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_PHONE_NUMBER;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_AMOUNT;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_DEPOSIT_TYPE;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_SENDER_USERTYPE;
import static com.mysivana.util.AppConstants.Notifications.TRANSACTION_REQUEST_ID;

public class MySivanaFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    private static final String CHANNEL_ID = "RequestMoneyChannelID";
    private String mBody;
    private String mTitle;
    private String transactionRequestId;
    private int notificationType;
    private String transactionCode, requestAmount, receiverPhone, receiverCountryCode, receiverEmail, receiverName, senderUserType, paymentType;
    NotificationManager notificationManager;
    private int notificationCount;
    int notificationID = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "remoteMessage:: " + remoteMessage.toString());

        Map<String, String> params = remoteMessage.getData();
        Log.d(TAG, "PARAMS_JSON_OBJECT:: " + params.toString());
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (params.size() > 0) {
            JSONObject object = new JSONObject(params);
            Log.d(TAG, "PUSH_JSON_OBJECT:: " + object.toString());


            transactionRequestId = object.optString("transactionRequestId");
            mBody = object.optString("body");
            mTitle = object.optString("title");
            notificationType = object.optInt("notificationType");
            transactionCode = object.optString("transactionCode");
            requestAmount = object.optString("requestAmount");
            receiverPhone = object.optString("receiverPhone");
            receiverCountryCode = object.optString("receiverCountryCode");
            receiverEmail = object.optString("receiverEmail");
            receiverName = object.optString("receiverName");
            senderUserType = object.optString("senderUserType");
            paymentType = object.optString("paymentType");

            //Calling method to generate notification
            if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mBody)) {
                sendNotification(mTitle, mBody);
            } else {
                Log.e(TAG, "Not Content to display Push Notification");
            }
        } else {
            Log.e(TAG, "Push Failure:: ");
        }
    }

    private void sendNotification(String title, String messageBody) {
        PendingIntent pendingContentIntent;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_logo);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }


        //Intent while clearing notification
        Intent intent = new Intent(this, DeleteIntentReceiver.class);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);




        switch (notificationType) {
            case 1:
                //transaction type notifications
                Intent receiptintent = new Intent(this, TransactionReceiptActivity.class);
                receiptintent.putExtra(TRANSACTION_CODE, transactionCode);
                receiptintent.putExtra(IS_FROM_LOOK_UP, false);
                receiptintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingContentIntent = PendingIntent.getActivity(this, 0, receiptintent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentTitle(title)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingContentIntent);

                break;
            case 2:
                //money request type notifications
                notificationCount = SharedPrefsUtils.getNotificationCount(getApplicationContext());
                notificationCount++;

                SharedPrefsUtils.setNotificationCount(getApplicationContext(),notificationCount);

                //notify new notification while activity is in foreground
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(AppConstants.INTENT_ACTION_NEW_NOTIFICATION);
                sendBroadcast(broadcastIntent);

                Intent _intent = new Intent(getApplicationContext(), NotificationActionService.class);
                _intent.putExtra(REQUESTED_SENDER_USERTYPE, senderUserType);
                _intent.putExtra(REQUESTED_AMOUNT, requestAmount);
                _intent.putExtra(RECEIVER_FULL_NAME, receiverName);
                _intent.putExtra(RECEIVER_PHONE_NUMBER, receiverPhone);
                _intent.putExtra(RECEIVER_COUNTRY_CODE, receiverCountryCode);
                _intent.putExtra(RECEIVER_EMAIL, receiverEmail);
                _intent.putExtra(TRANSACTION_REQUEST_ID, transactionRequestId);
                _intent.putExtra(REQUESTED_DEPOSIT_TYPE, paymentType);

                 pendingContentIntent = PendingIntent.getService(this, Integer.parseInt(transactionRequestId),
                        _intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

                notificationBuilder.setContentTitle(title)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingContentIntent)
                        .setDeleteIntent(deletePendingIntent);

                break;

        }




        if (transactionRequestId != null && !transactionRequestId.isEmpty()) {
            notificationID = Integer.parseInt(transactionRequestId);
        } else {
            notificationID++;
        }
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setShowBadge(true);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(notificationID, notificationBuilder.build());
        } else {
            notificationManager.notify(notificationID, notificationBuilder.build());

        }

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (token != SharedPrefsUtils.getFCMToken(this)) {
            sendRegistrationToServer(token);
        }

    }

    private void sendRegistrationToServer(String token) {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        this.startService(intent);
    }
}
