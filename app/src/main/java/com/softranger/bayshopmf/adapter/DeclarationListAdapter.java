package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Product;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/10/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class DeclarationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> mObjects;
    private static final int HEADER = 1;
    private static final int ITEM = 0;
    private static final int ACTION = 2;
    private ArrayList<Product> mProducts;

    public DeclarationListAdapter(InStockDetailed inStockDetailed) {
        mProducts = new ArrayList<>();
        mObjects = new ArrayList<>();
        mObjects.add(inStockDetailed);
        Product product = new Product.Builder().productName("").productUrl("").build();
        mObjects.add(product);
        mObjects.add(new Object());
    }

    @Override
    public int getItemViewType(int position) {
        if (mObjects.get(position) instanceof InStockDetailed) {
            return HEADER;
        } else if (mObjects.get(position) instanceof Product) {
            return ITEM;
        } else return ACTION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.declaration_header_layout, parent, false);
                return new HeaderViewHolder(view);
            case ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.declaration_item_layout, parent, false);
                return new ItemViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_declaration_item, parent, false);
                return new ActionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mObjects.get(position) instanceof Product) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            Product product = (Product) mObjects.get(position);
            itemHolder.mProduct = product;
            itemHolder.mProductName.setText(product.getProductName());
            itemHolder.mProductUrl.setText(product.getProductUrl());
            itemHolder.mProductPrice.setText(String.valueOf(product.getProductPrice()));
            itemHolder.mProductQuantity.setText(String.valueOf(product.getProductQuantity()));
        } else if (mObjects.get(position) instanceof InStockItem) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            String trackingNumber = ((InStockDetailed) mObjects.get(position)).getTrackingNumber();
            headerHolder.mTrackingNumber.setText(trackingNumber);
            headerHolder.mDepositName.setText("USA"); // TODO: 5/10/16 replace with parcel deposit name
        }
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    class ActionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final Button mAddFields;
        final Button mSaveItems;

        public ActionViewHolder(View itemView) {
            super(itemView);
            mAddFields = (Button) itemView.findViewById(R.id.addFieldsBtn);
            mSaveItems = (Button) itemView.findViewById(R.id.saveFieldsBtn);

            mAddFields.setOnClickListener(this);
            mSaveItems.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addFieldsBtn:
                    // TODO: 5/10/16 Add fields to list
                    break;
                case R.id.saveFieldsBtn:
                    // TODO: 5/10/16 Save all items
                    break;
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView mDepositName;
        final TextView mTrackingNumber;
        final EditText mGeneralDescription;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mDepositName = (TextView) itemView.findViewById(R.id.depositTextLabel);
            mTrackingNumber = (TextView) itemView.findViewById(R.id.trackingNumberLabel);
            mGeneralDescription = (EditText) itemView.findViewById(R.id.generalDescriptionLabel);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        final EditText mProductName;
        final EditText mProductUrl;
        final EditText mProductQuantity;
        final EditText mProductPrice;
        Product mProduct;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mProductName = (EditText) itemView.findViewById(R.id.productNameInput);
            mProductName.addTextChangedListener(new NameTextWatcher(mProduct, getAdapterPosition()));
            mProductUrl = (EditText) itemView.findViewById(R.id.productUrlInput);
            mProductUrl.addTextChangedListener(new UrlTextWatcher(mProduct, getAdapterPosition()));
            mProductQuantity = (EditText) itemView.findViewById(R.id.productQuantityInput);
            mProductQuantity.addTextChangedListener(new QuantityTextWatcher(mProduct, getAdapterPosition()));
            mProductPrice = (EditText) itemView.findViewById(R.id.productPriceInput);
            mProductPrice.addTextChangedListener(new PriceTextWatcher(mProduct, getAdapterPosition()));
        }
    }

    class NameTextWatcher implements TextWatcher {

        private Product mProduct;
        private int mPosition;

        public NameTextWatcher(Product productBuilder, int position) {
            mProduct = productBuilder;
            mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setProductName(s.toString());
//            notifyItemChanged(mPosition);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class UrlTextWatcher implements TextWatcher {

        private Product mProduct;
        private int mPosition;

        public UrlTextWatcher(Product productBuilder, int position) {
            mProduct = productBuilder;
            mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setProductUrl(s.toString());
//            notifyItemChanged(mPosition);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class QuantityTextWatcher implements TextWatcher {

        private Product mProduct;
        private int mPosition;

        public QuantityTextWatcher(Product productBuilder, int position) {
            mProduct = productBuilder;
            mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setProductQuantity(Integer.parseInt(s.toString()));
//            notifyItemChanged(mPosition);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class PriceTextWatcher implements TextWatcher {

        private Product mProduct;
        private int mPosition;

        public PriceTextWatcher(Product productBuilder, int position) {
            mProduct = productBuilder;
            mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setProductPrice(Double.parseDouble(s.toString()));
//            notifyItemChanged(mPosition);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
