package com.softranger.bayshopmf.adapter;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.packages.AwaitingDeclaration;
import com.softranger.bayshopmf.model.packages.CustomsHeld;
import com.softranger.bayshopmf.model.packages.DamageRecorded;
import com.softranger.bayshopmf.model.packages.HeldByUser;
import com.softranger.bayshopmf.model.packages.HeldDueToDebt;
import com.softranger.bayshopmf.model.packages.InProcessing;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.packages.LocalDepot;
import com.softranger.bayshopmf.model.packages.PUSParcel;
import com.softranger.bayshopmf.model.packages.Packed;
import com.softranger.bayshopmf.model.packages.Prohibited;
import com.softranger.bayshopmf.model.packages.Received;
import com.softranger.bayshopmf.model.packages.Sent;
import com.softranger.bayshopmf.model.packages.ToDelivery;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InProcessingDetailsAdapter<T extends PUSParcel> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PARCEL = 0, PRODUCT = 1, PROHIBITED = 2, CUSTOMS_HELD = 3;
    private ArrayList<Object> mItems;
    private ImagesAdapter.OnImageClickListener mOnImageClickListener;
    private OnItemClickListener mOnItemClickListener;
    private boolean mShowMap;

    public InProcessingDetailsAdapter(T parcel, ImagesAdapter.OnImageClickListener onImageClickListener) {
        mItems = new ArrayList<>();
        mItems.add(parcel);
        mItems.addAll(parcel.getProducts());
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
        if (mItems.get(position) instanceof PUSParcel) {
            return PARCEL;
        } else if (mItems.get(position) instanceof Product) {
            return PRODUCT;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

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

        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mItems.get(position) instanceof PUSParcel) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.mProcessingParcel = (T) mItems.get(position);
            headerHolder.mWarningWithImage.setVisibility(View.VISIBLE);
            // set top icon and text for in processing status
            if (mItems.get(position) instanceof InProcessing) {
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_packing_44dp);
                headerHolder.mWarningTextView.setText("Some text for in processing packages description will go here at the top"); // TODO: 7/18/16 replace text
            }
            // set top icon and text for packed status
            if (mItems.get(position) instanceof Packed) {
                headerHolder.mWarningImage.setImageResource(R.mipmap.awaiting_sending_24dp);
                headerHolder.mWarningTextView.setText("Some text for awaiting sending packages description will go here at the top"); // TODO: 7/18/16 replace text
            }
            // show pay the debt btn, set the debt text, set top icon and text for held due to debt status
            if (mItems.get(position) instanceof HeldDueToDebt) {
                headerHolder.mPayTheDebtLayout.setVisibility(View.VISIBLE);
                headerHolder.mDebtLabel.setText("$4030"); // TODO: 7/18/16 replace text
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_held_due_to_debt_44dp);
                headerHolder.mWarningTextView.setText("Some text for held due to debt packages description will go here at the top"); // TODO: 7/18/16 replace text
            }
            // show track parcel btn, set top icon and text for sent status
            if (mItems.get(position) instanceof Sent) {
                headerHolder.mSentParcelLayout.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_sent_44dp);
                headerHolder.mWarningTextView.setText("Some text for sent packages description will go here at the top"); // TODO: 7/18/16 replace text
            }
            // show upload and take picture btn, set top icon and text for customs held status
            if (mItems.get(position) instanceof CustomsHeld) {
                headerHolder.mUploadLayout.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_held_by_customs_60dp);
                headerHolder.mWarningTextView.setText("Some text for held by customs packages description will go here at the top"); // TODO: 7/18/16 replace text
            }
            // show
            if (mItems.get(position) instanceof Prohibited) {
                headerHolder.mProhibitionLayout.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_held_by_prohibition_44dp);
                headerHolder.mWarningTextView.setText("Some text for held by prohibition packages description will go here at the top"); // TODO: 7/18/16 replace text
            }

            if (mItems.get(position) instanceof LocalDepot) {
                headerHolder.mHomeDeliveryLayout.setVisibility(View.VISIBLE);
                headerHolder.mSelectButton.setVisibility(View.VISIBLE);
                headerHolder.mSelectButton.setText(Application.getInstance().getString(R.string.change_address));
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_local_deposit_44dp);
                headerHolder.mWarningTextView.setText("Some text for local depot packages description will go here at the top"); // TODO: 7/18/16 replace text
            }

            if (mItems.get(position) instanceof Received) {
                headerHolder.mReceivedOnMapLayout.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_received_44dp);
                headerHolder.mWarningTextView.setText("Some text for received packages description will go here at the top"); // TODO: 7/18/16 replace text
            }

            if (mItems.get(position) instanceof ToDelivery) {
                headerHolder.mToDeliveryDetails.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_take_to_delivery_44dp);
                headerHolder.mWarningTextView.setText("Some text for taken to delivery packages description will go here at the top"); // TODO: 7/18/16 replace text
            }

            if (mItems.get(position) instanceof AwaitingDeclaration) {
                headerHolder.mAwaitingDeclarationLayout.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.awaiting_declaration_44dp);
                headerHolder.mWarningTextView.setText("Some text for taken to delivery packages description will go here at the top"); // TODO: 7/18/16 replace text
            }

            if (mItems.get(position) instanceof HeldByUser) {
                headerHolder.mHeldByUserLayout.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.held_by_user_44dp);
                headerHolder.mWarningTextView.setText("Some text for taken to delivery packages description will go here at the top"); // TODO: 7/18/16 replace text
            }

            if (mItems.get(position) instanceof DamageRecorded) {
                headerHolder.mDamageRecordedLayout.setVisibility(View.VISIBLE);
                headerHolder.mWarningImage.setImageResource(R.mipmap.ic_held_by_damage_44dp);
                headerHolder.mWarningTextView.setText("Some text for taken to delivery packages description will go here at the top"); // TODO: 7/18/16 replace text
            }

            headerHolder.mDepositIcon.setImageResource(getStorageIcon(headerHolder.mProcessingParcel.getDeposit()));
            headerHolder.mParcelId.setText(headerHolder.mProcessingParcel.getCodeNumber());

            Address address = headerHolder.mProcessingParcel.getAddress();

            headerHolder.mStreet.setText(address.getStreet());
            headerHolder.mPhoneNumber.setText(address.getPhoneNumber());
            headerHolder.mCity.setText(address.getCity());
            headerHolder.mCountry.setText(address.getCountry());
            headerHolder.mPostalCode.setText(address.getPostalCode());
            headerHolder.mClientName.setText(address.getClientName());

