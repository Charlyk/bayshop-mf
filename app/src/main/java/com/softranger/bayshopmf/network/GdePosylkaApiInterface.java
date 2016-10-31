package com.softranger.bayshopmf.network;

import com.softranger.bayshopmf.model.tracking.CourierService;
import com.softranger.bayshopmf.model.tracking.TrackApiResponse;
import com.softranger.bayshopmf.model.tracking.TrackingResult;

import java.util.ArrayList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Eduard Albu on 10/31/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public interface GdePosylkaApiInterface {

    @GET("tracker/detect/{tracking}")
    Observable<TrackApiResponse<ArrayList<CourierService>>> detectCourierService(@Path("tracking") String trackingNumber);

    @GET("tracker/{slug}/{tracking}")
    Observable<TrackApiResponse<TrackingResult>> trackParcelByCourierAndTracking(@Path("slug") String courierService,
                                                                                 @Path("tracking") String trackingNumber);
}
