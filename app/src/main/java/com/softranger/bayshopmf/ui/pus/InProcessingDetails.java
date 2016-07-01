package com.softranger.bayshopmf.ui.pus;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.softranger.bayshopmf.model.packages.Received;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.auth.ForgotResultFragment;
import com.softranger.bayshopmf.ui.general.AddressesListFragment;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.gallery.GalleryActivity;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProcessingDetails<T extends PUSParcel> extends ParentFragment implements ImagesAdapter.OnImageClickListener,
        InProcessingDetailsAdapter.OnItemClickListener {

    private static final String PRODUCT_ARG = "in processing arguments";

    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private String mDeposit;
    private T mPackage;
    private AlertDialog mAlertDialog;
    private InProcessingDetailsAdapter<T> mAdapter;

    public InProcessingDetails() {
        // Required empty public constructor
    }

    public static <T extends PUSParcel> InProcessingDetails newInstance(@NonNull T product) {
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
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        intentFilter.addAction(Constants.ACTION_CHANGE_ADDRESS);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
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

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    switch (mPackage.getStatus()) {
                        case Constants.ParcelStatus.IN_PROCESSING:
                            mActivity.setToolbarTitle(getString(R.string.in_processing), true);
                            break;
                        case Constants.ParcelStatus.PACKED:
                            mActivity.setToolbarTitle(getString(R.string.awaiting_sending), true);
                            break;
                        case Constants.ParcelStatus.SENT:
                            mActivity.setToolbarTitle(getString(R.string.sent), true);
                            break;
                        case Constants.ParcelStatus.RECEIVED:
                            mActivity.setToolbarTitle(getString(R.string.received), true);
                            break;
                        case Constants.ParcelStatus.LOCAL_DEPO:
                            mActivity.setToolbarTitle(getString(R.string.local_deposit), true);
                            break;
                        case Constants.ParcelStatus.TAKEN_TO_DELIVERY:
                            mActivity.setToolbarTitle(getString(R.string.take_to_delivery), true);
                            break;
                        case Constants.ParcelStatus.CUSTOMS_HELD:
                            mActivity.setToolbarTitle(getString(R.string.held_by_customs), true);
                            break;
                        case Constants.ParcelStatus.DEPT:
                            mActivity.setToolbarTitle(getString(R.string.held_due_to_debt), true);
                            break;
                        case Constants.ParcelStatus.HELD_BY_PROHIBITION:
                            mActivity.setToolbarTitle(getString(R.string.held_by_prohibition), true);
                            break;
                    }
                    break;
                case Constants.ACTION_CHANGE_ADDRESS:
                    Address address = intent.getExtras().getParcelable("address");
                    mPackage.setAddress(address);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");
        mPackage = buildParcelDetails(data);
        mAdapter = new InProcessingDetailsAdapter<>(mPackage, InProcessingDetails.this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public <P extends PUSParcel> void onUploadDocumentClick(P item, int position) {

    }

    @Override
    public <P extends PUSParcel> void onTakePictureClick(P item, int position) {

    }

    @Override
    public <P extends PUSParcel> void onReturnToSenderClick(P item, int position) {

    }

    @Override
    public <P extends PUSParcel> void onConfirmAddressClick(P item, int position) {

    }

    @Override
    public <P extends PUSParcel> void onOrderDeliveryClick(P item, int position) {
        mAlertDialog = mActivity.getDialog(getString(R.string.confirm), getString(R.string.confirm_delivery)
                        + " " + item.getAddress().getClientName(), R.mipmap.ic_white_magic_kamaz_24dp,
                getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.addFragment(ForgotResultFragment.newInstance(getString(R.string.order_sent),
                                R.drawable.ic_sent, getString(R.string.thank_you), getString(R.string.please_wait_call),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mActivity.onBackPressed();
                                    }
                                }), false);
                        mAlertDialog.dismiss();
                    }
                }, getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
        mAlertDialog.show();
    }

    @Override
    public <P extends PUSParcel> void onSelectAddressClick(P item, int position) {
        mActivity.addFragment(AddressesListFragment.newInstance(true), true);
    }

    @Override
    public <P extends PUSParcel> void onSignatureOnMapClick(P item, int position) {
        mActivity.addFragment(ReceivedSignature.newInstance((Received) item), false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mTitleReceiver);
    }
}
