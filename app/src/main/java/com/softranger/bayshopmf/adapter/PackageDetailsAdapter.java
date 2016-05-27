package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public PackageDetailsAdapter(ArrayList<Product> products) {
        mProducts = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mDescription;
        final TextView mQuantity;
        final TextView mWeight;
        final TextView mPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.packageContentProductDescriptionLabel);
            mQuantity = (TextView) itemView.findViewById(R.id.packageCOntentUnitsQuantityLabel);
            mWeight = (TextView) itemView.findViewById(R.id.packageContentWeightLabel);
            mPrice = (TextView) itemView.findViewById(R.id.packageContentPriceLabel);
        }
    }
}
