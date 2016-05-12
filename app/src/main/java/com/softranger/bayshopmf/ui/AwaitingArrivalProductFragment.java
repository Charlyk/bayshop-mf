package com.softranger.bayshopmf.ui;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.ui.general.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.general.CheckProductFragment;
import com.softranger.bayshopmf.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AwaitingArrivalProductFragment extends Fragment implements View.OnClickListener {

    private static final String PRODUCT_ARG = "product";

    private TextView mProductId, mProductName, mProductTracking, mProductDate, mProductPrice, mPhotoState;
    private Button mGoToUrl, mEditDetails, mDeleteProduct, mCheckProduct, mAdditionalPhoto;
    private ImageView mStorageIcon, mPhotoPreview;

    private MainActivity mActivity;
    private Product mProduct;

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
        View view = inflater.inflate(R.layout.fragment_awaiting_arrival_product, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);
        mProduct = getArguments().getParcelable(PRODUCT_ARG);
        bindViews(view);
        mActivity.setToolbarTitle(mProduct.getProductId(), true);
        mProductId.setText(mProduct.getProductId());
        mProductName.setText(mProduct.getProductName());
        mProductTracking.setText(mProduct.getTrackingNumber());
        mProductDate.setText(mProduct.getDate());
        mProductPrice.setText(mProduct.getProductPrice());
        mStorageIcon.setImageResource(getStorageIcon(mProduct.getDeposit()));
        return view;
    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CheckProductFragment.ACTION_CHECK_IN_PROCESSING:
                    mCheckProduct.setSelected(true);
                    mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING:
                    mAdditionalPhoto.setSelected(true);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
                    break;
            }
        }
    };

    private int getStorageIcon(String storage) {
        switch (storage) {
            case Constants.USA:
                return R.mipmap.ic_usa_flag;
            case Constants.UK:
                return R.mipmap.ic_uk_flag;
            case Constants.DE:
                return R.mipmap.ic_de_flag;
        }
        return R.mipmap.ic_usa_flag;
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
                mActivity.addFragment(WebViewFragment.newInstance(mProduct.getProductUrl()), true);
                break;
            case R.id.awaitingDetailsEditButton:
                mActivity.addFragment(EditAwaitingFragment.newInstance(mProduct), true);
                break;
            case R.id.awaitingDetailsDeleteButton:
                deleteItem(mProduct);
                break;
            case R.id.awaitingDetailsCheckProductBtn:
                mActivity.addFragment(new CheckProductFragment(), true);
                break;
            case R.id.awaitingDetailsAdditionalPhotosBtn:
                mActivity.addFragment(new AdditionalPhotoFragment(), true);
                break;
        }
    }

    private void deleteItem(Product product) {
        View deleteDialog = LayoutInflater.from(mActivity).inflate(R.layout.delete_dialog, null, false);
        Button cancel = (Button) deleteDialog.findViewById(R.id.dialog_cancel_buttonn);
        Button delete = (Button) deleteDialog.findViewById(R.id.dialog_delete_button);
        final AlertDialog dialog = new AlertDialog.Builder(mActivity).setView(deleteDialog).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mStatusReceiver);
    }
}
