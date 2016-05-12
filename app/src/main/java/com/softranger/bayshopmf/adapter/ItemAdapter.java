package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.util.ViewAnimator;

import java.util.ArrayList;

/**
 * Created by eduard on 28.04.16.
 *
 */
public class ItemAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<T> mInStockItems;
    private OnItemClickListener mOnItemClickListener;

    private static final int IN_STOCK_ITEM = 0, PRODUCT = 1;

    public ItemAdapter(ArrayList<T> inStockItems, Context context) {
        mContext = context;
        mInStockItems = inStockItems;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mInStockItems.get(position) instanceof InStockItem) {
            return IN_STOCK_ITEM;
        } else if (mInStockItems.get(position) instanceof Product) {
            return PRODUCT;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case IN_STOCK_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new InStockViewHolder(view);
            case PRODUCT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arrival_list_item, parent, false);
                return new ProductViewHolder(view);
        }

        return new InStockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mInStockItems.get(position) instanceof InStockItem) {
            InStockViewHolder inStockViewHolder = (InStockViewHolder) holder;
            inStockViewHolder.mInStockItem = (InStockItem) mInStockItems.get(position);
            inStockViewHolder.mNameLabel.setText(inStockViewHolder.mInStockItem.getName());
            inStockViewHolder.mTrackingLabel.setText(inStockViewHolder.mInStockItem.getTrackingNumber());
        } else if (mInStockItems.get(position) instanceof Product) {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.mProduct = (Product) mInStockItems.get(position);
            productHolder.mItemName.setText(productHolder.mProduct.getProductName());
            productHolder.mItemId.setText(productHolder.mProduct.getProductId());
            productHolder.mTrackingNumber.setText(productHolder.mProduct.getTrackingNumber());
        }
    }

    @Override
    public int getItemCount() {
        return mInStockItems.size();
    }

    class InStockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView mNameLabel;
        final TextView mTrackingLabel;
        final ImageView mImageView;
        ViewAnimator mViewAnimator;
        InStockItem mInStockItem;
        View mView;

        public InStockViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnLongClickListener(this);
            mView.setOnClickListener(this);
            mNameLabel = (TextView) itemView.findViewById(R.id.product_name);
            mTrackingLabel = (TextView) itemView.findViewById(R.id.tracking_number);
            mImageView = (ImageView) itemView.findViewById(R.id.item_image);
            mImageView.setOnClickListener(this);
            mViewAnimator = new ViewAnimator();
            mViewAnimator.setAnimationListener(new ViewAnimator.AnimationListener() {
                @Override
                public void onAnimationStarted() {
                    @ColorInt int color = mInStockItem.isSelected() ? mContext.getResources().getColor(R.color.colorSelection) :
                            mContext.getResources().getColor(R.color.colorPrimary);

                    mView.setBackgroundColor(color);
                }

                @Override
                public void onAnimationFinished() {
                    @DrawableRes int image = mInStockItem.isSelected() ? R.mipmap.parcel_selected : R.mipmap.parcel_active;

                    mImageView.setImageResource(image);
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_image: {
                    mInStockItem.setSelected(!mInStockItem.isSelected());
                    mViewAnimator.flip(mImageView);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onIconClick(mInStockItem, mInStockItem.isSelected(), getAdapterPosition());
                    }
                    break;
                }
                default: {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onRowClick(mInStockItem, getAdapterPosition());
                    }
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mInStockItem.setSelected(!mInStockItem.isSelected());
            mViewAnimator.flip(mImageView);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onIconClick(mInStockItem, mInStockItem.isSelected(), getAdapterPosition());
            }
            return true;
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mItemId;
        final TextView mItemName;
        final TextView mTrackingNumber;
        Product mProduct;
        public ProductViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mItemId = (TextView) itemView.findViewById(R.id.itemIdLabel);
            mItemName = (TextView) itemView.findViewById(R.id.itemNameLabel);
            mTrackingNumber = (TextView) itemView.findViewById(R.id.itemTrackingLabel);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onProductClick(mProduct, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onRowClick(InStockItem inStockItem, int position);
        void onIconClick(InStockItem inStockItem, boolean isSelected, int position);
        void onProductClick(Product product, int position);
    }
}
