package com.softranger.bayshopmf.util.push;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.util.Application;

import retrofit2.Call;

/**
 * Created by Eduard Albu on 10/19/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "InstanceIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        if (Application.getInstance().isLoggedIn()) {
            Application.apiInterface().sendPushToken(token, Application.getInstance().getPushToken()).enqueue(mResponseCallback);
            Application.getInstance().setPushToken(token);
        }
    }

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            Log.d("Push", "Token was sent to server");
            Application.getInstance().setPushTokenSent(true);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Log.e("Push", errorData.getMessage());
            Application.getInstance().setPushTokenSent(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Log.e("Push", t.getMessage());
            Application.getInstance().setPushTokenSent(false);
        }
    };
}
