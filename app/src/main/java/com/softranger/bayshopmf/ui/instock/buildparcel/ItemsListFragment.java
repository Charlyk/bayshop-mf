package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.FirstStepAdapter;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.AddressesListFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends ParentFragment implements View.OnClickListener,
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
    private AlertDialog mBatteryDialog;
    private AlertDialog mWeightAlert;

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
        intentFilter.addAction(MainActivity.ACTION_ITEM_DELETED);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);

        mInParcelItems = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.buildFirstStepItemsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mAdapter = new FirstStepAdapter(mInParcelItems);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mTotalPrice = (TextView) view.findViewById(R.id.buildFirstFragmentTotalPriceLabel);
        mTotalWeight = (TextView) view.findViewById(R.id.buildFirstFragmentTotalWeightLabel);


        ImageButton nextButton = (ImageButton) view.findViewById(R.id.buildFirstStepNextButton);
        nextButton.setOnClickListener(this);

        String listItems = getString(R.string.list_items);
        mActivity.setToolbarTitle(listItems, true);
        mDeposit = getArguments().getString(DEPOSIT_ARG);
        boolean addNewBoxes = getArguments().getBoolean(ADD_ARG);

        if (addNewBoxes) {
            mInForming = getArguments().getParcelable(IN_FORMING_ARG);
            mInStockItems = getArguments().getParcelableArrayList(IN_STOCK_ARG);
            updateTotals(mInStockItems);
            sendPackagesToServer(mInForming == null ? "" : String.valueOf(mInForming.getId()));

            // if in forming is null then we should update counters because a new in forming was created
            if (mInForming == null) {
                // get current value
                int inFormingCount = Application.counters.get(Constants.ParcelStatus.LIVE);
                // increment it by one
                inFormingCount = inFormingCount + 1;
                // replace it in the counters map
                Application.counters.put(Constants.ParcelStatus.LIVE, inFormingCount);
                // update all counters
                mActivity.updateParcelCounters(Constants.ParcelStatus.LIVE);
            }
        } else {
            mInForming = getArguments().getParcelable(IN_FORMING_ARG);
            String url = Constants.Api.urlBuildStep(1, String.valueOf(mInForming.getId()));
            ApiClient.getInstance().getRequest(url, mHandler);
        }
        mActivity.toggleLoadingProgress(true);
        return view;
    }

    /**
     * Start PUS parcel creation by sending ID's for selected parcels to server
     * those ID's will be either added to existing parcel or will be created a new one
     * @param packageId either null or empty string to create a new parcel or
     *                  an id for an existing parcel to add selections to it
     */
    private void sendPackagesToServer(String packageId) {
        JSONArray boxesArray = new JSONArray();
        try {
            for (InStockItem item : mInStockItems) {
                boxesArray.put(item.getID());
            }
            FormBody.Builder body = new FormBody.Builder();
            body.add("boxes", String.valueOf(boxesArray));
            if (packageId == null || !packageId.equals("")) body.add("packageId", packageId);
            String url = Constants.Api.urlBuildStep(1);
            ApiClient.getInstance().postRequest(body.build(), url, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Compute total weight for a list of in stock items
     * @param inStockItems which weight we need to compute
     * @return total weight in kilos
     */
    private double getTotalWeight(ArrayList<InStockItem> inStockItems) {
        int totalWeight = 0;
        if (inStockItems != null) {
            for (InStockItem item : inStockItems) {
                totalWeight += item.getWeight();
            }
        }
        if (totalWeight > 0) {
            return ((double) totalWeight / 1000);
        } else {
            return 0;
        }
    }

    /**
     * Compute total price for a list of in stock items
     * @param inStockItems for which total price needed
     * @return total price for the given list of items
     */
    private float getTotalPrice(ArrayList<InStockItem> inStockItems) {
        int totalPrice = 0;
        if (inStockItems != null) {
            for (InStockItem item : inStockItems) {
                totalPrice += item.getPrice();
            }
        }
        if (totalPrice > 0) {
            return totalPrice;
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (getTotalWeight(mInParcelItems) > 31.0) {
            mWeightAlert = mActivity.getDialog(getString(R.string.overweight), getString(R.string.overweight_message), R.mipmap.ic_weight_48dp,
                    getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mWeightAlert.dismiss();
                        }
                    }, null, null);
            mWeightAlert.show();
            return;
        }


        mBatteryDialog = mActivity.getDialog(getString(R.string.li_ion), getString(R.string.has_li_ion_battery),
                R.mipmap.ic_battery_empty_24dp, getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mInForming.setHasBattery(true);
                        mBatteryDialog.dismiss();
                        mActivity.addFragment(AddressesListFragment.newInstance(mInForming), true);
                    }
                }, getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mInForming.setHasBattery(false);
                        mBatteryDialog.dismiss();
                        mActivity.addFragment(AddressesListFragment.newInstance(mInForming), true);
                    }
                });
        mBatteryDialog.show();
    }

    /**
     * Called to update totals at the bottom of the screen
     * @param items
     */
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
                case MainActivity.ACTION_ITEM_DELETED:
