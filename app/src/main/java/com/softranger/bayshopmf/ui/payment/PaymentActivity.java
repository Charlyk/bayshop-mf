package com.softranger.bayshopmf.ui.payment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.PaymentSelectorAdapter;
import com.softranger.bayshopmf.model.Currency;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

public class PaymentActivity extends ParentActivity implements
        View.OnClickListener {

    private PaymentHistoryFragment mHistoryFragment;
    private ImageButton mCurrencySelector;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        // initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.paymentActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // set currency selector
        mCurrencySelector = (ImageButton) findViewById(R.id.paymentActivityCurrencySelectorBtn);
        mCurrencySelector.setOnClickListener(this);
        mSpinner = (Spinner) findViewById(R.id.paymentActivityCurrencySelector);

        ArrayList<Currency> currencies = new ArrayList<>();
        String[] currenciesNames = getResources().getStringArray(R.array.currency_list);
        for (String s : currenciesNames) {
            Currency currency = new Currency(s, getCurrencyImage(s));
            currency.setCurrencyType(Currency.CurrencyType.getCurrencyType(s));
            currencies.add(currency);
        }

        final PaymentSelectorAdapter adapter = new PaymentSelectorAdapter(this, R.layout.currency_row_layout, currencies);
        adapter.setOnCountryClickListener(new PaymentSelectorAdapter.OnCurrencyClickListener() {
            @Override
            public void onCurrencyClick(Currency currency, int position) {
                mCurrencySelector.setImageResource(currency.getIconId());
                mHistoryFragment.showListByCurrency(currency.getCurrencyType());
                adapter.notifyDataSetChanged();
            }
        });
        mSpinner.setAdapter(adapter);

        mHistoryFragment = new PaymentHistoryFragment();
        replaceFragment(mHistoryFragment);
    }

    @DrawableRes
    private int getCurrencyImage(String currencyName) {
        if (currencyName.equals(Currency.CurrencyType.USD.name())) {
            return R.mipmap.ic_dollar_icon_black_24dp;
        } else if (currencyName.equals(Currency.CurrencyType.Euro.name())) {
            return R.mipmap.ic_euro_icon_black_24dp;
        } else if (currencyName.equals(Currency.CurrencyType.GBP.name())) {
            return R.mipmap.ic_pound_icon_black_24dp;
        } else {
            return R.mipmap.ic_all_icon_black_24dp;
        }
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (showAnimation)
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        else transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.paymentFragmentContainer, fragment);
        transaction.addToBackStack("DetailsFragment");
        transaction.commit();
    }

    @Override
    public void toggleLoadingProgress(boolean show) {

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.paymentFragmentContainer, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        mSpinner.performClick();
    }

    @Override
    public void onBackStackChanged() {

    }
}
