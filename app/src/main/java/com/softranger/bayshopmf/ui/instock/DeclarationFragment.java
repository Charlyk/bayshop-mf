package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.DeclarationListAdapter;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.Photo;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclarationFragment extends Fragment implements DeclarationListAdapter.OnActionButtonsClick {

    private static final String IN_STOCK_ARG = "in stock argument";

    private MainActivity mActivity;
    private DeclarationListAdapter mDeclarationAdapter;
    private RecyclerView mRecyclerView;
    private InStockDetailed mInStockDetailed;

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
        View view = inflater.inflate(R.layout.fragment_declaration, container, false);
        mActivity = (MainActivity) getActivity();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.declarationItemsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mInStockDetailed = getArguments().getParcelable(IN_STOCK_ARG);
        mDeclarationAdapter = new DeclarationListAdapter(mInStockDetailed);
        mDeclarationAdapter.setOnActionButtonsClickListener(this);
        mRecyclerView.setAdapter(mDeclarationAdapter);
        ApiClient.getInstance().sendRequest(Constants.Api.getMfDeclarationUrl(String.valueOf(mInStockDetailed.getID())), mDeclarationHandler);
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
        RequestBody body = new FormBody.Builder().add("title", inStockDetailed.getDescription())
                .add("declarationItems", String.valueOf(buildProductsArray(products))).build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.getMfDeclarationUrl(String.valueOf(mInStockDetailed.getID())), mEditDeclarationHandler);
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

    private Handler mEditDeclarationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            mActivity.onBackPressed();
                            Snackbar.make(mRecyclerView, getString(R.string.declaration_saved), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESONSE_UNAUTHORIZED: {

                }
            }
        }
    };

    private Handler mDeclarationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
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
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESONSE_UNAUTHORIZED: {

                }
            }
        }
    };
}
