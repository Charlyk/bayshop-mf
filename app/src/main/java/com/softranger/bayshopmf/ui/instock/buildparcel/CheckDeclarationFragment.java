package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.PackageDetailsAdapter;
import com.softranger.bayshopmf.model.InForming;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.ShippingMethod;
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
public class CheckDeclarationFragment extends Fragment implements View.OnClickListener {

    private static final String IN_FORMING_ARG = "in forming object argument";
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private InForming mInForming;
    private ArrayList<Product> mProducts;
    private PackageDetailsAdapter mAdapter;
    private TextView mTotalPriceLabel;
    private TextView mTotalWeightLabel;

    public CheckDeclarationFragment() {
        // Required empty public constructor
    }

    public static CheckDeclarationFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        CheckDeclarationFragment fragment = new CheckDeclarationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_declaration, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.check_list), true);
        mProducts = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.buildFourthStepDeclarationList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new PackageDetailsAdapter(mProducts);
        mTotalPriceLabel = (TextView) view.findViewById(R.id.buildFourthFragmentTotalPriceLabel);
        mTotalWeightLabel = (TextView) view.findViewById(R.id.buildFourthFragmentTotalWeightLabel);
        mRecyclerView.setAdapter(mAdapter);
        mInForming = getArguments().getParcelable(IN_FORMING_ARG);
        Button next = (Button) view.findViewById(R.id.buildFourthStepNextButton);
        next.setOnClickListener(this);
        mActivity.toggleLoadingProgress(true);
        RequestBody body = new FormBody.Builder()
                .add("shipperMeasureId", String.valueOf(mInForming.getShippingMethod().getId()))
                .build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.urlBuildStep(4, String.valueOf(mInForming.getId())), mHandler);
        return view;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONArray jsonDec = response.getJSONObject("data").getJSONArray("declarationItems");
                            for (int i = 0; i < jsonDec.length(); i++) {
                                JSONObject jsonProd = jsonDec.getJSONObject(i);
                                Product product = new Product.Builder()
                                        .id(jsonProd.getInt("id"))
                                        .productName(jsonProd.getString("title"))
                                        .productQuantity(jsonProd.getString("quantity"))
                                        .productPrice(jsonProd.getString("price"))
                                        .productUrl(jsonProd.getString("url"))
                                        .orderStorageId(jsonProd.getString("orderStorageId"))
                                        .weight(jsonProd.getString("weight"))
                                        .declarationId(jsonProd.getString("declarationId"))
                                        .build();
                                mProducts.add(product);
                            }
                            mInForming.setProducts(mProducts);
                            mAdapter.notifyDataSetChanged();
                            setTotals();
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
            }
            mActivity.toggleLoadingProgress(false);
        }
    };

    private void setTotals() {
        int weight = 0;
        double price = 0;

        for (Product product : mInForming.getProducts()) {
            weight += Double.parseDouble(product.getWeight());
            price += Double.parseDouble(product.getProductPrice());
        }

        mTotalPriceLabel.setText("$" + String.valueOf(price));
        mTotalWeightLabel.setText(String.valueOf(((float) (weight/1000))) + "kg.");
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.check_list), true);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }

    @Override
    public void onClick(View v) {
        mActivity.addFragment(new InsuranceFragment(), true);
    }
}
