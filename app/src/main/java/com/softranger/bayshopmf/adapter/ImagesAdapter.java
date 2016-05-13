package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private Product mProduct;

    public ImagesAdapter(Product product) {
        mProduct = product;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_image_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mProductImage.setImageResource(mProduct.getImages().get(position));
    }

    @Override
    public int getItemCount() {
        return mProduct.getImages().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mProductImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mProductImage = (ImageView) itemView.findViewById(R.id.productImageListItem);
        }
    }
}
