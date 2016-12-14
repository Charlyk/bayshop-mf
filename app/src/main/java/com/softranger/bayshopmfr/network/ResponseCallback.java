package com.softranger.bayshopmfr.network;

import android.content.Intent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmfr.BuildConfig;
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
    public static final String ACTION_NEW_VERSION = "com.softranger.bayshopmf.network.NEW_VERSION";
    public abstract void onSuccess(T data);
    public abstract void onFailure(ServerResponse errorData);
    public abstract void onError(Call<ServerResponse<T>> call, Throwable t);

    @Override
    public void onResponse(Call<ServerResponse<T>> call, Response<ServerResponse<T>> response) {
        String appHeader = response.headers().get("App");

        ServerResponse<T> serverResponse = response.body();
        ServerResponse errorResponse = getErrorResponse(call, response);

        int responseCode = 0;
        String responseMessage = "";

        if (serverResponse != null) {
            responseCode = serverResponse.getCode();
            responseMessage = serverResponse.getMessage();
        } else if (errorResponse != null) {
            responseCode = errorResponse.getCode();
            responseMessage = errorResponse.getMessage();
        }

        if (isInvalidMessage(responseMessage) || (appHeader != null && !appHeader.equals(BuildConfig.MF_VERSION))) {
            Intent intent = new Intent(ACTION_NEW_VERSION);
            // put the obtained code as a boolean in intent extras
            intent.putExtra("required", isInvalidMessage(responseMessage));
            // send the broadcast so the ui can update it self
            Application.getInstance().sendBroadcast(intent);
        }

        // if we've got invalid version messge we should stop all actions
        if (isInvalidMessage(responseMessage)) return;

        if (serverResponse != null) {
            String message = serverResponse.getMessage();
            if (message.equalsIgnoreCase(Constants.ApiResponse.OK_MESSAGE)) {
                onServerResponse(serverResponse);
                onSuccess(serverResponse.getData());
            } else {
                onFailure(serverResponse);
            }
        } else if (errorResponse != null) {
            onFailure(errorResponse);
        }
    }

    /**
     * Parse the response error body in a ServerResponse object
     *
     * @param response retrofit response
     * @return a {@link ServerResponse} object containing the error message and code
     */
    private ServerResponse getErrorResponse(Call<ServerResponse<T>> call, Response<ServerResponse<T>> response) {
        try {
            return new ObjectMapper().readValue(response.errorBody().string(), ServerResponse.class);
        } catch (IOException e) {
            onError(call, e);
        }
        return null;
    }

    /**
     * Check if the response code is in range of required update
     *
     * @param code from {@link ServerResponse#getCode()}
     * @return is or not in the range between 11...20
     */
    private boolean isCodeInRange(int code) {
        return code >= 11 && code <= 20;
    }

    private boolean isInvalidMessage(String message) {
        return message.equalsIgnoreCase("Invalid version");
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
