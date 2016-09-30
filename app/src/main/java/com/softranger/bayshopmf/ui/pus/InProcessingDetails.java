package com.softranger.bayshopmf.ui.pus;


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
import android.support.annotation.DrawableRes;
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
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ImagesAdapter;
import com.softranger.bayshopmf.adapter.InProcessingDetailsAdapter;
import com.softranger.bayshopmf.adapter.AddressListAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmf.model.product.Photo;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmf.ui.auth.ForgotResultFragment;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.gallery.GalleryActivity;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

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
public class InProcessingDetails extends ParentFragment implements ImagesAdapter.OnImageClickListener,
        InProcessingDetailsAdapter.OnItemClickListener, LoadingDialogFragment.OnDoneListener,
        PullToRefreshLayout.PullToRefreshListener {

    private static final String PRODUCT_ARG = "in processing arguments";
    private static final int UPLOAD_RESULT_CODE = 12;
    private static final int TAKE_PICTURE_CODE = 13;
    private static final int CAMERA_PERMISSION_CODE = 14;

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;

    private Unbinder mUnbinder;
    private MainActivity mActivity;
    private PUSParcel mPackage;
    private PUSParcelDetailed mPUSParcelDetailed;
    private AlertDialog mAlertDialog;
    private InProcessingDetailsAdapter mAdapter;
    private CustomTabsIntent mTabsIntent;
    private Call<ServerResponse<PUSParcelDetailed>> mCall;

    public InProcessingDetails() {
        // Required empty public constructor
    }

    public static InProcessingDetails newInstance(@NonNull PUSParcel product) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, product);
        InProcessingDetails fragment = new InProcessingDetails();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPackage = getArguments().getParcelable(PRODUCT_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_CHANGE_ADDRESS);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mCall = Application.apiInterface().getPUSParcelDetails(Application.currentToken, mPackage.getId());
        mActivity.toggleLoadingProgress(true);
        mCall.enqueue(mDetailsResponseCallback);

        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        mTabsIntent = tabsBuilder.build();
        return view;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.notifyItemChanged(0);
        }
    };

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
            mPUSParcelDetailed.setRealWeight(mPackage.getRealWeight());

            mAdapter = new InProcessingDetailsAdapter(mPUSParcelDetailed, InProcessingDetails.this);
            if (mPUSParcelDetailed.getParcelStatus() == PUSParcel.PUSStatus.in_the_way) {
                mAdapter.setShowMap(true);
            }
            mAdapter.setOnItemClickListener(InProcessingDetails.this);
            mRecyclerView.setAdapter(mAdapter);
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<PUSParcelDetailed>> call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(mPackage.getParcelStatus().statusName());
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                // TODO: 7/22/16 change with string resource
                Toast.makeText(mActivity, "We need your permission to take a picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Crete a *.jpg file from taken picture
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
            LoadingDialogFragment dialogFragment = LoadingDialogFragment.newInstance(requestCode);
            dialogFragment.setOnDoneListener(this);
            dialogFragment.show(mActivity.getFragmentManager(), "Loading dialog fragment");
        }
    }

    @Override
    public void onDone(int action) {
        String topMessage = "";
        @DrawableRes int image = R.mipmap.logo_toolbar;
        String middleMessage = "";
        String bottomMessage = getString(R.string.upload_wait);
        switch (action) {
            case UPLOAD_RESULT_CODE:
                topMessage = getString(R.string.document_uploaded);
                image = R.mipmap.ic_doc_55dp;
                middleMessage = getString(R.string.thank_you_for_document);
                break;
            case TAKE_PICTURE_CODE:
                topMessage = getString(R.string.photo_taken);
                image = R.mipmap.ic_photo_55dp;
                middleMessage = getString(R.string.thank_you_for_photo);
                break;
        }
        mActivity.addFragment(ForgotResultFragment.newInstance(topMessage, image, middleMessage, bottomMessage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        }), false);
    }

    @Override
    public void onUploadDocumentClick(PUSParcelDetailed item, int position) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, UPLOAD_RESULT_CODE);
    }

    @Override
    public void onTakePictureClick(PUSParcelDetailed item, int position) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            String[] permissions = new String[] {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(mActivity, permissions, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onReturnToSenderClick(PUSParcelDetailed item, int position) {
        mActivity.addFragment(ReturnAddressFragment.newInstance(item), true);
    }

    @Override
    public void onOrderDeliveryClick(PUSParcelDetailed item, int position) {
        mAlertDialog = mActivity.getDialog(getString(R.string.confirm), getString(R.string.confirm_delivery)
                        + " " + item.getAddress().getClientName(), R.mipmap.ic_order_delivery_white_30dp,
                getString(R.string.confirm), v -> {
                    mActivity.addFragment(ForgotResultFragment.newInstance(getString(R.string.order_sent),
                            R.mipmap.ic_order_sent_75dp, getString(R.string.thank_you_delivery), getString(R.string.please_wait_call),
                            v1 -> mActivity.onBackPressed()), false);
                    mAlertDialog.dismiss();
                }, getString(R.string.cancel), v -> mAlertDialog.dismiss(), R.color.colorGreenAction);
        mAlertDialog.show();
    }

    @Override
    public void onSelectAddressClick(PUSParcelDetailed item, int position) {
        mActivity.addFragment(AddressesListFragment.newInstance(AddressListAdapter.ButtonType.select), true);
    }

    @Override
    public void onCallCourierClick(PUSParcelDetailed item, int position) {

    }

    @Override
    public void onPayTheDebtClick(PUSParcelDetailed item, int position) {

    }

    @Override
    public void onGeolocationClick(PUSParcelDetailed item, int position) {
        mActivity.addFragment(ReceivedSignature.newInstance(item), false);
    }

    @Override
    public void onStartTrackingClick(PUSParcelDetailed item, int position) {
        if (item.getTrackingUrl() != null) {
            mTabsIntent.launchUrl(mActivity, Uri.parse(item.getTrackingUrl()));
        }
    }

    @Override
    public void onLeaveFeedbackClick(PUSParcelDetailed item, int position) {
        mActivity.addFragment(LeaveFeedbackFragment.newInstance(item), true);
    }

    @Override
    public void onEditDeclarationClick(PUSParcelDetailed item, int position) {

    }

    @Override
    public void onToDisbandClick(PUSParcelDetailed item, int position) {
        Application.apiInterface().sendParcelToDisband(Application.currentToken, item.getId())
                .enqueue(mDisbandResponeCallback);
    }

    @Override
    public void onUserHeldSendClick(PUSParcelDetailed item, int position) {
        mAlertDialog = mActivity.getDialog(getString(R.string.confirm), getString(R.string.confirm_delivery)
                        + " " + item.getAddress().getClientName(), R.mipmap.send_parcel_white_30dp,
                getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.addFragment(ForgotResultFragment.newInstance(getString(R.string.order_sent),
                                R.mipmap.ic_confirm_25dp, getString(R.string.thank_you_delivery), getString(R.string.please_wait_call),
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
                }, R.color.colorGreenAction);
        mAlertDialog.show();
    }

    @Override
    public void onAllowShippingClick(PUSParcelDetailed item, int position) {
        mActivity.toggleLoadingProgress(true);
        Application.apiInterface().allowDamagedParcelSending(Application.currentToken, mPackage.getId(), 1)
                .enqueue(mAllowShippingCallback);
    }

    @Override
    public void onAllowShippingDetailsClick(PUSParcelDetailed item, int position) {

    }

    @Override
    public void onDamageToDisbandClick(PUSParcelDetailed item, int position) {
        Application.apiInterface().sendParcelToDisband(Application.currentToken, item.getId())
                .enqueue(mDisbandResponeCallback);
    }

    @Override
    public void onDamageToDisbandDetailsClick(PUSParcelDetailed item, int position) {

    }

    private ResponseCallback mDisbandResponeCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.showResultActivity(getString(R.string.parcel_deleted),
                    getString(R.string.disband_request_sent), R.mipmap.ic_to_disband_30dp,
                    getString(R.string.request_received));
            Intent refresh = new Intent(AddAwaitingFragment.ACTION_ITEM_ADDED);
            int count = Application.counters.get(Constants.PARCELS);
            count = count - 1;
            Application.counters.put(Constants.PARCELS, count);
            mActivity.updateParcelCounters(Constants.PARCELS);
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
            mActivity.showResultActivity(getString(R.string.allow_shipping),
                    getString(R.string.allow_request_sent) + " " + mPUSParcelDetailed.getCodeNumber(),
                    R.mipmap.ic_to_disband_30dp, getString(R.string.we_will_send_parcel));
            Intent refresh = new Intent(AddAwaitingFragment.ACTION_ITEM_ADDED);
            int count = Application.counters.get(Constants.PARCELS);
            count = count - 1;
            Application.counters.put(Constants.PARCELS, count);
            mActivity.updateParcelCounters(Constants.PARCELS);
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
        mCall = Application.apiInterface().getPUSParcelDetails(Application.currentToken, mPackage.getId());
        mCall.enqueue(mDetailsResponseCallback);
    }
}
