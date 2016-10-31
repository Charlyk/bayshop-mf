package com.softranger.bayshopmfr.network;

import android.content.Intent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eduard Albu on 9/21/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public abstract class ResponseCallback<T> implements Callback<ServerResponse<T>> {

    public static final String ACTION_NO_CONNECTION = "com.softranger.bayshopmf.network.NO_INTERNET_CONNECTION";
    public abstract void onSuccess(T data);
    public abstract void onFailure(ServerResponse errorData);
    public abstract void onError(Call<ServerResponse<T>> call, Throwable t);

    @Override
    public void onResponse(Call<ServerResponse<T>> call, Response<ServerResponse<T>> response) {
        if (response.body() != null) {
            String message = response.body().getMessage();
            if (message.equalsIgnoreCase(Constants.ApiResponse.OK_MESSAGE)) {
                onServerResponse(response.body());
                onSuccess(response.body().getData());
            } else {
                onFailure(response.body());
            }
        } else {
            try {
                ServerResponse errorResponse = new ObjectMapper().readValue(response.errorBody().string(), ServerResponse.class);
                onFailure(errorResponse);
            } catch (IOException e) {
                onError(call, e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<ServerResponse<T>> call, Throwable t) {
        if (t instanceof ConnectException || t instanceof UnknownHostException) {
            Application.getInstance().sendBroadcast(new Intent(ACTION_NO_CONNECTION));
        } else {
            onError(call, t);
        }
    }

    public void onServerResponse(ServerResponse<T> serverResponse) {

    }
}
