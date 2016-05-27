package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ShippingMethodAdapter;
import com.softranger.bayshopmf.model.ShippingMethod;
import com.softranger.bayshopmf.ui.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShippingMethodFragment extends Fragment implements ShippingMethodAdapter.OnShippingClickListener {

    private MainActivity mActivity;
    private ShippingMethodAdapter mAdapter;

    public ShippingMethodFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shipping_method, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.shipping_method), true);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.buildShippingMethodList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new ShippingMethodAdapter(buildShippingMethods());
        mAdapter.setOnShippingClickListener(this);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private ArrayList<ShippingMethod> buildShippingMethods() {
        ArrayList<ShippingMethod> shippingMethods = new ArrayList<>();
        ShippingMethod plane = new ShippingMethod.Builder().name("By plane")
                .description("Pune pasilca in iashcic si o suie in avion, dupa toate mahinatiile estea avionu isi e zboru si vine, " +
                        "daca nu chica jios pin in moldova ap a s ai sheea she ai vrut pin amu.")
                .price(10).currency("$").build();
        ShippingMethod bus = new ShippingMethod.Builder().name("By bus")
                .description("Pune pasilca in iashcic si o suie in avtobus, dupa toate mahinatiile estea avtobusu se porneste si vine, " +
                        "daca nu se sparge roata sau hz ce, ap a s ai sheea she ai vrut pin amu.")
                .price(5).currency("$").build();
        ShippingMethod ship = new ShippingMethod.Builder().name("By vapor")
                .description("Pune pasilca in iashcic si o suie pe coraghie, dupa toate mahinatiile estea coraghia se porneste si vine, " +
                        "daca nu se ineaca pin in moldova ap a s ai sheea she ai vrut pin amu.")
                .price(3).currency("$").build();
        shippingMethods.add(plane);
        shippingMethods.add(bus);
        shippingMethods.add(ship);
        return shippingMethods;
    }

    @Override
    public void onDetailsClick(ShippingMethod shippingMethod, int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setTitle(shippingMethod.getName())
                .setMessage(shippingMethod.getDescription())
                .setPositiveButton(getString(R.string.ok), null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onSelectClick(ShippingMethod shippingMethod, int position) {
        mActivity.addFragment(new CheckDeclarationFragment(), true);
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.shipping_method), true);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }
}
