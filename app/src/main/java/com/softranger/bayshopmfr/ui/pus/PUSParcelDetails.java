package com.softranger.bayshopmfr.ui.pus;


import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.ImagesAdapter;
import com.softranger.bayshopmfr.adapter.InProcessingDetailsAdapter;
import com.softranger.bayshopmfr.model.address.Address;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.product.Photo;
import com.softranger.bayshopmfr.model.pus.PUSParcel;
import com.softranger.bayshopmfr.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmfr.ui.gallery.GalleryActivity;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class PUSParcelDetails extends ParentFragment implements ImagesAdapter.OnImageClickListener,
        InProcessingDetailsAdapter.OnItemClickListener, PullToRefreshLayout.PullToRefreshListener {

    private static final String PRODUCT_ARG = "in processing arguments";
    private static final int UPLOAD_RESULT_CODE = 12;
    private static final int TAKE_PICTURE_CODE = 13;
    private static final int CAMERA_PERMISSION_CODE = 14;

    @BindView(R.id.pusDetailsRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.buttonsLayoutHolder)
    FrameLayout mButtonsHolder;

    private Unbinder mUnbinder;
    private ParentActivity mActivity;
    //    private PUSParcel mPackage;
    private PUSParcelDetailed mPUSParcelDetailed;
    private String mPusParcelId;
    private AlertDialog mAlertDialog;
    private InProcessingDetailsAdapter mAdapter;
    private CustomTabsIntent mTabsIntent;
    private Call<ServerResponse<PUSParcelDetailed>> mCall;

    public PUSParcelDetails() {
        // Required empty public constructor
    }

    public static PUSParcelDetails newInstance(@NonNull String pusParcelId) {
        Bundle args = new Bundle();
        args.putString(PRODUCT_ARG, pusParcelId);
        PUSParcelDetails fragment = new PUSParcelDetails();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pus_details, container, false);
        mActivity = (ParentActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        mPusParcelId = getArguments().getString(PRODUCT_ARG);

        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_CHANGE_ADDRESS);
        intentFilter.addAction(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mActivity.toggleLoadingProgress(true);
        refreshFragment();

        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(getResources().getColor(R.color.colorAccent));
        tabsBuilder.setSecondaryToolbarColor(getResources().getColor(R.color.colorPrimary));
        tabsBuilder.setShowTitle(true);
        mTabsIntent = tabsBuilder.build();
        return view;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    refreshFragment();
                    break;
                case Constants.ACTION_CHANGE_ADDRESS:
                    Address address = intent.getExtras().getParcelable("address");
                    mPUSParcelDetailed.setAddress(address);
                default:
                    mAdapter.notifyItemChanged(0);
                    break;
            }
        }
    };

    @Override
    public void refreshFragment() {
        mCall = Application.apiInterface().getPUSParcelDetails(mPusParcelId);
        mCall.enqueue(mDetailsResponseCallback);
    }

    @Override
    public void onImageClick(ArrayList<Photo> images, int position) {
        Intent intent = new Intent(mActivity, GalleryActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("position", position);
        mActivity.startActivity(intent);
    }

    private ResponseCallback<PUSParcelDetailed> mDetailsResponseCallback = new ResponseCallback<PUSParcelDetailed>() {
        @Override
        public void onSuccess(PUSParcelDetailed data) {
            mPUSParcelDetailed = data;

            mActivity.setToolbarTitle(getString(data.getParcelStatus().statusName()));

            View bottom = getBottomView(mPUSParcelDetailed.getParcelStatus());
            if (bottom != null) {
                mButtonsHolder.addView(bottom, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
            }

            setBottomButtonsListeners();

            mAdapter = new InProcessingDetailsAdapter(mPUSParcelDetailed, mActivity, PUSParcelDetails.this);
            if (mPUSParcelDetailed.getParcelStatus() == PUSParcel.PUSStatus.in_the_way) {
                mAdapter.setShowMap(true);
            }
            mAdapter.setOnItemClickListener(PUSParcelDetails.this);
            mRecyclerView.setAdapter(mAdapter);
            mActivity.toggleLoadingProgress(false);
            mRecyclerView.setItemViewCacheSize(mAdapter.getItemCount());
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<PUSParcelDetailed>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.parcels);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.parcel_details;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

    private View getBottomView(PUSParcel.PUSStatus pusStatus) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (pusStatus.buttonsLayout() != -1) {
            return inflater.inflate(pusStatus.buttonsLayout(), null, false);
        } else {
            return null;
        }
    }

    /**
     * Set bottom buttons listeners according to parcel status
     */
    private void setBottomButtonsListeners() {
        switch (mPUSParcelDetailed.getParcelStatus()) {
            case held_by_prohibition:
                Button returnToSeller = ButterKnife.findById(mButtonsHolder, R.id.heldByProhibitionReturnBtn);
                returnToSeller.setOnClickListener((view) -> {
                    onReturnToSenderClick();
                });
                Button toDisband = ButterKnife.findById(mButtonsHolder, R.id.heldByProhibitionToDisbandBtn);
                toDisband.setOnClickListener((view) -> {
                    onToDisbandClick();
                });
                break;
            case held_by_damage:
                Button allowShipping = ButterKnife.findById(mButtonsHolder, R.id.damageAllowShipping);
                allowShipping.setOnClickListener((view) -> {
                    onAllowShippingClick();
                });
                Button disbandDamaged = ButterKnife.findById(mButtonsHolder, R.id.damageToDisband);
                disbandDamaged.setOnClickListener((view) -> {
                    onDamageToDisbandClick();
                });
                break;
//            case held_due_to_debt:
//                RelativeLayout payTheDebt = ButterKnife.findById(mButtonsHolder, R.id.payTheDebtLayout);
//                TextView debtLabel = ButterKnife.findById(mButtonsHolder, R.id.payTheDebtAmountLabel);
//                payTheDebt.setOnClickListener((view) -> {
//                    onPayTheDebtClick();
//                });
//                break;
            case sent:
                RelativeLayout trackParcel = ButterKnife.findById(mButtonsHolder, R.id.sentParcelHeaderLayout);
                trackParcel.setOnClickListener((view) -> {
                    onStartTrackingClick();
                });
                break;
//            case held_by_customs:
//                Button upload = ButterKnife.findById(mButtonsHolder, R.id.prohibitionHeldUploadDocumentBtn);
//                upload.setOnClickListener((view) -> {
//                    onUploadDocumentClick();
//                });
//                Button takePhoto = ButterKnife.findById(mButtonsHolder, R.id.prohibitionHeldTakePhotoBtn);
//                takePhoto.setOnClickListener((view) -> {
//                    onTakePictureClick();
//                });
//                break;
//            case local_depot:
//                RelativeLayout orderDelivery = ButterKnife.findById(mButtonsHolder, R.id.orderHomeDeliveryLayout);
//                orderDelivery.setOnClickListener((view) -> {
//                    onOrderDeliveryClick();
//                });
//                break;
            case in_the_way:
                LinearLayout callCourier = ButterKnife.findById(mButtonsHolder, R.id.takenCallCourierBtn);
                callCourier.setOnClickListener((view) -> {
                    onCallCourierClick();
                });
                break;
            case received:
                Button information = ButterKnife.findById(mButtonsHolder, R.id.geolocationButton);
                information.setOnClickListener((view) -> {
                    onGeolocationClick();
                });

                Button leaveReview = ButterKnife.findById(mButtonsHolder, R.id.leaveReviewButton);
                leaveReview.setOnClickListener((view) -> {
                    onLeaveFeedbackClick();
                });

                if (mPUSParcelDetailed.getComment() != null || mPUSParcelDetailed.getRating() > 0) {
                    leaveReview.setText(getString(R.string.edit_feedback));
                }
                break;
            case held_by_user:
                LinearLayout send = ButterKnife.findById(mButtonsHolder, R.id.heldByUserBtnLayout);
                send.setOnClickListener((view) -> {
                    onUserHeldSendClick();
                });
                break;
            case awaiting_declaration:
                LinearLayout editDeclaration = ButterKnife.findById(mButtonsHolder, R.id.awaitingDeclarationButtonLayout);
                editDeclaration.setOnClickListener((view) -> {
                    onEditDeclarationClick();
                });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(mActivity, getString(R.string.need_photo_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Crete a *.jpg file from taken picture
     *
     * @throws IOException if file was not created
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Bitmap mphoto = BitmapFactory.decodeFile(image.getAbsolutePath());
        Log.d(this.getClass().getSimpleName(), "Take picture done");
        return image;
    }

    /**
     * Start camera to take a picture for feedback
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri outputFileUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            if (requestCode == UPLOAD_RESULT_CODE) {
                Uri path = data.getData();
                File selectedFile = new File(path.getPath());
                Log.d(this.getClass().getSimpleName(), selectedFile.getAbsolutePath());
            } else if (requestCode == TAKE_PICTURE_CODE) {

                Log.d(this.getClass().getSimpleName(), "Take picture done");
            }
        }
    }

    public void onUploadDocumentClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, UPLOAD_RESULT_CODE);
    }

    public void onTakePictureClick() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(mActivity, permissions, CAMERA_PERMISSION_CODE);
        }
    }

    //----------------------- HELD BY PROHIBITION -----------------------//

    /**
     * Used to send a prohibited parcel back to seller
     */
    public void onReturnToSenderClick() {
        mActivity.addFragment(ReturnAddressFragment.newInstance(mPUSParcelDetailed), true);
    }

    /**
     * Used to disband a prohibited parcel
     */
    public void onToDisbandClick() {
        Application.apiInterface().sendParcelToDisband(mPUSParcelDetailed.getId())
                .enqueue(mDisbandResponeCallback);
    }


    //----------------------- LOCAL DEPOT -----------------------//

    /**
     * Order home delivery
     */
    public void onOrderDeliveryClick() {
        mAlertDialog = mActivity.getDialog(getString(R.string.confirm), getString(R.string.confirm_delivery)
                        + " " + mPUSParcelDetailed.getAddress().getClientName(), R.mipmap.ic_order_delivery_white_30dp,
                getString(R.string.confirm), v -> {
                    // TODO: 10/20/16 implement order delivery api
                    mActivity.showResultActivity(getString(R.string.order_sent), R.mipmap.ic_confirm_local_depot_250dp,
                            getString(R.string.please_wait_call));
                    mAlertDialog.dismiss();
                }, getString(R.string.cancel), v -> mAlertDialog.dismiss(), R.color.colorGreenAction);
        mAlertDialog.show();
    }

    @Override
    public void onSelectAddressClick(PUSParcelDetailed item, int position) {
        mActivity.addFragment(AddressesListFragment.newInstance(true), true);
    }

    @Override
    public void onStartTrackingClick(PUSParcelDetailed parcelDetailed, int position) {
        if (mPUSParcelDetailed.getTrackingUrl() != null) {
            mTabsIntent.launchUrl(mActivity, Uri.parse(mPUSParcelDetailed.getTrackingUrl()));
        }
    }

    //----------------------- TAKEN TO DELIVERY -----------------------//

    /**
     * Call courier
     */
    public void onCallCourierClick() {

    }

    //----------------------- HELD DUE TO DEBT -----------------------//

    /**
     * Pay the debt to send the parcel
     */
    public void onPayTheDebtClick() {

    }

    //----------------------- RECEIVED -----------------------//

    /**
     * Show received location and signature
     */
    public void onGeolocationClick() {
        mActivity.addFragment(ReceivedSignature.newInstance(mPUSParcelDetailed), false);
    }

    /**
     * leave feedback about our job
     */
    public void onLeaveFeedbackClick() {
        mActivity.addFragment(LeaveFeedbackFragment.newInstance(mPUSParcelDetailed), true);
    }

    //----------------------- SENT -----------------------//

    /**
     * start tracking a parcel
     */
    public void onStartTrackingClick() {
        if (mPUSParcelDetailed.getTrackingUrl() != null) {
            mTabsIntent.launchUrl(mActivity, Uri.parse(mPUSParcelDetailed.getTrackingUrl()));
        }
    }

    //----------------------- AWAITING DECLARATION -----------------------//

    /**
     * edit parcel declaration
     */
    public void onEditDeclarationClick() {

    }

    //----------------------- HELD BY USER -----------------------//

    /**
     * send a parcel which is held by user
     */
    public void onUserHeldSendClick() {
        mAlertDialog = mActivity.getDialog(getString(R.string.confirm), getString(R.string.confirm_delivery)
                        + " " + mPUSParcelDetailed.getAddress().getClientName(), R.mipmap.send_parcel_white_30dp,
                getString(R.string.confirm), v -> {
                    Application.apiInterface().sendHeldByUserParcel(mPUSParcelDetailed.getId(), 1)
                            .enqueue(mResponseCallback);
                    mActivity.toggleLoadingProgress(true);
                    mAlertDialog.dismiss();
                }, getString(R.string.cancel), v -> mAlertDialog.dismiss(), R.color.colorGreenAction);
        mAlertDialog.show();
    }

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            Intent update = new Intent(PUSParcelsFragment.ACTION_UPDATE);
            mActivity.sendBroadcast(update);
            mActivity.showResultActivity(getString(R.string.send_request), R.mipmap.ic_confirm_held_by_user_250dp,
                    getString(R.string.parcel_will_be_sent));
            mActivity.getFragmentManager().popBackStack();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            t.printStackTrace();
        }
    };

    //----------------------- HELD BY DAMAGE -----------------------//

    /**
     * allow shipping of a damaged parcel
     */
    public void onAllowShippingClick() {
        mActivity.toggleLoadingProgress(true);
        Application.apiInterface().allowDamagedParcelSending(mPusParcelId, 1)
                .enqueue(mAllowShippingCallback);
    }

    /**
     * send damaged parcel to disband
     */
    public void onDamageToDisbandClick() {
        Application.apiInterface().sendParcelToDisband(mPUSParcelDetailed.getId())
                .enqueue(mDisbandResponeCallback);
    }

    private ResponseCallback mDisbandResponeCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.showResultActivity(getString(R.string.disband_request_sent), R.mipmap.ic_disband_250dp,
                    getString(R.string.disband_request_received));
            Intent refresh = new Intent(PUSParcelsFragment.ACTION_UPDATE);
            int count = Application.counters.get(Constants.PARCELS);
            count = count - 1;
            Application.counters.put(Constants.PARCELS, count);
            mActivity.sendBroadcast(refresh);
            mActivity.onBackPressed();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    private ResponseCallback mAllowShippingCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.showResultActivity(getString(R.string.allow_request_sent) + " " + mPUSParcelDetailed.getCodeNumber(),
                    R.mipmap.ic_allow_shipping_250dp, getString(R.string.we_will_send_parcel));
            Intent refresh = new Intent(PUSParcelsFragment.ACTION_UPDATE);
            int count = Application.counters.get(Constants.PARCELS);
            count = count - 1;
            Application.counters.put(Constants.PARCELS, count);
            mActivity.sendBroadcast(refresh);
            mActivity.onBackPressed();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mCall = Application.apiInterface().getPUSParcelDetails(mPusParcelId);
        mCall.enqueue(mDetailsResponseCallback);
    }
}
