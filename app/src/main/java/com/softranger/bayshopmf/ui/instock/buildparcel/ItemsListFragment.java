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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.FirstStepAdapter;
import com.softranger.bayshopmf.model.InForming;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.PUSItem;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.general.StorageHolderFragment;
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
    private static final String ADD_ARG = "add new box";
    private static final String IN_FORMING_ARG = "in formig object";
    private static final String DEPOSIT_ARG = "selected deposit";
    private MainActivity mActivity;
    private FirstStepAdapter mAdapter;
    private TextView mTotalWeight;
    private TextView mTotalPrice;
    private ArrayList<InStockItem> mInStockItems;
    private RecyclerView mRecyclerView;
    private ArrayList<InStockItem> mInParcelItems;
    private String mDeposit;
    private static int removedPos = -1;
    private InForming mInForming;
    private String mCurrency;
    private CheckBox mCheckBox;

    public ItemsListFragment() {
        // Required empty public constructor
    }

    public static ItemsListFragment newInstance(@Nullable ArrayList<InStockItem> inStockItems, boolean add,
                                                @Nullable InForming inForming, @NonNull String deposit) {
        Bundle args = new Bundle();
        args.putBoolean(ADD_ARG, add);
        args.putString(DEPOSIT_ARG, deposit);
        args.putParcelableArrayList(IN_STOCK_ARG, inStockItems);
        args.putParcelable(IN_FORMING_ARG, inForming);
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

        mAdapter = new FirstStepAdapter(mInParcelItems);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mTotalPrice = (TextView) view.findViewById(R.id.buildFirstFragmentTotalPriceLabel);
        mTotalWeight = (TextView) view.findViewById(R.id.buildFirstFragmentTotalWeightLabel);


        Button nextButton = (Button) view.findViewById(R.id.buildFirstStepNextButton);
        nextButton.setOnClickListener(this);

        mCheckBox = (CheckBox) view.findViewById(R.id.buildFirstStepHasBattery);
        mCheckBox.setOnCheckedChangeListener(this);

        String listItems = getString(R.string.list_items);
        mActivity.setToolbarTitle(listItems, true);
        mDeposit = getArguments().getString(DEPOSIT_ARG);
        boolean addNewBoxes = getArguments().getBoolean(ADD_ARG);

        if (addNewBoxes) {
            mInForming = getArguments().getParcelable(IN_FORMING_ARG);
            mInStockItems = getArguments().getParcelableArrayList(IN_STOCK_ARG);
            updateTotals(mInStockItems);
            sendPackagesToServer(mInForming == null ? "" : String.valueOf(mInForming.getId()));
        } else {
            mInForming = getArguments().getParcelable(IN_FORMING_ARG);
            String url = Constants.Api.urlBuildStep(1, String.valueOf(mInForming.getId()));
            Log.d("ItemsListFragment N", url);
            ApiClient.getInstance().sendRequest(url, mCreateHandler);
        }
        mActivity.toggleLoadingProgress(true);
        return view;
    }

    private void sendPackagesToServer(String packageId) {
        JSONArray boxesArray = new JSONArray();
        try {
            for (InStockItem item : mInStockItems) {
                boxesArray.put(item.getID());
            }
            FormBody.Builder body = new FormBody.Builder();
            body.add("boxes", String.valueOf(boxesArray));
            if (!packageId.equals("")) body.add("packageId", packageId);
            String url = Constants.Api.urlBuildStep(1);
            Log.d("ItemsListFragment (S)", url);
            ApiClient.getInstance().sendRequest(body.build(), url, mCreateHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getTotalWeight(ArrayList<InStockItem> inStockItems) {
        int totalWeight = 0;
        for (InStockItem item : inStockItems) {
            totalWeight += item.getWeight();
        }
        return ((float) totalWeight / 1000);
    }

    private float getTotalPrice(ArrayList<InStockItem> inStockItems) {
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
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject jsonData = response.getJSONObject("data");
                            JSONArray jsonBoxes = jsonData.getJSONArray("boxes");
                            JSONObject jsoPus = jsonData.getJSONObject("packageRow");
                            JSONObject jsonWieghts = jsonData.getJSONObject("weights");
                            for (int i = 0; i < jsonBoxes.length(); i++) {
                                JSONObject jsonBox = jsonBoxes.getJSONObject(i);
                                int packageId = jsonBox.getInt("id");
                                mCurrency = jsonBox.getString("currency");
                                InStockItem item = new InStockItem.Builder()
                                        .id(packageId)
                                        .parcelId(jsonBox.getString("uid"))
                                        .name(jsonBox.getString("title"))
                                        .price(jsonBox.getDouble("price"))
                                        .currency(mCurrency)
                                        .weight(jsonWieghts.getJSONObject(String.valueOf(packageId)).getInt("weight"))
                                        .build();
                                mInParcelItems.add(item);
                            }
                            mInForming = new InForming.Builder()
                                    .id(jsoPus.getInt("id"))
                                    .uid(jsoPus.getString("uid"))
                                    .hasBattery(jsoPus.getInt("isBatteryLionExists"))
                                    .items(mInParcelItems)
                                    .build();
                            mCheckBox.setChecked(mInForming.isHasBattery());
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    } finally {
                        Intent refreshIntent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
                        refreshIntent.putExtra("deposit", mDeposit);
                        mActivity.sendBroadcast(refreshIntent);
                        mAdapter.notifyDataSetChanged();
                        updateTotals(mInParcelItems);
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
            MainActivity.inStockItems.clear();
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
                            JSONObject data = response.getJSONObject("data");
                            boolean hasParcels = data.getInt("isPackageHasMoreBoxes") == 1;
                            if (!hasParcels) {
                                mActivity.onBackPressed();
                            } else if (removedPos > -1) {
                                mAdapter.removeItem(removedPos);
                                removedPos = -1;
                                updateTotals(mInParcelItems);
                            }
                            Intent refreshIntent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
                            refreshIntent.putExtra("deposit", mDeposit);
                            mActivity.sendBroadcast(refreshIntent);
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
    public void onClick(View v) {
        mActivity.addFragment(SelectAddressFragment.newInstance(mInForming), true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mInForming.setHasBattery(isChecked);
    }

    private void updateTotals(ArrayList<InStockItem> items) {
        mTotalPrice.setText(mCurrency + String.valueOf(getTotalPrice(items)));
        mTotalWeight.setText(String.valueOf(getTotalWeight(items)) + "kg.");
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
        removedPos = position;
        ApiClient.getInstance().delete(Constants.Api.urlDeleteBoxFromParcel(String.valueOf(mInForming.getId()),
                String.valueOf(inStockItem.getID())), mDeleteHandler);
        mActivity.toggleLoadingProgress(true);
    }
}
