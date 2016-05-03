package com.softranger.bayshopmf;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by eduard on 29.04.16.
 */
public class Application extends android.app.Application {

    public static final String TOKEN_READY = "TOKEN_READY_BROADCAST";
    private static Application instance;
    public static String currentToken;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ApiClient.getInstance().logIn("admin@test.com", "111111", mAuthHandler);
    }

    private Handler mAuthHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        boolean error = response.getBoolean("error");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            Application.currentToken = data.optString("access_token");
                            sendBroadcast(new Intent(TOKEN_READY));
                        } else {
                            Log.e(this.getClass().getSimpleName(), response.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    break;
                }
            }
        }
    };
}
