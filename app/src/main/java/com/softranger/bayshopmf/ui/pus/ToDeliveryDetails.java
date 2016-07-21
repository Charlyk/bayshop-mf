package com.softranger.bayshopmf.ui.pus;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.Photo;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.ShippingMethod;
import com.softranger.bayshopmf.model.packages.PUSParcel;
import com.softranger.bayshopmf.model.packages.ToDelivery;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToDeliveryDetails extends ParentFragment implements OnMapReadyCallback, View.OnClickListener {


    private static final String TO_DELIVERY_ARG = "to delivery parcel arg";
    private MapView mMapView;
    private ToDelivery mToDelivery;

    private static final double LATITUDE = 47.043252904877306;
    private static final double LONGITUDE = 28.868207931518555;

//    private MainActivity mActivity;


    public ToDeliveryDetails() {
        // Required empty public constructor
    }

    public static ToDeliveryDetails newInstance(ToDelivery toDelivery) {
        Bundle args = new Bundle();
        args.putParcelable(TO_DELIVERY_ARG, toDelivery);
        ToDeliveryDetails fragment = new ToDeliveryDetails();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_delivery_details, container, false);

        mMapView = (MapView) view.findViewById(R.id.toDeliveryDetailsMapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.setClickable(false);
        mMapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(LATITUDE, LONGITUDE);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Courier Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // googleMap in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
//        JSONObject data = response.getJSONObject("data");
//        mToDelivery = buildParcelDetails(data);
    }

//    @Override
//    public void onHandleMessageEnd() {
//        mActivity.toggleLoadingProgress(false);
//    }

    private ToDelivery buildParcelDetails(JSONObject data) throws Exception {
        final String parcelStatus = data.getString("status");

        Address address = buildAddress(data.getJSONObject("address"));
        ShippingMethod shippingMethod = buildShippingMethod(data.getJSONObject("shipping"));
        ArrayList<Product> products = buildProducts(data.getJSONArray("boxes"));

        PUSParcel.Builder<ToDelivery> packageBuilder = new PUSParcel.Builder<>(mToDelivery)
                .id(data.getInt("id"))
                .codeNumber(data.getString("uid"))
                .created(data.getString("created"))
                .currency(data.getString("currency"))
                .totalPrice(data.getDouble("totalPrice"))
                .deliveryPrice(data.getDouble("deliveryPrice"))
                .name(data.getString("generalDescription"))
                .insuranceCommission(data.optDouble("insuranceCommission", 0))
                .status(data.getString("status"))
                .address(address)
                .shippingMethod(shippingMethod)
                .products(products)
                .deposit(mToDelivery.getDeposit());

        switch (parcelStatus) {
            case Constants.ParcelStatus.TAKEN_TO_DELIVERY:
                packageBuilder.takenToDeliveryTime(data.getString("takenToDeliveryTime"));
                break;
        }
        return packageBuilder.build();
    }

    private Address buildAddress(JSONObject jsonAddress) throws Exception {
        String clientName = jsonAddress.getString("first_name") + " "
                + jsonAddress.getString("last_name");
        return new Address.Builder()
                .postalCode(jsonAddress.getString("zip"))
                .city(jsonAddress.getString("city"))
                .state(jsonAddress.getString("state"))
                .phoneNumber(jsonAddress.getString("phone"))
                .country(jsonAddress.getString("country"))
                .street(jsonAddress.getString("address"))
                .lastName(jsonAddress.getString("last_name"))
                .firstName(jsonAddress.getString("first_name"))
                .clientName(clientName)
                .build();
    }

    private ShippingMethod buildShippingMethod(JSONObject jsonShippingMethod) throws Exception {
        return new ShippingMethod.Builder()
                .id(jsonShippingMethod.getInt("id"))
                .mEstimatedTime(jsonShippingMethod.getString("time"))
                .name(jsonShippingMethod.getString("title"))
                .description(jsonShippingMethod.getString("description"))
                .build();
    }

    private ArrayList<Product> buildProducts(JSONArray jsonProducts) throws Exception {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i < jsonProducts.length(); i++) {
            JSONObject object = jsonProducts.getJSONObject(i);
            ArrayList<Photo> photos = buildPhotos(object.getJSONArray("photos"));
            Product product = new Product.Builder()
                    .id(object.getInt("id"))
                    .barcode(object.getString("uid"))
                    .productName(object.getString("title"))
                    .productPrice(object.getString("price"))
                    .images(photos)
                    .productQuantity(object.getString("quantity"))
                    .build();
            products.add(product);
        }
        return products;
    }

    private ArrayList<Photo> buildPhotos(JSONArray jsonImages) throws Exception {
        ArrayList<Photo> photos = new ArrayList<>();
        for (int i = 0; i < jsonImages.length(); i++) {
            JSONObject object = jsonImages.getJSONObject(i);
            Photo photo = new Photo.Builder()
                    .bigImage(object.getString("photo"))
                    .smallImage(object.getString("photoThumbnail"))
                    .build();
            photos.add(photo);
        }
        return photos;
    }
}
