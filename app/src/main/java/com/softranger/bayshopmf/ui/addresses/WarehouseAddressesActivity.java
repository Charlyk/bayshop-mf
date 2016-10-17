package com.softranger.bayshopmf.ui.addresses;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WarehouseAddressesActivity extends ParentActivity {


    @BindView(R.id.warehouseAddressProgressBar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_addresses);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        replaceFragment(WarehouseAddressFragment.newInstance(Constants.US));
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.addressActivityContainer, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void toggleLoadingProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void replaceFragment(ParentFragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.addressActivityContainer, fragment, fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void onBackStackChanged() {

    }
}
