package com.softranger.bayshopmfr.ui.addresses;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.AddressListAdapter;
import com.softranger.bayshopmfr.model.CreationDetails;
import com.softranger.bayshopmfr.model.Shipper;
import com.softranger.bayshopmfr.model.address.Address;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.box.InStock;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.settings.SettingsFragment;
import com.softranger.bayshopmfr.ui.steps.ConfirmationFragment;
import com.softranger.bayshopmfr.ui.steps.ShippingMethodFragment;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;


public class AddressesListFragment extends ParentFragment implements AddressListAdapter.OnAddressClickListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    public static final String ACTION_ADDRESS_SELECT = "com.softranger.bayshopmf.ui.addresses.SELECT_ADDRESS";
    public static final String ACTION_START_ANIM = "START TOTALS ANIMATION";
    private static final String SHOW_SELECT_ARG = "show select button argument";
    private static final String BOXES_ARG = "BOXES ARG KEY";

    private ParentActivity mActivity;
    private AddressListAdapter mAdapter;
    private AlertDialog mAlertDialog;
    private Unbinder mUnbinder;
    private Call<ServerResponse<CreationDetails>> mResponseCall;
    private Call<ServerResponse<ArrayList<Address>>> mAddressesCall;
    private Call<ServerResponse> mDeleteCall;
    private ArrayList<InStock> mInStocks;
    private CreationDetails mCreationDetails;

    @BindView(R.id.buildSecondStepList) RecyclerView mRecyclerView;
    @BindView(R.id.addressFastScroller) FastScroller mFastScroller;
    @BindView(R.id.addressActionMenu) FloatingActionsMenu mActionsMenu;
    @BindView(R.id.addressFabBg) FrameLayout mFabBg;

    public boolean mIsGetRequest;
    public boolean mSendBack;
    private boolean mIsDeleteClicked;
    private int mDeleteAddressId;

    public AddressesListFragment() {
        // Required empty public constructor
    }

    public static AddressesListFragment newInstance(boolean sendBack) {
        Bundle args = new Bundle();
        args.putBoolean("isGet", true);
        args.putBoolean("sendBack", sendBack);
        AddressesListFragment fragment = new AddressesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AddressesListFragment newInstance(@NonNull ArrayList<InStock> inStocks) {
        Bundle args = new Bundle();
        args.putBoolean(SHOW_SELECT_ARG, true);
        args.putParcelableArrayList(BOXES_ARG, inStocks);
        AddressesListFragment fragment = new AddressesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_select_address, container, false);
        mActivity = (ParentActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, rootView);

        mActionsMenu.setOnFloatingActionsMenuUpdateListener(this);

        IntentFilter intentFilter = new IntentFilter(EditAddressActivity.ACTION_REFRESH_ADDRESS);
        intentFilter.addAction(ACTION_ADDRESS_SELECT);
        intentFilter.addAction(Application.ACTION_RETRY);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        ArrayList<Address> addresses = new ArrayList<>();
        mAdapter = new AddressListAdapter(addresses);
        mAdapter.setOnAddressClickListener(this);

        if (getArguments().containsKey(BOXES_ARG)) {
            mInStocks = getArguments().getParcelableArrayList(BOXES_ARG);
        } else if (getArguments().containsKey("isGet")) {
            mIsGetRequest = getArguments().getBoolean("isGet");
            mSendBack = getArguments().getBoolean("sendBack");
        }

        mRecyclerView.setAdapter(mAdapter);
        mFastScroller.setRecyclerView(mRecyclerView);

        refreshFragment();
        return rootView;
    }

    @Override
    public void refreshFragment() {
        if (!mIsGetRequest) {
            if (!Application.isAutopackaging() && !Application.askedAboutAutoPackaging()) {
                mAlertDialog = mActivity.getDialog(getString(R.string.use_autopacking),
                        getString(R.string.autopacking_descriptio), R.mipmap.ic_auto_packing_30dp,
                        getString(R.string.enable),
                        view -> {
                            Application.autoPackPrefs.edit().putBoolean(SettingsFragment.AUTOPACKAGING, true).apply();
                            mAlertDialog.dismiss();
                        }, getString(R.string.no_thanks),
                        view -> mAlertDialog.dismiss(), R.color.colorAccent);
                Application.setAskedAutoPackaging(true);
            }
            getAddressesList(mInStocks);
        } else {
            mAddressesCall = Application.apiInterface().getUserAddresses();
            mAddressesCall.enqueue(mAddressesListResponseCallback);
        }
    }

    private void getAddressesList(ArrayList<InStock> inStocks) {
        mActivity.toggleLoadingProgress(true);
        JSONArray boxesArray = new JSONArray();
        for (InStock i : inStocks) {
            boxesArray.put(i.getId());
        }

        mResponseCall = Application.apiInterface().createPusParcel(boxesArray.toString());
        mResponseCall.enqueue(mResponseCallback);
    }


    /**
     * Response callback used to receive all data in case of parcel building
     */
    private ResponseCallback<CreationDetails> mResponseCallback = new ResponseCallback<CreationDetails>() {
        @Override
        public void onSuccess(CreationDetails data) {
            mCreationDetails = data;
            mActivity.toggleLoadingProgress(false);
            // get addresses from creation details object
            ArrayList<Address> addresses = data.getAddresses();
            // get shippers from creation details object
            ArrayList<Shipper> methods = data.getShippers();
            // check if Autopackaging is enabled
            if (Application.isAutopackaging()) {
                // create a new temporary shipper object
                Shipper temp = new Shipper().setId(Application.getSelectedShipperId());
                // check if server addresses list contains our selected address
                boolean hasAddress = addresses.contains(new Address().setId(Application.getSelectedAddressId()));
                // check if shippers list contains our selected shipper
                ArrayList<Shipper> allowedShippers = new ArrayList<>();
                for (Shipper s : methods) {
                    int addressCountry = Application.getSelectedAddressCountry();
                    if (s.getCountryId() == addressCountry) {
                        allowedShippers.add(s);
                    }
                }
                boolean hasShipper = allowedShippers.contains(temp);
                // if we have the selected shipper and address
                // we can jump right to confirmation fragment;
                if (hasAddress && hasShipper) {
                    // get detailed shipper from the list
                    Shipper shipper = allowedShippers.get(allowedShippers.indexOf(temp));
                    // add confirmation fragment
                    mActivity.replaceFragment(ConfirmationFragment.newInstance(mCreationDetails,
                            String.valueOf(Application.getSelectedAddressId()),
                            Application.getSelectedShipperId(), shipper.getCalculatedPrice()));
                    mActivity.setToolbarTitle(getString(R.string.confirm));
                    return;
                } else if (hasAddress) {
                    // if the list does not contain the selected shipper
                    // we need to open shippers fragment
                    Address address = new Address();
                    address.setCountryId(Application.getSelectedAddressCountry());
                    address.setId(Application.getSelectedAddressId());
                    mActivity.replaceFragment(ShippingMethodFragment.newInstance(mCreationDetails, address));
                    mActivity.setToolbarTitle(getString(R.string.shipping_method));
                    return;
                }
            }
            // else if autopackaging is diabled or address is not selected
            // just refresh the list for current fragment
            mAdapter.refreshList(data.getAddresses());
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mActivity.onBackPressed();
        }

        @Override
        public void onError(Call<ServerResponse<CreationDetails>> call, Throwable t) {
            Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            mActivity.toggleLoadingProgress(false);
            mActivity.onBackPressed();
        }
    };

    /**
     * Receives actions to update addresses list
     */
    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case EditAddressActivity.ACTION_REFRESH_ADDRESS:
                    if (mInStocks != null && !mIsGetRequest) {
                        getAddressesList(mInStocks);
                    } else {
                        mAddressesCall = Application.apiInterface().getUserAddresses();
                        mAddressesCall.enqueue(mAddressesListResponseCallback);
                    }
                    break;
                case ACTION_ADDRESS_SELECT:
                    Address address = intent.getExtras().getParcelable("address");
                    onSelectAddressClick(address, 0);
                    break;
                case Application.ACTION_RETRY:
                    if (mIsDeleteClicked) {
                        deleteAddress(mDeleteAddressId);
                    } else {
                        mActivity.toggleLoadingProgress(true);
                        mActivity.removeNoConnectionView();
                        refreshFragment();
                    }
                    break;
            }
        }
    };


    /**
     * Starts {@link EditAddressActivity}
     */
    @OnClick(R.id.addressCreateFloatingBtn)
    void createNewAddress() {
        Intent editAddress = new Intent(mActivity, EditAddressActivity.class);
        mActivity.startActivity(editAddress);
    }

    @OnClick(R.id.addressSearchFloatingBtn)
    void searchAnAddress() {
        Intent search = new Intent(mActivity, SearchAddressActivity.class);
        search.putExtra(SearchAddressActivity.ADDRESSES, mAdapter.getAddresses());
        mActivity.startActivity(search);
    }

    /**
     * called by background layout to collapse action menu
     */
    @OnClick(R.id.addressFabBg)
    void collapseActionMenu() {
        mActionsMenu.collapse();
    }

    /**
     * {@link AddressListAdapter#mOnAddressClickListener}
     * Called when an address is clicked
     *
     * @param address  which was clicked
     * @param position position within adapter for clicked address
     */
    @Override
    public void onSelectAddressClick(Address address, int position) {
        // if mCreationDetails object is not null and is not a get request
        // this means we are at the first step of building a parcel
        // so we need to start shipping method fragment
        if (mCreationDetails != null && !mIsGetRequest) {
            if (Application.isAutopackaging() && !Application.isAutopackagingAddressSelected()) {
                mAlertDialog = mActivity.getDialog(getString(R.string.save_address),
                        getString(R.string.save_selected_address), R.mipmap.ic_address_pop_up_30dp,
                        getString(R.string.yes), (view) -> {
                            // save selected address to preferences
                            Application.autoPackPrefs.edit().putString(SettingsFragment.ADDRESS_ID,
                                    String.valueOf(address.getId())).apply();
                            Application.autoPackPrefs.edit().putInt(SettingsFragment.ADDRESS_COUNTRY,
                                    address.getCountryId()).apply();
                            Application.autoPackPrefs.edit().putString(SettingsFragment.ADDRESS_NAME,
                                    address.getClientName()).apply();
                            // dismiss dialog and add shipping method fragment
                            mAlertDialog.dismiss();
                            mActivity.addFragment(ShippingMethodFragment.newInstance(mCreationDetails,
                                    address), true);
                        }, getString(R.string.no), (view) -> {
                            // dismiss dialog and add shipping method fragment
                            mAlertDialog.dismiss();
                            mActivity.addFragment(ShippingMethodFragment.newInstance(mCreationDetails,
                                    address), true);
                        }, R.color.colorAccent);
                mAlertDialog.show();
            } else {
                mActivity.addFragment(ShippingMethodFragment.newInstance(mCreationDetails,
                        address), true);
            }
        } else if (mSendBack) {
            // otherwise if send back is true we need to send selected address as a broadcast message
            // and just close current fragment
            Intent changeAddress = new Intent(Constants.ACTION_CHANGE_ADDRESS);
            changeAddress.putExtra("address", address);
            mActivity.sendBroadcast(changeAddress);
            mActivity.onBackPressed();
        } else {
            // and finally if we are not building a parcel and we do not need to send the address back
            // just open edit activity so user can just edit selected address
            onEditAddressClick(address, position);
        }
    }

    /**
     * {@link AddressListAdapter#mOnAddressClickListener}
     * Called when add to favorites butten for an address is clicked
     *
     * @param address  for clicked button
     * @param position within the adapter
     * @param button   which was clicked
     */
    @Override
    public void onAddToFavoritesClick(Address address, int position, ImageButton button) {
        // for now we just change button icon
        if (address.isInFavorites()) {
            button.setImageResource(R.mipmap.ic_star_silver_24dpi);
            address.setInFavorites(false);
        } else {
            button.setImageResource(R.mipmap.ic_favorit_24dp);
            address.setInFavorites(true);
        }
    }

    /**
     * {@link com.softranger.bayshopmfr.adapter.AddressSpinnerAdapter#mOnItemClickListener}
     * Called when spinner edit button for an address item is clicked
     *
     * @param address  for clicked button
     * @param position within the adapter
     */
    @Override
    public void onEditAddressClick(Address address, int position) {

        Intent editAddress = new Intent(mActivity, EditAddressActivity.class);
        editAddress.putExtra(EditAddressActivity.ADDRESS_ID_EXTRA, address.getId());
        mActivity.startActivity(editAddress);
    }

    /**
     * {@link com.softranger.bayshopmfr.adapter.AddressSpinnerAdapter#mOnItemClickListener}
     * Called when spinner delete button for an address item is clicked
     *
     * @param address  for clicked button
     * @param position within the adapter
     */
    @Override
    public void onDeleteAddressClick(final Address address, final int position) {
        String message = getString(R.string.confirm_address_deleteing) + " " + address.getClientName() + " " + getString(R.string.address);
        mAlertDialog = mActivity.getDialog(getString(R.string.delete_address), message, R.mipmap.ic_delete_address_24dp, getString(R.string.confirm),
                v -> {
                    mAdapter.removeItem(position);
                    mDeleteAddressId = address.getId();
                    mIsDeleteClicked = true;
                    deleteAddress(mDeleteAddressId);
                    mAlertDialog.dismiss();
                }, getString(R.string.cancel), v -> mAlertDialog.dismiss(), 0);
        mAlertDialog.show();
    }

    private void deleteAddress(int deleteAddressId) {
        mDeleteCall = Application.apiInterface().deleteUserAddress(String.valueOf(deleteAddressId));
        mDeleteCall.enqueue(mDeleteCallback);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.addresses_list);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.addresses_list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mResponseCall != null) mResponseCall.cancel();
        mActivity.unregisterReceiver(mTitleReceiver);
        mUnbinder.unbind();
    }

    //--------------------------- Action Menu Callbacks ---------------------------//
    @Override
    public void onMenuExpanded() {
        mFabBg.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFabBg, "alpha", 0f, 1f);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    @Override
    public void onMenuCollapsed() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFabBg, "alpha", 1f, 0f);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFabBg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }


    /**
     * Callback used to receive the response for delete address click
     */
    private ResponseCallback mDeleteCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            Toast.makeText(mActivity, data.toString(), Toast.LENGTH_SHORT).show();
            mIsDeleteClicked = false;
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mIsDeleteClicked = false;
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            mIsDeleteClicked = false;
        }
    };


    /**
     * Callback which receives addresses from get request
     */
    private ResponseCallback<ArrayList<Address>> mAddressesListResponseCallback = new ResponseCallback<ArrayList<Address>>() {
        @Override
        public void onSuccess(ArrayList<Address> data) {
            mActivity.toggleLoadingProgress(false);
            mAdapter.refreshList(data);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<Address>>> call, Throwable t) {
            Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };
}
