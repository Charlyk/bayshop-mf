package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.softranger.bayshopmf.model.Item;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ViewAnimator;

import java.util.ArrayList;

/**
 * Created by eduard on 28.04.16.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Item> mItems;
    private OnItemClickListener mOnItemClickListener;

    public ItemAdapter(ArrayList<Item> items, Context context) {
        mContext = context;
        mItems = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        holder.mViewAnimator.setAnimationListener(new ViewAnimator.AnimationListener() {
            @Override
            public void onAnimationStarted() {
                @ColorInt int color = holder.mItem.isSelected() ? mContext.getResources().getColor(R.color.colorSelection) :
                        mContext.getResources().getColor(R.color.colorPrimary);

                holder.itemView.setBackgroundColor(color);
            }

            @Override
            public void onAnimationFinished() {
                @DrawableRes int image = holder.mItem.isSelected() ? R.mipmap.parcel_selected : R.mipmap.parcel_active;

                holder.mImageView.setImageResource(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final ImageView mImageView;
        ViewAnimator mViewAnimator;
        Item mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
            mImageView = (ImageView) itemView.findViewById(R.id.item_image);
            mImageView.setOnClickListener(this);
            mViewAnimator = new ViewAnimator();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_image: {
                    mItem.setSelected(!mItem.isSelected());
                    mViewAnimator.flip(mImageView);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onIconClick(mItem, mItem.isSelected(), getAdapterPosition());
                    }
                    break;
                }
                default: {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onRowClick(mItem, getAdapterPosition());
                    }
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mItem.setSelected(!mItem.isSelected());
            mViewAnimator.flip(mImageView);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onIconClick(mItem, mItem.isSelected(), getAdapterPosition());
            }
            return true;
        }
    }

    public interface OnItemClickListener {
        void onRowClick(Item item, int position);

        void onIconClick(Item item, boolean isSelected, int position);
    }
}
