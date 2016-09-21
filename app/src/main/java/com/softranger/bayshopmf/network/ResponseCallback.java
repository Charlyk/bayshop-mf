package com.softranger.bayshopmf.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.util.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eduard Albu on 9/21/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public abstract class ResponseCallback<T> implements Callback<ServerResponse<T>> {

    public abstract void onSuccess(T data);
    public abstract void onFailure(ServerResponse errorData);
    public abstract void onError(Call<ServerResponse<T>> call, Throwable t);

    @Override
    public void onResponse(Call<ServerResponse<T>> call, Response<ServerResponse<T>> response) {
        if (response.body() != null) {
            String message = response.body().getMessage();
            if (message.equals(Constants.ApiResponse.OK_MESSAGE)) {
                onSuccess(response.body().getData());
            } else {
                onFailure(response.body());
            }
        } else {
            try {
                ServerResponse errorResponse = new ObjectMapper().readValue(response.errorBody().string(), ServerResponse.class);
                onFailure(errorResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<ServerResponse<T>> call, Throwable t) {
        onError(call, t);
    }
}
