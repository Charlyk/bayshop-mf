package com.softranger.bayshopmf.ui.addresses;


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
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.AddressListAdapter;
import com.softranger.bayshopmf.model.CreationDetails;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.model.address.AddressesList;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.InStock;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.steps.ShippingMethodFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;


public class AddressesListFragment extends ParentFragment implements AddressListAdapter.OnAddressClickListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    public static final String ACTION_START_ANIM = "START TOTALS ANIMATION";
    private static final String SHOW_SELECT_ARG = "show select button argument";
    private static final String BOXES_ARG = "BOXES ARG KEY";

    private ParentActivity mActivity;
    private AddressListAdapter mAdapter;
    private AlertDialog mDeleteDialog;
    private Unbinder mUnbinder;
    private Call<ServerResponse<CreationDetails>> mResponseCall;
    private Call<ServerResponse<AddressesList>> mAddressesCall;
    private Call<ServerResponse> mDeleteCall;
    private ArrayList<InStock> mInStocks;
    private CreationDetails mCreationDetails;

    @BindView(R.id.buildSecondStepList) RecyclerView mRecyclerView;
    @BindView(R.id.addressFastScroller) FastScroller mFastScroller;
    @BindView(R.id.addressActionMenu) FloatingActionsMenu mActionsMenu;
    @BindView(R.id.addressFabBg) FrameLayout mFabBg;

    public boolean mIsGetRequest;
    public boolean mSendBack;

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

        if (!mIsGetRequest) {
            getAddressesList(mInStocks);
        } else {
            mAddressesCall = Application.apiInterface().getUserAddresses();
            mAddressesCall.enqueue(mAddressesListResponseCallback);
        }
        return rootView;
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
            mAdapter.refreshList(data.getAddresses());
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mActivity.onBackPressed();
        }

        @Override
        public void onError(Call<ServerResponse<CreationDetails>> call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
            }
        }
    };


    @OnClick(R.id.addressCreateFloatingBtn)
    void createNewAddress() {
        Intent editAddress = new Intent(mActivity, EditAddressActivity.class);
        mActivity.startActivity(editAddress);
    }

    @OnClick(R.id.addressSearchFloatingBtn)
    void searchAnAddress() {

    }

    @OnClick(R.id.addressFabBg)
    void collapseActionMenu() {
        mActionsMenu.collapse();
    }

    @Override
    public void onSelectAddressClick(Address address, int position) {
        if (mCreationDetails != null && !mIsGetRequest) {
            mActivity.addFragment(ShippingMethodFragment.newInstance(mCreationDetails,
                    String.valueOf(address.getId())), true);
        } else if (mSendBack) {
            Intent changeAddress = new Intent(Constants.ACTION_CHANGE_ADDRESS);
            changeAddress.putExtra("address", address);
            mActivity.sendBroadcast(changeAddress);
            mActivity.onBackPressed();
        } else {
            onEditAddressClick(address, position);
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
        Intent editAddress = new Intent(mActivity, EditAddressActivity.class);
        editAddress.putExtra(EditAddressActivity.ADDRESS_ID_EXTRA, address.getId());
        mActivity.startActivity(editAddress);
    }

    @Override
    public void onDeleteAddressClick(final Address address, final int position) {
        String message = getString(R.string.confirm_address_deleteing) + " " + address.getClientName() + " " + getString(R.string.address);
        mDeleteDialog = mActivity.getDialog(getString(R.string.delete_address), message, R.mipmap.ic_delete_address_24dp, getString(R.string.confirm),
                v -> {
                    mAdapter.removeItem(position);
                    mDeleteCall = Application.apiInterface().deleteUserAddress(String.valueOf(address.getId()));
                    mDeleteCall.enqueue(mDeleteCallback);
                    mDeleteDialog.dismiss();
                }, getString(R.string.cancel), v -> mDeleteDialog.dismiss(), 0);
        mDeleteDialog.show();
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
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * Callback which receives addresses from get request
     */
    private ResponseCallback<AddressesList> mAddressesListResponseCallback = new ResponseCallback<AddressesList>() {
        @Override
        public void onSuccess(AddressesList data) {
            mActivity.toggleLoadingProgress(false);
            mAdapter.refreshList(data.getAddresses());
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<AddressesList>> call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };
}
