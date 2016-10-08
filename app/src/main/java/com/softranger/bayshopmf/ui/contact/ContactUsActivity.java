package com.softranger.bayshopmf.ui.contact;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.MailAdapter;
import com.softranger.bayshopmf.adapter.PagerAdapter;
import com.softranger.bayshopmf.model.chat.MailMessage;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

public class ContactUsActivity extends ParentActivity implements MailAdapter.OnMailClickListener,
        MenuItem.OnMenuItemClickListener {

    private TextView mToolbarTitle;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private static boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mProgressBar = (ProgressBar) findViewById(R.id.mailActivityProgressBar);

        setUpTabsForAllMails();
    }

    private void setUpTabsForAllMails() {
        PagerAdapter adapter = new PagerAdapter(this, getSupportFragmentManager());
        adapter.setShowTitle(true);
        adapter.addFragment(new OrderMailsFragment(), getString(R.string.orders));
        adapter.addFragment(new ParcelMailsFragment(), getString(R.string.parcels));
        adapter.addFragment(new PersonalMailsFragment(), getString(R.string.personal));
        adapter.addFragment(new SystemMailsFragment(), getString(R.string.system));
        ViewPager viewPager = (ViewPager) findViewById(R.id.mailActivityViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.mailActivityTabBarLayout);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbarTitle.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        MenuItem changeStatusItem;
        if (!isEditMode) {
            inflater.inflate(R.menu.mails_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.searchMessage);
            changeStatusItem = menu.findItem(R.id.changeMessStatus);
            changeStatusItem.setOnMenuItemClickListener(this);
            searchItem.setOnMenuItemClickListener(this);
        } else {
            inflater.inflate(R.menu.edit_mails, menu);
            MenuItem searchItem = menu.findItem(R.id.deleteSelection);
            changeStatusItem = menu.findItem(R.id.changeMessagesStatus);
            changeStatusItem.setOnMenuItemClickListener(this);
            searchItem.setOnMenuItemClickListener(this);
        }
        return true;
    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {

    }

    @Override
    public void toggleLoadingProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void replaceFragment(ParentFragment fragment) {

    }

    public void composeMail(View view) {
        Intent compose = new Intent(this, NewMessageActivity.class);
        startActivity(compose);
    }

    //------------------------ Mails list callbacks ------------------------//
    @Override
    public void onMessageIconClicked(MailMessage message, int position, boolean isSelected) {
        isEditMode = !isEditMode;
        invalidateToolbar(isEditMode);
    }

    private void invalidateToolbar(boolean isEditMode) {
        @ColorRes int color = isEditMode ? R.color.colorPrimaryDark : android.R.color.white;
        @DrawableRes int navIcon = isEditMode ? R.mipmap.ic_back_white_24dp : R.mipmap.ic_back_24dp;


        String title = isEditMode ? "" : getString(R.string.internal_mail);

        mToolbar.setBackgroundColor(getResources().getColor(color));
        mToolbar.setNavigationIcon(navIcon);
        setToolbarTitle(title);

        invalidateOptionsMenu();
    }

    @Override
    public void onMessageClicked(MailMessage message, int position, boolean isSelected) {
        if (!isEditMode) {
            Intent chat = new Intent(this, ChatActivity.class);
            chat.putExtra("message", message);
            startActivity(chat);
        }
    }

    //------------------------ Menu click ------------------------//
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchMessage:
                Intent search = new Intent(this, SearchMailActivity.class);
                startActivity(search);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            isEditMode = false;
            invalidateToolbar(isEditMode);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {

    }
}
