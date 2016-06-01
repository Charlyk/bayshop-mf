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
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
import com.softranger.bayshopmf.model.InForming;
import com.softranger.bayshopmf.model.InProcessing;
import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.awaitingarrival.AwaitingArrivalProductFragment;
import com.softranger.bayshopmf.ui.inprocessing.InProcessingDetails;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.instock.DetailsFragment;
import com.softranger.bayshopmf.ui.instock.buildparcel.ItemsListFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageItemsFragment<T extends Parcelable> extends Fragment implements ItemAdapter.OnItemClickListener {

    public static final String ACTION_ITEM_CHANGED = "item was changed from a top fragment";
    private static final String URL_ARG = "URL to get information";
    private static final String DEPOSIT_ARG = "deposit argument";
    private MainActivity mActivity;
    public String mDeposit;
    private RecyclerView mRecyclerView;
    private Class mClass;
    private ArrayList<Object> mObjects;
    private ItemAdapter mAdapter;
    private String mUrl;
    private ArrayList<InStockItem> mDetailedList;

    public StorageItemsFragment() {
        // Required empty public constructor
    }


    // TODO: 5/26/16 trebuie ca receiveru sa se filtreze in dependenta de depositul curent al fragmentului



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

        IntentFilter intentFilter = new IntentFilter(ACTION_ITEM_CHANGED);
        intentFilter.addAction(MainActivity.ACTION_START_CREATING_PARCEL);
        intentFilter.addAction(Application.TOKEN_READY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.storage_itemsList);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(manager);

        mDetailedList = new ArrayList<>();

        mObjects = new ArrayList<>();
        mAdapter = new ItemAdapter(mActivity);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mUrl = getArguments().getString(URL_ARG);
        mDeposit = getArguments().getString(DEPOSIT_ARG);
        if (mUrl != null) {
            ApiClient.getInstance().sendRequest(mUrl, mStorageHandler);
            mActivity.toggleLoadingProgress(true);
        }
        return view;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            switch (intent.getAction()) {
                case ACTION_ITEM_CHANGED:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mDeposit.equalsIgnoreCase(intent.getStringExtra("deposit"))) {
                                mObjects.clear();
                                mActivity.toggleLoadingProgress(true);
                                ApiClient.getInstance().sendRequest(mUrl, mStorageHandler);
                            }
                        }
                    }, 200);
                    break;
                case MainActivity.ACTION_START_CREATING_PARCEL:
                    if (mDetailedList.size() == 0) {
                        Snackbar.make(mRecyclerView, getString(R.string.please_select_parcels), Snackbar.LENGTH_SHORT).show();
                        mActivity.mActionMenu.collapse();
                    } else {
                        ArrayList<InStockItem> inStockItems = new ArrayList<>();
                        inStockItems.addAll(mDetailedList);
                        mActivity.addFragment(ItemsListFragment.newInstance(inStockItems), true);
                        mActivity.mActionMenu.collapse();
                    }
                    break;
            }
        }
    };

    private Handler mStorageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            buildItemsList(response);
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
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
                // TODO: 5/31/16 handle results for each parcel status
                case Constants.ListToShow.RECEIVED:
                case Constants.ListToShow.SENT:
                case Constants.ListToShow.AWAITING_SENDING:
                case Constants.ListToShow.IN_FORMING:
                case Constants.ListToShow.IN_PROCESSING: {
                    JSONArray jsonData = response.getJSONArray("data");
                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonItem = jsonData.getJSONObject(i);
                        InProcessing inProcessing = new InProcessing.Builder()
                                .id(jsonItem.getInt("id"))
                                .codeNumber(jsonItem.optString("codeNumber", ""))
                                .name(jsonItem.getString("name"))
                                .weight(jsonItem.getInt("realWeight"))
                                .progress(jsonItem.optInt("percent", 0))
                                .createdDate(jsonItem.optString("created", ""))
                                .build();
                        mObjects.add(inProcessing);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refreshList(mObjects);
                    mActivity.toggleLoadingProgress(false);
                }
            });
        }
    }

    @Override
    public void onRowClick(final InStockItem inStockItem, int position) {
        mActivity.addFragment(DetailsFragment.newInstance(inStockItem), true);
        mActivity.setToolbarTitle(inStockItem.getName(), true);
    }

    @Override
    public void onNoDeclarationItemSelected(final InStockItem inStockItem, int position) {
        mActivity.addFragment(DetailsFragment.newInstance(inStockItem), true);
        Toast.makeText(mActivity, getString(R.string.fill_in_the_declaration), Toast.LENGTH_SHORT).show();
        mActivity.setToolbarTitle(inStockItem.getName(), true);
    }

    @Override
    public void onIconClick(InStockItem inStockItem, boolean isSelected, int position) {
        if (isSelected) mDetailedList.add(inStockItem);
        else mDetailedList.remove(inStockItem);
    }

    @Override
    public void onProductClick(Product product, int position) {
        mActivity.addFragment(AwaitingArrivalProductFragment.newInstance(product), true);
    }

    @Override
    public void onInProcessingProductClick(InProcessing inProcessingParcel, int position) {
        mActivity.addFragment(InProcessingDetails.newInstance(new InProcessingParcel.Builder().build()), true);
    }

    @Override
    public void onInFormingClick(InForming inForming, int position) {

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
