package com.mysivana.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mysivana.MSApplication;
import com.mysivana.mvp.api.CallbackWrapper;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.SharedPrefsUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegistrationIntentService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        UserResponse response = SharedPrefsUtils.getLoggedUserObject(this);
        if (response != null) {
            Observable<GenericResponse> observable = MSApplication.APIManager.mApiService.registerTokenToServer(response.getValue().getUserId(), response.getValue().getUserApiKey(), FirebaseInstanceId.getInstance().getToken(), response.getValue().getDeviceId());
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CallbackWrapper<GenericResponse>(null) {
                        @Override
                        protected void onSuccess(GenericResponse response) {
                            SharedPrefsUtils.saveFCMToken(getApplicationContext(), FirebaseInstanceId.getInstance().getToken());
                            stopSelf();
                        }

                        @Override
                        protected void onFailure(Throwable e) {
                            e.printStackTrace();
                            stopSelf();

                        }
                    });
        }
        return START_STICKY;
    }
}
