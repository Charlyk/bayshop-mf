package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.AwaitingArrival;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.services.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.services.CheckProductFragment;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AwaitingArrivalProductFragment extends ParentFragment implements View.OnClickListener {

    private static final String PRODUCT_ARG = "product";
    public static final String ACTION_UPDATE = "update data";

    private TextView mProductId;
    private TextView mProductName;
    private TextView mProductTracking;
    private TextView mProductDate;
    private TextView mProductPrice;
    private Button mCheckProduct;
    private Button mAdditionalPhoto;
    private ImageView mStorageIcon;
    private MainActivity mActivity;
    private AwaitingArrival mAwaitingArrival;

    private static boolean isDeleteClicked;

    public AwaitingArrivalProductFragment() {
        // Required empty public constructor
    }

    public static AwaitingArrivalProductFragment newInstance(AwaitingArrival awaitingArrival) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, awaitingArrival);
        AwaitingArrivalProductFragment fragment = new AwaitingArrivalProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_awaiting_arrival_product, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(ACTION_UPDATE);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);
        mAwaitingArrival = getArguments().getParcelable(PRODUCT_ARG);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.awaitingArrivalDetailsImageList);
        LinearLayout noPhotosLayout = (LinearLayout) rootView.findViewById(R.id.noPhotoLayoutHolder);

        recyclerView.setVisibility(View.GONE);
        noPhotosLayout.setVisibility(View.VISIBLE);

        bindViews(rootView);
        mProductId.setText(mAwaitingArrival.getUid());
        mProductName.setText(mAwaitingArrival.getTitle());
        mProductTracking.setText(mAwaitingArrival.getTracking());
        mProductDate.setText(Application.getFormattedDate(mAwaitingArrival.getCreatedDate()));
        mProductPrice.setText(mAwaitingArrival.getPrice());
        mStorageIcon.setImageResource(getStorageIcon(Constants.US));
        mActivity.toggleLoadingProgress(true);
        ApiClient.getInstance().getRequest(Constants.Api.urlWaitingArrivalDetails(String.valueOf(mAwaitingArrival.getId())), mHandler);
        return rootView;
    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CheckProductFragment.ACTION_CHECK_IN_PROCESSING:
//                    mAwaitingArrival.setCheckInProgress(true);
                    mCheckProduct.setSelected(true);
                    mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING:
//                    mAwaitingArrival.setPhotoInProgress(true);
                    mAdditionalPhoto.setSelected(true);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST:
//                    mAwaitingArrival.setPhotoInProgress(false);
                    mAdditionalPhoto.setSelected(false);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.additional_photo));
                    break;
                case CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT:
