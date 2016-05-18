package com.softranger.bayshopmf.ui.general;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.awaitingarrival.AwaitingArrivalProductFragment;
import com.softranger.bayshopmf.ui.inprocessing.InProcessingDetails;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.instock.DetailsFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageItemsFragment<T extends Parcelable> extends Fragment implements ItemAdapter.OnItemClickListener {

    private static final String URL_ARG = "URL to get information";
    private static final String DEPOSIT_ARG = "deposit argument";
    private MainActivity mActivity;
    public String mDeposit;
    private RecyclerView mRecyclerView;
    private Class mClass;
    private ArrayList<Object> mObjects;
    private ItemAdapter mAdapter;

    public StorageItemsFragment() {
        // Required empty public constructor
    }

    public static <T extends Parcelable> StorageItemsFragment newInstance(@NonNull ArrayList<T> items,
                                                                          @NonNull String deposit) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("items", items);
        StorageItemsFragment<T> fragment = new StorageItemsFragment<>();
        fragment.mDeposit = deposit;
        fragment.setArguments(args);
        return fragment;
    }

    public static StorageItemsFragment newInstance(@NonNull String url, @NonNull String deposit) {
        Bundle args = new Bundle();
        args.putString(URL_ARG, url);
        args.putString(DEPOSIT_ARG, deposit);
        StorageItemsFragment fragment = new StorageItemsFragment<>();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        mActivity = (MainActivity) getActivity();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.storage_itemsList);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(manager);

        mObjects = new ArrayList<>();
        mAdapter = new ItemAdapter(mActivity);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(Application.TOKEN_READY));
        String url = getArguments().getString(URL_ARG);
        mDeposit = getArguments().getString(DEPOSIT_ARG);
        if (url != null) {
            ApiClient.getInstance().sendRequest(url, mStorageHandler);
            mActivity.toggleLoadingProgress(true);
        }
        return view;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ApiClient.getInstance().sendRequest(Constants.Api.getStorageUrl(), mStorageHandler);
        }
    };

    private Handler mStorageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        boolean error = response.getBoolean("error");
                        if (!error) {
                            buildItemsList(response);
                        } else {
                            String message = response.optString("message", getString(R.string.unknown_error));
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mActivity.toggleLoadingProgress(false);
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
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
                    mActivity.toggleLoadingProgress(false);
                    break;
                }
                case Constants.ApiResponse.RESONSE_UNAUTHORIZED: {
                    mActivity.toggleLoadingProgress(false);
                    mActivity.logOut();
                }
            }
        }
    };

    private void buildItemsList(JSONObject response) {
        try {
            switch (StorageHolderFragment.listToShow) {
                case Constants.ListToShow.IN_STOCK: {
                    mObjects.add(new Object());
                    JSONArray jsonData = response.getJSONArray("data");
                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonItem = jsonData.getJSONObject(i);
                        InStockItem inStockItem = new InStockItem.Builder()
                                .deposit(mDeposit)
                                .id(jsonItem.getInt("id"))
                                .trackingNumber(jsonItem.getString("tracking"))
                                .name(jsonItem.optString("title", ""))
                                .parcelId(jsonItem.getString("uid"))
                                .hasDeclaration(jsonItem.getInt("isDeclarationFilled") == 1)
                                .build();
                        mObjects.add(inStockItem);
                    }
                    break;
                }
                case Constants.ListToShow.AWAITING_ARRIVAL: {
                    JSONArray jsonData = response.getJSONArray("data");
                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonItem = jsonData.getJSONObject(i);
                        Product inStockItem = new Product.Builder()
                                .deposit(mDeposit)
                                .id(jsonItem.getInt("id"))
                                .trackingNumber(jsonItem.getString("tracking"))
                                .productName(jsonItem.optString("title", ""))
                                .productId(jsonItem.getString("uid"))
                                .build();
                        mObjects.add(inStockItem);
                    }
                    break;
                }
                case Constants.ListToShow.IN_PROCESSING:

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mAdapter.refreshList(mObjects);
            mActivity.toggleLoadingProgress(false);
        }
    }

    @Override
    public void onRowClick(final InStockItem inStockItem, int position) {
        mActivity.addFragment(DetailsFragment.newInstance(inStockItem), true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.setToolbarTitle(inStockItem.getName(), true);
            }
        }, 300);
    }

    @Override
    public void onNoDeclarationItemSelected(InStockItem inStockItem, int position) {

    }

    @Override
    public void onIconClick(InStockItem inStockItem, boolean isSelected, int position) {

    }

    @Override
    public void onProductClick(Product product, int position) {
        mActivity.addFragment(AwaitingArrivalProductFragment.newInstance(product), true);
    }

    @Override
    public void onInProcessingProductClick(InProcessingParcel inProcessingParcel, int position) {
        mActivity.addFragment(InProcessingDetails.newInstance(inProcessingParcel), true);
    }

    @Override
    public void onCombineClick() {

    }

    @Override
    public void onCheckOrderClick() {

    }

    @Override
    public void onAdditionalPhotosClick() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
