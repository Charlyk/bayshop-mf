package com.softranger.bayshopmf.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Country;
import com.softranger.bayshopmf.model.CountryCode;
import com.softranger.bayshopmf.model.Language;
import com.softranger.bayshopmf.model.User;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Application.getInstance().isLoggedIn()) {
            mIntent = new Intent(this, MainActivity.class);
            ApiClient.getInstance().getRequest(Constants.Api.urlPersonalData(), mHandler);
        } else {
            mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            // build user
                            Application.user = new ObjectMapper().readValue(data.toString(), User.class);
                            Application.user.setLanguages(data.getJSONObject("languages"));
                            ApiClient.getInstance().getRequest(Constants.Api.urlParcelsCounter(), mCounterHandler);
                        } else {
                            Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = SplashActivity.this.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    private Handler mCounterHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            Iterator<String> keys = data.keys();
                            int parcelsCount = 0;
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (key.equals(Constants.ParcelStatus.AWAITING_ARRIVAL) ||
                                        key.equals(Constants.ParcelStatus.IN_STOCK) ||
                                        key.equals(Constants.ParcelStatus.RECEIVED)) {
                                    Application.counters.put(key, data.optInt(key, 0));
                                } else {
                                    parcelsCount = parcelsCount + data.optInt(key);
                                }
                            }
                            Application.counters.put(Constants.PARCELS, parcelsCount);
                            startActivity(mIntent);
                        } else {
                            Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = SplashActivity.this.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            finish();
        }
    };
}
