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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ImagesAdapter;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Photo;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.network.ImageDownloadThread;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.gallery.GalleryActivity;
import com.softranger.bayshopmf.ui.services.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.services.CheckProductFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends ParentFragment implements View.OnClickListener, ImagesAdapter.OnImageClickListener {


    private static final String ITEM_ARG = "ITEM ARGUMENT";
    private MainActivity mActivity;
    private Button mFillDeclaration;
    private Button mCheckProduct;
    private Button mAdditionalPhoto;
    private RecyclerView mRecyclerView;
    private InStockDetailed mInStockDetailed;
    private InStockItem mInStockItem;
    private View mRootView;
    private ImagesAdapter mImagesAdapter;
    private LinearLayout mHolderLayout;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(InStockItem inStockItem) {
        Bundle args = new Bundle();
        args.putParcelable(ITEM_ARG, inStockItem);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_details, container, false);
        mActivity = (MainActivity) getActivity(); // used as context to create views programmatically
        mHolderLayout = (LinearLayout) mRootView.findViewById(R.id.inStockDetailsHolderLayout);
        mHolderLayout.setVisibility(View.GONE);
        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(StorageItemsFragment.ACTION_ITEM_CHANGED);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.inStockDetailsImageList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mActivity.registerReceiver(mStatusReceiver, intentFilter);
        mInStockItem = getArguments().getParcelable(ITEM_ARG);
        mImagesAdapter = new ImagesAdapter(R.layout.in_stock_detailed_image);
        mImagesAdapter.setOnImageClickListener(this);
        mRecyclerView.setAdapter(mImagesAdapter);
        ApiClient.getInstance().getRequest(Constants.Api.urlDetailedInStock(String.valueOf(mInStockItem.getID())), mHandler);
        mActivity.toggleLoadingProgress(true);
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
                case StorageItemsFragment.ACTION_ITEM_CHANGED:
                    mHolderLayout.setVisibility(View.GONE);
                    ApiClient.getInstance().getRequest(Constants.Api.urlDetailedInStock(String.valueOf(mInStockItem.getID())), mHandler);
                    mActivity.toggleLoadingProgress(true);
                    break;
            }
        }
    };

    private void showDetails(final View view, final InStockDetailed detailed) {
        // fill text views
        final TextView tracking = (TextView) view.findViewById(R.id.details_tracking_label);
        final TextView date = (TextView) view.findViewById(R.id.details_date_label);
        final TextView weight = (TextView) view.findViewById(R.id.details_weight_label);
        final TextView price = (TextView) view.findViewById(R.id.details_price_label);

        final TextView uid = (TextView) view.findViewById(R.id.inStockDetailsItemId);
        final TextView description = (TextView) view.findViewById(R.id.inStockDetailsProductName);
        final ImageView storage = (ImageView) view.findViewById(R.id.inStockDetailsStorageIcon);

        final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        final SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Date createdDate;
                String strDate = "";
                try {
                    createdDate = inputFormat.parse(detailed.getDate());
                    strDate = outputFormat.format(createdDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                uid.setText(detailed.getParcelId());
                description.setText(detailed.getDescription());
                storage.setImageResource(getStorageIcon(detailed.getDeposit()));

                tracking.setText(detailed.getTrackingNumber());
                date.setText(strDate);
                weight.setText(detailed.getWeight() + "kg");
                price.setText(detailed.getCurency() + detailed.getPrice());

                mFillDeclaration = (Button) view.findViewById(R.id.fill_declarationButton);
                mCheckProduct = (Button) view.findViewById(R.id.check_productButton);
                mAdditionalPhoto = (Button) view.findViewById(R.id.additional_photoButton);

                if (detailed.getPhotoInProgress() == Constants.IN_PROGRESS) {
                    mAdditionalPhoto.setSelected(true);
                }

                if (detailed.getCheckInProgress() == Constants.IN_PROGRESS) {
                    mCheckProduct.setSelected(true);
                }

                if (detailed.isHasDeclaration()) {
                    mFillDeclaration.setText(mActivity.getString(R.string.edit_declaration));
                } else {
                    mFillDeclaration.setText(mActivity.getString(R.string.fill_in_the_declaration));
                }
                mFillDeclaration.setSelected(detailed.isHasDeclaration());

                mFillDeclaration.setOnClickListener(DetailsFragment.this);
                mCheckProduct.setOnClickListener(DetailsFragment.this);
                mAdditionalPhoto.setOnClickListener(DetailsFragment.this);
            }
        });
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fill_declarationButton:
                mActivity.addFragment(DeclarationFragment.newInstance(mInStockDetailed), true);
                break;
            case R.id.check_productButton:
                mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mInStockDetailed.getID()),
                        mInStockDetailed.getCheckInProgress() == Constants.IN_PROGRESS, false), true);
                break;
            case R.id.additional_photoButton:
                mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mInStockDetailed.getID()),
                        mInStockDetailed.getPhotoInProgress() == Constants.IN_PROGRESS, false), true);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mStatusReceiver);
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

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");
        mInStockDetailed = (InStockDetailed) new InStockDetailed.Builder()
                .date(data.getString("createdDate"))
                .price(data.getDouble("price"))
                .photoInProgress(data.getInt("photosInProgress"))
                .checkInProgress(data.getBoolean("checkProductInProgress") ? 1 : 0)
                .curency(data.getString("currency"))
                .weight(data.getDouble("weight"))
                .deposit(mInStockItem.getDeposit())
                .trackingNumber(data.getString("trackingNumber"))
                .hasDeclaration(data.getBoolean("declarationFilled"))
                .parcelId(data.getString("uid"))
                .id(Integer.parseInt(data.getString("id")))
                .build();
        JSONArray jsonPhotos = data.getJSONArray("photos");
        ArrayList<Photo> photos = new ArrayList<>();
        for (int i = 0; i < jsonPhotos.length(); i++) {
            JSONObject o = jsonPhotos.getJSONObject(i);
            Photo photo = new Photo.Builder()
                    .smallImage(o.getString("photoThumbnail"))
                    .bigImage(o.getString("photo"))
                    .build();
            photos.add(photo);
        }

        mInStockDetailed.setPhotoUrls(photos);

        mImagesAdapter.refreshList(mInStockDetailed.getPhotoUrls());
        showDetails(mRootView, mInStockDetailed);
        new ImageDownloadThread<>(mInStockDetailed.getPhotoUrls(), mImageDownloadHandler, mActivity).start();
    }

    private Handler mImageDownloadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mImagesAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onHandleMessageEnd() {
        mHolderLayout.setVisibility(View.VISIBLE);
        mActivity.toggleLoadingProgress(false);
    }
}
