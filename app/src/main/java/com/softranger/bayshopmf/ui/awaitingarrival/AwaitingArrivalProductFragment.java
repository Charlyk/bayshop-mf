package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
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
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.general.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.general.CheckProductFragment;
import com.softranger.bayshopmf.ui.general.StorageItemsFragment;
import com.softranger.bayshopmf.ui.general.WebViewFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;

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
    private View mRootView;

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
        ApiClient.getInstance().sendRequest(Constants.Api.getWaitingMfItem(String.valueOf(mProduct.getID())), mAwaitingListHandler);
        return mRootView;
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
                case AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST:
                    mAdditionalPhoto.setSelected(false);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.additional_photo));
                    break;
                case CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT:
                    mCheckProduct.setSelected(false);
                    mCheckProduct.setText(mActivity.getString(R.string.check_product));
                    break;
            }
        }
    };

    private Handler mDeleteHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equals("Ok");
                        if (error) {
                            Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                        } else {
                            mActivity.toggleLoadingProgress(false);
                            Intent intent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
                            intent.putExtra("deposit", mProduct.getDeposit());
                            mActivity.sendBroadcast(intent);
                            mActivity.onBackPressed();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
                    break;
                }
                case Constants.ApiResponse.RESONSE_UNAUTHORIZED: {
                    mActivity.toggleLoadingProgress(false);
                    mActivity.logOut();
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };

    private Handler mAwaitingListHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            mProduct.setDate(data.getString("createdDate"));
                            mProduct.setCurrency(data.getString("currency"));
                            mProduct.setProductPrice(data.getString("price"));
                            mProduct.setProductUrl(data.getString("productUrl"));
                            setDataInPlace(mProduct);
                        } else {
                            Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
                    break;
                }
                case Constants.ApiResponse.RESONSE_UNAUTHORIZED: {
                    mActivity.toggleLoadingProgress(false);
                    mActivity.logOut();
                }
            }
            mActivity.toggleLoadingProgress(false);
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
                deleteItem(mProduct);
                break;
            case R.id.awaitingDetailsCheckProductBtn:
                Toast.makeText(mActivity, getString(R.string.not_suported), Toast.LENGTH_SHORT).show();
//                mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mProduct.getID())), true);
                break;
            case R.id.awaitingDetailsAdditionalPhotosBtn:
                Toast.makeText(mActivity, getString(R.string.not_suported), Toast.LENGTH_SHORT).show();
//                mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mProduct.getID())), true);
                break;
        }
    }

    private void deleteItem(final Product product) {
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
                ApiClient.getInstance().delete(Constants.Api.getWaitingMfItem(String.valueOf(product.getID())), mDeleteHandler);
                mActivity.toggleLoadingProgress(true);
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
