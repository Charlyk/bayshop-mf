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

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.packages.CustomsHeld;
import com.softranger.bayshopmf.model.packages.InProcessing;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.packages.LocalDepot;
import com.softranger.bayshopmf.model.packages.PUSParcel;
import com.softranger.bayshopmf.model.packages.Prohibited;
import com.softranger.bayshopmf.model.packages.Sent;
import com.softranger.bayshopmf.model.packages.ToDelivery;
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

    public InProcessingDetailsAdapter(T parcel, ImagesAdapter.OnImageClickListener onImageClickListener) {
        mItems = new ArrayList<>();
        mItems.add(parcel);
        mItems.addAll(parcel.getProducts());
        mOnImageClickListener = onImageClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
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

            if (!(mItems.get(position) instanceof Sent) && !(mItems.get(position) instanceof ToDelivery)) {
                headerHolder.mSentParcelLayout.setVisibility(View.GONE);
            } else if (mItems.get(position) instanceof Sent || mItems.get(position) instanceof ToDelivery) {
                headerHolder.mSentParcelTrack.setText(headerHolder.mProcessingParcel.getTrackingNumber());
            }

            if (mItems.get(position) instanceof CustomsHeld) {
                headerHolder.mUploadLayout.setVisibility(View.VISIBLE);
            }

            if (mItems.get(position) instanceof Prohibited) {
                headerHolder.mProhibitionLayout.setVisibility(View.VISIBLE);
            }

            if (mItems.get(position) instanceof LocalDepot) {
                headerHolder.mHomeDeliveryLayout.setVisibility(View.VISIBLE);
                headerHolder.mSelectButton.setVisibility(View.VISIBLE);
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

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        final TextView mSentParcelTrack;
        final RelativeLayout mReturnButton;
        final RelativeLayout mConfirmAddressButton;
        final RelativeLayout mHomeDeliveryLayout;
        T mProcessingParcel;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mClientName = (TextView) itemView.findViewById(R.id.secondStepItemNameLabel);
            mStreet = (TextView) itemView.findViewById(R.id.secondStepAddressLabel);
            mPhoneNumber = (TextView) itemView.findViewById(R.id.secondStepPhoneNumberLabel);
            mCity = (TextView) itemView.findViewById(R.id.secondStepCityLabel);
            mCountry = (TextView) itemView.findViewById(R.id.secondStepCountryLabel);
            mPostalCode = (TextView) itemView.findViewById(R.id.secondStepPostalCodeLabel);
            mSelectButton = (Button) itemView.findViewById(R.id.secondStepSelectBtn);
            mEditButton = (ImageButton) itemView.findViewById(R.id.secondStepEditAddressButton);
            mAddToFavorite = (ImageButton) itemView.findViewById(R.id.secondStepAddToFavoritesAddressButton);
            mParcelId = (TextView) itemView.findViewById(R.id.inProcessingDetailsParcelIdLabel);
            mGoodsPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsGoodsPriceLabel);
            mCustomsClearance = (TextView) itemView.findViewById(R.id.inProcessingDetailsCustomsClearanceLabel);
            mShippingPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingPriceLabel);
            mTotalPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsTotalPriceLabel);
            mShippingBy = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingByLabel);
            mTrackingNumber = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingByTracking);
            mDepositIcon = (ImageView) itemView.findViewById(R.id.inProcessingDetailsStorageIcon);
            mUploadLayout = (LinearLayout) itemView.findViewById(R.id.uploadDocumentLayout);
            mTakePicture = (Button) itemView.findViewById(R.id.prohibitionHeldTakePhotoBtn);
            mUploadDocument = (Button) itemView.findViewById(R.id.prohibitionHeldUploadDocumentBtn);
            mProhibitionLayout = (LinearLayout) itemView.findViewById(R.id.heldByProhibitionHeaderLayout);
            mReturnButton = (RelativeLayout) itemView.findViewById(R.id.heldByProhibitionReturnBtn);
            mConfirmAddressButton = (RelativeLayout) itemView.findViewById(R.id.heldByProhibitionConfirmAddressBtn);
            mSentParcelLayout = (RelativeLayout) itemView.findViewById(R.id.sentParcelHeaderLayout);
            mSentParcelTrack = (TextView) itemView.findViewById(R.id.sentParcelTrackingNumberLabel);
            mHomeDeliveryLayout = (RelativeLayout) itemView.findViewById(R.id.orderHomeDeliveryLayout);

            mHomeDeliveryLayout.setOnClickListener(this);
            mReturnButton.setOnClickListener(this);
            mConfirmAddressButton.setOnClickListener(this);

            mTakePicture.setOnClickListener(this);
            mUploadDocument.setOnClickListener(this);
            mEditButton.setVisibility(View.GONE);
            mAddToFavorite.setVisibility(View.GONE);
            mSelectButton.setVisibility(View.GONE);
            mSelectButton.setOnClickListener(this);
            mHomeDeliveryLayout.setVisibility(View.GONE);
            mUploadLayout.setVisibility(View.GONE);
            mProhibitionLayout.setVisibility(View.GONE);
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
                case R.id.heldByProhibitionConfirmAddressBtn:
                    mOnItemClickListener.onConfirmAddressClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.orderHomeDeliveryLayout:
                    mOnItemClickListener.onOrderDeliveryClick(mProcessingParcel, getAdapterPosition());
                    break;
                case R.id.secondStepSelectBtn:
                    mOnItemClickListener.onSelectAddressClick(mProcessingParcel, getAdapterPosition());
                    break;
            }
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
        <T extends PUSParcel> void onConfirmAddressClick(T item, int position);
        <T extends PUSParcel> void onOrderDeliveryClick(T item, int position);
        <T extends PUSParcel> void onSelectAddressClick(T item, int position);
    }
}
