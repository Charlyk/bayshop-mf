package com.softranger.bayshopmf.ui.payment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.PagerAdapter;
import com.softranger.bayshopmf.adapter.PaymentSelectorAdapter;
import com.softranger.bayshopmf.model.payment.Currency;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class PaymentActivity extends AppCompatActivity implements PullToRefreshLayout.PullToRefreshListener {

    public static final String ACTION_REFRESH = "REFRESH HISTORIES";

    @BindView(R.id.paymentViewPager)
    ViewPager mViewPager;
    @BindView(R.id.paymentTabBarLayout)
    TabLayout mTabLayout;
    @BindView(R.id.paymentJellyRefresh)
    public JellyRefreshLayout mRefreshLayout;

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
        mRefreshLayout.setPullToRefreshListener(this);
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
