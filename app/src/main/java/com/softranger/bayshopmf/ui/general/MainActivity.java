package com.softranger.bayshopmf.ui.general;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Fragment;
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
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.ui.BlankFragment;
import com.softranger.bayshopmf.ui.addresses.WarehouseAddressesActivity;
import com.softranger.bayshopmf.ui.calculator.ShippingCalculatorActivity;
import com.softranger.bayshopmf.ui.contact.ContactUsActivity;
import com.softranger.bayshopmf.ui.services.ReplenishmentFragment;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.ui.auth.LoginActivity;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.payment.PaymentActivity;
import com.softranger.bayshopmf.ui.settings.SettingsActivity;
import com.softranger.bayshopmf.ui.storages.StorageHolderFragment;
import com.softranger.bayshopmf.ui.instock.buildparcel.ItemsListFragment;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.CustomExceptionHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

public class MainActivity extends ParentActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String ACTION_ITEM_DELETED = "ITEM_DELETED";
    private static final int PERMISSION_REQUEST_CODE = 1535;
    public static final String ACTION_UPDATE_TITLE = "update toolbar title";
    public ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    public Toolbar mToolbar;
    public FloatingActionsMenu mActionMenu;
    public static SelectedFragment selectedFragment;
    private ProgressBar mProgressBar;
    private FrameLayout mFabBackground;
    private ArrayList<FloatingActionButton> mActionButtons;
    public static ArrayList<InStockItem> inStockItems;
    private static String[] permissions;
    private NavigationView mNavigationView;

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
        supportPostponeEnterTransition();
        supportStartPostponedEnterTransition();

        IntentFilter intentFilter = new IntentFilter(SettingsActivity.ACTION_LOG_OUT);
        registerReceiver(mBroadcastReceiver, intentFilter);

        inStockItems = new ArrayList<>();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // set click listener to collapse button and hide layout if user press on screen
        mFabBackground = (FrameLayout) findViewById(R.id.fabBackgroundLayout);
        mFabBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mActionButtons = new ArrayList<>();

        // set listener to either show or hide white background
        mActionMenu = (FloatingActionsMenu) findViewById(R.id.fab);
        mActionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mFabBackground.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mFabBackground.setVisibility(View.GONE);
            }
        });

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.buildParcelButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBuildingPusParcel(false, null);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mProgressBar = (ProgressBar) findViewById(R.id.mainActivityProgressBar);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = mNavigationView.getHeaderView(0);
        TextView userNameLabel = (TextView) navHeaderView.findViewById(R.id.navHeaderUserNameLabel);
        TextView userIdLabel = (TextView) navHeaderView.findViewById(R.id.navHeaderUserIdLabel);
        if (Application.user != null) {
            String fullName = Application.user.getFirstName() + " " + Application.user.getLastName();
            userNameLabel.setText(fullName);
            userIdLabel.setText(Application.getInstance().getUserId());
        }
        LinearLayout addAwaiting = (LinearLayout) navHeaderView.findViewById(R.id.add_packageButtonIcon);
        addAwaiting.setOnClickListener(this);

        selectedFragment = SelectedFragment.IN_STOCK;
        replaceFragment(BlankFragment.newInstance());

        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        updateParcelCounters(null);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            return;
        }
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/BayShopMF/Errors");
        dir.mkdirs();
        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(dir.getAbsolutePath(), null);
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(customExceptionHandler);
        }
    }

    /**
     * Set the parcels count on the right side in the Navigation Drawer
     */
    public void updateParcelCounters(@Nullable String parcelStatus) {
        if (parcelStatus == null) {
            setMenuCounter(R.id.nav_waitingArrival, Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL));
            setMenuCounter(R.id.nav_inStock, Application.counters.get(Constants.ParcelStatus.IN_STOCK));
            setMenuCounter(R.id.nav_inForming, Application.counters.get(Constants.ParcelStatus.LIVE));
            setMenuCounter(R.id.nav_inProcessing, Application.counters.get(Constants.ParcelStatus.IN_PROCESSING));
            setMenuCounter(R.id.nav_awaitingSending, Application.counters.get(Constants.ParcelStatus.PACKED));
            setMenuCounter(R.id.nav_heldDueToDebt, Application.counters.get(Constants.ParcelStatus.DEPT));
            setMenuCounter(R.id.nav_heldByProhibition, Application.counters.get(Constants.ParcelStatus.HELD_BY_PROHIBITION));
            setMenuCounter(R.id.nav_sent, Application.counters.get(Constants.ParcelStatus.SENT));
            setMenuCounter(R.id.nav_heldByCustoms, Application.counters.get(Constants.ParcelStatus.CUSTOMS_HELD));
            setMenuCounter(R.id.nav_localDeposit, Application.counters.get(Constants.ParcelStatus.LOCAL_DEPO));
            setMenuCounter(R.id.nav_takeToDelivery, Application.counters.get(Constants.ParcelStatus.TAKEN_TO_DELIVERY));
            setMenuCounter(R.id.nav_received, Application.counters.get(Constants.ParcelStatus.RECEIVED));
//            setMenuCounter(R.id.nav_awaitingDeclaration, Application.counters.get(Constants.ParcelStatus.AWAITING_DECLARATION));
//            setMenuCounter(R.id.nav_packing, Application.counters.get(Constants.ParcelStatus.PACKING));
//            setMenuCounter(R.id.nav_heldByDamageRecord, Application.counters.get(Constants.ParcelStatus.DAMAGE_RECORDED));
//            setMenuCounter(R.id.nav_heldByUser, Application.counters.get(Constants.ParcelStatus.HELD_BY_USER));
            return;
        }

        switch (parcelStatus) {
            case Constants.ParcelStatus.AWAITING_ARRIVAL:
                setMenuCounter(R.id.nav_waitingArrival, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.IN_STOCK:
                setMenuCounter(R.id.nav_inStock, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.LIVE:
                setMenuCounter(R.id.nav_inForming, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.IN_PROCESSING:
                setMenuCounter(R.id.nav_inProcessing, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.PACKED:
                setMenuCounter(R.id.nav_awaitingSending, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.DEPT:
                setMenuCounter(R.id.nav_heldDueToDebt, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.HELD_BY_PROHIBITION:
                setMenuCounter(R.id.nav_heldByProhibition, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.SENT:
                setMenuCounter(R.id.nav_sent, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.CUSTOMS_HELD:
                setMenuCounter(R.id.nav_heldByCustoms, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.LOCAL_DEPO:
                setMenuCounter(R.id.nav_localDeposit, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.TAKEN_TO_DELIVERY:
                setMenuCounter(R.id.nav_takeToDelivery, Application.counters.get(parcelStatus));
                break;
            case Constants.ParcelStatus.RECEIVED:
                setMenuCounter(R.id.nav_received, Application.counters.get(parcelStatus));
                break;
//            case Constants.ParcelStatus.AWAITING_DECLARATION:
//                setMenuCounter(R.id.nav_awaitingDeclaration, Application.counters.get(Constants.ParcelStatus.AWAITING_DECLARATION));
//                break;
//            case Constants.ParcelStatus.PACKING:
//                setMenuCounter(R.id.nav_packing, Application.counters.get(Constants.ParcelStatus.PACKING));
//                break;
//            case Constants.ParcelStatus.DAMAGE_RECORDED:
//                setMenuCounter(R.id.nav_heldByDamageRecord, Application.counters.get(Constants.ParcelStatus.DAMAGE_RECORDED));
//                break;
//            case Constants.ParcelStatus.HELD_BY_USER:
//                setMenuCounter(R.id.nav_heldByUser, Application.counters.get(Constants.ParcelStatus.HELD_BY_USER));
//                break;
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

    /**
     * Starts the build parcel process
     *
     * @param continueBuilding true iff add button is clicked or false if create new is clicked
     * @param inForming        null if create new is clicked or an in forming parcel if need to continue
     */
    public void startBuildingPusParcel(boolean continueBuilding, @Nullable InForming inForming) {
        // check if there are any selected item in the list
        // if continueBuilding is true it does not matter if there are or not any parcel selected
        // we can just continue building the previous parcels
        if (!continueBuilding && inStockItems.size() <= 0) {
            Snackbar.make(mActionMenu, getString(R.string.please_select_parcels), Snackbar.LENGTH_SHORT).show();
            // we need to close floating menu and hide white background
            mActionMenu.collapse();
            return;
        }

        // here we need to decide if we need to continue or not
        if (continueBuilding) {
            // if there are any selected item then we need to tell to items list fragment that it should
            // add more items to clicked parcel, if not then it will use just GET method to obtain
            // already added parcels from server
            boolean add = inStockItems.size() > 0;
            addFragment(ItemsListFragment.newInstance(inStockItems, add, inForming, inForming.getDeposit()), false);
        } else {
            // if continueBuilding is false than we need just start a new parcel and add to it all
            // selected items
            addFragment(ItemsListFragment.newInstance(inStockItems, true, null, inStockItems.get(0).getDeposit()), false);
        }
        // we need to close floating menu and hide white background
        mActionMenu.collapse();
    }

    public void removeActionButtons() {
        for (FloatingActionButton button : mActionButtons) {
            mActionMenu.removeButton(button);
        }

        mActionButtons.clear();
    }

    public void addActionButtons(ArrayList<InForming> inFormingItems) {
        removeActionButtons();

        for (final InForming inForming : inFormingItems) {
            FloatingActionButton button = new FloatingActionButton(this);
            button.setSize(FloatingActionButton.SIZE_MINI);
            button.setIcon(R.mipmap.ic_parcel_24dp);
            button.setColorNormal(getResources().getColor(R.color.colorGreenAction));
            button.setColorPressed(getResources().getColor(R.color.colorGreenActionDark));
            button.setTitle(inForming.getCodeNumber());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBuildingPusParcel(true, inForming);
                }
            });
            mActionButtons.add(button);
            mActionMenu.addButton(button);
        }
    }

    @Override
    public void addFragment(Fragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (showAnimation)
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        else transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack("DetailsFragment");
        transaction.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActionMenu.setVisibility(View.GONE);
                mDrawerToggle.setDrawerIndicatorEnabled(false);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
                mDrawerToggle.syncState();
            }
        }, 300);
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
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void setToolbarTitle(final String title, final boolean hideLogo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
                        toolbarTitle.setText(title);
                        ImageView logo = (ImageView) findViewById(R.id.toolbar_logo);
                        logo.setVisibility(hideLogo ? View.GONE : View.VISIBLE);
                    }
                }, 300);
            }
        });
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean closeDrawer = true;
        boolean isStorageFragment = true;
        switch (id) {
            case R.id.nav_profileSettings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                closeDrawer = false;
                isStorageFragment = false;
                break;
            case R.id.nav_paymentHistory:
                Intent payment = new Intent(this, PaymentActivity.class);
                startActivity(payment);
                closeDrawer = false;
                isStorageFragment = false;
                break;
            case R.id.nav_shippingCalculator:
                Intent calculator = new Intent(this, ShippingCalculatorActivity.class);
                startActivity(calculator);
                closeDrawer = false;
                isStorageFragment = false;
                break;
            case R.id.nav_contactUs:
                Intent contactUs = new Intent(this, ContactUsActivity.class);
                startActivity(contactUs);
                closeDrawer = false;
                isStorageFragment = false;
                break;
            case R.id.nav_addresses:
                Intent addresses = new Intent(this, WarehouseAddressesActivity.class);
                startActivity(addresses);
                closeDrawer = false;
                isStorageFragment = false;
                break;
            case R.id.nav_inStock:
                selectedFragment = SelectedFragment.IN_STOCK;

                break;
            case R.id.nav_waitingArrival:
                selectedFragment = SelectedFragment.AWAITING_ARRIVAL;
                break;
            case R.id.nav_inProcessing:
                selectedFragment = SelectedFragment.IN_PROCESSING;
                break;
            case R.id.nav_inForming:
                selectedFragment = SelectedFragment.IN_FORMING;
                break;
            case R.id.nav_awaitingSending:
                selectedFragment = SelectedFragment.AWAITING_SENDING;
                break;
            case R.id.nav_sent:
                selectedFragment = SelectedFragment.SENT;
                break;
            case R.id.nav_received:
                selectedFragment = SelectedFragment.RECEIVED;
                break;
            case R.id.nav_localDeposit:
                selectedFragment = SelectedFragment.LOCAL_DEPO;
                break;
            case R.id.nav_heldByCustoms:
                selectedFragment = SelectedFragment.CUSTOMS_HELD;
                break;
            case R.id.nav_takeToDelivery:
                selectedFragment = SelectedFragment.TAKEN_TO_DELIVERY;
                break;
            case R.id.nav_heldByProhibition:
                selectedFragment = SelectedFragment.HELD_BY_PROHIBITION;
                break;
//            case R.id.nav_awaitingDeclaration:
//                selectedFragment = SelectedFragment.AWAITING_DECLARATION;
//                break;
            case R.id.nav_packing:
                selectedFragment = SelectedFragment.PACKING;
                break;
//            case R.id.nav_heldByDamageRecord:
//                selectedFragment = SelectedFragment.DAMAGE_RECORDED;
//                break;
//            case R.id.nav_heldByUser:
//                selectedFragment = SelectedFragment.HELD_BY_USER;
//                break;
            case R.id.nav_accountReplenishment:
                selectedFragment = SelectedFragment.ACCOUNT_REPLENISHMENT;
                isStorageFragment = false;
                changeList(new ReplenishmentFragment(), false);
                break;
        }

        if (isStorageFragment) {
            boolean showBtn = selectedFragment == SelectedFragment.IN_STOCK;
            changeList(StorageHolderFragment.newInstance(), showBtn);
        }

        if (closeDrawer)
            mDrawerLayout.closeDrawer(GravityCompat.START);

        if (mActionMenu.isExpanded()) {
            mActionMenu.collapse();
            mFabBackground.setVisibility(View.GONE);
        }
        return true;
    }

    private void changeList(Fragment fragment, boolean showFloatBtn) {
        replaceFragment(fragment);
        mActionMenu.setVisibility(showFloatBtn ? View.VISIBLE : View.GONE);
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
        } else if (mActionMenu.isExpanded()) {
            mActionMenu.collapse();
            mFabBackground.setVisibility(View.GONE);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            if (getFragmentManager().getBackStackEntryCount() == 1) {
                if (selectedFragment == SelectedFragment.IN_STOCK)
                    mActionMenu.setVisibility(View.VISIBLE);
                setToolbarToInitialState();
                switch (selectedFragment) {
                    case IN_STOCK:
                        setToolbarTitle(getString(R.string.warehouse_usa), true);
                        break;
                    case AWAITING_ARRIVAL:
                        setToolbarTitle(getString(R.string.awaiting_arrival), true);
                        break;
                    case IN_PROCESSING:
                        setToolbarTitle(getString(R.string.in_processing), true);
                        break;
                    case IN_FORMING:
                        setToolbarTitle(getString(R.string.in_forming), true);
                        break;
                    case AWAITING_SENDING:
                        setToolbarTitle(getString(R.string.awaiting_sending), true);
                        break;
                    case SENT:
                        setToolbarTitle(getString(R.string.sent), true);
                        break;
                    case RECEIVED:
                        setToolbarTitle(getString(R.string.received), true);
                        break;
                    case LOCAL_DEPO:
                        setToolbarTitle(getString(R.string.local_deposit), true);
                        break;
                    case TAKEN_TO_DELIVERY:
                        setToolbarTitle(getString(R.string.take_to_delivery), true);
                        break;
                    case CUSTOMS_HELD:
                        setToolbarTitle(getString(R.string.held_by_customs), true);
                        break;
                }
            } else {
                Intent updateTitleIntent = new Intent(ACTION_UPDATE_TITLE);
                sendBroadcast(updateTitleIntent);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_packageButtonIcon:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                addFragment(new AddAwaitingFragment(), true);
                setToolbarTitle(getString(R.string.add_awaiting_package), true);
                break;
            default:
                break;
        }
    }

    public void expandTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", 300);
        animation.setDuration(200).start();
    }

    public void collapseTextView(TextView tv, int numLines) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", numLines);
        animation.setDuration(200).start();
    }

    public interface OnEditDialogClickListener {
        void onPositiveClick(String newInput);

        void onNegativeClick();
    }



    public Handler mDeleteHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message");
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            boolean hasParcels = data.getInt("isPackageHasMoreBoxes") == 1;

                            if (!hasParcels) {
                                removeActionButtons();
                            }
                            Intent refreshIntent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
                            Intent deleteIntent = new Intent(ACTION_ITEM_DELETED);
                            deleteIntent.putExtra("hasMoreItems", hasParcels);
                            refreshIntent.putExtra("deposit", "us"); // TODO: 6/27/16 set with the actual selected storage
                            sendBroadcast(refreshIntent);
                            sendBroadcast(deleteIntent);
                        } else {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            toggleLoadingProgress(false);
        }
    };

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

    public enum SelectedFragment {
        IN_STOCK, AWAITING_ARRIVAL, IN_PROCESSING, IN_FORMING, AWAITING_SENDING, SENT, RECEIVED,
        LOCAL_DEPO, TAKEN_TO_DELIVERY, AWAITING_DECLARATION, PACKING, DAMAGE_RECORDED, HELD_BY_USER, ACCOUNT_REPLENISHMENT, HELD_BY_PROHIBITION, CUSTOMS_HELD
    }
}
