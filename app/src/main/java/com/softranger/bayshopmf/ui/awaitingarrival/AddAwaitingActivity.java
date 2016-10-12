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
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.DeclarationListAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.Product;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.util.Application;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class AddAwaitingActivity extends AppCompatActivity implements Animator.AnimatorListener,
        MenuItem.OnMenuItemClickListener, DeclarationListAdapter.OnActionButtonsClick {

    public static final String PRODUCTS_ARRAY = "PRODUCTS ARRAY KEY";

    private static final int MAX_HEIGHT = Application.getPixelsFromDp(150);
    private boolean mIsExpanded;
    private boolean mIsAnimating;

    private ArrayList<Product> mProducts;
    private DeclarationListAdapter mAdapter;
    private CustomTabsIntent mTabsIntent;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_awaiting);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener((view) -> onBackPressed());

        Intent intent = getIntent();
        if (intent.hasExtra(PRODUCTS_ARRAY)) {
            mProducts = intent.getExtras().getParcelableArrayList(PRODUCTS_ARRAY);
        } else {
            mProducts = new ArrayList<>();
        }
        mAdapter = new DeclarationListAdapter(mProducts, mProducts.size() > 0, true);
        mAdapter.setOnActionButtonsClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        // create tabs intent for products url
        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(getResources().getColor(R.color.colorAccent));
        mTabsIntent = tabsBuilder.build();
    }

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

    @OnClick(R.id.addAwaitingServicesLayout)
    void expandServicesLayout() {
        if (mIsAnimating) return;
        float fromHeight;
        float toHeight;
        if (!mIsExpanded) {
            fromHeight = 0;
            toHeight = MAX_HEIGHT;
        } else {
            fromHeight = MAX_HEIGHT;
            toHeight = 0;
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mExpandedServicesLayout.getLayoutParams();

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

    @OnClick(R.id.addAwaitingAddFieldButton)
    void addNewProduct() {
        mAdapter.addNewProductCard();
    }

    @OnClick(R.id.addAwaitingSaveButton)
    void saveAwaitingParcel() {

    }

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
            super.onBackPressed();
        }
    }

    @Override
    public void onDeleteClick(Product product, int position) {
        mAdapter.removeItem(position);
    }

    @Override
    public void onOpenUrl(String url, int position) {
        if (url.length() > 0) {
            mTabsIntent.launchUrl(this, Uri.parse(url));
        }
    }

    @Override
    public void onTrackingChanged(String currentTracking) {

    }

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {

        }

        @Override
        public void onFailure(ServerResponse errorData) {

        }

        @Override
        public void onError(Call call, Throwable t) {

        }
    };
}
