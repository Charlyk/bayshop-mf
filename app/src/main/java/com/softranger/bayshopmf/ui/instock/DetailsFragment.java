package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ImagesAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.InStock;
import com.softranger.bayshopmf.model.box.InStockDetailed;
import com.softranger.bayshopmf.model.product.Photo;
import com.softranger.bayshopmf.network.ImageDownloadThread;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.gallery.GalleryActivity;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.services.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.services.CheckProductFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends ParentFragment implements View.OnClickListener, ImagesAdapter.OnImageClickListener {


    private static final String ITEM_ARG = "ITEM ARGUMENT";
    private MainActivity mActivity;
    private Unbinder mUnbinder;

    @BindView(R.id.fill_declarationButton) Button mFillDeclaration;
    @BindView(R.id.check_productButton) Button mCheckProduct;
    @BindView(R.id.additional_photoButton) Button mAdditionalPhoto;
    @BindView(R.id.inStockDetailsImageList) RecyclerView mRecyclerView;
    @BindView(R.id.inStockDetailsHolderLayout) LinearLayout mHolderLayout;
    @BindView(R.id.noPhotoLayoutHolder) LinearLayout mNoPhotoLayout;

    @BindView(R.id.details_tracking_label) TextView tracking;
    @BindView(R.id.details_date_label)  TextView date;
    @BindView(R.id.details_weight_label)  TextView weight;
    @BindView(R.id.details_price_label)  TextView price;

    @BindView(R.id.inStockDetailsItemId)  TextView uid;
    @BindView(R.id.inStockDetailsProductName)  TextView description;
    @BindView(R.id.inStockDetailsStorageIcon)  ImageView storage;

    private InStockDetailed mInStockDetailed;
    private InStock mInStockItem;
    private ImagesAdapter mImagesAdapter;


    private Call<ServerResponse<InStockDetailed>> mCall;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(InStock inStock) {
        Bundle args = new Bundle();
        args.putParcelable(ITEM_ARG, inStock);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mActivity = (MainActivity) getActivity(); // used as context to create views programmatically
        mUnbinder = ButterKnife.bind(this, rootView);
        // hide entire layout while we are loading data
        mHolderLayout.setVisibility(View.GONE);
        // create an intent filter and add al actions needed to update the layout in case of any changes
        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(AddAwaitingFragment.ACTION_ITEM_ADDED);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);

        // set up recycler view
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mImagesAdapter = new ImagesAdapter(R.layout.product_image_list_item);
        mImagesAdapter.setOnImageClickListener(this);
        mRecyclerView.setAdapter(mImagesAdapter);

        // get in stock item from arguments
        mInStockItem = getArguments().getParcelable(ITEM_ARG);

        // send request to server for item details
        mCall = Application.apiInterface().getInStockItemDetails(Application.currentToken, mInStockItem.getId());
        mActivity.toggleLoadingProgress(true);
        mCall.enqueue(mResponseCallback);
        return rootView;
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
                case AddAwaitingFragment.ACTION_ITEM_ADDED:
                    // send request to server for item details
                    mCall = Application.apiInterface().getInStockItemDetails(Application.currentToken, mInStockItem.getId());
                    mHolderLayout.setVisibility(View.GONE);
                    mActivity.toggleLoadingProgress(true);
                    mCall.enqueue(mResponseCallback);
                    break;
            }
        }
    };

    private ResponseCallback<InStockDetailed> mResponseCallback = new ResponseCallback<InStockDetailed>() {
        @Override
        public void onSuccess(InStockDetailed data) {
            mInStockDetailed = data;
            if (data.getPhotos().size() <= 0) {
                mRecyclerView.setVisibility(View.GONE);
                mNoPhotoLayout.setVisibility(View.VISIBLE);
            } else {
                mImagesAdapter.refreshList(mInStockDetailed.getPhotos());
                new ImageDownloadThread<>(mInStockDetailed.getPhotos(), mImageDownloadHandler, mActivity).start();
            }
            showDetails(mInStockDetailed);
            mHolderLayout.setVisibility(View.VISIBLE);
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            mActivity.toggleLoadingProgress(false);
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call<ServerResponse<InStockDetailed>> call, Throwable t) {
            mActivity.toggleLoadingProgress(false);
            // TODO: 9/22/16 hanlde errors
        }
    };

    private void showDetails(final InStockDetailed detailed) {
        // fill text views
        final SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

        if (detailed == null) {
            mActivity.onBackPressed();
            return;
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uid.setText(detailed.getUid());
                String strDescription = mInStockItem.getTitle();
                @ColorRes int textColor = android.R.color.black;
                if (strDescription == null || strDescription.equals("null") || strDescription.equals("")) {
                    strDescription = getString(R.string.declaration_not_filled);
                    textColor = android.R.color.darker_gray;
                }
                description.setText(strDescription);
                description.setTextColor(getResources().getColor(textColor));
                storage.setImageResource(R.mipmap.ic_usa_flag);

                tracking.setText(detailed.getTracking());
                date.setText(outputFormat.format(detailed.getCreatedDate()));
                weight.setText(detailed.getWeight() + "kg");
                price.setText(detailed.getCurrency() + detailed.getPrice());

                if (detailed.getPhotosInProgress() == Constants.IN_PROGRESS) {
                    mAdditionalPhoto.setSelected(true);
                }

                if (detailed.isCheckInProgress()) {
                    mCheckProduct.setSelected(true);
                }

                if (detailed.getDeclarationFilled() != 0) {
                    mFillDeclaration.setText(mActivity.getString(R.string.edit_declaration));
                } else {
                    mFillDeclaration.setText(mActivity.getString(R.string.fill_in_the_declaration));
                }
                mFillDeclaration.setSelected(detailed.getDeclarationFilled() != 0);

                mFillDeclaration.setOnClickListener(DetailsFragment.this);
                mCheckProduct.setOnClickListener(DetailsFragment.this);
                mAdditionalPhoto.setOnClickListener(DetailsFragment.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fill_declarationButton:
                mActivity.addFragment(DeclarationFragment.newInstance(mInStockDetailed), true);
                break;
            case R.id.check_productButton:
                mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mInStockDetailed.getId()),
                        mInStockDetailed.isCheckInProgress(), false), true);
                break;
            case R.id.additional_photoButton:
                mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mInStockDetailed.getId()),
                        mInStockDetailed.getPhotosInProgress() == Constants.IN_PROGRESS, false), true);
                break;
        }
    }

    @Override
    public void onImageClick(ArrayList<Photo> images, int position) {
        try {
            Intent intent = new Intent(mActivity, GalleryActivity.class);
            intent.putExtra("images", images);
            intent.putExtra("position", position);
            mActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mImageDownloadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (msg.what == ImageDownloadThread.FINISHED) {
                        mImagesAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    @Override
    public String getFragmentTitle() {
        return mInStockItem.getUid() + " " + getString(R.string.details);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.in_stock_details;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mStatusReceiver);
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
    }
}
