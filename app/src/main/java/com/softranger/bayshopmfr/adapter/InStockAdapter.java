package com.softranger.bayshopmfr.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.box.InStock;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ViewAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eduard Albu on 9/21/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class InStockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<InStock> mInStocks;
    private Context mContext;
    private OnInStockClickListener mOnInStockClickListener;
    private OnAdditionalBtnsClickListener mOnAdditionalBtnsClickListener;
    private SimpleDateFormat mDateFormat;
    private static final int HEADER = 0;

    public InStockAdapter(ArrayList<InStock> inStocks, Context context) {
        mInStocks = inStocks;
        mContext = context;
        mDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }

    public void setOnInStockClickListener(OnInStockClickListener onInStockClickListener) {
        mOnInStockClickListener = onInStockClickListener;
    }

    public void setOnAdditionalBtnsClickListener(OnAdditionalBtnsClickListener onAdditionalBtnsClickListener) {
        mOnAdditionalBtnsClickListener = onAdditionalBtnsClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == HEADER) return HEADER;
        else return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.additional_buttons, parent, false);
                return new ServicesHolder(view);
            }

            default: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_stock_item, parent, false);
                return new ItemHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.mInStock = mInStocks.get(position - 1);

            // set parcel uid
            itemHolder.mUidLabel.setText(itemHolder.mInStock.getUid());

            // set item image and background
            @DrawableRes int image;
            // check if declaration is filled
            if (itemHolder.mInStock.getDeclarationFilled() != 0) {
                // if is filled check if item is selected and get for it the right image
                image = itemHolder.mInStock.isSelected() ? R.mipmap.ic_check_button_50dp : R.mipmap.ic_red_button_50dp;

                // if is selected set item background as selection color
                if (itemHolder.mInStock.isSelected()) {
                    @ColorInt int color = mContext.getResources().getColor(R.color.colorSelection);
                    itemHolder.mView.setBackgroundColor(color);
                } else {
                    // otherwise set it as selectable item
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.selectable_background);
                    itemHolder.mView.setBackgroundDrawable(drawable);
                }
            } else {
                // if declaration is not filled just set gray image as item icon
                // and the background as selectable item
                image = R.mipmap.ic_silver_button_50dp;
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.selectable_background);
                itemHolder.mView.setBackgroundDrawable(drawable);
            }

            // finally set the image into image view
            itemHolder.mImageView.setImageResource(image);

            // check if item title (general description) is null or not
            boolean isNullTitle = itemHolder.mInStock.getTitle() == null;

            // if is null set text as "Declaration is not filled" and text color to darker_gray
            if (isNullTitle) {
                itemHolder.mDescriptionLabel.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
                itemHolder.mDescriptionLabel.setText(mContext.getString(R.string.declaration_not_filled));
            } else {
                // otherwise set text color to black
                itemHolder.mDescriptionLabel.setTextColor(mContext.getResources().getColor(android.R.color.black));
                // and set description into text view
                itemHolder.mDescriptionLabel.setText(itemHolder.mInStock.getTitle());
            }

            // get parcel creation date
            Date parcelDate = new Date();
            if (itemHolder.mInStock.getCreatedDate() != null) {
                parcelDate = itemHolder.mInStock.getCreatedDate();
            }

            // convert weight from grams to kilograms
            double price = Double.parseDouble(itemHolder.mInStock.getPrice());
            int grams = Integer.parseInt(itemHolder.mInStock.getWeight());
            double kilos = grams / 1000;

            // set the date into it's label
            itemHolder.mDateLabel.setText(mDateFormat.format(parcelDate));

            // set the weight into it's label
            itemHolder.mWeightLabel.setText(kilos + mContext.getString(R.string.kilos));

            // set the price into it's label
            itemHolder.mPriceLabel.setText(Constants.USD_SYMBOL + price);

            // calculate how many days current parcel spent at warehouse
            int storageDays = itemHolder.mInStock.getFreeStorage();
            String test = mContext.getResources().getQuantityString(R.plurals.days_remained, storageDays, storageDays);
            // set the result into remaining label
            itemHolder.mRemainingLabel.setText(test);

            // set remaining text background to correspond to spent time
            itemHolder.mRemainingLabel.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(getBarColors(storageDays)));
        }
    }

    @Override
    public int getItemCount() {
        return mInStocks.size() + 1;
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ViewAnimator.AnimationListener {

        @BindView(R.id.inStockUidLabel) TextView mUidLabel;
        @BindView(R.id.inStockDescriptionlabel) TextView mDescriptionLabel;
        @BindView(R.id.inStockDateLabel) TextView mDateLabel;
        @BindView(R.id.inStockWeightLabel) TextView mWeightLabel;
        @BindView(R.id.inStockPriceLabel) TextView mPriceLabel;
        @BindView(R.id.inStockItemImage) ImageView mImageView;
        @BindView(R.id.inStockRemainingLabel) TextView mRemainingLabel;

        View mView;
        ViewAnimator mViewAnimator;
        InStock mInStock;


        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
            mView.setOnClickListener(this);

            mViewAnimator = new ViewAnimator();
            mViewAnimator.setAnimationListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnInStockClickListener != null) {
                mOnInStockClickListener.onItemClick(mInStock, getAdapterPosition());
            }
        }

        @OnClick(R.id.inStockItemImage)
        void toggleItemSelection() {
            if (mInStock.getDeclarationFilled() != 0) {
                mInStock.setSelected(!mInStock.isSelected());
                mViewAnimator.flip(mImageView);
                if (mOnInStockClickListener != null) {
                    mOnInStockClickListener.onIconClick(mInStock, mInStock.isSelected(), getAdapterPosition());
                }
            } else {
                if (mOnInStockClickListener != null) {
                    mOnInStockClickListener.onNoDeclarationClick(mInStock, getAdapterPosition());
                }
            }
        }

        @Override
        public void onAnimationStarted() {
            if (mInStock.isSelected()) {
                @ColorInt int color = mContext.getResources().getColor(R.color.colorPrimary);
                mView.setBackgroundColor(color);
            } else {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.selectable_background);
                mView.setBackgroundDrawable(drawable);
            }
        }

        @Override
        public void onAnimationFinished() {
            @DrawableRes int image = mInStock.isSelected() ? R.mipmap.ic_check_button_50dp : R.mipmap.ic_red_button_50dp;
            mImageView.setImageResource(image);
        }
    }

    public class ServicesHolder extends RecyclerView.ViewHolder {
        public ServicesHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.additional_photoButton)
        void showInfoAboutAdditionalPhoto() {
            if (mOnAdditionalBtnsClickListener != null) {
                mOnAdditionalBtnsClickListener.additionalPhotoClick();
            }
        }

        @OnClick(R.id.check_productButton)
        void showInfoAboutParcelVerification() {
            if (mOnAdditionalBtnsClickListener != null) {
                mOnAdditionalBtnsClickListener.verificationClick();
            }
        }

        @OnClick(R.id.divide_photoButton)
        void showInfoAboutDividingParcel() {
            if (mOnAdditionalBtnsClickListener != null) {
                mOnAdditionalBtnsClickListener.divideParcelClick();
            }
        }

        @OnClick(R.id.repack_productButton)
        void showInfoAboutRepacking() {
            if (mOnAdditionalBtnsClickListener != null) {
                mOnAdditionalBtnsClickListener.repackingClick();
            }
        }
    }

    @DrawableRes
    private int getBarColors(int remains) {
        if (remains >= 25) {
            return R.drawable.green_5dp_corner;
        } else if (remains > 15 && remains < 25) {
            return R.drawable.yelow_5dp_corner;
        } else {
            return R.drawable.red_5dp_corner;
        }
    }

    private int getSpentDays(InStock inStock) {
        if (inStock == null) return 1;
        Date today = new Date();
        Date created = inStock.getCreatedDate();
        if (created == null) return 13;
        long diff = today.getTime() - created.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public interface OnInStockClickListener {
        void onItemClick(InStock inStock, int position);
        void onIconClick(InStock inStock, boolean isSelected, int position);
        void onNoDeclarationClick(InStock inStock, int position);
    }

    public interface OnAdditionalBtnsClickListener {
        void additionalPhotoClick();

        void verificationClick();

        void divideParcelClick();

        void repackingClick();
    }
}
