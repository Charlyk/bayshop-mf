package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SecondStepAdapter;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.packages.LocalDepot;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ColorGroupSectionTitleIndicator;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectAddressFragment extends ParentFragment implements SecondStepAdapter.OnAddressClickListener,
        MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    private static final String TO_DELIVER = "TO_DELIVER_ARG";
    private static final String IN_FORMING_ARG = "in forming argument";
    private MainActivity mActivity;
    private SecondStepAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private InForming mInForming;
    private ArrayList<Address> mAddresses;
    private ArrayList<LocalDepot> mToDeliverLIst;


    public SelectAddressFragment() {
        // Required empty public constructor
    }

    public static SelectAddressFragment newInstance() {
        Bundle args = new Bundle();
        SelectAddressFragment fragment = new SelectAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public static SelectAddressFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        SelectAddressFragment fragment = new SelectAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.address_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.addressSearch);
        MenuItem addAddressItem = menu.findItem(R.id.addressAdd);
        addAddressItem.setOnMenuItemClickListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        SearchManager searchManager = (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(mActivity.getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_parcel_address, container, false);
        if (getActivity() instanceof MainActivity) {
            mActivity = (MainActivity) getActivity();
        }

        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        intentFilter.addAction(EditAddressFragment.ACTION_REFRESH_ADDRESS);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        ColorGroupSectionTitleIndicator indicator = (ColorGroupSectionTitleIndicator)
                view.findViewById(R.id.buildSecondStepFastScrollerSectionIndicator);

        mActivity.setToolbarTitle(getString(R.string.addresses_list), true);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.buildSecondStepList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAddresses = new ArrayList<>();
        mAdapter = new SecondStepAdapter(mAddresses);
        mAdapter.setOnAddressClickListener(this);

        if (getArguments().containsKey(IN_FORMING_ARG)) {
            mInForming = getArguments().getParcelable(IN_FORMING_ARG);
        }

        mRecyclerView.setAdapter(mAdapter);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.buildSecondStepFastScroller);
        fastScroller.setRecyclerView(mRecyclerView);
        mRecyclerView.setOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(indicator);
        if (mInForming != null) {
            getAddressesList();
        }
        return view;
    }

    private void getAddressesList() {
        RequestBody body = new FormBody.Builder()
                .add("isBatteryLionExists", String.valueOf(mInForming.isHasBattery() ? 1 : 0))
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlBuildStep(2, String.valueOf(mInForming.getId())), mHandler);
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.addresses_list), true);
                    break;
                case EditAddressFragment.ACTION_REFRESH_ADDRESS:
                    getAddressesList();
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
    public void onSelectAddressClick(Address address, int position) {
        if (mInForming != null) {
            mInForming.setAddress(address);
            mActivity.addFragment(ShippingMethodFragment.newInstance(mInForming), true);
        } else if (mToDeliverLIst != null) {
            // TODO: 6/27/16 do something with the list
        }
    }

    @Override
    public void onAddToFavoritesClick(Address address, int position, ImageButton button) {
        if (address.isInFavorites()) {
            button.setImageResource(R.mipmap.ic_star_silver_24dpi);
            address.setInFavorites(false);
        } else {
            button.setImageResource(R.mipmap.ic_favorit_24dp);
            address.setInFavorites(true);
        }
    }

    @Override
    public void onEditAddressClick(Address address, int position) {
        mActivity.addFragment(EditAddressFragment.newInstance(address), false);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        if (newText == null || newText.equals("")) {
            mAdapter.replaceList(mAddresses);
        } else {
            // TODO: 6/17/16 test this for performance
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<Address> searched = new ArrayList<>();
                    for (Address address : mAddresses) {
                        if (address.getClientName().toLowerCase().contains(newText.toLowerCase())) {
                            searched.add(address);
                        }
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.replaceList(searched);
                        }
                    });
                }
            }).start();
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mActivity.addFragment(EditAddressFragment.newInstance(null), false);
        return true;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        mAddresses.clear();
        JSONObject data = response.getJSONObject("data");
        JSONArray addressesJSON = data.getJSONArray("addresses");
        for (int i = 0; i < addressesJSON.length(); i++) {
            JSONObject a = addressesJSON.getJSONObject(i);
            String name = a.getString("shipping_first_name") + " " + a.getString("shipping_last_name");
            Address address = new Address.Builder()
                    .id(a.getInt("id"))
                    .clientName(name)
                    .firstName(a.getString("shipping_first_name"))
                    .lastName(a.getString("shipping_last_name"))
                    .street(a.getString("shipping_address"))
                    .city(a.getString("shipping_city"))
                    .country(a.getString("countryTitle"))
                    .postalCode(a.getString("shipping_zip"))
                    .phoneNumber(a.optString("phone", ""))
                    .phoneCode(a.optString("shipping_phone_code", ""))
                    .build();
            mAddresses.add(address);
        }
        mAdapter.refreshList();
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }
}
