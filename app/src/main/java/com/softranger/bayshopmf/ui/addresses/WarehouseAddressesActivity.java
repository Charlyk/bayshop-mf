package com.softranger.bayshopmf.ui.addresses;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.StorageTabAdapter;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class WarehouseAddressesActivity extends ParentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_addresses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setUpTabsForAllStorages();
    }

    private void setUpTabsForAllStorages() {
        StorageTabAdapter adapter = new StorageTabAdapter(this, getSupportFragmentManager());
        adapter.addFragment(WarehouseAddressFragment.newInstance(Constants.US), Constants.USA);
        ViewPager viewPager = (ViewPager) findViewById(R.id.mailActivityViewPager);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {

    }

    @Override
    public void toggleLoadingProgress(boolean show) {

    }

    @Override
    public void replaceFragment(Fragment fragment) {

    }

    @Override
    public void onBackStackChanged() {

    }
}
