package com.softranger.bayshopmfr.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.box.Product;
import com.softranger.bayshopmfr.util.Application;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * Created by Eduard Albu on 5/10/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class DeclarationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Product> mProducts;
    private OnActionButtonsClick mOnActionButtonsClick;
    private static final int TRACKING = 0, ITEM = 1;
    private boolean mShowTracking;
    private String mTrackingNum;

    public DeclarationListAdapter(ArrayList<Product> products, boolean hasDeclaration, boolean showTracking) {
        mProducts = products;
        mShowTracking = showTracking;
        if (!hasDeclaration) {
            addNewProductCard();
        }
    }

    public void setOnActionButtonsClickListener(OnActionButtonsClick onActionButtonsClickListener) {
        mOnActionButtonsClick = onActionButtonsClickListener;
    }

    public void setTrackingNum(String trackingNum) {
        mTrackingNum = trackingNum;
    }

    public int addNewProductCard() {
        Product product = new Product();
        mProducts.add(product);
        int position = mProducts.indexOf(product);
        final int index = mShowTracking ? position + 1 : position;
        notifyItemInserted(index);
        return index;
    }

    public void addItem(Product product) {
        mProducts.add(0, product);
        notifyItemInserted(0);
    }

    public void removeItem(int position) {
        if (mProducts.size() == 1)
            return;
        mProducts.remove(mShowTracking ? position - 1 : position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowTracking && position == TRACKING) {
            return TRACKING;
        }
        return ITEM;
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TRACKING: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_number_layout, parent, false);
                return new TrackingHolder(view);
            }
            default: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.declaration_item_layout, parent, false);
                return new ItemViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            Product product = mProducts.get(mShowTracking ? position - 1 : position);
            itemHolder.mProduct = product;
            itemHolder.mProductName.addTextChangedListener(new NameTextWatcher(itemHolder.mProduct));
            itemHolder.mProductName.setText(product.getTitle());
            itemHolder.mProductUrl.addTextChangedListener(new UrlTextWatcher(itemHolder.mProduct));
            itemHolder.mProductUrl.setText(product.getUrl());
            itemHolder.mProductPrice.addTextChangedListener(new PriceTextWatcher(itemHolder.mProduct));
            itemHolder.mProductPrice.setText(String.valueOf(product.getPrice()));
            itemHolder.mProductQuantity.addTextChangedListener(new QuantityTextWatcher(itemHolder.mProductQuantity, itemHolder.mProduct));
            String quantity = String.valueOf(product.getQuantity());
            if (quantity.equals("")) quantity = "1";
            itemHolder.mProductQuantity.setText(quantity);
        } else if (holder instanceof TrackingHolder) {
            TrackingHolder trackingHolder = (TrackingHolder) holder;
            trackingHolder.mEditText.setText(mTrackingNum);
        }
    }

    @Override
    public int getItemCount() {
        if (mShowTracking) return mProducts.size() + 1;
        else return mProducts.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.productNameInput)
        EditText mProductName;
        @BindView(R.id.productUrlInput)
        EditText mProductUrl;
        @BindView(R.id.productQuantityInput)
        EditText mProductQuantity;
        @BindView(R.id.productPriceInput)
        EditText mProductPrice;
        Product mProduct;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.declarationOpenUrlBtn, R.id.declarationItemDeleteButton})
        void didClickItemButtons(View v) {
            switch (v.getId()) {
                case R.id.declarationOpenUrlBtn:
                    String productUrl = String.valueOf(mProductUrl.getText());
                    if (productUrl != null && !productUrl.equals("") && mOnActionButtonsClick != null) {
                        mOnActionButtonsClick.onOpenUrl(productUrl, getAdapterPosition());
                    }
                    break;
                case R.id.declarationItemDeleteButton:
                    if (mOnActionButtonsClick != null) {
                        mOnActionButtonsClick.onDeleteClick(mProduct, getAdapterPosition());
                    }
                    break;
            }
        }

        @OnFocusChange({R.id.productNameInput, R.id.productUrlInput, R.id.productQuantityInput, R.id.productPriceInput})
        void onFocusChaged(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.productNameInput:
                    if (hasFocus) mProductName.setHint("");
                    else
                        mProductName.setHint(Application.getInstance().getString(R.string.example_product));
                    break;
                case R.id.productUrlInput:
                    if (hasFocus) mProductUrl.setHint("");
                    else
                        mProductUrl.setHint(Application.getInstance().getString(R.string.http_example_com_example_product));
                    break;
                case R.id.productQuantityInput:
                    if (hasFocus) mProductQuantity.setHint("");
                    else mProductQuantity.setHint(Application.getInstance().getString(R.string._0));
                    break;
                case R.id.productPriceInput:
                    if (hasFocus) mProductPrice.setHint("");
                    else mProductPrice.setHint(Application.getInstance().getString(R.string._0_0));
                    break;
            }
        }
    }

    class TrackingHolder extends RecyclerView.ViewHolder implements TextWatcher {
        @BindView(R.id.addAwaitingTrackingNumInput)
        EditText mEditText;

        public TrackingHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mEditText.addTextChangedListener(this);
        }

        @OnFocusChange(R.id.addAwaitingTrackingNumInput)
        void onFocusChanged(View view, boolean hasFocus) {
            if (hasFocus) {
                mEditText.setHint("");
            } else {
                mEditText.setHint(Application.getInstance().getString(R.string.tracking_example));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mOnActionButtonsClick != null) {
                mOnActionButtonsClick.onTrackingChanged(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class NameTextWatcher implements TextWatcher {

        private Product mProduct;

        public NameTextWatcher(Product productBuilder) {
            mProduct = productBuilder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setTitle(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class UrlTextWatcher implements TextWatcher {

        private Product mProduct;

        public UrlTextWatcher(Product productBuilder) {
            mProduct = productBuilder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setUrl(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class QuantityTextWatcher implements TextWatcher {

        private Product mProduct;
        private EditText mEditText;

        public QuantityTextWatcher(EditText editText, Product productBuilder) {
            mProduct = productBuilder;
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null || s.equals("")) {
                s = "1";
                if (mEditText != null) mEditText.setText(s);
            }
            mProduct.setQuantity(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class PriceTextWatcher implements TextWatcher {

        private Product mProduct;

        public PriceTextWatcher(Product productBuilder) {
            mProduct = productBuilder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setPrice(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public interface OnActionButtonsClick {

        void onDeleteClick(Product product, int position);

        void onOpenUrl(String url, int position);

        void onTrackingChanged(String currentTracking);
    }
}
