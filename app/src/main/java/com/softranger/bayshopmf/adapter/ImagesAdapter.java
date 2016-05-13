package com.softranger.bayshopmf.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.softranger.bayshopmf.R;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private ArrayList<Integer> mImages;
    private OnImageClickListener mOnImageClickListener;

    @LayoutRes private int mLayoutId;

    public int imageHeight;

    public ImagesAdapter(ArrayList<Integer> images, @LayoutRes int layoutId) {
        mImages = images;
        mLayoutId = layoutId;
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        imageHeight = view.getMeasuredHeight();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mProductImage.setImageResource(mImages.get(position));
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mProductImage;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mProductImage = (ImageView) itemView.findViewById(R.id.productImageListItem);
        }

        @Override
        public void onClick(View v) {
            if (mOnImageClickListener != null) {
                mOnImageClickListener.onImageClick(mImages, getAdapterPosition());
            }
        }
    }

    public interface OnImageClickListener {
        void onImageClick(ArrayList<Integer> images, int position);
    }
}
