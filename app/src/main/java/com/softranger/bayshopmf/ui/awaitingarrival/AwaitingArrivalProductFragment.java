package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.services.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.services.CheckProductFragment;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.ui.general.WebViewFragment;
import com.softranger.bayshopmf.util.Constants;

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

    private TextView mProductId, mProductName, mProductTracking, mProductDate, mProductPrice, mPhotoState;
    private Button mGoToUrl, mEditDetails, mDeleteProduct, mCheckProduct, mAdditionalPhoto;
    private ImageView mStorageIcon, mPhotoPreview;

    private MainActivity mActivity;
    private Product mProduct;
    private View mRootView;

    private static boolean isDeleteClicked;

    public AwaitingArrivalProductFragment() {
        // Required empty public constructor
    }

    public static AwaitingArrivalProductFragment newInstance(Product product) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, product);
        AwaitingArrivalProductFragment fragment = new AwaitingArrivalProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_awaiting_arrival_product, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(ACTION_UPDATE);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);
        mProduct = getArguments().getParcelable(PRODUCT_ARG);
        bindViews(mRootView);
        mActivity.setToolbarTitle(mProduct.getProductId(), true);
        mProductId.setText(mProduct.getProductId());
        mProductName.setText(mProduct.getProductName());
        mProductTracking.setText(mProduct.getTrackingNumber());
        mProductDate.setText(mProduct.getDate());
        mProductPrice.setText(mProduct.getProductPrice());
        mStorageIcon.setImageResource(getStorageIcon(mProduct.getDeposit()));
        mActivity.toggleLoadingProgress(true);
        ApiClient.getInstance().getRequest(Constants.Api.urlWaitingArrivalDetails(String.valueOf(mProduct.getID())), mHandler);
        return mRootView;
    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CheckProductFragment.ACTION_CHECK_IN_PROCESSING:
                    mProduct.setCheckInProgress(true);
                    mCheckProduct.setSelected(true);
                    mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING:
                    mProduct.setPhotoInProgress(true);
                    mAdditionalPhoto.setSelected(true);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST:
                    mProduct.setPhotoInProgress(false);
                    mAdditionalPhoto.setSelected(false);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.additional_photo));
                    break;
                case CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT:
                    mProduct.setCheckInProgress(false);
                    mCheckProduct.setSelected(false);
                    mCheckProduct.setText(mActivity.getString(R.string.check_product));
                    break;
                case ACTION_UPDATE:
                    ApiClient.getInstance().getRequest(Constants.Api.urlWaitingArrivalDetails(String.valueOf(mProduct.getID())), mHandler);
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
        mProductPrice.setText(product.getCurrency() + product.getProductPrice());

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
        mPhotoState = (TextView) view.findViewById(R.id.awaitingDetailsStateDescription);

        // Buttons
        mGoToUrl = (Button) view.findViewById(R.id.awaitingDetailsGoToUrlBtn);
        mGoToUrl.setOnClickListener(this);

        mEditDetails = (Button) view.findViewById(R.id.awaitingDetailsEditButton);
        mEditDetails.setOnClickListener(this);

        mDeleteProduct = (Button) view.findViewById(R.id.awaitingDetailsDeleteButton);
        mDeleteProduct.setOnClickListener(this);

        mCheckProduct = (Button) view.findViewById(R.id.awaitingDetailsCheckProductBtn);
        mCheckProduct.setOnClickListener(this);

        mAdditionalPhoto = (Button) view.findViewById(R.id.awaitingDetailsAdditionalPhotosBtn);
        mAdditionalPhoto.setOnClickListener(this);

        // Image views
        mStorageIcon = (ImageView) view.findViewById(R.id.awaitingDetailsStorageIcon);
        mPhotoPreview = (ImageView) view.findViewById(R.id.awaitingDetailsPhotoPreview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.awaitingDetailsGoToUrlBtn:
                if (mProduct.getProductUrl() == null || mProduct.getProductUrl().equals("")) {
                    Snackbar.make(mRootView, "There is no url for this product", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mActivity.addFragment(WebViewFragment.newInstance(mProduct.getProductUrl()), true);
                break;
            case R.id.awaitingDetailsEditButton:
                mActivity.addFragment(EditAwaitingFragment.newInstance(mProduct), true);
                break;
            case R.id.awaitingDetailsDeleteButton:
                isDeleteClicked = true;
                deleteItem(mProduct);
                break;
            case R.id.awaitingDetailsCheckProductBtn:
                mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mProduct.getID()),
                        mProduct.isCheckInProgress(), true), true);
                break;
            case R.id.awaitingDetailsAdditionalPhotosBtn:
                mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mProduct.getID()),
                        mProduct.isPhotoInProgress(), true), true);
                break;
        }
    }
    AlertDialog dialog = null;
    private void deleteItem(final Product product) {
        dialog = mActivity.getDialog("Delete", getString(R.string.confirm_deleting) + " "
                + mProduct.getProductName() + "?", R.mipmap.ic_delete_box_24dpi,
                "Yes", new View.OnClickListener() {
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
                }, "No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) dialog.dismiss();
                    }
                });
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
            mActivity.toggleLoadingProgress(false);
            Intent intent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
            intent.putExtra("deposit", mProduct.getDeposit());
            mActivity.sendBroadcast(intent);
            mActivity.onBackPressed();
        } else {
            JSONObject data = response.getJSONObject("data");
            mProduct.setDate(data.getString("createdDate"));
            mProduct.setCurrency(data.getString("currency"));
            mProduct.setProductPrice(data.getString("price"));
            mProduct.setProductUrl(data.getString("productUrl"));
            mProduct.setCheckInProgress(data.getInt("verificationPackageRequested") == 1);
            mProduct.setCheckComment(data.getString("verificationPackageRequestedComments"));
            mProduct.setPhotoInProgress(data.getInt("photosPackageRequested") == 1);
            mProduct.setPhotoComment(data.getString("photosPackageRequestedComments"));
            setDataInPlace(mProduct);
        }
    }

    @Override
    public void onHandleMessageEnd() {
        isDeleteClicked = false;
        mActivity.toggleLoadingProgress(false);
    }
}
