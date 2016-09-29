package com.softranger.bayshopmf.ui.addresses;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SecondStepAdapter;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.instock.InStockFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ColorGroupSectionTitleIndicator;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressesListFragment extends ParentFragment implements SecondStepAdapter.OnAddressClickListener,
        MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    public static final String ACTION_START_ANIM = "START TOTALS ANIMATION";
    private static final String SHOW_SELECT_ARG = "show select button argument";
    private MainActivity mActivity;
    private SecondStepAdapter mAdapter;
    private ArrayList<Address> mAddresses;
    private static boolean isPost;
    private SecondStepAdapter.ButtonType mButtonType;
    private AlertDialog mDeleteDialog;
    private static boolean deleteClicked;
    private RelativeLayout mRootView;

    public AddressesListFragment() {
        // Required empty public constructor
    }

    public static AddressesListFragment newInstance(SecondStepAdapter.ButtonType buttonType) {
        Bundle args = new Bundle();
        AddressesListFragment fragment = new AddressesListFragment();
        fragment.setArguments(args);
        fragment.mButtonType = buttonType;
        return fragment;
    }


    public static AddressesListFragment newInstance() {
        Bundle args = new Bundle();
        args.putBoolean(SHOW_SELECT_ARG, true);
        AddressesListFragment fragment = new AddressesListFragment();
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
        mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_build_parcel_address, container, false);
        mActivity = (MainActivity) getActivity();

        IntentFilter intentFilter = new IntentFilter(EditAddressFragment.ACTION_REFRESH_ADDRESS);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        ColorGroupSectionTitleIndicator indicator = (ColorGroupSectionTitleIndicator)
                mRootView.findViewById(R.id.buildSecondStepFastScrollerSectionIndicator);

        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.buildSecondStepList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAddresses = new ArrayList<>();
        mAdapter = new SecondStepAdapter(mAddresses, mButtonType);
        mAdapter.setOnAddressClickListener(this);


        recyclerView.setAdapter(mAdapter);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) mRootView.findViewById(R.id.buildSecondStepFastScroller);
        fastScroller.setRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(indicator);

        getAddressesList(isPost);
        return mRootView;
    }

    private void getAddressesList(boolean isPost) {
        mActivity.toggleLoadingProgress(true);
        if (isPost) {

        } else {
            ApiClient.getInstance().getRequest(Constants.Api.urlAddressesList(), mHandler);
        }
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case EditAddressFragment.ACTION_REFRESH_ADDRESS:
                    getAddressesList(isPost);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.changeActionMenuColor(MainActivity.ActionMenuColor.yellow);
        mActivity.unregisterReceiver(mTitleReceiver);
    }

    @Override
    public void onSelectAddressClick(Address address, int position) {
        Intent changeAddress = new Intent(Constants.ACTION_CHANGE_ADDRESS);
        changeAddress.putExtra("address", address);
        mActivity.sendBroadcast(changeAddress);
        mActivity.onBackPressed();
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

    }

    @Override
    public void onDeleteAddressClick(final Address address, final int position) {
        String message = getString(R.string.confirm_address_deleteing) + " " + address.getClientName() + " " + getString(R.string.address);
        mDeleteDialog = mActivity.getDialog(getString(R.string.delete_address), message, R.mipmap.ic_delete_address_24dp, getString(R.string.confirm),
                v -> {
                    mAdapter.removeItem(position);
                    deleteClicked = true;
                    ApiClient.getInstance().delete(Constants.Api.urlGetAddress(String.valueOf(address.getId())), mHandler);
                    mDeleteDialog.dismiss();
                }, getString(R.string.cancel), v -> mDeleteDialog.dismiss(), 0);
        mDeleteDialog.show();
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
            new Thread(() -> {
                final ArrayList<Address> searched = new ArrayList<>();
                for (Address address : mAddresses) {
                    if (address.getClientName().toLowerCase().contains(newText.toLowerCase())) {
                        searched.add(address);
                    }
                }
                mActivity.runOnUiThread(() -> mAdapter.replaceList(searched));
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
        if (!deleteClicked) {
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
        } else {
            deleteClicked = false;
        }
    }

    @Override
    public void finallyMethod() {
        mActivity.toggleLoadingProgress(false);
        Intent intent = new Intent(ACTION_START_ANIM);
        intent.putExtra("up", true);
        mActivity.sendBroadcast(intent);
        mActivity.changeActionMenuColor(MainActivity.ActionMenuColor.green);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.addresses_list);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.addresses_list;
    }
}
