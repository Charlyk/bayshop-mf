package com.softranger.bayshopmf.network;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eduard on 29.04.16.
 */
public class ApiClient {

    private static ApiClient instance;
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static String deviceId;
    private static OkHttpClient httpClient;

    private ApiClient() {
        deviceId = Settings.Secure.getString(Application.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public void logIn(String email, String password, Handler handler) {
        try {
            HttpUrl httpUrl = HttpUrl.parse(Constants.Api.urlAuth());

            RequestBody authBody = new FormBody.Builder()
                    .add(EMAIL_KEY, email).add(PASSWORD_KEY, password).build();
            Request request = new Request.Builder()
                    .post(authBody)
                    .url(httpUrl)
                    .build();
            execute(request, handler);
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = Constants.ApiResponse.RESPONSE_FAILED;
            message.obj = e;
            handler.sendMessage(message);
        }
    }

    public void delete(String url, Handler handler) {
        if (Application.currentToken != null) {
            HttpUrl httpUrl = HttpUrl.parse(url);
            Request request = new Request.Builder()
                    .url(httpUrl)
                    .addHeader("Bearer", Application.currentToken)
                    .addHeader("DeviceId", deviceId)
                    .delete()
                    .build();
            execute(request, handler);
        }
    }

    public void sendRequest(RequestBody requestBody, String urlString, Handler handler) {
        if (Application.currentToken != null) {
            HttpUrl url = HttpUrl.parse(urlString);
            Request request = new Request.Builder()
                    .addHeader("Bearer", Application.currentToken)
                    .addHeader("DeviceId", deviceId)
                    .post(requestBody).url(url).build();
            execute(request, handler);
        }
    }

    public void sendRequest(String urlString, Handler handler) {
        if (Application.currentToken != null) {
            HttpUrl url = HttpUrl.parse(urlString);
            Request request = new Request.Builder().
                    addHeader("Bearer", Application.currentToken)
                    .addHeader("DeviceId", deviceId)
                    .url(url)
                    .build();
            execute(request, handler);
        }
    }

    private void execute(Request request, final Handler handler) {
        final Message message = handler.obtainMessage();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                message.what = Constants.ApiResponse.RESPONSE_FAILED;
                message.obj = e;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final int responseCode = response.code();
                if (responseCode >= 200 && responseCode < 300) {
                    message.arg1 = responseCode;
                    message.what = Constants.ApiResponse.RESPONSE_OK;
                    message.obj = response.body().string();
                } else if (responseCode == 401) {
                    message.arg1 = responseCode;
                    message.what = Constants.ApiResponse.RESPONSE_OK;
                    message.obj = response.body().string();
                } else {
                    message.arg1 = responseCode;
                    message.what = Constants.ApiResponse.RESPONSE_ERROR;
                    message.obj = response;
                }
                handler.sendMessage(message);
                response.body().close();
            }
        });
    }
}
