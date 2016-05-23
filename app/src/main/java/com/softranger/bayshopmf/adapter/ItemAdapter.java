package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.util.ViewAnimator;

import java.util.ArrayList;

/**
 * Created by eduard on 28.04.16.
 *
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Object> mInStockItems;
    private OnItemClickListener mOnItemClickListener;

    private static final int HEADER = -1, IN_STOCK_ITEM = 0, PRODUCT = 1, IN_PROCESSING = 2;

    public ItemAdapter(Context context) {
        mContext = context;
        mInStockItems = new ArrayList<>();
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
        } else if (mInStockItems.get(position) instanceof InProcessingParcel) {
            return IN_PROCESSING;
        }
        return HEADER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case IN_STOCK_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new InStockViewHolder(view);
            case PRODUCT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arrival_list_item, parent, false);
                return new ProductViewHolder(view);
            case IN_PROCESSING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_procesing_list_item, parent, false);
                return new InProcessingViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.additional_services_layout, parent, false);
                return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mInStockItems.get(position) instanceof InStockItem) {
            InStockViewHolder inStockViewHolder = (InStockViewHolder) holder;
            inStockViewHolder.mInStockItem = (InStockItem) mInStockItems.get(position);
            @DrawableRes int image = inStockViewHolder.mInStockItem.isHasDeclaration() ? R.mipmap.parcel_active : R.mipmap.unactive_parcel;
            inStockViewHolder.mImageView.setImageResource(image);
            inStockViewHolder.mUIDLabel.setText(inStockViewHolder.mInStockItem.getParcelId());
            inStockViewHolder.mProductName.setText(inStockViewHolder.mInStockItem.getName());
            inStockViewHolder.mTrackingLabel.setText(inStockViewHolder.mInStockItem.getTrackingNumber());
        } else if (mInStockItems.get(position) instanceof Product) {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.mProduct = (Product) mInStockItems.get(position);
            productHolder.mItemName.setText(productHolder.mProduct.getProductName());
            productHolder.mItemId.setText(productHolder.mProduct.getProductId());
            productHolder.mTrackingNumber.setText(productHolder.mProduct.getTrackingNumber());
        } else if (mInStockItems.get(position) instanceof InProcessingParcel) {
            InProcessingViewHolder processingHolder = (InProcessingViewHolder) holder;
            processingHolder.mProduct = (InProcessingParcel) mInStockItems.get(position);
            processingHolder.mParcelId.setText(processingHolder.mProduct.getParcelId());
            processingHolder.mProductName.setText(processingHolder.mProduct.getParcelName());
            processingHolder.mCreatedDate.setText(processingHolder.mProduct.getCreatedDate());
            processingHolder.mProgress.setText(processingHolder.mProduct.getProcessingProgress() + "%");
            processingHolder.mWeight.setText(processingHolder.mProduct.getWeight());
            processingHolder.mProcessingProgressBar.setProgress(processingHolder.mProduct.getProcessingProgress());
        }
    }

    @Override
    public int getItemCount() {
        return mInStockItems.size();
    }

    public void refreshList(ArrayList<Object> objects) {
        mInStockItems.clear();
        mInStockItems.addAll(objects);
        notifyDataSetChanged();
    }

    class InStockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, ViewAnimator.AnimationListener {
        final TextView mUIDLabel;
        final TextView mTrackingLabel;
        final TextView mProductName;
        final ImageView mImageView;
        ViewAnimator mViewAnimator;
        InStockItem mInStockItem;
        View mView;

        public InStockViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnLongClickListener(this);
            mView.setOnClickListener(this);
            mUIDLabel = (TextView) itemView.findViewById(R.id.product_name);
            mProductName = (TextView) itemView.findViewById(R.id.productNameLabel);
            mTrackingLabel = (TextView) itemView.findViewById(R.id.tracking_number);
            mImageView = (ImageView) itemView.findViewById(R.id.item_image);
            mImageView.setOnClickListener(this);
            mViewAnimator = new ViewAnimator();
            mViewAnimator.setAnimationListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_image: {
                    if (mInStockItem.isHasDeclaration()) {
                        mInStockItem.setSelected(!mInStockItem.isSelected());
                        mViewAnimator.flip(mImageView);
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onIconClick(mInStockItem, mInStockItem.isSelected(), getAdapterPosition());
                        }
                    } else {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onNoDeclarationItemSelected(mInStockItem, getAdapterPosition());
                        }
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
            if (mInStockItem.isHasDeclaration()) {
                mInStockItem.setSelected(!mInStockItem.isSelected());
                mViewAnimator.flip(mImageView);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onIconClick(mInStockItem, mInStockItem.isSelected(), getAdapterPosition());
                }
            } else {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onNoDeclarationItemSelected(mInStockItem, getAdapterPosition());
                }
            }
            return true;
        }

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

    class InProcessingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mParcelId, mProductName, mCreatedDate, mProgress, mWeight;
        final ProgressBar mProcessingProgressBar;
        InProcessingParcel mProduct;
        public InProcessingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mParcelId = (TextView) itemView.findViewById(R.id.inProcessingParcelId);
            mProductName = (TextView) itemView.findViewById(R.id.inProcessingProductName);
            mCreatedDate = (TextView) itemView.findViewById(R.id.inProcessingCreatedDate);
            mProgress = (TextView) itemView.findViewById(R.id.inProcessingProgress);
            mWeight = (TextView) itemView.findViewById(R.id.inProcessingWeight);
            mProcessingProgressBar = (ProgressBar) itemView.findViewById(R.id.inProcessingProgressBar);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onInProcessingProductClick(mProduct, getAdapterPosition());
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final LinearLayout mCombineBtn;
        final LinearLayout mCheckBtn;
        final LinearLayout mPhotosBtn;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mCombineBtn = (LinearLayout) itemView.findViewById(R.id.inStockAddsCombineParcelsBtn);
            mCheckBtn = (LinearLayout) itemView.findViewById(R.id.inStockAddsOrderCheckBtn);
            mPhotosBtn = (LinearLayout) itemView.findViewById(R.id.inStockAddsAdditionalPhotosBtn);

            mCombineBtn.setOnClickListener(this);
            mCheckBtn.setOnClickListener(this);
            mPhotosBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.inStockAddsCombineParcelsBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onCombineClick();
                    }
                    break;
                case R.id.inStockAddsOrderCheckBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onCheckOrderClick();
                    }
                    break;
                case R.id.inStockAddsAdditionalPhotosBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onAdditionalPhotosClick();
                    }
                    break;
            }
        }
    }

    public interface OnItemClickListener {
        void onRowClick(InStockItem inStockItem, int position);
        void onNoDeclarationItemSelected(InStockItem inStockItem, int position);
        void onIconClick(InStockItem inStockItem, boolean isSelected, int position);
        void onProductClick(Product product, int position);
        void onInProcessingProductClick(InProcessingParcel inProcessingParcel, int position);
        void onCombineClick();
        void onCheckOrderClick();
        void onAdditionalPhotosClick();
    }
}
