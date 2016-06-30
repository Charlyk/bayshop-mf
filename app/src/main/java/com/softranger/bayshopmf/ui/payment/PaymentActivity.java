package com.softranger.bayshopmf.ui.payment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.softranger.bayshopmf.R;

public class PaymentActivity extends AppCompatActivity {

    private PaymentHistoryFragment mHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mHistoryFragment = new PaymentHistoryFragment();
        replaceFragment(mHistoryFragment);
    }

    public void addFragment(Fragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (showAnimation)
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        else transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.paymentFragmentContainer, fragment);
        transaction.addToBackStack("DetailsFragment");
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.paymentFragmentContainer, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
}
