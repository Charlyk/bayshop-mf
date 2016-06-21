package com.softranger.bayshopmf.ui.general;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.ui.auth.LoginActivity;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.storages.StorageHolderFragment;
import com.softranger.bayshopmf.ui.instock.buildparcel.ItemsListFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.CustomExceptionHandler;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1535;
    public static final String ACTION_UPDATE_TITLE = "update toolbar title";
    public static final String ACTION_START_CREATING_PARCEL = "start creating a new parcel";
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout addAwaiting = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.add_packageButtonIcon);
        addAwaiting.setOnClickListener(this);

        selectedFragment = SelectedFragment.IN_STOCK;
        replaceFragment(StorageHolderFragment.newInstance());

        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

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

    public void toggleLoadingProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void startBuildingPusParcel(boolean continueBuilding, @Nullable InForming inForming) {
        if (!continueBuilding && inStockItems.size() <= 0) {
            Snackbar.make(mActionMenu, getString(R.string.please_select_parcels), Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (continueBuilding) {
            boolean add = inStockItems.size() > 0;
            addFragment(ItemsListFragment.newInstance(inStockItems, add, inForming, inForming.getDeposit()), false);
            mActionMenu.collapse();
        } else {
            addFragment(ItemsListFragment.newInstance(inStockItems, true, null, inStockItems.get(0).getDeposit()), false);
            mActionMenu.collapse();
        }
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
            button.setColorNormal(getResources().getColor(R.color.colorGreenAction));
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

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_inStock:
                selectedFragment = SelectedFragment.IN_STOCK;
                changeList(StorageHolderFragment.newInstance(), true);
                break;
            case R.id.nav_waitingArrival:
                selectedFragment = SelectedFragment.AWAITING_ARRIVAL;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_inProcessing:
                selectedFragment = SelectedFragment.IN_PROCESSING;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_inForming:
                selectedFragment = SelectedFragment.IN_FORMING;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_awaitingSending:
                selectedFragment = SelectedFragment.AWAITING_SENDING;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_sent:
                selectedFragment = SelectedFragment.SENT;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_received:
                selectedFragment = SelectedFragment.RECEIVED;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_localDeposit:
                selectedFragment = SelectedFragment.LOCAL_DEPO;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_heldByCustoms:
                selectedFragment = SelectedFragment.CUSTOMS_HELD;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_takeToDelivery:
                selectedFragment = SelectedFragment.TAKEN_TO_DELIVERY;
                changeList(StorageHolderFragment.newInstance(), false);
                break;
            case R.id.nav_logOut:
                logOut();
                break;
        }
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
                        setToolbarTitle(getString(R.string.in_stock), true);
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

                        break;
                    case TAKEN_TO_DELIVERY:

                        break;
                    case CUSTOMS_HELD:

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

    /**
     * Create an alert dialog with BayShop design
     * @param title which will be shown in the dialog header
     * @param message will be shown in dialog body
     * @param imageResource will be shown at the left of title
     * @param positiveButtonText text for right side button
     * @param onPositiveButtonClickListener click listener for positive button(can be null)
     * @param negativeButtonText text for left side button
     * @param onNegativeButtonClickListener click listener for negative button(can be null)
     * @return an Alert Dialog with specified data to be shown on the screen
     */
    public AlertDialog getDialog(@NonNull String title, @NonNull String message, @DrawableRes int imageResource,
                                 @Nullable String positiveButtonText,
                                 @Nullable View.OnClickListener onPositiveButtonClickListener,
                                 @Nullable String negativeButtonText, @Nullable View.OnClickListener onNegativeButtonClickListener) {
        // inflate dialog layout and bind all views
        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null, false);
        ImageView dialogImage = (ImageView) dialogLayout.findViewById(R.id.alertDialogImageLabel);
        TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.alertDialogTitleLabel);
        TextView dialogMessage = (TextView) dialogLayout.findViewById(R.id.alertDialogMessageLabel);
        Button negativeButton = (Button) dialogLayout.findViewById(R.id.alertDialogNegativeButton);
        Button positiveButton = (Button) dialogLayout.findViewById(R.id.alertDialogPositiveButton);

        // set not null data, as text and image for dialog
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogImage.setImageResource(imageResource);

        // check and set buttons either text and listener or visibility to GONE
        if (positiveButtonText != null) {
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(onPositiveButtonClickListener);
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        if (negativeButtonText != null) {
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(onNegativeButtonClickListener);
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        // Create the dialog with the given layout and return it
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setView(dialogLayout);
        return dialogBuilder.create();
    }

    public AlertDialog getEditDialog(@NonNull String title, @NonNull String message, @DrawableRes int imageResource,
                                     @Nullable String positiveButtonText,
                                     @Nullable String negativeButtonText, int inputType,
                                     @Nullable final OnEditDialogClickListener onEditDialogClickListener) {
        // inflate dialog layout and bind all views
        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.edit_text_dialog, null, false);
        ImageView dialogImage = (ImageView) dialogLayout.findViewById(R.id.editDialogImageLabel);
        TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.editDialogTitleLabel);
        final EditText dialogMessage = (EditText) dialogLayout.findViewById(R.id.editDialogInput);
        Button negativeButton = (Button) dialogLayout.findViewById(R.id.editDialogNegativeButton);
        Button positiveButton = (Button) dialogLayout.findViewById(R.id.editDialogPositiveButton);

        // set not null data, as text and image for dialog
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogImage.setImageResource(imageResource);

        // check and set buttons either text and listener or visibility to GONE
        if (positiveButtonText != null) {
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditDialogClickListener != null) {
                        onEditDialogClickListener.onPositiveClick(String.valueOf(dialogMessage.getText()));
                    }
                }
            });
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        if (negativeButtonText != null) {
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditDialogClickListener != null) {
                        onEditDialogClickListener.onNegativeClick();
                    }
                }
            });
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        // Create the dialog with the given layout and return it
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setView(dialogLayout);
        return dialogBuilder.create();
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

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public enum SelectedFragment {
        IN_STOCK, AWAITING_ARRIVAL, IN_PROCESSING, IN_FORMING, AWAITING_SENDING, SENT, RECEIVED,
        LOCAL_DEPO, TAKEN_TO_DELIVERY, CUSTOMS_HELD
    }
}