package com.softranger.bayshopmfr.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.PagerAdapter;
import com.softranger.bayshopmfr.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class PaymentActivity extends AppCompatActivity implements PullToRefreshLayout.PullToRefreshListener {

    public static final String ACTION_REFRESH = "REFRESH HISTORIES";

    @BindView(R.id.paymentViewPager)
    ViewPager mViewPager;
    @BindView(R.id.paymentTabBarLayout)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        // initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.paymentActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setUpHistoryPages();
    }

    private void setUpHistoryPages() {
        PagerAdapter pagerAdapter = new PagerAdapter(this, getSupportFragmentManager());
        pagerAdapter.setShowTitle(true);
        pagerAdapter.addFragment(PaymentHistoryFragment.newInstance(Constants.Period.all.toString()), getString(R.string.all));
        pagerAdapter.addFragment(PaymentHistoryFragment.newInstance(Constants.Period.month.toString()), getString(R.string.last_month));
        pagerAdapter.addFragment(PaymentHistoryFragment.newInstance(Constants.Period.week.toString()), getString(R.string.last_week));
        pagerAdapter.addFragment(PaymentHistoryFragment.newInstance(Constants.Period.one.toString()), getString(R.string.today));
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        sendBroadcast(new Intent(ACTION_REFRESH));
    }
}