//            headerHolder.mGoodsPrice.setText(headerHolder.mProcessingParcel.getGoodsPrice());
            headerHolder.mShippingPrice.setText(headerHolder.mProcessingParcel.getCurrency() + ""
                    + headerHolder.mProcessingParcel.getDeliveryPrice());

//            headerHolder.mCustomsClearance.setText(headerHolder.mProcessingParcel.getCurrency() + ""
//                    + headerHolder.mProcessingParcel.getCustomsClearance());
            headerHolder.mTotalPrice.setText(headerHolder.mProcessingParcel.getCurrency() + ""
                    + headerHolder.mProcessingParcel.getTotalPrice());
            headerHolder.mShippingBy.setText(headerHolder.mProcessingParcel.getShippingMethod().getName());
            String description = headerHolder.mProcessingParcel.getName();
            if (description == null || description.equals("null") || description.equals("")) {
                headerHolder.mDescriptionlabel.setText(Application.getInstance().getString(R.string.no_description));
                headerHolder.mDescriptionlabel.setTextColor(Application.getInstance().getResources().getColor(android.R.color.darker_gray));
            } else {
                headerHolder.mDescriptionlabel.setText(headerHolder.mProcessingParcel.getName());
            }
//            headerHolder.mTrackingNumber.setText(headerHolder.mProcessingParcel.getTrackingNumber());
        } else if (mItems.get(position) instanceof Product) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.mProduct = (Product) mItems.get(position);
            itemHolder.mProductId.setText(itemHolder.mProduct.getBarcode());
            itemHolder.mProductName.setText(itemHolder.mProduct.getProductName());
            itemHolder.mPrice.setText(itemHolder.mProduct.getProductPrice());
            itemHolder.mItemCount.setText(itemHolder.mProduct.getProductQuantity());
            if (itemHolder.mProduct.getImages().size() > 0) {
                ImagesAdapter imagesAdapter = new ImagesAdapter(itemHolder.mProduct.getImages(), R.layout.product_image_list_item);
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
    private int getStorageIcon(String storage) {
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

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {
        final TextView mParcelId, mGoodsPrice, mCustomsClearance, mShippingPrice,
                mTotalPrice, mShippingBy, mTrackingNumber;
        final ImageView mDepositIcon;
        final TextView mClientName;
        final TextView mStreet, mPhoneNumber, mCity, mCountry, mPostalCode;
        final Button mSelectButton;
        final ImageButton mEditButton;
        final ImageButton mAddToFavorite;
        final Button mUploadDocument;
        final Button mTakePicture;
        final LinearLayout mUploadLayout;
        final LinearLayout mProhibitionLayout;
        final RelativeLayout mSentParcelLayout;
        final RelativeLayout mHomeDeliveryLayout;
        final RelativeLayout mReceivedOnMapLayout;
        final LinearLayout mToDeliveryDetails;
        final LinearLayout mCallCourierBtn;
        final LinearLayout mWarningWithImage;
        final Button mGeolocation;
        final RelativeLayout mPayTheDebtLayout;
        final TextView mDebtLabel;
        final ImageView mWarningImage;
        final TextView mWarningTextView;
        final TextView mDescriptionlabel;
        final Button mLeaveFeedback;
        final MapView mMapView;

        // awaiting declaration
        final LinearLayout mAwaitingDeclarationLayout;
        final Button mEditDeclarationBtn;

        // held by prohibition
        final Button mReturnToSeller;
        final Button mToDisband;

        // held by user
        final LinearLayout mHeldByUserLayout;
        final Button mUserHeldSendBtn;

        // damage recorded
        final LinearLayout mDamageRecordedLayout;
        final Button mAllowShipping;
        final Button mDamageToDisband;
        final ImageButton mAllowShippingDetails;
        final ImageButton mDamageToDisbandDetails;

        GoogleMap mGoogleMap;
        T mProcessingParcel;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mMapView = (MapView) itemView.findViewById(R.id.courierMapView);
            if (mShowMap) {
                // map
                if (mMapView != null) {
                    mMapView.onCreate(null);
                    mMapView.onResume();
                    mMapView.getMapAsync(this);
                }
            }

            // address
            mClientName = (TextView) itemView.findViewById(R.id.secondStepItemNameLabel);
            mStreet = (TextView) itemView.findViewById(R.id.secondStepAddressLabel);
            mPhoneNumber = (TextView) itemView.findViewById(R.id.secondStepPhoneNumberLabel);
            mCity = (TextView) itemView.findViewById(R.id.secondStepCityLabel);
            mCountry = (TextView) itemView.findViewById(R.id.secondStepCountryLabel);
            mPostalCode = (TextView) itemView.findViewById(R.id.secondStepPostalCodeLabel);
            // address select button
            // visible only for local depot items
            mSelectButton = (Button) itemView.findViewById(R.id.secondStepSelectBtn);
            // address edit button, need here just to hide it
            mEditButton = (ImageButton) itemView.findViewById(R.id.secondStepEditAddressButton);
            // address add to favorites button needed here to hide it
            mAddToFavorite = (ImageButton) itemView.findViewById(R.id.secondStepAddToFavoritesAddressButton);
            // pus parcel id
            mParcelId = (TextView) itemView.findViewById(R.id.inProcessingDetailsParcelIdLabel);
            mDescriptionlabel = (TextView) itemView.findViewById(R.id.heldByProhibitionDescriptionLabel);
            // price layout
            mGoodsPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsGoodsPriceLabel);
            mCustomsClearance = (TextView) itemView.findViewById(R.id.inProcessingDetailsCustomsClearanceLabel);
            mShippingPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingPriceLabel);
            mTotalPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsTotalPriceLabel);
            // shipping method layout
            mShippingBy = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingByLabel);
            // tracking number layout
            mTrackingNumber = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingByTracking);
            mDepositIcon = (ImageView) itemView.findViewById(R.id.inProcessingDetailsStorageIcon);
            // held by customs layout
            mUploadLayout = (LinearLayout) itemView.findViewById(R.id.uploadDocumentLayout);
            mTakePicture = (Button) itemView.findViewById(R.id.prohibitionHeldTakePhotoBtn);
            mUploadDocument = (Button) itemView.findViewById(R.id.prohibitionHeldUploadDocumentBtn);
            // held by prohibition layout
            mProhibitionLayout = (LinearLayout) itemView.findViewById(R.id.heldByProhibitionHeaderLayout);
            mReturnToSeller = (Button) itemView.findViewById(R.id.heldByProhibitionReturnBtn);
            mToDisband = (Button) itemView.findViewById(R.id.heldByProhibitionToDisbandBtn);
            // sent parcel layout
            mSentParcelLayout = (RelativeLayout) itemView.findViewById(R.id.sentParcelHeaderLayout);
            // local depot layout
            mHomeDeliveryLayout = (RelativeLayout) itemView.findViewById(R.id.orderHomeDeliveryLayout);
            // received layout
            mReceivedOnMapLayout = (RelativeLayout) itemView.findViewById(R.id.receivedSignatureOnMapLayout);
            mGeolocation = (Button) itemView.findViewById(R.id.geolocationButton);
            // awaiting declaration items
            mAwaitingDeclarationLayout = (LinearLayout) itemView.findViewById(R.id.awaitingDeclarationButtonLayout);
            mEditDeclarationBtn = (Button) itemView.findViewById(R.id.editAwaitingDeclarationBtn);
            // held by user
            mHeldByUserLayout = (LinearLayout) itemView.findViewById(R.id.heldByUserBtnLayout);
            mUserHeldSendBtn = (Button) itemView.findViewById(R.id.heldByUserSendBtn);
            // damage recorded
            mDamageRecordedLayout = (LinearLayout) itemView.findViewById(R.id.damageRecordedLayout);
            mAllowShipping = (Button) itemView.findViewById(R.id.damageAllowShipping);
            mDamageToDisband = (Button) itemView.findViewById(R.id.damageToDisband);
            mAllowShippingDetails = (ImageButton) itemView.findViewById(R.id.damageAllowShippingDetails);
            mDamageToDisbandDetails = (ImageButton) itemView.findViewById(R.id.damageToDisbandDetails);

            mToDeliveryDetails = (LinearLayout) itemView.findViewById(R.id.takeToDeliveryDetailsHeaderLayout);
            mCallCourierBtn = (LinearLayout) itemView.findViewById(R.id.takeToDeliveryDetailsCallBtn);
            mWarningWithImage = (LinearLayout) itemView.findViewById(R.id.warningLayoutWithIcon);
            mWarningImage = (ImageView) itemView.findViewById(R.id.warningWithIconImageView);
            mWarningTextView = (TextView) itemView.findViewById(R.id.warningWithIconLabel);
            mPayTheDebtLayout = (RelativeLayout) itemView.findViewById(R.id.payTheDebtLayout);
            mDebtLabel = (TextView) itemView.findViewById(R.id.payTheDebtAmountLabel);
            mLeaveFeedback = (Button) itemView.findViewById(R.id.leaveReviewButton);

            mAllowShipping.setOnClickListener(this);
            mDamageToDisband.setOnClickListener(this);
            mAllowShippingDetails.setOnClickListener(this);
            mDamageToDisbandDetails.setOnClickListener(this);
            mUserHeldSendBtn.setOnClickListener(this);
            mEditDeclarationBtn.setOnClickListener(this);
            mLeaveFeedback.setOnClickListener(this);
            mCallCourierBtn.setOnClickListener(this);
            mHomeDeliveryLayout.setOnClickListener(this);
            mReturnToSeller.setOnClickListener(this);
            mPayTheDebtLayout.setOnClickListener(this);
            mGeolocation.setOnClickListener(this);
            mSentParcelLayout.setOnClickListener(this);
            mToDisband.setOnClickListener(this);

            mReceivedOnMapLayout.setVisibility(View.GONE);
            mTakePicture.setOnClickListener(this);
            mUploadDocument.setOnClickListener(this);
            mEditButton.setVisibility(View.GONE);
            mAddToFavorite.setVisibility(View.GONE);
            mSelectButton.setVisibility(View.GONE);
            mSelectButton.setOnClickListener(this);
            mHomeDeliveryLayout.setVisibility(View.GONE);
            mUploadLayout.setVisibility(View.GONE);
            mToDeliveryDetails.setVisibility(View.GONE);
            mProhibitionLayout.setVisibility(View.GONE);
            mSentParcelLayout.setVisibility(View.GONE);
            mWarningWithImage.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener == null) return;
            switch (v.getId()) {
                case R.id.prohibitionHeldTakePhotoBtn:
                    mOnItemClickListener.onTakePictureClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.prohibitionHeldUploadDocumentBtn:
                    mOnItemClickListener.onUploadDocumentClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.heldByProhibitionReturnBtn:
                    mOnItemClickListener.onReturnToSenderClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.orderHomeDeliveryLayout:
                    mOnItemClickListener.onOrderDeliveryClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.secondStepSelectBtn:
                    mOnItemClickListener.onSelectAddressClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.takeToDeliveryDetailsCallBtn:
                    mOnItemClickListener.onCallCourierClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.payTheDebtLayout:
                    mOnItemClickListener.onPayTheDebtClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.geolocationButton:
                    mOnItemClickListener.onGeolocationClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.sentParcelHeaderLayout:
                    mOnItemClickListener.onStartTrackingClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.leaveReviewButton:
                    mOnItemClickListener.onLeaveFeedbackClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.editAwaitingDeclarationBtn:
                    mOnItemClickListener.onEditDeclarationClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.heldByProhibitionToDisbandBtn:
                    mOnItemClickListener.onToDisbandClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.heldByUserSendBtn:
                    mOnItemClickListener.onUserHeldSendClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.damageAllowShipping:
                    mOnItemClickListener.onAllowShippingClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.damageAllowShippingDetails:
                    mOnItemClickListener.onAllowShippingDetailsClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.damageToDisband:
                    mOnItemClickListener.onDamageToDisbandClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.damageToDisbandDetails:
                    mOnItemClickListener.onDamageToDisbandDetailsClick(mProcessingParcel, getAdapterPosition());
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
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView mProductId, mProductName, mItemCount, mPrice;
        final RecyclerView mPhotosList;
        Product mProduct;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mProductId = (TextView) itemView.findViewById(R.id.inProcessingDetailsProductMfId);
            mProductName = (TextView) itemView.findViewById(R.id.inProcessingDetailsProductNameLabel);
            mItemCount = (TextView) itemView.findViewById(R.id.inProcessingDetailsProductItemsCount);
            mPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsProductPrice);
            mPhotosList = (RecyclerView) itemView.findViewById(R.id.inProcessingDetailsProductImagesList);
            mPhotosList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        <T extends PUSParcel> void onUploadDocumentClick(T item, int position);
        <T extends PUSParcel> void onTakePictureClick(T item, int position);
        <T extends PUSParcel> void onReturnToSenderClick(T item, int position);
        <T extends PUSParcel> void onOrderDeliveryClick(T item, int position);
        <T extends PUSParcel> void onSelectAddressClick(T item, int position);
        <T extends PUSParcel> void onCallCourierClick(T item, int position);
        <T extends PUSParcel> void onPayTheDebtClick(T item, int position);
        <T extends PUSParcel> void onGeolocationClick(T item, int position);
        <T extends PUSParcel> void onStartTrackingClick(T item, int position);
        <T extends PUSParcel> void onLeaveFeedbackClick(T item, int position);
        <T extends PUSParcel> void onEditDeclarationClick(T item, int position);
        <T extends PUSParcel> void onToDisbandClick(T item, int position);
        <T extends PUSParcel> void onUserHeldSendClick(T item, int position);
        <T extends PUSParcel> void onAllowShippingClick(T item, int position);
        <T extends PUSParcel> void onAllowShippingDetailsClick(T item, int position);
        <T extends PUSParcel> void onDamageToDisbandClick(T item, int position);
        <T extends PUSParcel> void onDamageToDisbandDetailsClick(T item, int position);
    }
}
