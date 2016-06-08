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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ShippingMethodAdapter;
import com.softranger.bayshopmf.model.InForming;
import com.softranger.bayshopmf.model.ShippingMethod;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
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
public class ShippingMethodFragment extends Fragment implements ShippingMethodAdapter.OnShippingClickListener {

    private static final String IN_FORMING_ARG = "in forming object arg";
    private MainActivity mActivity;
    private ShippingMethodAdapter mAdapter;
    private InForming mInForming;
    private ArrayList<ShippingMethod> mMethods;
    private RecyclerView mRecyclerView;

    public ShippingMethodFragment() {
        // Required empty public constructor
    }

    public static ShippingMethodFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        ShippingMethodFragment fragment = new ShippingMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shipping_method, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.shipping_method), true);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.buildShippingMethodList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mMethods = new ArrayList<>();
        mAdapter = new ShippingMethodAdapter(mMethods);
        mAdapter.setOnShippingClickListener(this);
        mInForming = getArguments().getParcelable(IN_FORMING_ARG);
        mRecyclerView.setAdapter(mAdapter);
        mActivity.toggleLoadingProgress(true);
        RequestBody body = new FormBody.Builder()
                .add("memberAddress", String.valueOf(mInForming.getAddress().getId()))
                .build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.urlBuildStep(3, String.valueOf(mInForming.getId())), mHandler);
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
                            mMethods.clear();
                            JSONArray methodsArray = response.getJSONObject("data").getJSONArray("shippingList");
                            for (int i = 0; i < methodsArray.length(); i++) {
                                JSONObject jsonMethod = methodsArray.getJSONObject(i);
                                ShippingMethod method = new ShippingMethod.Builder()
                                        .id(jsonMethod.getInt("id"))
                                        .name(jsonMethod.getString("title"))
                                        .mEstimatedTime(jsonMethod.getString("estimateTime"))
                                        .maxWeight(jsonMethod.getDouble("maxWeight"))
                                        .description(jsonMethod.getString("description"))
                                        .calculatedPrice(jsonMethod.getDouble("calculatedPrice"))
                                        .rank(jsonMethod.getInt("rank"))
                                        .currency(jsonMethod.getString("currency"))
                                        .build();
                                mMethods.add(method);
                            }
                            mAdapter.refreshList(mMethods);
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

    @Override
    public void onDetailsClick(ShippingMethod shippingMethod, int position) {
        String description = Html.fromHtml(shippingMethod.getDescription()).toString();
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setTitle(shippingMethod.getName())
                .setMessage(description)
                .setPositiveButton(getString(R.string.ok), null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onSelectClick(ShippingMethod shippingMethod, int position) {
        mInForming.setShippingMethod(shippingMethod);
        mActivity.addFragment(CheckDeclarationFragment.newInstance(mInForming), true);
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.shipping_method), true);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }
}
