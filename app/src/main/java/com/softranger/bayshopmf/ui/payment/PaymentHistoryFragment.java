package com.softranger.bayshopmf.ui.payment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.HistoryAdapter;
import com.softranger.bayshopmf.model.History;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.ParentFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentHistoryFragment extends ParentFragment implements RadioGroup.OnCheckedChangeListener,
        SwipeRefreshLayout.OnRefreshListener, HistoryAdapter.OnHistoryClickListener {

    private PaymentActivity mActivity;

    private SwipeRefreshLayout mRefreshLayout;
    private ProgressBar mProgressBar;
    private ArrayList<History> mUSDHistories;
    private ArrayList<History> mEuroHistories;
    private ArrayList<History> mPoundHistories;
    private ArrayList<History> mAllHistories;
    private HistoryAdapter mAdapter;
    private static Constants.Period period;

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);
        mActivity = (PaymentActivity) getActivity();
        RadioGroup periodSelector = (RadioGroup) view.findViewById(R.id.paymentHistoryPeriodSelector);
        periodSelector.setOnCheckedChangeListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.paymentHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.paymentHistoryRefreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        mProgressBar = (ProgressBar) view.findViewById(R.id.paymentHistoryProgressBar);

        mUSDHistories = new ArrayList<>();
        mEuroHistories = new ArrayList<>();
        mPoundHistories = new ArrayList<>();
        mAllHistories = new ArrayList<>();
        mAdapter = new HistoryAdapter(mActivity, mAllHistories);
        mAdapter.setOnHistoryClickListener(this);
        recyclerView.setAdapter(mAdapter);
        period = Constants.Period.one;
        ApiClient.getInstance().getRequest(Constants.Api.urlUserBalance(period), mHandler);
        mProgressBar.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.paymentHistoryPeriodToday:
                period = Constants.Period.one;
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.paymentHistoryPeriodWeek:
                period = Constants.Period.week;
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.paymentHistoryPeriodMonth:
                period = Constants.Period.month;
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.paymentHistoryPeriodAll:
                period = Constants.Period.all;
                mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
        ApiClient.getInstance().getRequest(Constants.Api.urlUserBalance(period), mHandler);
    }

    @Override
    public void onRefresh() {
        ApiClient.getInstance().getRequest(Constants.Api.urlUserBalance(period), mHandler);
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        mAllHistories.clear();
        mUSDHistories.clear();
        mEuroHistories.clear();
        mPoundHistories.clear();

        JSONObject data = response.getJSONObject("data");
        JSONObject balanceList = data.getJSONObject("balanceList");
        Iterator<String> balanceKeys = balanceList.keys();
        while (balanceKeys.hasNext()) {
            String key = balanceKeys.next();
            JSONArray jsonHistories = balanceList.getJSONArray(key);
            for (int i = 0; i < jsonHistories.length(); i++) {
                JSONObject jsonHistory = jsonHistories.getJSONObject(i);
                History history = new History.Builder()
                        .paymentType(jsonHistory.getString("type"))
                        .comment(jsonHistory.getString("comment"))
                        .date(jsonHistory.getString("created"))
                        .summ(jsonHistory.getDouble("summ"))
                        .transactionId(jsonHistory.getString("trid"))
                        .build();

                if (key.equals(Currency.usd.name())) {
                    mUSDHistories.add(history);
                } else if (key.equals(Currency.eur.name())) {
                    mEuroHistories.add(history);
                } else if (key.equals(Currency.gbp.name())){
                    mPoundHistories.add(history);
                }

                mAllHistories.addAll(mUSDHistories);
                mAllHistories.addAll(mEuroHistories);
                mAllHistories.addAll(mPoundHistories);
            }
        }
        mAdapter.refreshList(mAllHistories);
    }

    @Override
    public void onHandleMessageEnd() {
        mProgressBar.setVisibility(View.GONE);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHistoryClick(History history, int postion) {

    }

    enum Currency {
        usd, eur, gbp
    }
}
