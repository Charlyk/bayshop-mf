package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.model.box.Box;
import com.softranger.bayshopmf.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InProcessingDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PARCEL = 0, PRODUCT = 1, PROHIBITED = 2, CUSTOMS_HELD = 3;
    private ArrayList<Object> mItems;
    private ImagesAdapter.OnImageClickListener mOnImageClickListener;
    private OnItemClickListener mOnItemClickListener;
    private boolean mShowMap;
    private Context mContext;

    public InProcessingDetailsAdapter(PUSParcelDetailed parcel, Context context,
                                      ImagesAdapter.OnImageClickListener onImageClickListener) {
        mItems = new ArrayList<>();
        mContext = context;
        mItems.add(parcel);
        mItems.addAll(parcel.getBoxes());
        mOnImageClickListener = onImageClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setShowMap(boolean showMap) {
        mShowMap = showMap;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof PUSParcelDetailed) {
            return PARCEL;
        } else if (mItems.get(position) instanceof Box) {
            return PRODUCT;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case PARCEL: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_processing_details_header, parent, false);
                return new HeaderViewHolder(view);
            }
            case PRODUCT: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_processing_product_list_item, parent, false);
                return new ItemViewHolder(view);
            }
        }

        return new EmptyViewHolder(null);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mItems.get(position) instanceof PUSParcelDetailed) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.mProcessingParcel = (PUSParcelDetailed) mItems.get(position);
            headerHolder.mWarningWithImage.setVisibility(View.VISIBLE);

            headerHolder.mStatusBarView.setProgress(headerHolder.mProcessingParcel.getParcelStatus().index(),
                    mContext.getString(headerHolder.mProcessingParcel.getParcelStatus().statusName()));

            // check parcel status and set visibility of additional buttons and views
            // also set the right warning text and icon
            String warningMessage = "";
            switch (headerHolder.mProcessingParcel.getParcelStatus()) {
                case processing:
                    warningMessage = "Some text for in processing packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case awaiting_sending:
                    warningMessage = "Some text for awaiting sending packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case held_due_to_debt:
                    warningMessage = "Some text for held due to debt packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case held_by_prohibition:
                    warningMessage = "Some text for held by prohibition packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case held_by_damage:
                    warningMessage = "Some text for taken to delivery packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case sent:
                    warningMessage = "Some text for sent packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case held_by_customs:
                    warningMessage = headerHolder.mProcessingParcel.getCustomsCause(); // TODO: 7/18/16 replace text
                    break;
                case local_depot:
                    warningMessage = "Some text for local depot packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case in_the_way:
                    headerHolder.mToDeliveryDetails.setVisibility(View.VISIBLE);
                    warningMessage = "Some text for taken to delivery packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
                case received:
                    warningMessage = "Some text for received packages description will go here at the top"; // TODO: 7/18/16 replace text
                    break;
            }

            headerHolder.mWarningTextView.setText(warningMessage);

            // set the storage icon before UID label also set the UID text
            headerHolder.mDepositIcon.setImageResource(getStorageIcon(null)); // null return USA storage icon
            headerHolder.mParcelId.setText(headerHolder.mProcessingParcel.getCodeNumber());

            // get shipping address and set all texts in their positions
            Address address = headerHolder.mProcessingParcel.getAddress();
            headerHolder.mStreet.setText(address.getStreet());
            headerHolder.mPhoneNumber.setText(address.getPhoneNumber());
            headerHolder.mCity.setText(address.getCity());
            headerHolder.mCountry.setText(address.getCountry());
            headerHolder.mPostalCode.setText(address.getPostalCode());
            // build full name from first and last names
            String clientFullName = address.getFirstName() + " " + address.getLastName();
            headerHolder.mClientName.setText(clientFullName);
            // append the currency to delivery price and set it in label
            String deliveryPrice = headerHolder.mProcessingParcel.getCurrency() + ""
                    + headerHolder.mProcessingParcel.getDeliveryPrice();
            headerHolder.mShippingPrice.setText(deliveryPrice);
            // append the currency to total price and set it in label
            String totalPrice = headerHolder.mProcessingParcel.getCurrency() + ""
                    + headerHolder.mProcessingParcel.getTotalPrice();
            headerHolder.mTotalPrice.setText(totalPrice);
            // set shipping method name
            headerHolder.mShippingBy.setText(headerHolder.mProcessingParcel.getShippingMethod().getName());

            // if the description is null set the text color to gray and text to Description not filled
            // otherwise set the parcel description in label
            headerHolder.mTrackingNumber.setText(headerHolder.mProcessingParcel.getTrackingNum());
        } else if (mItems.get(position) instanceof Box) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.mProduct = (Box) mItems.get(position);
            itemHolder.mProductId.setText(itemHolder.mProduct.getUid());
            itemHolder.mProductName.setText(itemHolder.mProduct.getTitle());
            itemHolder.mPrice.setText(itemHolder.mProduct.getPrice());
            itemHolder.mItemCount.setText(String.valueOf(itemHolder.mProduct.getQuantity()));
            if (itemHolder.mProduct.getPhotos().size() > 0) {
                ImagesAdapter imagesAdapter = new ImagesAdapter(itemHolder.mProduct.getPhotos(), R.layout.product_image_list_item);
                imagesAdapter.setOnImageClickListener(mOnImageClickListener);
                itemHolder.mPhotosList.setAdapter(imagesAdapter);
            } else {
                itemHolder.mPhotosList.setVisibility(View.GONE);
            }
        }
    }

    //Recycling GoogleMap for list item
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder.getClass() == HeaderViewHolder.class) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            // Cleanup MapView here?
            if (headerViewHolder.mGoogleMap != null) {
                headerViewHolder.mGoogleMap.clear();
                headerViewHolder.mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @DrawableRes
    private int getStorageIcon(@Nullable String storage) {
        if (storage == null) return R.mipmap.ic_usa_flag;
        switch (storage) {
            case Constants.DE:
                return R.mipmap.ic_de_flag;
            case Constants.GB:
                return R.mipmap.ic_uk_flag;
            default:
                return R.mipmap.ic_usa_flag;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback,
            ParcelStatusBarView.OnStatusBarReadyListener {

        @BindView(R.id.courierMapView) MapView mMapView;
        @BindView(R.id.addressItemNameLabel) TextView mClientName;
        @BindView(R.id.addressPhoneNumberLabel) TextView mPhoneNumber;
        @BindView(R.id.addressCityLabel) TextView mCity;
        @BindView(R.id.addressCountryLabel) TextView mCountry;
        @BindView(R.id.addressPostalCodeLabel) TextView mPostalCode;
        @BindView(R.id.addressStreetLabel) TextView mStreet;
        @BindView(R.id.inProcessingDetailsParcelIdLabel) TextView mParcelId;
        @BindView(R.id.inProcessingDetailsGoodsPriceLabel) TextView mGoodsPrice;
        @BindView(R.id.inProcessingDetailsCustomsClearanceLabel) TextView mCustomsClearance;
        @BindView(R.id.inProcessingDetailsShippingPriceLabel) TextView mShippingPrice;
        @BindView(R.id.inProcessingDetailsTotalPriceLabel) TextView mTotalPrice;
        @BindView(R.id.inProcessingDetailsShippingByTracking) TextView mTrackingNumber;
        @BindView(R.id.warningItemLabel)
        TextView mWarningTextView;
        @BindView(R.id.inProcessingDetailsShippingByLabel) TextView mShippingBy;

        @BindView(R.id.pusDetailsStatusProgress)
        ParcelStatusBarView mStatusBarView;

        @BindView(R.id.addressEditButton) ImageButton mEditButton;
        @BindView(R.id.addressAddToFavoritesButton) ImageButton mAddToFavorite;

        @BindView(R.id.inProcessingDetailsStorageIcon) ImageView mDepositIcon;

        @BindView(R.id.takeToDeliveryDetailsHeaderLayout) LinearLayout mToDeliveryDetails;
        @BindView(R.id.warningItemLayout)
        LinearLayout mWarningWithImage;
        @BindView(R.id.shippingByLayoutButton) LinearLayout mShippingByLayout;
        @BindView(R.id.addressItemShadowSeparator)
        View mAddressShadow;
        @BindView(R.id.addressItemSeparator)
        View mAddressSeparator;

        GoogleMap mGoogleMap;
        PUSParcelDetailed mProcessingParcel;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mStatusBarView.setOnStatusBarReadyListener(this);

            if (mShowMap) {
                // map
                if (mMapView != null) {
                    mMapView.onCreate(null);
                    mMapView.onResume();
                    mMapView.getMapAsync(this);
                }
            }

            mShippingByLayout.setOnClickListener(this);

            mEditButton.setVisibility(View.GONE);
            mAddToFavorite.setVisibility(View.GONE);
            mToDeliveryDetails.setVisibility(View.GONE);
            mWarningWithImage.setVisibility(View.GONE);
            mAddressShadow.setVisibility(View.GONE);
            mAddressSeparator.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener == null) return;
            switch (v.getId()) {
                case R.id.addressItemLayout:
                    mOnItemClickListener.onSelectAddressClick(mProcessingParcel, getAdapterPosition());
                    break;
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(Application.getInstance().getApplicationContext());
            mGoogleMap = googleMap;

            final double LATITUDE = 47.043252904877306;
            final double LONGITUDE = 28.868207931518555;

            LatLng latLng = new LatLng(LATITUDE, LONGITUDE);
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Courier Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // googleMap in the Google Map
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        @Override
        public void onStatusBarReady() {
            if (mProcessingParcel != null) {
                mStatusBarView.setProgress(mProcessingParcel.getParcelStatus().index(),
                        mContext.getString(mProcessingParcel.getParcelStatus().statusName()));
            }
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.inProcessingDetailsProductMfId) TextView mProductId;
        @BindView(R.id.inProcessingDetailsProductNameLabel) TextView mProductName;
        @BindView(R.id.inProcessingDetailsProductItemsCount) TextView mItemCount;
        @BindView(R.id.inProcessingDetailsProductPrice) TextView mPrice;
        @BindView(R.id.inProcessingDetailsProductImagesList) RecyclerView mPhotosList;

        Box mProduct;
        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPhotosList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onSelectAddressClick(PUSParcelDetailed item, int position);
    }
}
