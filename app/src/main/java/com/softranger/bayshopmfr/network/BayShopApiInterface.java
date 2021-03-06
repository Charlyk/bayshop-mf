package com.softranger.bayshopmfr.network;

import com.softranger.bayshopmfr.model.CalculatorResult;
import com.softranger.bayshopmfr.model.CreationDetails;
import com.softranger.bayshopmfr.model.NotificationSettings;
import com.softranger.bayshopmfr.model.Shipper;
import com.softranger.bayshopmfr.model.WarehouseAddress;
import com.softranger.bayshopmfr.model.address.Address;
import com.softranger.bayshopmfr.model.address.AddressToEdit;
import com.softranger.bayshopmfr.model.app.ParcelsCount;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.box.AwaitingArrival;
import com.softranger.bayshopmfr.model.box.AwaitingArrivalDetails;
import com.softranger.bayshopmfr.model.box.Declaration;
import com.softranger.bayshopmfr.model.box.InStockDetailed;
import com.softranger.bayshopmfr.model.box.InStockList;
import com.softranger.bayshopmfr.model.box.TrackingInfo;
import com.softranger.bayshopmfr.model.payment.PaymentHistories;
import com.softranger.bayshopmfr.model.pus.PUSParcel;
import com.softranger.bayshopmfr.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmfr.model.pus.PUSStatuses;
import com.softranger.bayshopmfr.model.tracking.TrackingData;
import com.softranger.bayshopmfr.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public interface BayShopApiInterface {

    @FormUrlEncoded
    @POST("parcel-change-address/{parcelId}")
    Call<ServerResponse> returnToSellerAddress(@Path("parcelId") String parcelId,
                                               @Field("shipping_first_name") String firstName, @Field("shipping_last_name") String lastName,
                                               @Field("shipping_address") String address, @Field("shipping_city") String city,
                                               @Field("shipping_country") String country, @Field("shipping_zip") String postalCode,
                                               @Field("shipping_phone") String phoneNumber, @Field("shipping_state") String state);

    // change "us" to needed storage, for now we have just "us"
    @GET("waiting-mf-list/us")
    Call<ServerResponse<ArrayList<AwaitingArrival>>> getAwaitingArrivalItems();

    @DELETE("waiting-mf/{id}")
    Call<ServerResponse> deleteAwaitingParcel(@Path("id") String parcelId);

    @DELETE("waiting-mf/{id}")
    Observable<ServerResponse> deleteAwaitingArrivalParcel(@Path("id") String parcelId);

    @GET("member/personal-data")
    Call<ServerResponse<User>> getUserPersonalData();

    @GET("member/parcels-counter")
    Call<ServerResponse<ParcelsCount>> getParcelsCounters();

    @FormUrlEncoded
    @POST("auth")
    Call<ServerResponse<User>> loginWithSocialNetworks(@Field("code") String socialCode,
                                                       @Field("type") String socialNetworkName);

    @FormUrlEncoded
    @POST("auth")
    Call<ServerResponse<User>> loginWithCredentials(@Field("email") String email,
                                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("waiting-mf/{id}")
    Call<ServerResponse<AwaitingArrivalDetails>> addNewAwaitingParcel(@Path("id") String parcelId,
                                                                      @Field("storage") String storage,
                                                                      @Field("tracking") String trackingNum,
                                                                      @Field("declarations") String productsJsonArray);

    @GET("waiting-mf/{id}")
    Call<ServerResponse<AwaitingArrivalDetails>> getAwaitingParcelDetails(@Path("id") String parcelId);

    // replace "us" to needed storage, for now we have just "us"
    @GET("mf-list/in-stock/us")
    Call<ServerResponse<InStockList>> getInStockItems();

    @GET("mf-list/{id}")
    Call<ServerResponse<InStockDetailed>> getInStockItemDetails(@Path("id") String itemId);

    @GET("mf-storage-declaration/{id}")
    Call<ServerResponse<Declaration>> getInStockItemDeclaration(@Path("id") String itemId);

    @FormUrlEncoded
    @POST("mf-storage-declaration/{id}")
    Call<ServerResponse> saveInStockItemDeclaration(@Path("id") String itemId,
                                                    @Field("declarationItems") String declarationItemsArray); // array of products in JSON format

    // replace "us" to needed storage, for now we have just "us"
    @GET("parcels/us")
    Call<ServerResponse<PUSStatuses>> getAllParcelsFromServer();

    @GET("parcels/view/{id}")
    Call<ServerResponse<PUSParcelDetailed>> getPUSParcelDetails(@Path("id") String parcelId);

    @DELETE("parcels/view/{parcelId}")
    Call<ServerResponse> sendParcelToDisband(@Path("parcelId") String parcelId);

    @FormUrlEncoded
    @POST("parcels/view/{parcelId}")
    Call<ServerResponse> sendHeldByUserParcel(@Path("parcelId") String parcelId,
                                              @Field("sentOnUserAlert") int send);

    @Multipart
    @POST("parcels/view/{parcelId}")
    Call<ServerResponse> leaveFeedback(@Path("parcelId") String parcelId, @Part("comment") RequestBody comment,
                                       @Part("rating") RequestBody rating, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("parcels/view/{parcelId}")
    Call<ServerResponse> leaveFeedback(@Path("parcelId") String parcelId, @Field("comment") String comment,
                                       @Field("rating") int rating);

    @FormUrlEncoded
    @POST("parcels/view/{parcelId}")
    Call<ServerResponse> allowDamagedParcelSending(@Path("parcelId") String parcelId,
                                                   @Field("confirm") int alow);

    @FormUrlEncoded
    @POST("parcel-create")
    Call<ServerResponse<CreationDetails>> createPusParcel(@Field("boxes") String boxesArray);

    @GET("member-address/{id}")
    Call<ServerResponse<AddressToEdit>> getMemberAddress(@Path("id") String addressId);

    @GET("member-address/phone-formats")
    Call<ServerResponse<AddressToEdit>> getPhoneCodes();

    @FormUrlEncoded
    @POST("member-address/{id}")
    Call<ServerResponse<AddressToEdit>> saveMemberAddress(@Path("id") String addressId,
                                                          @Field("shipping_first_name") String firstName,
                                                          @Field("shipping_last_name") String lastName,
                                                          @Field("shipping_email") String email,
                                                          @Field("shipping_address") String address,
                                                          @Field("shipping_city") String city,
                                                          @Field("shipping_zip") String zip,
                                                          @Field("shipping_phone_code") String phoneCode,
                                                          @Field("shipping_phone") String phoneNumber,
                                                          @Field("countryId") int countryId,
                                                          @Field("shipping_state") String state);

    @FormUrlEncoded
    @PUT("parcel-create")
    Call<ServerResponse<String>> createNewPusParcel(@Field("boxes") String boxesArray,
                                                    @Field("addressId") String addressId,
                                                    @Field("shipperId") String shipperId,
                                                    @Field("insurance") int insurance,
                                                    @Field("sentOnUserAlert") int sendOnAlert,
                                                    @Field("useAdditionalMaterials") int additionalMaterials);

    // replace "us" to needed storage, for now we have just "us"
    @GET("parcels/us/{status}")
    Call<ServerResponse<ArrayList<PUSParcel>>> getParcelsByStatus(@Path("status") String status);

    @FormUrlEncoded
    @PUT("waiting-mf/edit/{itemId}")
    Call<ServerResponse> requestPhotoForAwaiting(@Path("itemId") String itemdId,
                                                 @Field("photosPackageRequested") int request,  // 1 or 0
                                                 @Field("photosPackageRequestedComments") String comment);

    @FormUrlEncoded
    @PUT("waiting-mf/{itemId}")
    Call<ServerResponse> requestCheckForAwaiting(@Path("itemId") String itemdId,
                                                 @Field("verificationPackageRequested") int request,  // 1 or 0
                                                 @Field("verificationPackageRequestedComments") String comment);

    @FormUrlEncoded
    @POST("storage")
    Call<ServerResponse> requestParcelVerification(@Field("id") String parcelId, @Field("comments") String comment,
                                                   @Field("verification") int enable);

    @FormUrlEncoded
    @POST("storage")
    Call<ServerResponse> requestAdditionalPhotos(@Field("id") String parcelId, @Field("comments") String comment,
                                                 @Field("photo") int enable);

    @FormUrlEncoded
    @POST("storage")
    Call<ServerResponse> requestParcelRepacking(@Field("id") String parcelId, @Field("comments") String comment,
                                                @Field("repacking") int enable);

    @GET("balance/")
    Call<ServerResponse<PaymentHistories>> getPaymentHistoryForPeriod(@Query("period") String period);

    @GET("member-address")
    Call<ServerResponse<ArrayList<Address>>> getUserAddresses();

    @DELETE("member-address/{id}")
    Call<ServerResponse> deleteUserAddress(@Path("id") String addressId);

    @GET("get-member-mf-addresses")
    Call<ServerResponse<ArrayList<WarehouseAddress>>> getWarehouseAddress();

    @FormUrlEncoded
    @POST("auth/restore")
    Call<ServerResponse> requestPasswordRestoring(@Field("email") String email);

    @FormUrlEncoded
    @POST("auth/register")
    Call<ServerResponse> createNewAccount(@Field("email") String email, @Field("first_name") String firstName,
                                          @Field("last_name") String lastName, @Field("password") String password);

    @GET("member-shippers")
    Call<ServerResponse<ArrayList<Shipper>>> getMemberShippers();

    @FormUrlEncoded
    @POST("member/personal-data")
    Call<ServerResponse> saveUserPersonalData(@Field("surname") String surname,
                                              @Field("name") String name,
                                              @Field("countryId") String countryId,
                                              @Field("phone_code") String phoneCode,
                                              @Field("phone") String phone,
                                              @Field("token_gcm") String gcmToken,
                                              @Field("languageId") String languageId);

    @FormUrlEncoded
    @POST("member/new-password")
    Call<ServerResponse> changeUserPassword(@Field("old") String oldPassword,
                                            @Field("new") String newPassword);

    @FormUrlEncoded
    @POST("member/mail-options/")
    Call<ServerResponse<NotificationSettings>> changeUserNotificationsSettings(@Field("obtainSms") int sms,
                                                                               @Field("obtainGcm") int push,
                                                                               @Field("obtainMails") int emails);

    @GET("member/mail-options/")
    Call<ServerResponse<NotificationSettings>> getUserNotificationsSettings();

    @FormUrlEncoded
    @POST("member/gcm-token")
    Call<ServerResponse> sendPushToken(@Field("token_current") String currentToken, @Field("token_old") String lastToken);

    @FormUrlEncoded
    @POST("calculator")
    Call<ServerResponse<CalculatorResult>> computeShippingCost(@Field("storage_id") String storageId,
                                                               @Field("country_id") String countryId,
                                                               @Field("weight") double weight,
                                                               @Field("volume[x]") double volumeX,
                                                               @Field("volume[y]") double volumeY,
                                                               @Field("volume[z]") double volumeZ);

    @DELETE("auth/?")
    Observable<ServerResponse> logOut(@Query("gcm_token") String pushToken);

    @GET("additional-services/")
    Call<ServerResponse<HashMap<String, Double>>> getAdditionalServicesPrices();

    @GET("additional-services/")
    Observable<ServerResponse<HashMap<String, Double>>> getAdditionalServicesPrice();

    @GET("waiting-mf-tracking/{id}")
    Observable<ServerResponse<TrackingInfo>> getAwaitingParcelTrackingInfo(@Path("id") String parcelId);

    @GET("parcel-tracking/{partyNumber}")
    Call<ServerResponse<TrackingData>> getSentParcelTrackingData(@Path("partyNumber") String partyNumber);
}
