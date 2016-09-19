package com.softranger.bayshopmf.ui.contact;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.MailAdapter;
import com.softranger.bayshopmf.model.MailMessage;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParcelMailsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    public ParcelMailsFragment() {
        // Required empty public constructor
    }


    private ArrayList<MailMessage> mMessages;
    private ContactUsActivity mActivity;
    private MailAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_mails, container, false);
        mActivity = (ContactUsActivity) getActivity();
        mMessages = new ArrayList<>();
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mailSwipeRefreshLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(R.color.colorAccent);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mailsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new MailAdapter(mMessages);
        mAdapter.setOnMailClickListener(mActivity);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(false);
    }
}
