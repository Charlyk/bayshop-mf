package com.softranger.bayshopmf.util;

import android.app.Activity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Eduard Albu on 5/27/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class FacebookAuth {
    private static FacebookAuth instance;
    private OnLoginDataReadyListener mResponseListener;

    public static synchronized FacebookAuth getInstance() {
        if (instance == null) {
            instance = new FacebookAuth();
        }
        return instance;
    }


    /**
     * Call if you want the user to login with his facebook account
     * @param activity needed to initialize the Facebook LoginManager
     * @param listener used to set the login listener
     */
    public void facebookLogin(Activity activity, OnLoginDataReadyListener listener, CallbackManager callbackManager) {
        mResponseListener = listener;
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile", "user_friends", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getUserData();
                    }

                    @Override
                    public void onCancel() {
                        if (mResponseListener != null) {
                            mResponseListener.onCanceled();
                        }
                    }

                    @Override
                    public void onError(FacebookException error) {
                        if (mResponseListener != null) {
                            mResponseListener.onCanceled();
                        }
                    }
                });

    }


    /**
     * Creates an Facebook Greph request witch will grab the user data
     * such as name id and picture for now
     */
    public void getUserData() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String token = accessToken.getToken();
        if (mResponseListener != null) {
            mResponseListener.onLoginDataReady(token);
        }
    }

    public interface OnLoginDataReadyListener {
        void onLoginDataReady(String facebookData);
        void onCanceled();
    }
}
