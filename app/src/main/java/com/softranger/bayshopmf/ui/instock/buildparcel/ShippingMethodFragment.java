package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ShippingMethodAdapter;
import com.softranger.bayshopmf.model.ShippingMethod;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShippingMethodFragment extends ParentFragment implements ShippingMethodAdapter.OnShippingClickListener {

    private static final String IN_FORMING_ARG = "in forming object arg";
    private ParentActivity mActivity;
    private ShippingMethodAdapter mAdapter;
    private InForming mInForming;
    private ArrayList<ShippingMethod> mMethods;
    private RecyclerView mRecyclerView;

    public ShippingMethodFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.address_menu, menu);
        menu.clear();
    }

    public static ShippingMethodFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        ShippingMethodFragment fragment = new ShippingMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mActivity = (ParentActivity) getActivity();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragmentSwipeRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mMethods = new ArrayList<>();
        mAdapter = new ShippingMethodAdapter(mMethods);
        mAdapter.setOnShippingClickListener(this);
        mInForming = getArguments().getParcelable(IN_FORMING_ARG);
        mRecyclerView.setAdapter(mAdapter);
        mActivity.toggleLoadingProgress(true);
        RequestBody body = new FormBody.Builder()
                .add("memberAddress", String.valueOf(mInForming.getAddress().getId()))
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlBuildStep(3, String.valueOf(mInForming.getId())), mHandler);
        return view;
    }

    @Override
    public void onDetailsClick(ShippingMethod shippingMethod, int position, TextView detailsTextView, ImageButton detailsButton) {
        if (detailsTextView.getLineCount() == 4) {
            mActivity.expandTextView(detailsTextView);
        } else {
            mActivity.collapseTextView(detailsTextView, 4);
        }
    }

    @Override
    public void onSelectClick(ShippingMethod shippingMethod, int position) {
        mInForming.setShippingMethod(shippingMethod);
        mActivity.addFragment(InsuranceFragment.newInstance(mInForming), true);
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        mMethods.clear();
        JSONArray methodsArray = response.getJSONObject("data").getJSONArray("shippingList");
        for (int i = 0; i < methodsArray.length(); i++) {
            JSONObject jsonMethod = methodsArray.getJSONObject(i);
            ShippingMethod method = new ShippingMethod.Builder()
                    .id(jsonMethod.getInt("id"))
                    .name(jsonMethod.getString("title"))
                    .mEstimatedTime(jsonMethod.getString("estimateTime"))
                    .maxWeight(jsonMethod.getDouble("maxWeight"))
                    .description(jsonMethod.getString("description"))
                    .calculatedPrice(jsonMethod.getDouble("calculatedPrice"))
//                    .rank(jsonMethod.getInt("rank"))
                    .currency(jsonMethod.getString("currency"))
                    .build();
            mMethods.add(method);
        }
        mAdapter.refreshList(mMethods);
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.shipping_method);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.shipping_method;
    }
}
