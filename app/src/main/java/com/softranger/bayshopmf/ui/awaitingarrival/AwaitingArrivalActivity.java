package com.softranger.bayshopmf.ui.awaitingarrival;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AwaitingArrivalActivity extends ParentActivity {

    @BindView(R.id.loadingProgreessBar)
    ProgressBar mProgressBar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awaiting_arrival);
        ButterKnife.bind(this);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        getFragmentManager().addOnBackStackChangedListener(this);
        String parcelId = getIntent().getExtras().getString("id");
        replaceFragment(AwaitingArrivalProductFragment.newInstance(parcelId));
        setToolbarTitle(getString(R.string.details));
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getFragmentManager();
        // get index of the last fragment to be able to get it's tag
        int currentStackIndex = manager.getBackStackEntryCount() - 1;
        // if we don't have fragments in back stack just return
        if (manager.getBackStackEntryCount() == 0) {
            setToolbarTitle(getString(R.string.details));
            selectedFragment = SelectedFragment.in_stock_details;
            return;
        }
        // otherwise get the framgent from backstack and cast it to ParentFragment so we could get it's title
        ParentFragment fragment = (ParentFragment) manager.findFragmentByTag(
                manager.getBackStackEntryAt(currentStackIndex).getName());
        // finaly set the title in the toolbar
        setToolbarTitle(fragment.getFragmentTitle());
        // now we need to update the current selected fragment
        selectedFragment = fragment.getSelectedFragment();
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbarTitle.setText(title);
    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (showAnimation)
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        else transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void toggleLoadingProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void replaceFragment(ParentFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
}
