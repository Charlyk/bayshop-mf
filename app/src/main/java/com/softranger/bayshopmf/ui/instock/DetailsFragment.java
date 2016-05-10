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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.ui.DeclarationFragment;
import com.softranger.bayshopmf.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements View.OnClickListener {

    public static final String ACTION_CHECK_IN_PROCESSING = "ACTION CHECK IN PROCESSING";
    public static final String ACTION_PHOTO_IN_PROCESSING = "ACTION PHOTO IN PROCESSING";
    private static final String ITEM_ARG = "ITEM ARGUMENT";
    private MainActivity mActivity;
    private Button mFillDeclaration;
    private Button mCheckProduct;
    private Button mAdditionalPhoto;
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
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.details_gridLayout);
        mActivity = (MainActivity) getActivity(); // used as context to create views programmatically
        IntentFilter intentFilter = new IntentFilter(ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(ACTION_PHOTO_IN_PROCESSING);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);
        loadImages(gridLayout, new ArrayList<String>());
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
                case ACTION_CHECK_IN_PROCESSING:
                    mCheckProduct.setSelected(true);
                    mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
                    break;
                case ACTION_PHOTO_IN_PROCESSING:
                    mAdditionalPhoto.setSelected(true);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
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
     * @param gridLayout to add the views in
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void loadImages(GridLayout gridLayout, ArrayList<String> imagesUrl) {
        // get maximal column numbers from layout
        final int columns = gridLayout.getColumnCount();
        gridLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int widthAndHeight = gridLayout.getMeasuredWidth() / 2;
        int startRow = 2; // first two rows are in use by top buttons and parcel details
        // TODO: 5/5/16 replace 12 with image list size
        for (int i = 0, c = 0, r = startRow; i < 5 ; i++, c++) {

            // jump to a new row once we get to column count limit
            if (c == columns) {
                c = 0;
                r++;
            }

            // create a layout params object and set parameters to add a new view to the layout
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(new ViewGroup.LayoutParams(widthAndHeight, widthAndHeight));
            // set row number for current view
            layoutParams.rowSpec = GridLayout.spec(r);
            float weight = 1.0f; // set the weight for newer android version
            // set column number, layout gravity and column weight
            layoutParams.columnSpec = GridLayout.spec(c, GridLayout.FILL, weight);
            // create any view and set layout params for them
            ImageView button = new ImageView(mActivity);
            layoutParams.setMargins(5, 5, 5, 5);
            button.setImageResource(R.drawable.example_image);
            button.setScaleType(ImageView.ScaleType.FIT_XY);
            button.setLayoutParams(layoutParams);
            gridLayout.addView(button);
        }
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
        mActivity.setToolbarTitle("", false);
        mActivity.unregisterReceiver(mStatusReceiver);
    }
}
