package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.DeclarationListAdapter;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclarationFragment extends ParentFragment implements DeclarationListAdapter.OnActionButtonsClick {

    private static final String IN_STOCK_ARG = "in stock argument";

    private MainActivity mActivity;
    private DeclarationListAdapter mDeclarationAdapter;
    private RecyclerView mRecyclerView;
    private InStockDetailed mInStockDetailed;
    private static boolean isSaveClicked;
    private CustomTabsIntent mTabsIntent;

    public DeclarationFragment() {
        // Required empty public constructor
    }

    public static DeclarationFragment newInstance(InStockDetailed inStockDetailed) {
        Bundle args = new Bundle();
        args.putParcelable(IN_STOCK_ARG, inStockDetailed);
        DeclarationFragment fragment = new DeclarationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mActivity = (MainActivity) getActivity();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentSwipeRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mInStockDetailed = getArguments().getParcelable(IN_STOCK_ARG);
        mDeclarationAdapter = new DeclarationListAdapter(mInStockDetailed);
        mDeclarationAdapter.setOnActionButtonsClickListener(this);
        mRecyclerView.setAdapter(mDeclarationAdapter);
        mActivity.toggleLoadingProgress(true);
        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(mActivity.getResources().getColor(R.color.colorPrimary));
        mTabsIntent = tabsBuilder.build();
        ApiClient.getInstance().getRequest(Constants.Api.urlMfDeclaration(String.valueOf(mInStockDetailed.getID())), mHandler);
        return view;
    }

    @Override
    public void onAddFieldsClick() {
        mDeclarationAdapter.addNewProductCard();
        mRecyclerView.smoothScrollToPosition(mDeclarationAdapter.getItemCount() - 1);
    }

    @Override
    public void onSaveItemsClick(InStockDetailed inStockDetailed, ArrayList<Product> products) {
        for (Product product : products) {
            int productIndex = products.indexOf(product);
            if (productIndex < (products.size() - 1)) {
                if (!isAllDataProvided(product)) {
                    if (!product.getProductName().equals(""))
                        Snackbar.make(mRecyclerView, getString(R.string.specify_all_data) + " "
                                + product.getProductName(), Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(mRecyclerView, getString(R.string.specify_all_data) + " item "
                                + (products.indexOf(product) + 1), Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (inStockDetailed.getDescription().equals("")) {
            Snackbar.make(mRecyclerView, getString(R.string.please_enter_description), Snackbar.LENGTH_SHORT).show();
            return;
        }
        isSaveClicked = true;
        RequestBody body = new FormBody.Builder().add("title", inStockDetailed.getDescription())
                .add("declarationItems", String.valueOf(buildProductsArray(products))).build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlMfDeclaration(String.valueOf(mInStockDetailed.getID())), mHandler);
    }

    @Override
    public void onDeleteClick(int position) {
        mActivity.hideKeyboard();
    }

    @Override
    public void onOpenUrl(String url, int position) {
        mTabsIntent.launchUrl(mActivity, Uri.parse(url));
    }

    private JSONArray buildProductsArray(ArrayList<Product> products) {
        JSONArray productsArray = new JSONArray();
        try {
            for (Product product : products) {
                JSONObject jsonProduct = new JSONObject();
                jsonProduct.put("title", product.getProductName());
                jsonProduct.put("quantity", product.getProductQuantity());
                jsonProduct.put("price", product.getProductPrice());
                jsonProduct.put("url", product.getProductUrl());
                if (product.getProductId() != null && !product.getProductId().equals("")) {
                    jsonProduct.put("declarationItemId", product.getProductId());
                }
                productsArray.put(jsonProduct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productsArray;
    }

    private boolean isAllDataProvided(Product product) {
        return !product.getProductName().equals("") && !product.getProductUrl().equals("")
                && !product.getProductPrice().equals("") && !product.getProductQuantity().equals("")
                && !product.getProductQuantity().equals("0") && !product.getProductPrice().equals("0");
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        if (isSaveClicked) {
            Intent update = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
            update.putExtra("deposit", mInStockDetailed.getDeposit());
            mActivity.sendBroadcast(update);
            mActivity.onBackPressed();
            mActivity.hideKeyboard();
            Snackbar.make(mRecyclerView, getString(R.string.declaration_saved), Snackbar.LENGTH_SHORT).show();
        } else {
            JSONObject data = response.getJSONObject("data");
            mInStockDetailed.setDescription(data.getString("title"));
            mDeclarationAdapter.notifyItemChanged(0);
            JSONArray products = data.getJSONArray("declarationItems");
            for (int i = 0; i < products.length(); i++) {
                JSONObject p = products.getJSONObject(i);
                Product product = new Product.Builder()
                        .productName(p.getString("title"))
                        .productQuantity(p.getString("quantity"))
                        .productPrice(p.getString("price"))
                        .productUrl(p.getString("url"))
                        .productId("declarationItemId")
                        .build();
                mDeclarationAdapter.addItem(product);
            }
        }

        isSaveClicked = false;
    }

    @Override
    public void finallyMethod() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.edit_declaration);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.edit_declaration;
    }
}
