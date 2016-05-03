package com.softranger.bayshopmf;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        holder.itemView.setSelected(holder.mItem.isSelected());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,
            ViewAnimator.AnimationListener {
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
            mViewAnimator.setAnimationListener(this);
        }

        @Override
        public void onAnimationStarted() {

        }

        @Override
        public void onAnimationFinished() {
            notifyItemChanged(getAdapterPosition());
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

        void onLongClick(Item item, boolean isSelected, int position);
    }
}
