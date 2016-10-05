package com.softranger.bayshopmf.ui.payment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.HistoryAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.payment.History;
import com.softranger.bayshopmf.model.payment.PaymentHistories;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;

public class PaymentHistoryFragment extends Fragment implements HistoryAdapter.OnHistoryClickListener {

    private static final String PERIOD = "PERIOD";
    private PaymentActivity mActivity;
    private ArrayList<History> mAllHistories;
    private Unbinder mUnbinder;
    private HistoryAdapter mAdapter;
    private String period;
    private Call<ServerResponse<PaymentHistories>> mCall;

    @BindView(R.id.paymentHistoryRecyclerView)
    RecyclerView mRecyclerView;

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    public static PaymentHistoryFragment newInstance(String period) {
        Bundle args = new Bundle();
        args.putString(PERIOD, period);
        PaymentHistoryFragment fragment = new PaymentHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);
        mActivity = (PaymentActivity) getActivity();

        IntentFilter intentFilter = new IntentFilter(PaymentActivity.ACTION_REFRESH);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mAllHistories = new ArrayList<>();
        mAdapter = new HistoryAdapter(mActivity, mAllHistories);
        mAdapter.setOnHistoryClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        period = getArguments().getString(PERIOD);

        mCall = Application.apiInterface().getPaymentHistoryForPeriod(period.toString());
        mCall.enqueue(mResponseCallback);
        return view;
    }

    private ResponseCallback<PaymentHistories> mResponseCallback = new ResponseCallback<PaymentHistories>() {
        @Override
        public void onSuccess(PaymentHistories data) {
            mAllHistories.clear();
            for (Map.Entry<String, ArrayList<History>> set : data.getHistoriesMap().entrySet()) {
                mAllHistories.addAll(set.getValue());
            }
            Collections.sort(mAllHistories, (lhs, rhs) -> lhs.getDate().compareTo(rhs.getDate()));
            Collections.reverse(mAllHistories);
            mAdapter.refreshList(mAllHistories);
            mActivity.mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Log.e("PaymentHistory", errorData.getMessage());
            mActivity.mRefreshLayout.setRefreshing(false);
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call<ServerResponse<PaymentHistories>> call, Throwable t) {
            t.printStackTrace();
            mActivity.mRefreshLayout.setRefreshing(false);
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };

    public void onRefresh() {
        mCall = Application.apiInterface().getPaymentHistoryForPeriod(period.toString());
        mCall.enqueue(mResponseCallback);
    }

    @Override
    public void onHistoryClick(History history, int position) {
        Intent showDetails = new Intent(mActivity, PaymentDetailsActivity.class);
        showDetails.putExtra("history", history);
        mActivity.startActivity(showDetails);
    }
}
