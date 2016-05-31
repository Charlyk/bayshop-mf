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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.FirstStepAdapter;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.general.StorageItemsFragment;
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
public class ItemsListFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        FirstStepAdapter.OnItemClickListener {

    private static final String IN_STOCK_ARG = "in stock items argument";
    private MainActivity mActivity;
    private FirstStepAdapter mAdapter;
    private TextView mTotalWeight;
    private TextView mTotalPrice;
    private ArrayList<InStockItem> mInStockItems;
    private RecyclerView mRecyclerView;
    private ArrayList<InStockItem> mInParcelItems;

    public ItemsListFragment() {
        // Required empty public constructor
    }

    public static ItemsListFragment newInstance(ArrayList<InStockItem> inStockItems) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(IN_STOCK_ARG, inStockItems);
        ItemsListFragment fragment = new ItemsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_parcel_first_step, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);

        mInParcelItems = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.buildFirstStepItemsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mInStockItems = getArguments().getParcelableArrayList(IN_STOCK_ARG);
        if (mInStockItems == null) mInStockItems = new ArrayList<>();
        mAdapter = new FirstStepAdapter(mInParcelItems);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mTotalPrice = (TextView) view.findViewById(R.id.buildFirstFragmentTotalPriceLabel);
        mTotalWeight = (TextView) view.findViewById(R.id.buildFirstFragmentTotalWeightLabel);
        updateTotals();

        Button nextButton = (Button) view.findViewById(R.id.buildFirstStepNextButton);
        nextButton.setOnClickListener(this);

        CheckBox hasBattery = (CheckBox) view.findViewById(R.id.buildFirstStepHasBattery);
        hasBattery.setOnCheckedChangeListener(this);

        String listItems = getString(R.string.list_items);
        mActivity.setToolbarTitle(listItems, true);
        sendPackagesToServer();
        return view;
    }

    private void sendPackagesToServer() {
        JSONArray boxesArray = new JSONArray();
        try {
            for (InStockItem item : mInStockItems) {
                boxesArray.put(item.getParcelId());
            }
            RequestBody body = new FormBody.Builder()
                    .add("boxes", String.valueOf(boxesArray))
                    .build();
            ApiClient.getInstance().sendRequest(body, Constants.Api.parcelStepUrl(1), mCreateHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getTotalWeight(ArrayList<InStockItem> inStockItems) {
        int totalWeight = 0;
        for (InStockItem item : inStockItems) {
            totalWeight = totalWeight + 1;
        }
        return totalWeight;
    }

    private double getTotalPrice(ArrayList<InStockItem> inStockItems) {
        int totalPrice = 0;
        for (InStockItem item : inStockItems) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    private Handler mCreateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        ArrayList<InStockItem> inStockItems = new ArrayList<>();
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject jsonData = response.getJSONObject("data");
                            JSONArray jsonBoxes = jsonData.getJSONArray("boxes");
                            for (int i = 0; i < jsonBoxes.length(); i++) {
                                JSONObject jsonBox = jsonBoxes.getJSONObject(i);
                                InStockItem item = new InStockItem.Builder()
                                        .id(jsonBox.getInt("id"))
                                        .parcelId(jsonBox.getString("uid"))
                                        .name(jsonBox.getString("title"))
                                        .price(jsonBox.getDouble("price"))
                                        .currency(jsonBox.getString("currency"))
                                        .build();
                                mInParcelItems.add(item);
                            }
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    } finally {
                        mAdapter.notifyDataSetChanged();
                        updateTotals();
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


    private Handler mDeleteHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {

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


    @Override
    public void onClick(View v) {
        mActivity.addFragment(new SelectAddressFragment(), true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    private void updateTotals() {
        mTotalPrice.setText(String.valueOf(getTotalPrice(mInStockItems)));
        mTotalWeight.setText(String.valueOf(getTotalWeight(mInParcelItems)));
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.list_items), true);
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
    public void onDeleteClick(InStockItem inStockItem, int position) {
        mAdapter.removeItem(position);
        if (mAdapter.getItemCount() == 0) {
            mActivity.onBackPressed();
        } else {
            updateTotals();
        }
    }
}
