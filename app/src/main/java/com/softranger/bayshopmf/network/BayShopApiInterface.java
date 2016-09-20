package com.softranger.bayshopmf.network;

import com.softranger.bayshopmf.model.AwaitingArrival;
import com.softranger.bayshopmf.model.ServerResponse;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public interface BayShopApiInterface {

    @FormUrlEncoded
    @POST("parcel-change-address/{parcelId}")
    Call<ServerResponse> returnToSellerAddress(
            @Header("Bearer") String token,
            @Path("parcelId") String parcelId,
            @Field("shipping_first_name") String firstName,
            @Field("shipping_last_name") String lastName,
            @Field("shipping_address") String address,
            @Field("shipping_city") String city,
            @Field("shipping_country") String country,
            @Field("shipping_zip") String postalCode,
            @Field("shipping_phone") String phoneNumber,
            @Field("shipping_state") String state);

    // TODO: 9/20/16 change "us" to needed storage, for now we have just "us"
    @GET("waiting-mf-list/us")
    Call<ServerResponse<ArrayList<AwaitingArrival>>> getAwaitingArrivalItems(@Header("Bearer") String token);

    @DELETE("waiting-mf/{id}")
    Call<ServerResponse> deleteAwaitingParcel(@Header("Bearer") String token, @Path("id") String parcelId);
}
