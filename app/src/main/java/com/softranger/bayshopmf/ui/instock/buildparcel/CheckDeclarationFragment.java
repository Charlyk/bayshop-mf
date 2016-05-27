package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.PackageDetailsAdapter;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.ui.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckDeclarationFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;

    public CheckDeclarationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_declaration, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.check_list), true);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.buildFourthStepDeclarationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        PackageDetailsAdapter adapter = new PackageDetailsAdapter(new ArrayList<Product>());
        recyclerView.setAdapter(adapter);
        Button next = (Button) view.findViewById(R.id.buildFourthStepNextButton);
        next.setOnClickListener(this);
        return view;
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.check_list), true);
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
    public void onClick(View v) {
        mActivity.addFragment(new InsuranceFragment(), true);
    }
}
