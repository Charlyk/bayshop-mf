package com.softranger.bayshopmf.ui.general;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.CustomExceptionHandler;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends ParentActivity implements NavigationView.OnNavigationItemSelectedListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    public static final String ACTION_ITEM_DELETED = "ITEM_DELETED";
    private static final int PERMISSION_REQUEST_CODE = 1535;
    public static final String ACTION_UPDATE_TITLE = "update toolbar title";
    public ActionBarDrawerToggle mDrawerToggle;
    private static String[] permissions;
    private String mFirstToolbarTitle;
    public static int toolbarHeight;
    private static SelectedFragment selectedFromMenu;

    @BindView(R.id.fullScreenContainer) public LinearLayout mFrameLayout;
    @BindView(R.id.fabBackgroundLayout) FrameLayout mFabBackground;
    @BindView(R.id.activityActionMenu) public FloatingActionButton mActionsMenu;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.mainActivityProgressBar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

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

        toolbarHeight = mToolbar.getHeight();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // add back stack listener to update toolbar title and other visuals
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        // set click listener to collapse button and hide layout if user press on screen
        mFabBackground.setOnClickListener((view) -> onBackPressed());

        // initialize navigation drawer
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // initialize navigation view and it's header
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AwaitingArrivalFragment.ADD_PARCEL_RC:
                    sendBroadcast(new Intent(AwaitingArrivalFragment.ACTION_ITEM_ADDED));
                    break;
            }
        }
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

    @OnClick(R.id.activityActionMenu)
    void onActionBtnClick() {
        Intent createParcel = new Intent(InStockFragment.ACTION_CREATE_PARCEL);
        sendBroadcast(createParcel);
    }

    public void toggleActionMenuVisibility(boolean show) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mActionsMenu, "scaleX",
                show ? 0f : 1f,
                show ? 1f : 0f);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mActionsMenu, "scaleY",
                show ? 0f : 1f,
                show ? 1f : 0f);

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(200);
        set.playTogether(scaleX, scaleY);
        if (show) mActionsMenu.setVisibility(View.VISIBLE);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) mActionsMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.start();
    }

    public void changeActionMenuColor(ActionMenuColor actionMenuColor) {
        int colorFrom = 0;
        int colorTo = 0;
        int colorPressed = 0;
        switch (actionMenuColor) {
            case green:
                colorFrom = getResources().getColor(R.color.colorLocalDepot);
                colorTo = getResources().getColor(R.color.colorGreenAction);
                colorPressed = getResources().getColor(R.color.colorGreenActionDark);
                break;
            case yellow:
                colorFrom = getResources().getColor(R.color.colorGreenAction);
                colorTo = getResources().getColor(R.color.colorLocalDepot);
                colorPressed = getResources().getColor(R.color.colorLocalDepotDark);
                break;
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation.addUpdateListener(animator -> mActionsMenu.setColorNormal((int) animator.getAnimatedValue()));
        colorAnimation.start();
        mActionsMenu.setColorPressed(colorPressed);
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
    public void replaceFragment(ParentFragment fragment) {
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
        selectedFromMenu = selectedFragment;
        if (closeDrawer) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            toggleActionMenuVisibility(false);
        }
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
        } else if (InStockFragment.canHideTotals()) {
            Intent intent = new Intent(InStockFragment.ACTION_HIDE_TOTALS);
            sendBroadcast(intent);
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
    public void onMenuExpanded() {

    }

    @Override
    public void onMenuCollapsed() {

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
            selectedFragment = selectedFromMenu;
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

    public enum ActionMenuColor {
        green, yellow
    }
}