//                    mAwaitingArrival.setCheckInProgress(false);
                    mCheckProduct.setSelected(false);
                    mCheckProduct.setText(mActivity.getString(R.string.check_product));
                    break;
                case ACTION_UPDATE:
                    ApiClient.getInstance().getRequest(Constants.Api.urlWaitingArrivalDetails(String.valueOf(mAwaitingArrival.getId())), mHandler);
                    Intent refresh = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
                    mActivity.sendBroadcast(refresh);
                    break;
            }
        }
    };

    private int getStorageIcon(String storage) {
        switch (storage) {
            case Constants.US:
                return R.mipmap.ic_usa_flag;
            case Constants.GB:
                return R.mipmap.ic_uk_flag;
            case Constants.DE:
                return R.mipmap.ic_de_flag;
        }
        return R.mipmap.ic_usa_flag;
    }

    private void setDataInPlace(Product product) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        mProductId.setText(product.getProductId());
        mProductName.setText(product.getProductName());
        mProductTracking.setText(product.getTrackingNumber());
        Date date = new Date();
        try {
            date = input.parse(product.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mProductDate.setText(output.format(date));
        String productPrice = product.getCurrency() + product.getProductPrice();
        mProductPrice.setText(productPrice);

        mCheckProduct.setSelected(product.isCheckInProgress());
        if (mCheckProduct.isSelected()) {
            mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
        }

        mAdditionalPhoto.setSelected(product.isPhotoInProgress());
        if (mAdditionalPhoto.isSelected()) {
            mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
        }
    }

    private void bindViews(View view) {
        // Text views
        mProductId = (TextView) view.findViewById(R.id.awaitingDetailsItemId);
        mProductName = (TextView) view.findViewById(R.id.awaitingDetailsProductName);
        mProductTracking = (TextView) view.findViewById(R.id.awaitingDetailsProductTracking);
        mProductDate = (TextView) view.findViewById(R.id.awaitingDetailsDate);
        mProductPrice = (TextView) view.findViewById(R.id.awaitingDetailsPrice);

        // Buttons

        Button editDetails = (Button) view.findViewById(R.id.awaitingDetailsEditButton);
        editDetails.setOnClickListener(this);

        ImageButton deleteProduct = (ImageButton) view.findViewById(R.id.awaitingDetailsDeleteButton);
        deleteProduct.setOnClickListener(this);

        mCheckProduct = (Button) view.findViewById(R.id.awaitingDetailsCheckProductBtn);
        mCheckProduct.setOnClickListener(this);

        mAdditionalPhoto = (Button) view.findViewById(R.id.awaitingDetailsAdditionalPhotosBtn);
        mAdditionalPhoto.setOnClickListener(this);

        // Image views
        mStorageIcon = (ImageView) view.findViewById(R.id.awaitingDetailsStorageIcon);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.awaitingDetailsEditButton:
//                mActivity.addFragment(EditAwaitingFragment.newInstance(mAwaitingArrival), true);
//                break;
//            case R.id.awaitingDetailsDeleteButton:
//                isDeleteClicked = true;
//                deleteItem(mAwaitingArrival);
//                break;
//            case R.id.awaitingDetailsCheckProductBtn:
//                mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mAwaitingArrival.getId()),
//                        mAwaitingArrival.isCheckInProgress(), true), true);
//                break;
//            case R.id.awaitingDetailsAdditionalPhotosBtn:
//                mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mAwaitingArrival.getId()),
//                        mAwaitingArrival.isPhotoInProgress(), true), true);
//                break;
        }
    }
    AlertDialog dialog = null;
    private void deleteItem(final Product product) {
        dialog = mActivity.getDialog(getString(R.string.delete), getString(R.string.confirm_deleting) + " "
                + mAwaitingArrival.getTitle() + "?", R.mipmap.ic_delete_box_24dp,
                getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiClient.getInstance().delete(Constants.Api.urlWaitingArrivalDetails(
                                String.valueOf(product.getID())), mHandler);
                        mActivity.toggleLoadingProgress(true);
                        dialog.dismiss();
                        int count = Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL);
                        count -= 1;
                        Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, count);
                        mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
                    }
                }, getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) dialog.dismiss();
                    }
                }, 0);
        if (dialog != null) dialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mStatusReceiver);
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        if (isDeleteClicked) {
            mActivity.toggleLoadingProgress(true);
            Intent intent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
            intent.putExtra("deposit", Constants.US);
            mActivity.sendBroadcast(intent);
            mActivity.onBackPressed();
        } else {
//            JSONObject data = response.getJSONObject("data");
//            mAwaitingArrival.setDate(data.getString("createdDate"));
//            mAwaitingArrival.setCurrency(data.getString("currency"));
//            mAwaitingArrival.setProductPrice(data.getString("price"));
//            mAwaitingArrival.setProductUrl(data.getString("productUrl"));
//            mAwaitingArrival.setCheckInProgress(data.getInt("verificationPackageRequested") == 1);
//            mAwaitingArrival.setCheckComment(data.getString("verificationPackageRequestedComments"));
//            mAwaitingArrival.setPhotoInProgress(data.getInt("photosPackageRequested") == 1);
//            mAwaitingArrival.setPhotoComment(data.getString("photosPackageRequestedComments"));
//            setDataInPlace(mAwaitingArrival);
        }
    }

    @Override
    public void onHandleMessageEnd() {
        isDeleteClicked = false;
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.awaiting_arrival);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.awaiting_arrival;
    }
}
