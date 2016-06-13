package com.softranger.bayshopmf.adapter;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InProcessingDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PARCEL = 0, PRODUCT = 1;
    private ArrayList<Object> mItems;
    private ImagesAdapter.OnImageClickListener mOnImageClickListener;

    public InProcessingDetailsAdapter(ArrayList<Object> items, ImagesAdapter.OnImageClickListener onImageClickListener) {
        mItems = items;
        mOnImageClickListener = onImageClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof InProcessingParcel) {
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
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.mProcessingParcel = (InProcessingParcel) mItems.get(position);
            headerHolder.mDepositIcon.setImageResource(getStorageIcon(headerHolder.mProcessingParcel.getDeposit()));
            headerHolder.mParcelId.setText(headerHolder.mProcessingParcel.getParcelId());

            Address address = headerHolder.mProcessingParcel.getAddress();
            String addressBuilder = address.getClientName() +
                    "\n" + address.getStreet() + " " +
                    "\n" + address.getCity() + ", " + address.getCountry() +
                    "\n" + address.getPostalCode() +
                    "\n" + address.getPhoneNumber();
            headerHolder.mShippingAddress.setText(addressBuilder);

            headerHolder.mGoodsPrice.setText(headerHolder.mProcessingParcel.getGoodsPrice());
            headerHolder.mShippingPrice.setText(headerHolder.mProcessingParcel.getShippingPrice());
            headerHolder.mCustomsClearance.setText(headerHolder.mProcessingParcel.getCustomsClearance());
            headerHolder.mTotalPrice.setText(headerHolder.mProcessingParcel.getTotalPrice());
            headerHolder.mShippingBy.setText(headerHolder.mProcessingParcel.getShippingBy());
            headerHolder.mTrackingNumber.setText(headerHolder.mProcessingParcel.getTrackingNumber());
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.mProduct = (Product) mItems.get(position);
            itemHolder.mProductId.setText(itemHolder.mProduct.getProductId());
            itemHolder.mProductName.setText(itemHolder.mProduct.getProductName());
            itemHolder.mPrice.setText(itemHolder.mProduct.getProductPrice());
            itemHolder.mItemCount.setText(itemHolder.mProduct.getProductQuantity());
            ImagesAdapter imagesAdapter = new ImagesAdapter(itemHolder.mProduct.getImages(), R.layout.product_image_list_item);
            imagesAdapter.setOnImageClickListener(mOnImageClickListener);
            itemHolder.mPhotosList.setAdapter(imagesAdapter);
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView mParcelId, mShippingAddress, mGoodsPrice, mCustomsClearance, mShippingPrice,
                mTotalPrice, mShippingBy, mTrackingNumber;
        final ImageView mDepositIcon;
        InProcessingParcel mProcessingParcel;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mParcelId = (TextView) itemView.findViewById(R.id.inProcessingDetailsParcelIdLabel);
            mShippingAddress = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingAddressLabel);
            mGoodsPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsGoodsPriceLabel);
            mCustomsClearance = (TextView) itemView.findViewById(R.id.inProcessingDetailsCustomsClearanceLabel);
            mShippingPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingPriceLabel);
            mTotalPrice = (TextView) itemView.findViewById(R.id.inProcessingDetailsTotalPriceLabel);
            mShippingBy = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingByLabel);
            mTrackingNumber = (TextView) itemView.findViewById(R.id.inProcessingDetailsShippingByTracking);
            mDepositIcon = (ImageView) itemView.findViewById(R.id.inProcessingDetailsStorageIcon);
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
}
