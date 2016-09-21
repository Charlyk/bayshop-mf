package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.product.Product;
import com.softranger.bayshopmf.util.Application;

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
    private Object mActionHolder;
    private OnActionButtonsClick mOnActionButtonsClick;
    private InStockDetailed mInStockDetailed;

    public DeclarationListAdapter(InStockDetailed inStockDetailed) {
        mProducts = new ArrayList<>();
        mObjects = new ArrayList<>();
        // add in stock detailed item for list header
        mObjects.add(inStockDetailed);
        mInStockDetailed = inStockDetailed;
        // add an object for bottom action item
        // there are buttons add and save
        mActionHolder = new Object();
        mObjects.add(mActionHolder);

        if (!mInStockDetailed.isHasDeclaration()) {
            addNewProductCard();
        }
    }

    public void setOnActionButtonsClickListener(OnActionButtonsClick onActionButtonsClickListener) {
        mOnActionButtonsClick = onActionButtonsClickListener;
    }

    public void addNewProductCard() {
        Product product = new Product.Builder().productName("").productUrl("").productPrice("").productQuantity("").build();
        final int actionPosition = mObjects.indexOf(mActionHolder);
        mProducts.add(actionPosition - 1, product);
        mObjects.add(actionPosition, product);
        notifyItemInserted(actionPosition);
    }

    public void addItem(Product product) {
        mProducts.add(0, product);
        mObjects.add(1, product);
        notifyItemInserted(1);
    }

    public void removeItem(Product itemToRemove) {
        if (mObjects.size() > 3) {
            mProducts.remove(itemToRemove);
            final int itemPosition = mObjects.indexOf(itemToRemove);
            mObjects.remove(itemToRemove);
            notifyItemRemoved(itemPosition);
        }
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
            itemHolder.mProductName.addTextChangedListener(new NameTextWatcher(itemHolder.mProduct));
            itemHolder.mProductName.setText(product.getProductName());
            itemHolder.mProductUrl.addTextChangedListener(new UrlTextWatcher(itemHolder.mProduct));
            itemHolder.mProductUrl.setText(product.getProductUrl());
            itemHolder.mProductPrice.addTextChangedListener(new PriceTextWatcher(itemHolder.mProduct));
            itemHolder.mProductPrice.setText(String.valueOf(product.getProductPrice()));
            itemHolder.mProductQuantity.addTextChangedListener(new QuantityTextWatcher(itemHolder.mProduct));
            itemHolder.mProductQuantity.setText(String.valueOf(product.getProductQuantity()));
        } else if (mObjects.get(position) instanceof InStockItem) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.mInStockDetailed = (InStockDetailed) mObjects.get(position);
            headerHolder.mGeneralDescription.addTextChangedListener(new DescriptionTextWatcher(headerHolder.mInStockDetailed));
            String description = "";
            if (headerHolder.mInStockDetailed.getDescription() != null
                    && !headerHolder.mInStockDetailed.getDescription().equals("null")) {
                description = headerHolder.mInStockDetailed.getDescription();
            }
            headerHolder.mGeneralDescription.setText(description);
//            String trackingNumber = headerHolder.mInStockDetailed.getTrackingNumber();
//            headerHolder.mTrackingNumber.setText(trackingNumber);
//            headerHolder.mDepositName.setText(headerHolder.mInStockDetailed.getDeposit());
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
                    if (mOnActionButtonsClick != null) {
                        mOnActionButtonsClick.onAddFieldsClick();
                    }
                    // TODO: 5/10/16 Add fields to list
                    break;
                case R.id.saveFieldsBtn:
                    if (mOnActionButtonsClick != null) {
                        mOnActionButtonsClick.onSaveItemsClick(mInStockDetailed, mProducts);
                    }
                    // TODO: 5/10/16 Save all items
                    break;
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {

//        final TextView mDepositName;
//        final TextView mTrackingNumber;
        final EditText mGeneralDescription;
        InStockDetailed mInStockDetailed;

        public HeaderViewHolder(View itemView) {
            super(itemView);
//            mDepositName = (TextView) itemView.findViewById(R.id.depositTextLabel);
//            mTrackingNumber = (TextView) itemView.findViewById(R.id.trackingNumberLabel);
            mGeneralDescription = (EditText) itemView.findViewById(R.id.generalDescriptionLabel);
            mGeneralDescription.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mGeneralDescription.setHint("");
            } else {
                mGeneralDescription.setHint(Application.getInstance().getString(R.string.general_description_hint));
            }
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

        final EditText mProductName;
        final EditText mProductUrl;
        final EditText mProductQuantity;
        final EditText mProductPrice;
        final ImageButton mGoToUrlBtn;
        final ImageButton mItemDeleteButton;
        Product mProduct;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mProductName = (EditText) itemView.findViewById(R.id.productNameInput);
            mProductName.setOnFocusChangeListener(this);
            mProductUrl = (EditText) itemView.findViewById(R.id.productUrlInput);
            mProductUrl.setOnFocusChangeListener(this);
            mProductQuantity = (EditText) itemView.findViewById(R.id.productQuantityInput);
            mProductQuantity.setOnFocusChangeListener(this);
            mProductPrice = (EditText) itemView.findViewById(R.id.productPriceInput);
            mProductPrice.setOnFocusChangeListener(this);
            mItemDeleteButton = (ImageButton) itemView.findViewById(R.id.declarationItemDeleteButton);
            mItemDeleteButton.setOnClickListener(this);
            mGoToUrlBtn = (ImageButton) itemView.findViewById(R.id.declarationOpenUrlBtn);
            mGoToUrlBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.declarationOpenUrlBtn:
                    String productUrl = String.valueOf(mProductUrl.getText());
                    if (productUrl != null && !productUrl.equals("") && mOnActionButtonsClick != null) {
                        mOnActionButtonsClick.onOpenUrl(productUrl, getAdapterPosition());
                    }
                    break;
                case R.id.declarationItemDeleteButton:
                    removeItem(mProduct);
                    if (mOnActionButtonsClick != null) {
                        mOnActionButtonsClick.onDeleteClick(getAdapterPosition());
                    }
                    break;
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.productNameInput:
                    if (hasFocus) mProductName.setHint("");
                    else mProductName.setHint(Application.getInstance().getString(R.string.example_product));
                    break;
                case R.id.productUrlInput:
                    if (hasFocus) mProductUrl.setHint("");
                    else mProductUrl.setHint(Application.getInstance().getString(R.string.http_example_com_example_product));
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

    class DescriptionTextWatcher implements TextWatcher {

        private InStockDetailed mInStockDetailed;

        public DescriptionTextWatcher(InStockDetailed inStockDetailed) {
            mInStockDetailed = inStockDetailed;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mInStockDetailed.setDescription(String.valueOf(s));
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
            mProduct.setProductName(String.valueOf(s));
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
            mProduct.setProductUrl(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class QuantityTextWatcher implements TextWatcher {

        private Product mProduct;

        public QuantityTextWatcher(Product productBuilder) {
            mProduct = productBuilder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProduct.setProductQuantity(String.valueOf(s));
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
            mProduct.setProductPrice(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public interface OnActionButtonsClick {
        void onAddFieldsClick();
        void onSaveItemsClick(InStockDetailed inStockDetailed, ArrayList<Product> products);
        void onDeleteClick(int position);
        void onOpenUrl(String url, int position);
    }
}
