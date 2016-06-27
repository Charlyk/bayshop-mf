package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.Product;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/27/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class PackageDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> mProducts;
    private OnEditClickListener mOnEditClickListener;
    private static final int SEPARATOR = 0, ITEM = 1;

    public PackageDetailsAdapter(ArrayList<InStockDetailed> products) {
        mProducts = new ArrayList<>();
        for (InStockDetailed detailed : products) {
            mProducts.add(detailed);
            mProducts.addAll(detailed.getProducts());
        }
    }

    public void addItems(ArrayList<InStockDetailed> detailedList) {
        for (InStockDetailed detailed : detailedList) {
            mProducts.add(detailed);
            mProducts.addAll(detailed.getProducts());
        }
        notifyDataSetChanged();
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        mOnEditClickListener = onEditClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mProducts.get(position) instanceof InStockDetailed) {
            return SEPARATOR;
        } else {
            return ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SEPARATOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_declaration_separator, parent, false);
                return new SeparatorHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_content_item, parent, false);
                return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.mProduct = (Product) mProducts.get(position);
            itemHolder.mDescription.setText(itemHolder.mProduct.getProductName());
            itemHolder.mQuantity.setText(itemHolder.mProduct.getProductQuantity());
            int weight = Integer.parseInt(itemHolder.mProduct.getWeight());
            double w = weight / 1000;
            itemHolder.mWeight.setText(String.valueOf(w));
            itemHolder.mPrice.setText(itemHolder.mProduct.getProductPrice());
        } else if (holder instanceof SeparatorHolder) {
            SeparatorHolder separatorHolder = (SeparatorHolder) holder;
            separatorHolder.mDetailed = (InStockDetailed) mProducts.get(position);
            separatorHolder.mBoxUID.setText(separatorHolder.mDetailed.getParcelId());
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class SeparatorHolder extends RecyclerView.ViewHolder {
        final TextView mBoxUID;
        InStockDetailed mDetailed;

        public SeparatorHolder(View itemView) {
            super(itemView);
            mBoxUID = (TextView) itemView.findViewById(R.id.checkDeclarationSeparatorTitleLabel);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mDescription;
        final TextView mQuantity;
        final TextView mWeight;
        final TextView mPrice;
        final LinearLayout mPriceLayout;
        Product mProduct;

        public ItemHolder(View itemView) {
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
