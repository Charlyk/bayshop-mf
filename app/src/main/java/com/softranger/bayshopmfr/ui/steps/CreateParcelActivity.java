package com.softranger.bayshopmfr.ui.steps;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.box.InStock;
import com.softranger.bayshopmfr.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateParcelActivity extends ParentActivity {

    public final static String SELECTED_ITEMS = "SELECTED IN STOCK ITEMS";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_title) TextView mToolbarTitle;
    @BindView(R.id.createParcelProgressBar) ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parcel);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        mToolbar.setNavigationIcon(getToolbarNavIcon());
        mToolbar.setNavigationOnClickListener((view) -> {onBackPressed();});

        setToolbarTitle(getString(R.string.select_address));

        // check if we have passed selected items to this activity
        if (intent.hasExtra(SELECTED_ITEMS)) {
            ArrayList<InStock> selectedItems = intent.getExtras().getParcelableArrayList(SELECTED_ITEMS);
            if (selectedItems != null) {
                // if the list is not null add address list framgent to this activity
                getFragmentManager().addOnBackStackChangedListener(this);
                replaceFragment(AddressesListFragment.newInstance(selectedItems));
            } else {
                // otherwise finish activity because we need in stock items to continue
                finish();
            }
        } else {
            // if not we need to finish it because we can not create a parcel without selected items
            finish();
        }
    }

    @DrawableRes
    private int getToolbarNavIcon() {
        final int count = getFragmentManager().getBackStackEntryCount();
        if (count <= 0) return R.mipmap.ic_close_white_24dp;
        else return R.mipmap.ic_arrow_back_white;
    }

    @Override
    public void setToolbarTitle(String title) {
        runOnUiThread(() -> {mToolbarTitle.setText(title);});
    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (showAnimation)
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        else transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.createFragmentContainer, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void toggleLoadingProgress(boolean show) {
        runOnUiThread(() -> {mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);});
    }

    @Override
    public void replaceFragment(ParentFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.createFragmentContainer, fragment, fragment.getClass().getSimpleName());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getFragmentManager();
        // get index of the last fragment to be able to get it's tag
        int currentStackIndex = manager.getBackStackEntryCount() - 1;
        // change toolbar nav icon if needed
        mToolbar.setNavigationIcon(getToolbarNavIcon());
        // if we don't have fragments in back stack just return
        if (manager.getBackStackEntryCount() == 0) {
            setToolbarTitle(getString(R.string.select_address));
            selectedFragment = SelectedFragment.select_address;
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
}
