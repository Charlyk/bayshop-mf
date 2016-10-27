package com.softranger.bayshopmf.adapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.product.Photo;
import com.softranger.bayshopmf.network.ImageDownloadThread;
import com.softranger.bayshopmf.util.Application;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private ArrayList<Photo> mImages;
    private OnImageClickListener mOnImageClickListener;

    @LayoutRes private int mLayoutId;

    public int imageHeight;

    public ImagesAdapter(ArrayList<Photo> images, @LayoutRes int layoutId) {
        mImages = images;
        mLayoutId = layoutId;
        downloadImages();
    }

    public ImagesAdapter(@LayoutRes int layoutId) {
        mImages = new ArrayList<>();
        mLayoutId = layoutId;
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    public void addImage(Photo photo) {
        mImages.add(photo);
        notifyItemInserted(mImages.indexOf(photo));
        downloadImages();
    }

    public void refreshList(ArrayList<Photo> photos) {
        mImages.clear();
        mImages.addAll(photos);
        notifyDataSetChanged();
        downloadImages();
    }
    public void addImages(ArrayList<Photo> photos) {
        mImages.addAll(photos);
        notifyDataSetChanged();
        downloadImages();
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
        Photo photo = mImages.get(position);
        Picasso.with(Application.getInstance()).load(photo.getSmallImage()).into(holder.mProductImage);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mProductImage;
        final ProgressBar mProgressBar;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mProductImage = (ImageView) itemView.findViewById(R.id.productImageListItem);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.productImageListItemProgress);
        }

        @Override
        public void onClick(View v) {
            if (mOnImageClickListener != null) {
                mOnImageClickListener.onImageClick(mImages, getAdapterPosition());
            }
        }
    }

    private void downloadImages() {
        new ImageDownloadThread<>(mImages, mHandler, Application.getInstance().getApplicationContext()).start();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Photo) {
                final int position = mImages.indexOf(msg.obj);
                notifyItemChanged(position);
            }
        }
    };

    public interface OnImageClickListener {
        void onImageClick(ArrayList<Photo> images, int position);
    }
}
