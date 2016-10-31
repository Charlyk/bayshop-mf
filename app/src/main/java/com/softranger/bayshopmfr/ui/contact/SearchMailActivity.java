package com.softranger.bayshopmfr.ui.contact;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.MailAdapter;
import com.softranger.bayshopmfr.model.chat.MailMessage;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.util.ArrayList;

public class SearchMailActivity extends ParentActivity implements MenuItemCompat.OnActionExpandListener,
        SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener, MailAdapter.OnMailClickListener {

    private ArrayList<MailMessage> mMessages;
    private MailAdapter mAdapter;

    private MenuItem mSearchItem;
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mMessages = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mailsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MailAdapter(mMessages);
        mAdapter.setOnMailClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {

    }

    @Override
    public void toggleLoadingProgress(boolean show) {

    }

    @Override
    public void replaceFragment(ParentFragment fragment) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        mSearchItem = menu.findItem(R.id.searchMessage);
        MenuItemCompat.setOnActionExpandListener(mSearchItem, this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (mSearchItem != null) {
            mSearchView = (SearchView) mSearchItem.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(this);
        }

        mSearchItem.expandActionView();
        mSearchView.setIconified(false);
        mSearchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onMessageIconClicked(MailMessage message, int position, boolean isSelected) {

    }

    @Override
    public void onMessageClicked(MailMessage message, int position, boolean isSelected) {

    }

    @Override
    public void onBackStackChanged() {

    }
}
