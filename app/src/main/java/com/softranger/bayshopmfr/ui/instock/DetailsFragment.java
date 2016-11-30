package com.softranger.bayshopmfr.ui.instock;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.ImagesAdapter;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.box.InStockDetailed;
import com.softranger.bayshopmfr.model.product.Photo;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.gallery.GalleryActivity;
import com.softranger.bayshopmfr.ui.general.DeclarationActivity;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.ui.services.AdditionalPhotoFragment;
import com.softranger.bayshopmfr.ui.services.CheckProductFragment;
import com.softranger.bayshopmfr.ui.services.RepackingFragment;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends ParentFragment implements ImagesAdapter.OnImageClickListener, PullToRefreshLayout.PullToRefreshListener {

    private static final String ITEM_ARG = "ITEM ARGUMENT";
    public static final int DECLARATION_RC = 1528;
    private ParentActivity mActivity;
    private Unbinder mUnbinder;

    @BindView(R.id.fill_declarationButton)
    Button mFillDeclaration;
    @BindView(R.id.checkProductTitle)
    TextView mCheckProduct;
    @BindView(R.id.additionalPhotosTitle)
    TextView mAdditionalPhoto;
    @BindView(R.id.repackingTitle)
    TextView mRepackingBtn;
    @BindView(R.id.divideParcelTitle)
    TextView mDivideParcel;
    @BindView(R.id.inStockDetailsImageList)
    RecyclerView mRecyclerView;
    @BindView(R.id.inStockDetailsHolderLayout)
    LinearLayout mHolderLayout;
    @BindView(R.id.noPhotoLayoutHolder)
    LinearLayout mNoPhotoLayout;
    @BindView(R.id.jellyPullToRefresh)
    JellyRefreshLayout mRefreshLayout;

    @BindView(R.id.details_date_label)
    TextView date;
    @BindView(R.id.details_weight_label)
    TextView weight;
    @BindView(R.id.details_price_label)
    TextView price;

    @BindView(R.id.additionalButtonsPhotoPrice)
    TextView mPhotosPrice;
    @BindView(R.id.additionalButtonsCheckPrice)
    TextView mCheckPrice;
    @BindView(R.id.additionalButtonsDividePrice)
    TextView mDividePrice;
    @BindView(R.id.additionalButtonsRepackPrice)
    TextView mRepackPrice;

    @BindView(R.id.inStockDetailsItemId)
    TextView uid;
    @BindView(R.id.inStockDetailsProductName)
    TextView description;

    private InStockDetailed mInStockDetailed;
    private ImagesAdapter mImagesAdapter;
    private String mParcelId;

    private Call<ServerResponse<InStockDetailed>> mCall;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String inStockid) {
        Bundle args = new Bundle();
        args.putString(ITEM_ARG, inStockid);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_in_stock_details, container, false);
        mActivity = (ParentActivity) getActivity(); // used as context to create views programmatically
        mUnbinder = ButterKnife.bind(this, rootView);
        // hide entire layout while we are loading data
        mHolderLayout.setVisibility(View.GONE);
        // create an intent filter and add al actions needed to update the layout in case of any changes
        IntentFilter intentFilter = new IntentFilter(CheckProductFragment.ACTION_CHECK_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING);
        intentFilter.addAction(AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST);
        intentFilter.addAction(CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT);
        intentFilter.addAction(InStockFragment.ACTION_UPDATE_LIST);
        intentFilter.addAction(RepackingFragment.TOGGLE_REPACKING);
        intentFilter.addAction(Application.ACTION_RETRY);
        mActivity.registerReceiver(mStatusReceiver, intentFilter);

        mRefreshLayout.setPullToRefreshListener(this);

        // set up recycler view
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mImagesAdapter = new ImagesAdapter(R.layout.product_image_list_item);
        mImagesAdapter.setOnImageClickListener(this);
        mRecyclerView.setAdapter(mImagesAdapter);

        // get in stock item from arguments
        mParcelId = getArguments().getString(ITEM_ARG);

        mFillDeclaration.setVisibility(View.GONE);

        mActivity.toggleLoadingProgress(true);
        getItemDetails();
        return rootView;
    }

    private void getItemDetails() {
        String photoPrice = Constants.USD_SYMBOL + Application.servicesPrices.get(Constants.Services.PHOTOS);
        String checkPrice = Constants.USD_SYMBOL + Application.servicesPrices.get(Constants.Services.VERIFICATION);
        String dividePrice = Constants.USD_SYMBOL + 0;
        String repackPrice = Constants.USD_SYMBOL + Application.servicesPrices.get(Constants.Services.REPACKING);

        mPhotosPrice.setText(photoPrice);
        mCheckPrice.setText(checkPrice);
        mDividePrice.setText(dividePrice);
        mRepackPrice.setText(repackPrice);

        // send request to server for item details
        mCall = Application.apiInterface().getInStockItemDetails(mParcelId);
        mCall.enqueue(mResponseCallback);
    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CheckProductFragment.ACTION_CHECK_IN_PROCESSING:
                    mInStockDetailed.setCheckInProgress(true);
                    mCheckProduct.setSelected(true);
                    mCheckProduct.setText(mActivity.getString(R.string.check_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_PHOTO_IN_PROCESSING:
                    mInStockDetailed.setPhotosInProgress(1);
                    mAdditionalPhoto.setSelected(true);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
                    break;
                case AdditionalPhotoFragment.ACTION_CANCEL_PHOTO_REQUEST:
                    mInStockDetailed.setPhotosInProgress(0);
                    mAdditionalPhoto.setSelected(false);
                    mAdditionalPhoto.setText(mActivity.getString(R.string.additional_photo));
                    break;
                case CheckProductFragment.ACTION_CANCEL_CHECK_PRODUCT:
                    mInStockDetailed.setCheckInProgress(false);
                    mCheckProduct.setSelected(false);
                    mCheckProduct.setText(mActivity.getString(R.string.check_product));
                    break;
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                case InStockFragment.ACTION_UPDATE_LIST:
                    // send request to server for item details
                    mActivity.hideKeyboard();
                    refreshFragment();
                    break;
                case RepackingFragment.TOGGLE_REPACKING:
                    mInStockDetailed.setRepackingRequested(intent.getExtras().getInt("enable"));
                    mRepackingBtn.setSelected(mInStockDetailed.getRepackingRequested() != 0);
                    String title = mInStockDetailed.getRepackingRequested() != 0 ?
                            getString(R.string.repacking_in_progress) : getString(R.string.repacking);
                    mRepackingBtn.setText(title);
                    break;
            }
        }
    };

    @Override
    public void refreshFragment() {
        mCall = Application.apiInterface().getInStockItemDetails(mParcelId);
        mCall.enqueue(mResponseCallback);
    }

    private ResponseCallback<InStockDetailed> mResponseCallback = new ResponseCallback<InStockDetailed>() {
        @Override
        public void onSuccess(InStockDetailed data) {
            mInStockDetailed = data;
            if (data.getPhotos().size() <= 0) {
                mRecyclerView.setVisibility(View.GONE);
                mNoPhotoLayout.setVisibility(View.VISIBLE);
            } else {
                mImagesAdapter.refreshList(mInStockDetailed.getPhotos());
            }
            showDetails(mInStockDetailed);
            mHolderLayout.setVisibility(View.VISIBLE);
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null) mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            if (mActivity != null) mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null) mRefreshLayout.setRefreshing(false);
            Toast.makeText(Application.getInstance(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call<ServerResponse<InStockDetailed>> call, Throwable t) {
            if (mActivity != null) mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null) mRefreshLayout.setRefreshing(false);
            Toast.makeText(Application.getInstance(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    };

    @OnClick(R.id.inStockDetailsMfCopyBtn)
    void copyParcelUID() {
        if (mInStockDetailed == null) return;
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager)
                mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", mInStockDetailed.getUid());
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mActivity, getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    private void showDetails(final InStockDetailed detailed) {
        // fill text views
        final SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

        if (detailed == null) {
            mActivity.onBackPressed();
            return;
        }

        mActivity.runOnUiThread(() -> {
            uid.setText(detailed.getUid());
            String strDescription = detailed.getTitle();
            @ColorRes int textColor = android.R.color.black;
            if (strDescription == null || strDescription.equals("null") || strDescription.equals("")) {
                strDescription = getString(R.string.declaration_not_filled);
                textColor = android.R.color.darker_gray;
            }
            description.setText(strDescription);
            description.setTextColor(getResources().getColor(textColor));

            date.setText(outputFormat.format(detailed.getCreatedDate()));
            String strWeight = detailed.getWeight() + "kg / " + detailed.getVolumeWeight() + getString(R.string.vkg);
            weight.setText(strWeight);
            price.setText(detailed.getCurrency() + detailed.getPrice());

            mAdditionalPhoto.setSelected(detailed.getPhotosInProgress() != 0);
            if (mAdditionalPhoto.isSelected()) {
                mAdditionalPhoto.setText(mActivity.getString(R.string.photos_in_progress));
            }

            mCheckProduct.setSelected(detailed.isCheckInProgress());
            if (mCheckProduct.isSelected()) {
                mCheckProduct.setText(mActivity.getString(R.string.photos_in_progress));
            }

            mRepackingBtn.setSelected(detailed.getRepackingRequested() != 0);
            String title = mRepackingBtn.isSelected() ?
                    getString(R.string.repacking_in_progress) : getString(R.string.repacking);
            mRepackingBtn.setText(title);

            if (detailed.getDeclarationFilled() != 0) {
                mFillDeclaration.setText(mActivity.getString(R.string.edit_declaration));
            } else {
                mFillDeclaration.setText(mActivity.getString(R.string.fill_in_the_declaration));
            }
            mFillDeclaration.setSelected(detailed.getDeclarationFilled() != 0);
            mFillDeclaration.setVisibility(View.VISIBLE);
        });
    }

    @OnClick({R.id.repack_productButton, R.id.fill_declarationButton, R.id.check_productButton, R.id.additional_photoButton})
    void onButtnsAreClicked(View v) {
        switch (v.getId()) {
            case R.id.fill_declarationButton:
                Intent editDeclaration = new Intent(mActivity, DeclarationActivity.class);
                editDeclaration.putExtra(DeclarationActivity.IN_STOCK_ID, mInStockDetailed.getId());
                editDeclaration.putExtra(DeclarationActivity.HAS_DECLARATION, mInStockDetailed.getDeclarationFilled() != 0);
                mActivity.startActivityForResult(editDeclaration, DECLARATION_RC);
                break;
            case R.id.check_productButton:
                mActivity.addFragment(CheckProductFragment.newInstance(String.valueOf(mInStockDetailed.getId()),
                        mInStockDetailed.isCheckInProgress(), false), true);
                break;
            case R.id.additional_photoButton:
                mActivity.addFragment(AdditionalPhotoFragment.newInstance(String.valueOf(mInStockDetailed.getId()),
                        mInStockDetailed.getPhotosInProgress() == Constants.IN_PROGRESS, false), true);
                break;
            case R.id.repack_productButton:
                mActivity.addFragment(RepackingFragment.newInstance(mInStockDetailed.getRepackingRequested() != 0, String.valueOf(mInStockDetailed.getId())), true);
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

    @Override
    public String getFragmentTitle() {
        return getString(R.string.details);
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

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getItemDetails();
    }
}
