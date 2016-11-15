package com.softranger.bayshopmfr.ui.awaitingarrival;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.box.AwaitingArrival;
import com.softranger.bayshopmfr.model.box.AwaitingArrivalDetails;
import com.softranger.bayshopmfr.model.tracking.Checkpoint;
import com.softranger.bayshopmfr.model.tracking.Courier;
import com.softranger.bayshopmfr.model.tracking.CourierService;
import com.softranger.bayshopmfr.model.tracking.TrackApiResponse;
import com.softranger.bayshopmfr.model.tracking.TrackingResult;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.general.DeclarationActivity;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.ui.services.AdditionalPhotoFragment;
import com.softranger.bayshopmfr.ui.services.CheckProductFragment;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;
import com.softranger.bayshopmfr.util.widget.ParcelStatusBarView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AwaitingArrivalProductFragment extends ParentFragment implements ParcelStatusBarView.OnStatusBarReadyListener {

    private static final String PRODUCT_ARG = "product";

    @BindView(R.id.awaitingDetailsItemId) TextView mProductId;
    @BindView(R.id.awaitingDetailsProductName) TextView mProductName;
    @BindView(R.id.awaitingDetailsProductTracking) TextView mProductTracking;
    @BindView(R.id.awaitingDetailsDate) TextView mProductDate;
    @BindView(R.id.awaitingDetailsPrice) TextView mProductPrice;
    @BindView(R.id.awaitingDetailsCheckProductBtn)
    TextView mCheckProduct;
    @BindView(R.id.awaitingDetailsAdditionalPhotosBtn)
    TextView mAdditionalPhoto;
    @BindView(R.id.noPhotoLayoutHolder) LinearLayout mNoPhotosHolder;
    @BindView(R.id.awaitingArrivalDetailsLayout) LinearLayout mHolderLayout;
    @BindView(R.id.awaitingTrackingStatusBarView)
    ParcelStatusBarView mStatusBarView;

    private ParentActivity mActivity;
    private Unbinder mUnbinder;
    private Call<ServerResponse<AwaitingArrivalDetails>> mCall;
    private AwaitingArrivalDetails mArrivalDetails;
    private String mAwaitingArrivalId;
    private AlertDialog mAlertDialog;

    public AwaitingArrivalProductFragment() {
        // Required empty public constructor
    }

    public static AwaitingArrivalProductFragment newInstance(String awaitingArrivalId) {
        Bundle args = new Bundle();
        args.putString(PRODUCT_ARG, awaitingArrivalId);
        AwaitingArrivalProductFragment fragment = new AwaitingArrivalProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_awaiting_arrival_product, container, false);
        mActivity = (ParentActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, rootView);

        mStatusBarView.setNewColorsMap(AwaitingArrivalFragment.COLOR_MAP);
        mStatusBarView.setOnStatusBarReadyListener(this);

        // hide all layout while we don't have detailed parcel
        mHolderLayout.setVisibility(View.GONE);

        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(AwaitingArrivalFragment.ACTION_LIST_CHANGED);
        intentFilter.addAction(Application.ACTION_RETRY);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);

        mAwaitingArrivalId = getArguments().getString(PRODUCT_ARG);

        mNoPhotosHolder.setVisibility(View.VISIBLE);

        mCall = Application.apiInterface().getAwaitingParcelDetails(mAwaitingArrivalId);
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
                case Application.ACTION_RETRY:
                    mActivity.removeNoConnectionView();
                    mActivity.toggleLoadingProgress(true);
                    refreshFragment();
                    break;
                case AwaitingArrivalFragment.ACTION_LIST_CHANGED:
                    refreshFragment();
                    break;
            }
        }
    };

    @Override
    public void refreshFragment() {
        mCall = Application.apiInterface().getAwaitingParcelDetails(mAwaitingArrivalId);
        mCall.enqueue(mResponseCallback);
    }

    private ResponseCallback<AwaitingArrivalDetails> mResponseCallback = new ResponseCallback<AwaitingArrivalDetails>() {
        @Override
        public void onSuccess(AwaitingArrivalDetails data) {
            mArrivalDetails = data;
            setDataInPlace(mArrivalDetails);
            mActivity.toggleLoadingProgress(false);
            getTrackingCourierService(data);
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
        int quantity = arrivalDetails.getProducts().size();
        String desc = quantity + " " + (quantity > 1 ? getString(R.string.products) : getString(R.string.product));
        mProductName.setText(desc);
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
        if (mArrivalDetails == null) return;
        Intent editParcel = new Intent(mActivity, DeclarationActivity.class);
        editParcel.putExtra(DeclarationActivity.SHOW_TRACKING, true);
        editParcel.putExtra(DeclarationActivity.PRODUCTS_ARRAY, mArrivalDetails.getProducts());
        editParcel.putExtra(DeclarationActivity.AWAITING_ID, mArrivalDetails.getId());
        editParcel.putExtra(DeclarationActivity.TRACKING_NUM, mArrivalDetails.getTracking());
        mActivity.startActivityForResult(editParcel, AwaitingArrivalFragment.ADD_PARCEL_RC);
    }

    private void getTrackingCourierService(AwaitingArrival data) {
        String trackingNumber = data.getTracking();
        // get courier service by tracking number
        Application.trackApiInterface().detectCourierService(trackingNumber)
                .subscribeOn(Schedulers.io())
                .subscribe(arrayListTrackApiResponse -> {
                    // on response get first courier service from the list
                    ArrayList<CourierService> services = arrayListTrackApiResponse.getData();
                    if (services != null && services.size() > 0) {
                        CourierService service = services.get(0);
                        Courier courier = service.getCourier();
                        if (courier != null) {
                            getTrackingInfo(data, courier.getSlug(), trackingNumber);
                        }
                    }
                });
    }

    private void getTrackingInfo(AwaitingArrival awaitingArrival, String slug, String trackingNumber) {
        Application.trackApiInterface().trackParcelByCourierAndTracking(slug, trackingNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackingResultTrackApiResponse -> {
                    if (trackingResultTrackApiResponse.getResult() == TrackApiResponse.Result.waiting) {
                        getTrackingInfo(awaitingArrival, slug, trackingNumber);
                    } else {
                        // when we have an response show the progressbar
                        TrackingResult result = trackingResultTrackApiResponse.getData();
                        awaitingArrival.setTrackingResult(result);
                        if (result != null) {
                            onStatusBarReady();
                        }
                    }
                });
    }

    @OnClick(R.id.awaitingDetailsDeleteButton)
    void deleteCurrentParcel() {
        deleteItem(mArrivalDetails);
    }

    private void deleteItem(final AwaitingArrival product) {
        mAlertDialog = mActivity.getDialog(getString(R.string.delete), getString(R.string.confirm_deleting) + " "
                        + product.getTitle() + "?", R.mipmap.ic_delete_parcel_popup_30dp,
                getString(R.string.yes), v -> {
                    mActivity.toggleLoadingProgress(true);

                    Application.apiInterface().deleteAwaitingParcel(mArrivalDetails.getId()).enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getMessage().equals(Constants.ApiResponse.OK_MESSAGE)) {
                                    // get parcels count
                                    int count = Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL);
                                    // decrease it by one
                                    count -= 1;
                                    // put it back
                                    Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, count);
                                    Intent intent = new Intent(AwaitingArrivalFragment.ACTION_LIST_CHANGED);
                                    mActivity.sendBroadcast(intent);
                                    mActivity.onBackPressed();
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
                }, getString(R.string.no), v -> {
                    if (mAlertDialog != null) mAlertDialog.dismiss();
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

    @Override
    public void onStatusBarReady() {
        if (mArrivalDetails != null && mArrivalDetails.getTrackingResult() != null) {
            TrackingResult result = mArrivalDetails.getTrackingResult();
            if (result.getCheckpoints() != null && result.getCheckpoints().size() > 0) {
                Checkpoint checkpoint = result.getCheckpoints().get(0);
                String statusName = checkpoint.getCheckpointStatus() == Checkpoint.CheckpointStatus.other ?
                        checkpoint.getStatusName() : Application.getInstance().getString(checkpoint.getCheckpointStatus().translatedStatus());

                int statusStep;
                if (checkpoint.getCheckpointStatus() == Checkpoint.CheckpointStatus.delivered) {
                    statusStep = 2;
                } else {
                    statusStep = 1;
                }

                mStatusBarView.setProgress(statusStep, statusName);
            } else {
                mStatusBarView.setProgress(0, Application.getInstance().getString(R.string.geting_status));
            }
        } else {
            mStatusBarView.setProgress(0, Application.getInstance().getString(R.string.geting_status));
        }
    }
}
