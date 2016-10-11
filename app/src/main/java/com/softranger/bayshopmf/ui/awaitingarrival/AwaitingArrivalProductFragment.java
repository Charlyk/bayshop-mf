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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.AwaitingArrival;
import com.softranger.bayshopmf.model.box.AwaitingArrivalDetails;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.services.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.services.CheckProductFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AwaitingArrivalProductFragment extends ParentFragment {

    private static final String PRODUCT_ARG = "product";
    public static final String ACTION_UPDATE = "update data";

    @BindView(R.id.awaitingDetailsItemId) TextView mProductId;
    @BindView(R.id.awaitingDetailsProductName) TextView mProductName;
    @BindView(R.id.awaitingDetailsProductTracking) TextView mProductTracking;
    @BindView(R.id.awaitingDetailsDate) TextView mProductDate;
    @BindView(R.id.awaitingDetailsPrice) TextView mProductPrice;
    @BindView(R.id.awaitingDetailsCheckProductBtn) Button mCheckProduct;
    @BindView(R.id.awaitingDetailsAdditionalPhotosBtn) Button mAdditionalPhoto;
    @BindView(R.id.awaitingDetailsStorageIcon) ImageView mStorageIcon;
    @BindView(R.id.awaitingArrivalDetailsImageList) RecyclerView mRecyclerView;
    @BindView(R.id.noPhotoLayoutHolder) LinearLayout mNoPhotosHolder;
    @BindView(R.id.awaitingArrivalDetailsLayout) LinearLayout mHolderLayout;

    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private Call<ServerResponse<AwaitingArrivalDetails>> mCall;
    private AwaitingArrivalDetails mArrivalDetails;
    private AwaitingArrival mAwaitingArrival;
    private AlertDialog mAlertDialog;

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
        mUnbinder = ButterKnife.bind(this, rootView);

        // hide all layout while we don't have detailed parcel
        mHolderLayout.setVisibility(View.GONE);

        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(ACTION_UPDATE);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);

        mAwaitingArrival = getArguments().getParcelable(PRODUCT_ARG);

        mNoPhotosHolder.setVisibility(View.VISIBLE);

        mStorageIcon.setImageResource(R.mipmap.ic_usa_flag);

        mCall = Application.apiInterface().getAwaitingParcelDetails(mAwaitingArrival.getId());
        mActivity.toggleLoadingProgress(true);
        mCall.enqueue(mResponseCallback);
        return rootView;
    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CheckProductFragment.ACTION_CHECK_IN_PROCESSING:
                    mArrivalDetails.setVerificationRequested(1);
                    mCheckProduct.setSelected(true);
                    mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING:
                    mArrivalDetails.setPhotoRequested(1);
                    mAdditionalPhoto.setSelected(true);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST:
                    mArrivalDetails.setPhotoRequested(0);
                    mAdditionalPhoto.setSelected(false);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.additional_photo));
                    break;
                case CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT:
                    mArrivalDetails.setVerificationRequested(0);
                    mCheckProduct.setSelected(false);
                    mCheckProduct.setText(mActivity.getString(R.string.check_product));
                    break;
                case ACTION_UPDATE:
                    mCall = Application.apiInterface().getAwaitingParcelDetails(mAwaitingArrival.getId());
                    mCall.enqueue(mResponseCallback);
                    Intent refresh = new Intent(AddAwaitingFragment.ACTION_ITEM_ADDED);
                    mActivity.sendBroadcast(refresh);
                    break;
            }
        }
    };

    private ResponseCallback<AwaitingArrivalDetails> mResponseCallback = new ResponseCallback<AwaitingArrivalDetails>() {
        @Override
        public void onSuccess(AwaitingArrivalDetails data) {
            mArrivalDetails = data;
            setDataInPlace(mArrivalDetails);
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<AwaitingArrivalDetails>> call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            mActivity.toggleLoadingProgress(false);
        }
    };

    private void setDataInPlace(AwaitingArrivalDetails arrivalDetails) {
        mProductId.setText(arrivalDetails.getUid());
        mProductName.setText(arrivalDetails.getTitle());
        mProductTracking.setText(arrivalDetails.getTracking());

        mProductDate.setText(Application.getFormattedDate(arrivalDetails.getCreatedDate()));
        String productPrice = arrivalDetails.getCurrency() + arrivalDetails.getPrice();
        mProductPrice.setText(productPrice);

        mCheckProduct.setSelected(arrivalDetails.getVerificationRequested() == 1);
        if (mCheckProduct.isSelected()) {
            mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
        }

        mAdditionalPhoto.setSelected(arrivalDetails.getPhotoRequested() == 1);
        if (mAdditionalPhoto.isSelected()) {
            mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
        }

        mHolderLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.awaitingDetailsCheckProductBtn)
    void toggleCheckProduct() {
        mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mArrivalDetails.getId()),
                mArrivalDetails.getVerificationRequested() != 0, true), true);
    }

    @OnClick(R.id.awaitingDetailsAdditionalPhotosBtn)
    void toggleAdditionalPhotos() {
        mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mArrivalDetails.getId()),
                mArrivalDetails.getPhotoRequested() != 0, true), true);
    }

    @OnClick(R.id.awaitingDetailsEditButton)
    void editParcelDetails() {
        mActivity.addFragment(EditAwaitingFragment.newInstance(mArrivalDetails), true);
    }

    @OnClick(R.id.awaitingDetailsDeleteButton)
    void deleteCurrentParcel() {
        deleteItem(mArrivalDetails);
    }

    private void deleteItem(final AwaitingArrival product) {
        mAlertDialog = mActivity.getDialog(getString(R.string.delete), getString(R.string.confirm_deleting) + " "
                        + product.getTitle() + "?", R.mipmap.ic_delete_box_24dp,
                getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.toggleLoadingProgress(true);

                        Application.apiInterface().deleteAwaitingParcel(mArrivalDetails.getId()).enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().getMessage().equals(Constants.ApiResponse.OK_MESSAGE)) {
                                        Intent intent = new Intent(AddAwaitingFragment.ACTION_ITEM_ADDED);
                                        mActivity.sendBroadcast(intent);
                                        mActivity.onBackPressed();

                                        // get parcels count
                                        int count = Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL);
                                        // decrease it by one
                                        count -= 1;
                                        // put it back
                                        Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, count);
                                        // update counters from menu
                                        mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
                                    } else {
                                        Toast.makeText(mActivity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    try {
                                        ServerResponse serverResponse = new ObjectMapper().readValue(response.errorBody().string(), ServerResponse.class);
                                        Toast.makeText(mActivity, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                mActivity.toggleLoadingProgress(false);
                            }
                        });

                        // close the dialog
                        mAlertDialog.dismiss();
                    }
                }, getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAlertDialog != null) mAlertDialog.dismiss();
                    }
                }, 0);
        if (mAlertDialog != null) mAlertDialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mStatusReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
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