//                    boolean hasParcels = intent.getExtras().getBoolean("hasMoreItems");
//                    if (removedPos > -1) {
//                        mAdapter.removeItem(removedPos);
//                        removedPos = -1;
//                        updateTotals(mInParcelItems);
//                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }

    /**
     * Called when delete button within an item is clicked
     * @param inStockItem which item is to be deleted
     * @param position of deleted item in the list
     */
    @Override
    public void onDeleteClick(InStockItem inStockItem, final int position) {
        final InStockItem item = mAdapter.removeItem(position);
        updateTotals(mAdapter.getList());
        if (mAdapter.getItemCount() > 0) {
            Snackbar.make(mRecyclerView, mActivity.getString(R.string.item_deleted), Snackbar.LENGTH_SHORT)
                    .setAction(mActivity.getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapter.insertItem(position, item);
                            updateTotals(mAdapter.getList());
                        }
                    }).setActionTextColor(mActivity.getResources()
                    .getColor(R.color.colorGreenAction))
                    // add a callback to know when the Snackbar goes away
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            // check the event status and delete schedule from server if
                            // the Snackbar was not dismissed by "Undo" button click
                            switch (event) {
                                case DISMISS_EVENT_TIMEOUT:
                                case DISMISS_EVENT_CONSECUTIVE:
                                case DISMISS_EVENT_MANUAL:
                                    deleteItem(item);
                                    break;
                            }
                        }
                    }).show();
        } else {
            deleteItem(item);
            mActivity.toggleLoadingProgress(true);
            mActivity.onBackPressed();
        }
    }

    /**
     * Send delete request to server to remove given item from the parcel
     * @param inStockItem which need to be removed from current parcel
     */
    private void deleteItem(InStockItem inStockItem) {
        ApiClient.getInstance().delete(Constants.Api.urlDeleteBoxFromParcel(String.valueOf(mInForming.getId()),
                String.valueOf(inStockItem.getID())), mActivity.mDeleteHandler);
        // if building is started than we need to update nav drawer counter for in stock
        int inStockCount = Application.counters.get(Constants.ParcelStatus.IN_STOCK);
        // subtract from current count the selected items count to show to user
        inStockCount = inStockCount + 1;

        int inFormingCount = Application.counters.get(Constants.ParcelStatus.LIVE);

        inFormingCount = inFormingCount - 1;

        Application.counters.put(Constants.ParcelStatus.IN_STOCK, inStockCount);
        Application.counters.put(Constants.ParcelStatus.LIVE, inFormingCount);
        mActivity.updateParcelCounters(Constants.ParcelStatus.IN_STOCK);
        mActivity.updateParcelCounters(Constants.ParcelStatus.LIVE);
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject jsonData = response.getJSONObject("data");
        JSONArray jsonBoxes = jsonData.getJSONArray("boxes");
        JSONObject jsoPus = jsonData.getJSONObject("packageRow");
        JSONObject jsonWieghts = jsonData.getJSONObject("weights");
        ArrayList<InStockDetailed> detailedList = new ArrayList<>();
        for (int i = 0; i < jsonBoxes.length(); i++) {
            JSONObject jsonBox = jsonBoxes.getJSONObject(i);
            int packageId = jsonBox.getInt("id");
            mCurrency = jsonBox.getString("currency");
            InStockDetailed item = new InStockDetailed();
            item.setID(packageId);
            item.setParcelId(jsonBox.getString("uid"));
            item.setName(jsonBox.getString("title"));
            item.setPrice(jsonBox.getDouble("price"));
            item.setCurrency(mCurrency);
            item.setWeight(jsonWieghts.getJSONObject(String.valueOf(packageId)).getInt("weight"));
            mInParcelItems.add(item);
            detailedList.add(item);
        }
        mInForming = new InForming.Builder()
                .id(jsoPus.getInt("id"))
                .uid(jsoPus.getString("uid"))
                .hasBattery(jsoPus.getInt("isBatteryLionExists"))
                .items(detailedList)
                .build();

        // if any items were added to this parcel then we need to update parcels counters
        if (mInStockItems != null) {
            // if building is started than we need to update nav drawer counter for in stock
            int count = Application.counters.get(Constants.ParcelStatus.IN_STOCK);
            // subtract from current count the selected items count to show to user
            count = count - mInStockItems.size();

            Application.counters.put(Constants.ParcelStatus.IN_STOCK, count);
            mActivity.updateParcelCounters(Constants.ParcelStatus.IN_STOCK);
        }
    }

    @Override
    public void finallyMethod() {
        Intent refreshIntent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
        refreshIntent.putExtra("deposit", mDeposit);
        mActivity.sendBroadcast(refreshIntent);
        mAdapter.notifyDataSetChanged();
        updateTotals(mInParcelItems);
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
        MainActivity.inStockItems.clear();
    }
}
