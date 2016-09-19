package com.softranger.bayshopmf.ui.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

public class SettingsActivity extends ParentActivity implements FragmentManager.OnBackStackChangedListener {

    public static final String ACTION_LOG_OUT = "action log out";

    private TextView mToolbarTitle;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsActivityToolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back_24dp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.settingsProgressBar);

        mToolbarTitle = (TextView) findViewById(R.id.settingsActivityToolbarTitle);
        SettingsFragment settingsFragment = new SettingsFragment();
        replaceFragment(settingsFragment);
    }

    @Override
    public void setToolbarTitle(final String title) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbarTitle.setText(title);
            }
        }, 200);
    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (showAnimation)
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        else transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.settingsActivityContainer, fragment);
        transaction.addToBackStack("DetailsFragment");
        transaction.commit();
    }

    @Override
    public void toggleLoadingProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.settingsActivityContainer, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getFragmentManager();
        // get index of the last fragment to be able to get it's tag
        int currentStackIndex = manager.getBackStackEntryCount() - 1;
        // if we don't have fragments in back stack just return
        if (manager.getBackStackEntryCount() == 0) return;
        // otherwise get the framgent from backstack and cast it to ParentFragment so we could get it's title
        ParentFragment fragment = (ParentFragment) manager.findFragmentByTag(
                manager.getBackStackEntryAt(currentStackIndex).getName());
        // finaly set the title in the toolbar
        setToolbarTitle(fragment.getFragmentTitle());
        // now we need to update the current selected fragment
        selectedFragment = fragment.getSelectedFragment();
    }
}
