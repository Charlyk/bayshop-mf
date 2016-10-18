package com.softranger.bayshopmf.ui.awaitingarrival;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.DeclarationListAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.Product;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class AddAwaitingActivity extends AppCompatActivity implements Animator.AnimatorListener,
        MenuItem.OnMenuItemClickListener, DeclarationListAdapter.OnActionButtonsClick {

    // this is key to get products array from intent extras
    public static final String PRODUCTS_ARRAY = "PRODUCTS ARRAY KEY";
    // key for show tracking boolean value used by adapter
    public static final String SHOW_TRACKING = "SHOW TRACKING FIELD";
    // key to ge id for parcel needed to edit
    public static final String AWAITING_ID = "PARCEL TO EDIT ID";
    // tracking number key
    public static final String TRACKING_NUM = "TRACKING NUMBER";
    // this is action to put in result intent
    public static final String ACTION_REFRESH_AWAITING = "REFRESH AWAITING LIST";

    // maximum height for bottom additional services layout
    private static final int MAX_HEIGHT = Application.getPixelsFromDp(150);
    // show if bottom layout is expanded or not
    private boolean mIsExpanded;
    // show if currently bottom layout is animating
    private boolean mIsAnimating;

    // product array list
    private ArrayList<Product> mProducts;
    // adapter used to edit parcel declaration
    private DeclarationListAdapter mAdapter;
    // chrome tabs intent used to open products url
    private CustomTabsIntent mTabsIntent;
    // call to server for saving parcel details
    private Call<ServerResponse> mCall;

    // current entered parcel tracking number
    private String mTrackingNumber;
    // parcel to edit id
    private String mParcelId;
    private boolean mShowTracking;

    @BindView(R.id.addAwaitingToolbar)
    Toolbar mToolbar;
    @BindView(R.id.addAwaitingPreCheckCheckBox)
    CheckBox mPreCheck;
    @BindView(R.id.addAwaitingPrePhotoCheckBox)
    CheckBox mPrePhoto;
    @BindView(R.id.addAwaitingRepackCheckBox)
    CheckBox mRepack;
    @BindView(R.id.addAwaitingExpandedServicesLayout)
    LinearLayout mExpandedServicesLayout;
    @BindView(R.id.addAwaitingRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.addAwaitingProgressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.addAwaitingToolbarTitle)
    TextView mToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_awaiting);
        // bind all views
        ButterKnife.bind(this);
        // set toolbar as action bar
        setSupportActionBar(mToolbar);
        // hide toolbar title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // set toolbar navigation icon click listener
        mToolbar.setNavigationOnClickListener((view) -> onBackPressed());

        // try to get products array from intent extras
        Intent intent = getIntent();

        if (intent.hasExtra(AWAITING_ID)) {
            mParcelId = intent.getExtras().getString(AWAITING_ID);
            mToolbarTitle.setText(getString(R.string.edit_details));
        } else {
            mParcelId = "";
        }

        if (intent.hasExtra(TRACKING_NUM)) {
            mTrackingNumber = intent.getExtras().getString(TRACKING_NUM);
        } else {
            mTrackingNumber = "";
        }

        mShowTracking = intent.getExtras().getBoolean(SHOW_TRACKING);

        if (intent.hasExtra(PRODUCTS_ARRAY)) {
            mProducts = intent.getExtras().getParcelableArrayList(PRODUCTS_ARRAY);
        } else {
            // if intent does not contain any products just create a new array
            mProducts = new ArrayList<>();
        }
        // create an instance of adater
        mAdapter = new DeclarationListAdapter(mProducts, mProducts.size() > 0, mShowTracking);
        mAdapter.setOnActionButtonsClickListener(this);
        mAdapter.setTrackingNum(mTrackingNumber);
        // set the adapter to recycler view
        mRecyclerView.setAdapter(mAdapter);

        // create tabs intent for products url
        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(getResources().getColor(R.color.colorAccent));
        mTabsIntent = tabsBuilder.build();
    }

    /**
     * This toggles progress bar visibility
     *
     * @param show true to set as visible or false to set as gone
     */
    private void toggleLoadingProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.info_menu, menu);
        menu.findItem(R.id.toolbar_help).setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_help:
                Toast.makeText(this, "Help will be here", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    /**
     * Expands bottom additional services layout with animation
     */
    @OnClick(R.id.addAwaitingServicesLayout)
    void expandServicesLayout() {
        // if is already animating, let it finish
        if (mIsAnimating) return;
        // get start and end values
        float fromHeight;
        float toHeight;
        if (!mIsExpanded) {
            // if is not expanded we need to start from 0 to MAX_HEIGHT
            fromHeight = 0;
            toHeight = MAX_HEIGHT;
        } else {
            // otherwise start from MAX_HEIGHT and finish to 0
            fromHeight = MAX_HEIGHT;
            toHeight = 0;
        }

        // get services layout params
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mExpandedServicesLayout.getLayoutParams();

        // create an animation for expand or collapse action
        ValueAnimator animation = ValueAnimator.ofFloat(fromHeight, toHeight);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addUpdateListener(animation1 -> {
            float animatedValue = (float) animation1.getAnimatedValue();
            layoutParams.height = (int) animatedValue;
            mExpandedServicesLayout.setLayoutParams(layoutParams);
        });
        animation.addListener(this);
        animation.start();
    }

    /**
     * Add a new empty product card and a product to {@link AddAwaitingActivity#mProducts}
     */
    @OnClick(R.id.addAwaitingAddFieldButton)
    void addNewProduct() {
        int position = mAdapter.addNewProductCard();
        mRecyclerView.smoothScrollToPosition(position);
    }

    /**
     * Save all products to server
     */
    @OnClick(R.id.addAwaitingSaveButton)
    void saveAwaitingParcel() {
        // check if thracking number is not empty
        if (mShowTracking && (mTrackingNumber == null || mTrackingNumber.length() <= 0)) {
            Toast.makeText(this, getString(R.string.enter_tracking_num), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // create a json array with all product information
            JSONArray productsArray = new JSONArray();
            for (Product product : mProducts) {
                JSONObject jsonProduct = new JSONObject();
                jsonProduct.put("title", product.getTitle());
                jsonProduct.put("url", product.getUrl());
                jsonProduct.put("quantity", product.getQuantity());
                jsonProduct.put("price", product.getPrice());
                if (product.getItemId() != null) {
                    jsonProduct.put("id", product.getItemId());
                }
                productsArray.put(jsonProduct);
            }

            // send all information to server
            mCall = Application.apiInterface().addNewAwaitingParcel(mParcelId, Constants.USA,
                    mTrackingNumber, productsArray.toString());
            mCall.enqueue(mResponseCallback);
            toggleLoadingProgress(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enable or disable additional services
     */
    @OnClick({R.id.addAwaitingPreCheckBtn, R.id.addAwaitingPrePhotoBtn, R.id.addAwaitingRepackBtn})
    void onAdditionalServicesClick(View view) {
        switch (view.getId()) {
            case R.id.addAwaitingPreCheckBtn:
                mPreCheck.setChecked(!mPreCheck.isChecked());
                break;
            case R.id.addAwaitingPrePhotoBtn:
                mPrePhoto.setChecked(!mPrePhoto.isChecked());
                break;
            case R.id.addAwaitingRepackBtn:
                mRepack.setChecked(!mRepack.isChecked());
                break;
        }
    }

    /**
     * Animator listener
     */
    @Override
    public void onAnimationStart(Animator animation) {
        mIsAnimating = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mIsAnimating = false;
        mIsExpanded = !mIsExpanded;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mIsAnimating = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onBackPressed() {
        if (mIsExpanded) {
            expandServicesLayout();
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    /**
     * Delete a product from {@link AddAwaitingActivity#mProducts} and from {@link DeclarationListAdapter}
     * @param product which will be removed
     * @param position for this product in the adapter
     */
    @Override
    public void onDeleteClick(Product product, int position) {
        mAdapter.removeItem(position);
    }

    /**
     * Open product url in {@link AddAwaitingActivity#mTabsIntent}
     * @param url to be opened
     * @param position for the product with above url
     */
    @Override
    public void onOpenUrl(String url, int position) {
        mTabsIntent.launchUrl(this, Uri.parse(url));
    }

    /**
     * This is triggered every time tracking number is changed
     * @param currentTracking the result tracking after changing it
     */
    @Override
    public void onTrackingChanged(String currentTracking) {
        mTrackingNumber = currentTracking;
    }

    /**
     * Receives response from server and checks if it is successful or not
     */
    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            Intent refreshIntent = new Intent(ACTION_REFRESH_AWAITING);
            setResult(RESULT_OK, refreshIntent);
            // TODO: 10/13/16 start result activity
            finish();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            toggleLoadingProgress(false);
            Toast.makeText(AddAwaitingActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call call, Throwable t) {
            toggleLoadingProgress(false);
            Toast.makeText(AddAwaitingActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}