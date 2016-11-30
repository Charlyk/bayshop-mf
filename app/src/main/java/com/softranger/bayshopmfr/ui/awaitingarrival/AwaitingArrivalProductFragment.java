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

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.box.AwaitingArrival;
import com.softranger.bayshopmfr.model.box.AwaitingArrivalDetails;
import com.softranger.bayshopmfr.model.box.TrackingInfo;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AwaitingArrivalProductFragment extends ParentFragment implements ParcelStatusBarView.OnStatusBarReadyListener {

    private static final String PRODUCT_ARG = "product";
    public static final String ACTION_ITEM_CHANGED = "com.softranger.bayshopmfr.ui.awaitingarrival.ITEM_CHANGED";

    @BindView(R.id.awaitingDetailsItemId) TextView mProductId;
    @BindView(R.id.awaitingDetailsProductName) TextView mProductName;
    @BindView(R.id.awaitingDetailsProductTracking) TextView mProductTracking;
    @BindView(R.id.awaitingDetailsDate) TextView mProductDate;
    @BindView(R.id.awaitingDetailsPrice) TextView mProductPrice;
    @BindView(R.id.preCheckTitle)
    TextView mCheckProduct;
    @BindView(R.id.preAditionalPhotosTitle)
    TextView mAdditionalPhoto;
    @BindView(R.id.preAdditionalPhotosPrice)
    TextView mPhotoPrice;
    @BindView(R.id.preCheckPrice)
    TextView mCheckingPrice;
    @BindView(R.id.noPhotoLayoutHolder) LinearLayout mNoPhotosHolder;
    @BindView(R.id.awaitingArrivalDetailsLayout) LinearLayout mHolderLayout;
    @BindView(R.id.awaitingTrackingStatusBarView)
    ParcelStatusBarView mStatusBarView;
    @BindView(R.id.awaitingItemStatusLabel)
    TextView mStatusLabel;

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

        mStatusLabel.setAlpha(0f);
        mStatusBarView.setStatusNameLabel(mStatusLabel);
        mStatusBarView.setNewColorsMap(AwaitingArrivalFragment.COLOR_MAP);
        mStatusBarView.setOnStatusBarReadyListener(this);

        // hide all layout while we don't have detailed parcel
        mHolderLayout.setVisibility(View.GONE);

        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(ACTION_ITEM_CHANGED);
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
                // action received when user requested product verification
                case CheckProductFragment.ACTION_CHECK_IN_PROCESSING:
                    mArrivalDetails.setVerificationRequested(1);
                    mCheckProduct.setSelected(true);
                    mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
                    break;
                // action receiver when user requested additional photos
                case AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING:
                    mArrivalDetails.setPhotoRequested(1);
                    mAdditionalPhoto.setSelected(true);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
                    break;
                // action received when user canceled additional photos request
                case AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST:
                    mArrivalDetails.setPhotoRequested(0);
                    mAdditionalPhoto.setSelected(false);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.additional_photo));
                    break;
                // action received when user canceled product verification request
                case CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT:
                    mArrivalDetails.setVerificationRequested(0);
                    mCheckProduct.setSelected(false);
                    mCheckProduct.setText(mActivity.getString(R.string.check_product));
                    break;
                // action received from no internet view and used to send details request again
                case Application.ACTION_RETRY:
                    mActivity.removeNoConnectionView();
                    mActivity.toggleLoadingProgress(true);
                    refreshFragment();
                    break;
                // action received from onActivityResult() of AwaitingArrivalActivity
                // used to refresh current fragment details
                case ACTION_ITEM_CHANGED:
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

    /**
     * Response callback which receives the response with a detailed awaiting arrival parcel
     */
    private ResponseCallback<AwaitingArrivalDetails> mResponseCallback = new ResponseCallback<AwaitingArrivalDetails>() {
        @Override
        public void onSuccess(AwaitingArrivalDetails data) {
            // asign the details to global variable
            mArrivalDetails = data;
            // set all data in their views to show it to user
            setDataInPlace(mArrivalDetails);
            // hide loading progress
            mActivity.toggleLoadingProgress(false);
            // try to get tracking info
            Application.apiInterface().getAwaitingParcelTrackingInfo(data.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(trackingInfoServerResponse -> {
                        if (trackingInfoServerResponse.getData() != null) {
                            TrackingInfo trackingInfo = trackingInfoServerResponse.getData();
                            mArrivalDetails.setTrackingStatus(trackingInfo);
                            if (mStatusBarView != null && mStatusLabel != null) {
                                mStatusBarView.setProgress(trackingInfo.getTrackingStatus().progress());
                                mStatusLabel.setText(trackingInfo.getTrackingStatus().translatedStatus());
                            }
                        } else {
                            Toast.makeText(mActivity, trackingInfoServerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                        Toast.makeText(mActivity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<AwaitingArrivalDetails>> call, Throwable t) {
            Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            mActivity.toggleLoadingProgress(false);
        }
    };


    /**
     * Method used to set all data in their views in the UI
     *
     * @param arrivalDetails received from server
     */
    private void setDataInPlace(AwaitingArrivalDetails arrivalDetails) {
        mProductId.setText(arrivalDetails.getUid());
        int quantity = arrivalDetails.getProducts().size();
        String desc = quantity + " " + (quantity > 1 ? getString(R.string.products) : getString(R.string.product));
        mProductName.setText(desc);
        mProductTracking.setText(arrivalDetails.getTracking());

        if (Application.servicesPrices != null) {
            String photos = arrivalDetails.getCurrency() + Application.servicesPrices.get(Constants.Services.PRE_PHOTOS);
            String checking = arrivalDetails.getCurrency() + Application.servicesPrices.get(Constants.Services.PRE_VERIFICATION);
            mPhotoPrice.setText(photos);
            mCheckingPrice.setText(checking);
        }

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

    /**
     * Called when user taps on check product button
     */
    @OnClick(R.id.awaitingDetailsCheckProductBtn)
    void toggleCheckProduct() {
        mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mArrivalDetails.getId()),
                mArrivalDetails.getVerificationRequested() != 0, true), true);
    }

    /**
     * Called when user taps on additional photos button
     */
    @OnClick(R.id.awaitingDetailsAdditionalPhotosBtn)
    void toggleAdditionalPhotos() {
        mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mArrivalDetails.getId()),
                mArrivalDetails.getPhotoRequested() != 0, true), true);
    }

    /**
     * Called when user click on edit details button
     */
    @OnClick(R.id.awaitingDetailsEditButton)
    void editParcelDetails() {
        if (mArrivalDetails == null) return;
        // if we have parcel details we need to pass them to DeclarationActivity
        Intent editParcel = new Intent(mActivity, DeclarationActivity.class);
        editParcel.putExtra(DeclarationActivity.SHOW_TRACKING, true);
        // products array list which user can edit
        editParcel.putExtra(DeclarationActivity.PRODUCTS_ARRAY, mArrivalDetails.getProducts());
        // current parcel id used to save changes on server
        editParcel.putExtra(DeclarationActivity.AWAITING_ID, mArrivalDetails.getId());
        // parcel tracking number so user could edit it if needed
        editParcel.putExtra(DeclarationActivity.TRACKING_NUM, mArrivalDetails.getTracking());
        // start activity for result so we can get notification in on activity result
        mActivity.startActivityForResult(editParcel, AwaitingArrivalFragment.ADD_PARCEL_RC);
    }

    /**
     * Method called when user want to delete a parcel
     */
    @OnClick(R.id.awaitingDetailsDeleteButton)
    void deleteCurrentParcel() {
        deleteItem(mArrivalDetails);
    }

    /**
     * Called to delete a parcel from server
     *
     * @param product you want to delete
     */
    private void deleteItem(final AwaitingArrival product) {
        // create an alert dialog to show to users because this action can't be undone
        mAlertDialog = mActivity.getDialog(getString(R.string.delete), getString(R.string.confirm_deleting) + " "
                        + product.getUid() + "?", R.mipmap.ic_delete_parcel_popup_30dp,
                getString(R.string.yes), v -> {
                    // if user confirmed deleting we need to send delete request to server
                    mActivity.toggleLoadingProgress(true);
                    // start deleting process
                    Application.apiInterface().deleteAwaitingArrivalParcel(mArrivalDetails.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(serverResponse -> {
                                if (serverResponse != null && serverResponse.getMessage().equals(Constants.ApiResponse.OK_MESSAGE)) {
                                    // get parcels count
                                    int count = Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL);
                                    // decrease it by one
                                    count -= 1;
                                    // put it back
                                    Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, count);
                                    Intent intent = new Intent(AwaitingArrivalFragment.ACTION_LIST_CHANGED);
                                    intent.putExtra("id", product.getId());
                                    mActivity.sendBroadcast(intent);
                                    mActivity.onBackPressed();
                                } else if (serverResponse != null) {
                                    Toast.makeText(mActivity, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                mActivity.toggleLoadingProgress(false);
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
        if (mArrivalDetails != null && mStatusBarView != null && mStatusLabel != null) {
            mStatusBarView.setProgress(mArrivalDetails.getTrackingStatus().progress());
            mStatusLabel.setText(mArrivalDetails.getTrackingStatus().translatedStatus());
        }
    }
}
