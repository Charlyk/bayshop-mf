package com.softranger.bayshopmf.ui.general;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.addresses.WarehouseAddressesActivity;
import com.softranger.bayshopmf.ui.auth.LoginActivity;
import com.softranger.bayshopmf.ui.awaitingarrival.AwaitingArrivalFragment;
import com.softranger.bayshopmf.ui.calculator.ShippingCalculatorActivity;
import com.softranger.bayshopmf.ui.contact.ContactUsActivity;
import com.softranger.bayshopmf.ui.instock.InStockFragment;
import com.softranger.bayshopmf.ui.payment.PaymentActivity;
import com.softranger.bayshopmf.ui.pus.PUSParcelsFragment;
import com.softranger.bayshopmf.ui.pus.ReceivedFragment;
import com.softranger.bayshopmf.ui.settings.SettingsActivity;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.CustomExceptionHandler;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.widget.TotalsView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class MainActivity extends ParentActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ACTION_REFRESH = "START REFRESHING";
    public static final String ACTION_ITEM_DELETED = "ITEM_DELETED";
    private static final int PERMISSION_REQUEST_CODE = 1535;
    public static final String ACTION_UPDATE_TITLE = "update toolbar title";
    public ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    public Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private FrameLayout mFabBackground;
    private static String[] permissions;
    private NavigationView mNavigationView;
    private String mFirstToolbarTitle;
    public static int toolbarHeight;

    @BindView(R.id.fullScreenContainer) public FrameLayout mFrameLayout;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, MainActivity.permissions, PERMISSION_REQUEST_CODE);
                } else {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/Signals/Errors");
                    dir.mkdirs();
                    CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(dir.getAbsolutePath(), null);
                    if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
                        Thread.setDefaultUncaughtExceptionHandler(customExceptionHandler);
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        supportPostponeEnterTransition();
        supportStartPostponedEnterTransition();

        IntentFilter intentFilter = new IntentFilter(SettingsActivity.ACTION_LOG_OUT);
        registerReceiver(mBroadcastReceiver, intentFilter);

        mToolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbarHeight = mToolbar.getHeight();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // set click listener to collapse button and hide layout if user press on screen
        mFabBackground = ButterKnife.findById(this, R.id.fabBackgroundLayout);
        mFabBackground.setOnClickListener((view) -> {
            onBackPressed();
        });

        // initialize navigation drawer
        mDrawerLayout = ButterKnife.findById(this, R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mProgressBar = ButterKnife.findById(this, R.id.mainActivityProgressBar);

        // initialize navigation view and it's header
        mNavigationView = ButterKnife.findById(this, R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = mNavigationView.getHeaderView(0);
        TextView userNameLabel = ButterKnife.findById(navHeaderView, R.id.navHeaderUserNameLabel);
        TextView userIdLabel = ButterKnife.findById(navHeaderView, R.id.navHeaderUserIdLabel);
        if (Application.user != null) {
            String fullName = Application.user.getFirstName() + " " + Application.user.getLastName();
            userNameLabel.setText(fullName);
            userIdLabel.setText(Application.getInstance().getUserId().toUpperCase());
        }

        // add first fragment to container
        selectedFragment = SelectedFragment.awaiting_arrival;
        replaceFragment(AwaitingArrivalFragment.newInstance());
        mFirstToolbarTitle = getString(selectedFragment.fragmentName());
        setToolbarTitle(mFirstToolbarTitle);

        // create a permission list
        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        // update parcels counter
        updateParcelCounters(null);

        // check app permissions to write and read external storage and request them if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            return;
        }
        // initialize custom error handler so if there are any unhandled exception to write it in a file
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/BayShopMF/Errors");
        dir.mkdirs();
        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(dir.getAbsolutePath(), null);
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(customExceptionHandler);
        }
    }

    public void addFullScreenFragment(ParentFragment parentFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fullScreenContainer, parentFragment, parentFragment.getClass().getSimpleName());
        transaction.addToBackStack(parentFragment.getClass().getSimpleName());
        transaction.commit();
    }

    /**
     * Set the parcels count on the right side in the Navigation Drawer
     */
    public void updateParcelCounters(@Nullable String parcelStatus) {
        if (parcelStatus == null) {
            setMenuCounter(R.id.nav_waitingArrival, Application.getCount(Constants.ParcelStatus.AWAITING_ARRIVAL));
            setMenuCounter(R.id.nav_inStock, Application.getCount(Constants.ParcelStatus.IN_STOCK));
            setMenuCounter(R.id.nav_received, Application.getCount(Constants.ParcelStatus.RECEIVED));
            setMenuCounter(R.id.nav_parcels, Application.getCount(Constants.PARCELS));
            return;
        }

        switch (parcelStatus) {
            case Constants.ParcelStatus.AWAITING_ARRIVAL:
                setMenuCounter(R.id.nav_waitingArrival, Application.getCount(parcelStatus));
                break;
            case Constants.ParcelStatus.IN_STOCK:
                setMenuCounter(R.id.nav_inStock, Application.getCount(parcelStatus));
                break;
            case Constants.ParcelStatus.RECEIVED:
                setMenuCounter(R.id.nav_received, Application.getCount(parcelStatus));
                break;
            case Constants.PARCELS:
                setMenuCounter(R.id.nav_parcels, Application.getCount(parcelStatus));
                break;
        }
    }

    /**
     * Either show or hide the progress bar
     *
     * @param show true to show the bar or false to hide it
     */
    @Override
    public void toggleLoadingProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (showAnimation)
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        else transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();

        new Handler().postDelayed(() -> {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerToggle.setToolbarNavigationClickListener(view -> onBackPressed());
            mDrawerToggle.syncState();
        }, 100);
    }

    public void logOut() {
        Application.getInstance().setLoginStatus(false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void setToolbarTitle(final String title) {
        runOnUiThread(() -> new Handler().postDelayed(() -> {
            TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
            toolbarTitle.setText(title);
        }, 100));
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        View view = mNavigationView.getMenu().findItem(itemId).getActionView();
        TextView countLabel = (TextView) view.findViewById(R.id.navCounterTextView);
        if (count <= 0) {
            view.setVisibility(View.GONE);
        } else {
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
            }
            countLabel.setText(String.valueOf(count));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean closeDrawer = true;
        switch (id) {
            case R.id.nav_profileSettings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                closeDrawer = false;
                break;
            case R.id.nav_paymentHistory:
                Intent payment = new Intent(this, PaymentActivity.class);
                startActivity(payment);
                closeDrawer = false;
                break;
            case R.id.nav_shippingCalculator:
                Intent calculator = new Intent(this, ShippingCalculatorActivity.class);
                startActivity(calculator);
                closeDrawer = false;
                break;
            case R.id.nav_contactUs:
                Intent contactUs = new Intent(this, ContactUsActivity.class);
                startActivity(contactUs);
                closeDrawer = false;
                break;
            case R.id.nav_addresses:
                Intent addresses = new Intent(this, WarehouseAddressesActivity.class);
                startActivity(addresses);
                closeDrawer = false;
                break;
            case R.id.nav_inStock:
                selectedFragment = SelectedFragment.in_stock;
                replaceFragment(InStockFragment.newInstance());
                mFirstToolbarTitle = getString(selectedFragment.fragmentName());
                setToolbarTitle(mFirstToolbarTitle);
                break;
            case R.id.nav_waitingArrival:
                selectedFragment = SelectedFragment.awaiting_arrival;
                replaceFragment(AwaitingArrivalFragment.newInstance());
                mFirstToolbarTitle = getString(selectedFragment.fragmentName());
                setToolbarTitle(mFirstToolbarTitle);
                break;
            case R.id.nav_parcels:
                selectedFragment = SelectedFragment.parcels;
                replaceFragment(PUSParcelsFragment.newInstance());
                mFirstToolbarTitle = getString(selectedFragment.fragmentName());
                setToolbarTitle(mFirstToolbarTitle);
                break;
            case R.id.nav_received:
                selectedFragment = SelectedFragment.received;
                replaceFragment(ReceivedFragment.newInstance());
                mFirstToolbarTitle = getString(selectedFragment.fragmentName());
                setToolbarTitle(mFirstToolbarTitle);
                break;
//            case R.id.nav_accountReplenishment:
//                selectedFragment = SelectedFragment.account_replenishment;
//                replaceFragment(new ReplenishmentFragment());
//                mFirstToolbarTitle = getString(selectedFragment.fragmentName());
//                setToolbarTitle(mFirstToolbarTitle);
//                break;
        }

        if (closeDrawer)
            mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setToolbarToInitialState() {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            if (getFragmentManager().getBackStackEntryCount() == 1) {
                setToolbarToInitialState();
            } else {
                Intent updateTitleIntent = new Intent(ACTION_UPDATE_TITLE);
                sendBroadcast(updateTitleIntent);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        Intent refresh = new Intent(ACTION_REFRESH);
        sendBroadcast(refresh);
    }

    public interface OnEditDialogClickListener {
        void onPositiveClick(String newInput);

        void onNegativeClick();
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SettingsActivity.ACTION_LOG_OUT)) {
                logOut();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getFragmentManager();
        // get index of the last fragment to be able to get it's tag
        int currentStackIndex = manager.getBackStackEntryCount() - 1;
        // if we don't have fragments in back stack just return
        if (manager.getBackStackEntryCount() == 0) {
            setToolbarTitle(mFirstToolbarTitle);
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
