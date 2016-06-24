package com.softranger.bayshopmf.ui.inprocessing;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ImagesAdapter;
import com.softranger.bayshopmf.adapter.InProcessingDetailsAdapter;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.Photo;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.ShippingMethod;
import com.softranger.bayshopmf.model.packages.PUSParcel;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.gallery.GalleryActivity;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProcessingDetails<T extends PUSParcel> extends Fragment implements ImagesAdapter.OnImageClickListener {

    private static final String PRODUCT_ARG = "in processing arguments";

    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private ArrayList<Object> mObjects;
    private PUSParcel mDetailedPUSParcel;
    private String mDeposit;
    private T mPackage;
    private InProcessingDetailsAdapter<T> mAdapter;

    public InProcessingDetails() {
        // Required empty public constructor
    }

    public static<T extends PUSParcel> InProcessingDetails newInstance(@NonNull T product) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, product);
        InProcessingDetails<T> fragment = new InProcessingDetails<>();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_processing_details, container, false);
        mActivity = (MainActivity) getActivity();
        mPackage = getArguments().getParcelable(PRODUCT_ARG);
        mDeposit = mPackage.getDeposit();
        mObjects = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.inProcessingDetailsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        ApiClient.getInstance().sendRequest(Constants.Api.urlViewParcelDetails(String
                .valueOf(mPackage.getId())), mDetailsHandler);
        return view;
    }

    @Override
    public void onImageClick(ArrayList<Photo> images, int position) {
        Intent intent = new Intent(mActivity, GalleryActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("position", position);
        mActivity.startActivity(intent);
    }

    private Handler mDetailsHandler = new Handler(Looper.getMainLooper()) {
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
                            mPackage = buildParcelDetails(data);
                            mAdapter = new InProcessingDetailsAdapter<>(mPackage, InProcessingDetails.this);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
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
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };

    private T buildParcelDetails(JSONObject data) {
        try {
            final String parcelStatus = data.getString("status");

            Address address = buildAddress(data.getJSONObject("address"));
            ShippingMethod shippingMethod = buildShippingMethod(data.getJSONObject("shipping"));
            ArrayList<Product> products = buildProducts(data.getJSONArray("boxes"));

            T.Builder<T> packageBuilder = new T.Builder<>(mPackage)
                    .id(data.getInt("id"))
                    .codeNumber(data.getString("uid"))
                    .created(data.getString("created"))
                    .currency(data.getString("currency"))
                    .totalPrice(data.getDouble("totalPrice"))
                    .deliveryPrice(data.getDouble("deliveryPrice"))
                    .name(data.getString("generalDescription"))
                    .insuranceCommission(data.getDouble("insuranceCommission"))
                    .status(data.getString("status"))
                    .address(address)
                    .shippingMethod(shippingMethod)
                    .products(products)
                    .deposit(mDeposit);

            switch (parcelStatus) {
                case Constants.ParcelStatus.IN_PROCESSING:
                    packageBuilder.percentage((int) data.getDouble("percent"));
                    break;
                case Constants.ParcelStatus.PACKED:
                    packageBuilder.packedTime(data.getString("packedTime"));
                    packageBuilder.percentage((int) data.getDouble("percent"));
                    break;
                case Constants.ParcelStatus.SENT:
                    packageBuilder.sentTime(data.getString("sentTime"));
                    packageBuilder.trackingNumber(data.getString("tracking"));
                    break;
                case Constants.ParcelStatus.RECEIVED:
                    packageBuilder.receivedTime(data.getString("receivedTime"));
                    break;
                case Constants.ParcelStatus.LOCAL_DEPO:
                    packageBuilder.localDepotTime(data.getString("localDepoTime"));
                    break;
                case Constants.ParcelStatus.TAKEN_TO_DELIVERY:
                    packageBuilder.takenToDeliveryTime(data.getString("takenToDeliveryTime"));
                    break;
                case Constants.ParcelStatus.CUSTOMS_HELD:
                    packageBuilder.customsHeldTime(data.getString("customsHeldTime"));
                    break;
                case Constants.ParcelStatus.DEPT:

                    break;
                case Constants.ParcelStatus.HELD_BY_PROHIBITION:
                    packageBuilder.prohibitionHeldReason(data.getString("prohibitionHeldReason"));
                    break;
            }
            return packageBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Address buildAddress(JSONObject jsonAddress) throws Exception {
        return new Address.Builder()
                .postalCode(jsonAddress.getString("zip"))
                .city(jsonAddress.getString("city"))
                .state(jsonAddress.getString("state"))
                .phoneNumber(jsonAddress.getString("phone"))
                .country(jsonAddress.getString("country"))
                .street(jsonAddress.getString("address"))
                .lastName(jsonAddress.getString("last_name"))
                .firstName(jsonAddress.getString("first_name"))
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
