package com.softranger.bayshopmfr.ui.contact;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.MailAdapter;
import com.softranger.bayshopmfr.model.chat.MailMessage;

import java.util.ArrayList;

import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParcelMailsFragment extends Fragment implements PullToRefreshLayout.PullToRefreshListener {


    public ParcelMailsFragment() {
        // Required empty public constructor
    }


    private ArrayList<MailMessage> mMessages;
    private ContactUsActivity mActivity;
    private MailAdapter mAdapter;
    private JellyRefreshLayout mRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mActivity = (ContactUsActivity) getActivity();
        mMessages = new ArrayList<>();
        mRefreshLayout = (JellyRefreshLayout) view.findViewById(R.id.jellyPullToRefresh);
        mRefreshLayout.setPullToRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new MailAdapter(mMessages);
        mAdapter.setOnMailClickListener(mActivity);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.setRefreshing(false);
    }
}