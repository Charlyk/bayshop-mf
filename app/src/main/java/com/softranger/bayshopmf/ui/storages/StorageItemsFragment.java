package com.softranger.bayshopmf.ui.storages;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
import com.softranger.bayshopmf.model.packages.CustomsHeld;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.packages.InProcessing;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.packages.LocalDepot;
import com.softranger.bayshopmf.model.packages.PUSParcel;
import com.softranger.bayshopmf.model.packages.Packed;
import com.softranger.bayshopmf.model.packages.Received;
import com.softranger.bayshopmf.model.packages.Sent;
import com.softranger.bayshopmf.model.packages.ToDelivery;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.awaitingarrival.AwaitingArrivalProductFragment;
import com.softranger.bayshopmf.ui.inprocessing.InProcessingDetails;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.instock.DetailsFragment;
import com.softranger.bayshopmf.ui.instock.buildparcel.ItemsListFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageItemsFragment extends Fragment implements ItemAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final String ACTION_ITEM_CHANGED = "item was changed from a top fragment";
    private static final String URL_ARG = "URL to get information";
    private static final String DEPOSIT_ARG = "deposit argument";
    private MainActivity mActivity;
    public String mDeposit;
    private RecyclerView mRecyclerView;
    private ArrayList<Object> mObjects;
    private ItemAdapter mAdapter;
    private static String url;
    private TextView mNoValueText;
    private ArrayList<InForming> mInFormingItems;
    private SwipeRefreshLayout mRefreshLayout;

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

        // create an intent filter to get broadcast messages to update the list
        IntentFilter intentFilter = new IntentFilter(ACTION_ITEM_CHANGED);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        // bind the recycler view which will hold all lists in this fragment
        mRecyclerView = (RecyclerView) view.findViewById(R.id.storage_itemsList);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(manager);

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
        mInFormingItems = new ArrayList<>();

        // Create the adapter for this fragment and pass it to recycler view
        mAdapter = new ItemAdapter(mActivity);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        // get the url and deposit id from arguments and make a request to the server with the passed
        // in arguments url
        url = getArguments().getString(URL_ARG);
        mDeposit = getArguments().getString(DEPOSIT_ARG);
        if (url != null) {
            ApiClient.getInstance().sendRequest(url, mStorageHandler);
            mActivity.toggleLoadingProgress(true);
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
                                ApiClient.getInstance().sendRequest(url, mStorageHandler);
                            }
                        }
                    }, 100);
                    break;
            }
        }
    };

    /**
     * Handler used to obtain either server response or an error with a message about the occurred
     * If an error occurs it will be shown to the user else the response will be passed to
     * {@link StorageItemsFragment#buildItemsList(JSONObject)} and a list with corresponding items
     * will be built and passed to adapter
     */
    private Handler mStorageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", mActivity.getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            buildItemsList(response);
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                            mActivity.toggleLoadingProgress(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        mActivity.toggleLoadingProgress(false);
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
            }
            mRefreshLayout.setRefreshing(false);
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
            mInFormingItems.clear();
            if (MainActivity.selectedFragment == MainActivity.SelectedFragment.IN_STOCK) {
                mInFormingItems = new ArrayList<>();
                mObjects.add(new Object());
                JSONObject jsonData = response.getJSONObject("data");
                JSONArray inStockList = jsonData.getJSONArray("list");
                JSONArray livePackages = jsonData.getJSONArray("livePackages");
                for (int i = 0; i < inStockList.length(); i++) {
                    JSONObject jsonItem = inStockList.getJSONObject(i);
                    String parcelName = jsonItem.getString("title");
                    if (parcelName == null || parcelName.equals("null")) parcelName = "";
                    InStockItem inStockItem = new InStockItem.Builder()
                            .deposit(mDeposit)
                            .id(jsonItem.getInt("id"))
                            .trackingNumber(jsonItem.optString("tracking", ""))
                            .name(parcelName)
                            .parcelId(jsonItem.optString("uid", ""))
                            .hasDeclaration(jsonItem.getInt("isDeclarationFilled") == 1)
                            .build();
                    mObjects.add(inStockItem);
                }

                for (int i = 0; i < livePackages.length(); i++) {
                    JSONObject pack = livePackages.getJSONObject(i);
                    InForming inForming = new InForming.Builder()
                            .codeNumber(pack.getString("codeNumber"))
                            .id(pack.getInt("id"))
                            .deposit(mDeposit)
                            .build();
                    mInFormingItems.add(inForming);
                }
                if (mInFormingItems.size() > 0)
                    mActivity.addActionButtons(mInFormingItems);
            } else {
                JSONArray arrayData = response.getJSONArray("data");
                switch (MainActivity.selectedFragment) {
                    case AWAITING_ARRIVAL: {
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
                    case IN_FORMING: {
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject o = arrayData.getJSONObject(i);
                            InForming inForming = new InForming.Builder()
                                    .id(o.getInt("id"))
                                    .codeNumber(o.optString("codeNumber", ""))
                                    .createdDate(o.optString("created", ""))
                                    .weight(o.getInt("realWeight"))
                                    .name(o.optString("name", ""))
                                    .deposit(mDeposit)
                                    .build();
                            mObjects.add(inForming);
                        }
                        if (mObjects.size() == 0) mActivity.removeActionButtons();
                        break;
                    }
                    case LOCAL_DEPO:
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject object = arrayData.getJSONObject(i);
                            LocalDepot localDepot = new LocalDepot();
                            mObjects.add(buildGeneralPackage(object, localDepot));
                        }
                        break;
                    case TAKEN_TO_DELIVERY:
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject object = arrayData.getJSONObject(i);
                            ToDelivery toDelivery = new ToDelivery();
                            mObjects.add(buildGeneralPackage(object, toDelivery));
                        }
                        break;
                    case CUSTOMS_HELD:
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject object = arrayData.getJSONObject(i);
                            CustomsHeld customsHeld = new CustomsHeld();
                            mObjects.add(buildGeneralPackage(object, customsHeld));
                        }
                        break;
                    case RECEIVED:
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject object = arrayData.getJSONObject(i);
                            Received received = new Received();
                            mObjects.add(buildGeneralPackage(object, received));
                        }
                        break;
                    case SENT:
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject object = arrayData.getJSONObject(i);
                            Sent sent = new Sent();
                            mObjects.add(buildGeneralPackage(object, sent));
                        }
                        break;
                    case AWAITING_SENDING:
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject object = arrayData.getJSONObject(i);
                            Packed packed = new Packed();
                            mObjects.add(buildGeneralPackage(object, packed));
                        }
                        break;
                    case IN_PROCESSING: {
                        for (int i = 0; i < arrayData.length(); i++) {
                            JSONObject object = arrayData.getJSONObject(i);
                            InProcessing inProcessing = new InProcessing();
                            mObjects.add(buildGeneralPackage(object, inProcessing));
                        }
                        break;
                    }
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

    private <T extends PUSParcel> T buildGeneralPackage(JSONObject jsonItem, T parcel) throws Exception {
        parcel = new T.Builder<>(parcel)
                .created(jsonItem.optString("created", ""))
                .percentage(jsonItem.optInt("percent", 0))
                .id(jsonItem.getInt("id"))
                .codeNumber(jsonItem.optString("codeNumber", ""))
                .name(jsonItem.optString("name", ""))
                .realWeight(jsonItem.getInt("realWeight"))
                .deposit(mDeposit)
                .build();
        return parcel;
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
        if (isSelected) MainActivity.inStockItems.add(inStockItem);
        else MainActivity.inStockItems.remove(inStockItem);
    }

    @Override
    public void onProductClick(Product product, int position) {
        mActivity.addFragment(AwaitingArrivalProductFragment.newInstance(product), true);
    }

    @Override
    public <T extends PUSParcel> void onInProcessingProductClick(T processingPackage, int position) {
        mActivity.addFragment(InProcessingDetails.newInstance(processingPackage), true);
    }

    @Override
    public void onInFormingClick(InForming inForming, int position) {
        mActivity.addFragment(ItemsListFragment.newInstance(null, false, inForming, mDeposit), true);
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

    @Override
    public void onRefresh() {
        // TODO: 6/2/16 check to send the request only for current deposit
        ApiClient.getInstance().sendRequest(url, mStorageHandler);
    }
}
