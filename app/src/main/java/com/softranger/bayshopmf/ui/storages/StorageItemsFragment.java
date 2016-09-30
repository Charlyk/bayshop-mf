package com.softranger.bayshopmf.ui.storages;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
import com.softranger.bayshopmf.adapter.AddressListAdapter;
import com.softranger.bayshopmf.model.product.Product;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageItemsFragment extends ParentFragment implements ItemAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public static final String ACTION_ITEM_CHANGED = "item was changed from a top fragment";
    private static final String URL_ARG = "URL to get information";
    private static final String DEPOSIT_ARG = "deposit argument";
    private MainActivity mActivity;
    public String mDeposit;
    private ArrayList<Object> mObjects;
    private ItemAdapter mAdapter;
    private static String url;
    private TextView mNoValueText;
    private SwipeRefreshLayout mRefreshLayout;
    private AlertDialog mAlertDialog;
    private boolean isDeleteClicked;
    private Unbinder mUnbinder;

    @BindView(R.id.buildParcelButton)
    FloatingActionButton mActionButton;

    public StorageItemsFragment() {
        // Required empty public constructor
    }

    public static StorageItemsFragment newInstance(@NonNull String url, @NonNull String deposit) {
        Bundle args = new Bundle();
        args.putString(URL_ARG, url);
        args.putString(DEPOSIT_ARG, deposit);
        StorageItemsFragment fragment = new StorageItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        // create an intent filter to get broadcast messages to update the list
        IntentFilter intentFilter = new IntentFilter(ACTION_ITEM_CHANGED);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        Button orderDeliveryBtn = (Button) view.findViewById(R.id.storageItemsOrderDeliveryBtn);
        orderDeliveryBtn.setOnClickListener(this);

        // bind the recycler view which will hold all lists in this fragment
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.storage_itemsList);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(manager);

        // bind refresh layout to be able to send a request again
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.storageItemsFragmentSwipeRefresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        // bind a no value placeholder
        mNoValueText = (TextView) view.findViewById(R.id.storageItemsNoValueText);

        // initialize objects list which will hold all type of items and then will be passed to adapter
        mObjects = new ArrayList<>();

        // initialize in forming items list which will be passed to activity and used to add floating
        // buttons with corresponding parcels

        // Create the adapter for this fragment and pass it to recycler view
        mAdapter = new ItemAdapter(mActivity);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);

        // get the url and deposit id from arguments and make a request to the server with the passed
        // in arguments url
        url = getArguments().getString(URL_ARG);
        mDeposit = getArguments().getString(DEPOSIT_ARG);
        if (url != null) {
            ApiClient.getInstance().getRequest(url, mHandler);
            mActivity.toggleLoadingProgress(true);
        }

        if (MainActivity.selectedFragment != ParentActivity.SelectedFragment.awaiting_arrival) {
            mActionButton.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Either show or hide the no value place holder text
     *
     * @param show
     */
    private void showNoValueText(boolean show) {
        mNoValueText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.buildParcelButton)
    void addAwaitingParcel() {
        mActivity.addFragment(AddAwaitingFragment.newInstance(), false);
    }


    /**
     * Broadcast used to update current list when user changes an item from an deeper fragment
     * it is just making a new request to specified url to get the updated list from server
     */
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
                                ApiClient.getInstance().getRequest(url, mHandler);
                            }
                        }
                    }, 50);
                    break;
            }
        }
    };


    /**
     * Called to build the list and pass it to adapter
     *
     * @param response in json from server
     */
    private void buildItemsList(JSONObject response) {
        try {
            mObjects.clear();
            JSONArray arrayData = response.getJSONArray("data");
            switch (MainActivity.selectedFragment) {
                case awaiting_arrival: {
                    for (int i = 0; i < arrayData.length(); i++) {
                        JSONObject jsonItem = arrayData.getJSONObject(i);
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
            }
        } catch (Exception e) {
            Log.e("StorageItems", "URL: " + url + "(" + String.valueOf(MainActivity.selectedFragment) + ")");
            e.printStackTrace();
        } finally {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refreshList(mObjects);
                    if (mObjects.size() <= 0) showNoValueText(true);
                    else showNoValueText(false);
                    mActivity.toggleLoadingProgress(false);
                    mRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onInProcessingProductClick(PUSParcel processingPackage, int position) {

    }

    /**
     * Called when Combine button within In Stock list header is clicked
     */
    @Override
    public void onCombineClick() {

    }

    /**
     * Called when order check button within In Stock list header is clicked
     */
    @Override
    public void onCheckOrderClick() {

    }

    /**
     * Called when order photos button within In Stock list header is clicked
     */
    @Override
    public void onAdditionalPhotosClick() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onRefresh() {
        // TODO: 6/2/16 check to send the request only for current deposit
        ApiClient.getInstance().getRequest(url, mHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.storageItemsOrderDeliveryBtn:
                mActivity.addFragment(AddressesListFragment.newInstance(AddressListAdapter.ButtonType.select), false);
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        if (!isDeleteClicked) {
            buildItemsList(response);
        } else {
            isDeleteClicked = false;
        }
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(MainActivity.selectedFragment.fragmentName());
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.selectedFragment;
    }
}
