package com.softranger.bayshopmf.network;

import com.softranger.bayshopmf.model.CreationDetails;
import com.softranger.bayshopmf.model.InStockList;
import com.softranger.bayshopmf.model.box.AwaitingArrival;
import com.softranger.bayshopmf.model.app.ParcelsCount;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.AwaitingArrivalDetails;
import com.softranger.bayshopmf.model.box.Declaration;
import com.softranger.bayshopmf.model.box.InStock;
import com.softranger.bayshopmf.model.box.InStockDetailed;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmf.model.pus.PUSStatuses;
import com.softranger.bayshopmf.model.user.User;

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

    // change "us" to needed storage, for now we have just "us"
    @GET("waiting-mf-list/us")
    Call<ServerResponse<ArrayList<AwaitingArrival>>> getAwaitingArrivalItems(@Header("Bearer") String token);

    @DELETE("waiting-mf/{id}")
    Call<ServerResponse> deleteAwaitingParcel(@Header("Bearer") String token, @Path("id") String parcelId);

    @GET("member/personal-data")
    Call<ServerResponse<User>> getUserPersonalData(@Header("Bearer") String token);

    @GET("member/parcels-counter")
    Call<ServerResponse<ParcelsCount>> getParcelsCounters(@Header("Bearer") String token);

    @FormUrlEncoded
    @POST("auth")
    Call<ServerResponse<User>> loginWithSocialNetworks(@Field("code") String socialCode,
                                                       @Field("type") String socialNetworkName);

    @FormUrlEncoded
    @POST("auth")
    Call<ServerResponse<User>> loginWithCredentials(@Field("email") String email,
                                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("waiting-mf/add")
    Call<ServerResponse<AwaitingArrival>> addNewAwaitingParcel(@Header("Bearer") String token,
                                                               @Field("storage") String storage,
                                                               @Field("tracking") String tracking,
                                                               @Field("title") String description,
                                                               @Field("url") String url,
                                                               @Field("packagePrice") String packagePrice);

    @GET("waiting-mf/{id}")
    Call<ServerResponse<AwaitingArrivalDetails>> getAwaitingParcelDetails(@Header("Bearer") String token,
                                                                          @Path("id") String parcelId);

    // replace "us" to needed storage, for now we have just "us"
    @GET("mf-list/in-stock/us")
    Call<ServerResponse<InStockList>> getInStockItems(@Header("Bearer") String token);

    @GET("mf-list/{id}")
    Call<ServerResponse<InStockDetailed>> getInStockItemDetails(@Header("Bearer") String token,
                                                                @Path("id") String itemId);

    @GET("mf-storage-declaration/{id}")
    Call<ServerResponse<Declaration>> getInStockItemDeclaration(@Header("Bearer") String token,
                                                                @Path("id") String itemId);

    @FormUrlEncoded
    @POST("mf-storage-declaration/{id}")
    Call<ServerResponse> saveInStockItemDeclaration(@Header("Bearer") String token,
                                                    @Path("id") String itemId,
                                                    @Field("title") String title,
                                                    @Field("declarationItems") String declarationItemsArray); // array of products in JSON format

    @GET("parcels/us")
    Call<ServerResponse<PUSStatuses>> getAllParcelsFromServer(@Header("Bearer") String token);

    @GET("parcels/view/{id}")
    Call<ServerResponse<PUSParcelDetailed>> getPUSParcelDetails(@Header("Bearer") String token,
                                                                @Path("id") String parcelId);

    @DELETE("parcels/view/{parcelId}")
    Call<ServerResponse> sendParcelToDisband(@Header("Bearer") String token,
                                             @Path("parcelId") String parcelId);

    @FormUrlEncoded
    @POST("parcels/view/{parcelId}")
    Call<ServerResponse> leaveFeedback(@Header("Bearer") String token, @Path("parcelId") String parcelId,
                                       @Field("comment") String comment, @Field("rating") String rating);

    @FormUrlEncoded
    @POST("parcels/view/{parcelId}")
    Call<ServerResponse> allowDamagedParcelSending(@Header("Bearer") String token, @Path("parcelId") String parcelId,
                                       @Field("confirm") int alow);

    @FormUrlEncoded
    @POST("parcel-create")
    Call<ServerResponse<CreationDetails>> createPusParcel(@Header("Bearer") String token);
}
