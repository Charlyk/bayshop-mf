package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/27/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class PackageDetailsAdapter extends RecyclerView.Adapter<PackageDetailsAdapter.ViewHolder> {

    private ArrayList<Product> mProducts;
    private OnEditClickListener mOnEditClickListener;

    public PackageDetailsAdapter(ArrayList<Product> products) {
        mProducts = products;
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        mOnEditClickListener = onEditClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mProduct = mProducts.get(position);
        holder.mDescription.setText(holder.mProduct.getProductName());
        holder.mQuantity.setText(holder.mProduct.getProductQuantity());
        int weight = Integer.parseInt(holder.mProduct.getWeight());
        double w = weight / 1000;
        holder.mWeight.setText(String.valueOf(w));
        holder.mPrice.setText(holder.mProduct.getProductPrice());
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mDescription;
        final TextView mQuantity;
        final TextView mWeight;
        final TextView mPrice;
        final LinearLayout mPriceLayout;
        Product mProduct;
        public ViewHolder(View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.packageContentProductDescriptionLabel);
            mQuantity = (TextView) itemView.findViewById(R.id.packageCOntentUnitsQuantityLabel);
            mWeight = (TextView) itemView.findViewById(R.id.packageContentWeightLabel);
            mPrice = (TextView) itemView.findViewById(R.id.packageContentPriceLabel);
            mPriceLayout = (LinearLayout) itemView.findViewById(R.id.checkDeclarationPriceItemLayout);
            mPriceLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnEditClickListener != null) {
                mOnEditClickListener.onEditClicked(mProduct, getAdapterPosition());
            }
        }
    }

    public interface OnEditClickListener {
        void onEditClicked(Product product, int position);
    }
}
