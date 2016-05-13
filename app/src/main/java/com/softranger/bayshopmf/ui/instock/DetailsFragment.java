package com.softranger.bayshopmf.ui.instock;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ImagesAdapter;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.ui.GalleryActivity;
import com.softranger.bayshopmf.ui.general.AdditionalPhotoFragment;
import com.softranger.bayshopmf.ui.general.CheckProductFragment;
import com.softranger.bayshopmf.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements View.OnClickListener, ImagesAdapter.OnImageClickListener {


    private static final String ITEM_ARG = "ITEM ARGUMENT";
    private MainActivity mActivity;
    private Button mFillDeclaration;
    private Button mCheckProduct;
    private Button mAdditionalPhoto;
    private RecyclerView mRecyclerView;
    private InStockDetailed mInStockDetailed;

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
        final View view = inflater.inflate(R.layout.fragment_details, container, false);
        mActivity = (MainActivity) getActivity(); // used as context to create views programmatically
        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.inStockDetailsImageList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2, LinearLayoutManager.VERTICAL, false));
        mActivity.registerReceiver(mStatusReceiver, intentFilter);
        loadImages(new ArrayList<String>());
        final InStockItem inStockItem = getArguments().getParcelable(ITEM_ARG);
        mInStockDetailed = getItemDetails(inStockItem);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDetails(view, mInStockDetailed);
            }
        }, 500);
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

    private InStockDetailed getItemDetails(InStockItem item) {
        // TODO: 5/5/16 make a request to server for item details
        return (InStockDetailed) new InStockDetailed.Builder()
                .price("$ 200")
                .date(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()))
                .weight("15 kg.")
                .name(item.getName())
                .trackingNumber(item.getTrackingNumber())
                .hasDeclaration(item.isHasDeclaration())
                .build();
    }

    private void showDetails(View view, InStockDetailed detailed) {
        // fill text views
        TextView tracking = (TextView) view.findViewById(R.id.details_tracking_label);
        TextView date = (TextView) view.findViewById(R.id.details_date_label);
        TextView weight = (TextView) view.findViewById(R.id.details_weight_label);
        TextView price = (TextView) view.findViewById(R.id.details_price_label);

        tracking.setText(detailed.getTrackingNumber());
        date.setText(detailed.getDate());
        weight.setText(detailed.getWeight());
        price.setText(detailed.getPrice());

        // set buttons listeners
        mFillDeclaration = (Button) view.findViewById(R.id.fill_declarationButton);
        mCheckProduct = (Button) view.findViewById(R.id.check_productButton);
        mAdditionalPhoto = (Button) view.findViewById(R.id.additional_photoButton);

        mFillDeclaration.setOnClickListener(this);
        mCheckProduct.setOnClickListener(this);
        mAdditionalPhoto.setOnClickListener(this);
    }

    /**
     * This method will add images to the grid layout starting from third row
     * (first two rows are in use by additional buttons and parcel details)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void loadImages(ArrayList<String> imagesUrl) {
        ArrayList<Integer> images = new ArrayList<>();
        for (int i = 0; i < 5 ; i++) {
            images.add(R.drawable.computer_mac_image);
        }
        ImagesAdapter adapter = new ImagesAdapter(images, R.layout.in_stock_detailed_image);
        adapter.setOnImageClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fill_declarationButton:
                mActivity.addFragment(DeclarationFragment.newInstance(mInStockDetailed), true);
                break;
            case R.id.check_productButton:
                mActivity.addFragment(new CheckProductFragment(), true);
                break;
            case R.id.additional_photoButton:
                mActivity.addFragment(new AdditionalPhotoFragment(), true);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mStatusReceiver);
    }

    @Override
    public void onImageClick(ArrayList<Integer> images, int position) {
        Intent intent = new Intent(mActivity, GalleryActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("position", position);
        mActivity.startActivity(intent);
    }
}
