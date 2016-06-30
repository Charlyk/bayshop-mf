package com.softranger.bayshopmf.ui.inprocessing;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.softranger.bayshopmf.ui.ParentFragment;
import com.softranger.bayshopmf.ui.gallery.GalleryActivity;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProcessingDetails<T extends PUSParcel> extends ParentFragment implements ImagesAdapter.OnImageClickListener {

    private static final String PRODUCT_ARG = "in processing arguments";

    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private String mDeposit;
    private T mPackage;

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.inProcessingDetailsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        ApiClient.getInstance().getRequest(Constants.Api.urlViewParcelDetails(String
                .valueOf(mPackage.getId())), mHandler);
        mActivity.toggleLoadingProgress(true);
        return view;
    }

    @Override
    public void onImageClick(ArrayList<Photo> images, int position) {
        Intent intent = new Intent(mActivity, GalleryActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("position", position);
        mActivity.startActivity(intent);
    }

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
                    .insuranceCommission(data.optDouble("insuranceCommission", 0))
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

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");
        mPackage = buildParcelDetails(data);
        InProcessingDetailsAdapter<T> adapter = new InProcessingDetailsAdapter<>(mPackage, InProcessingDetails.this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }
}
