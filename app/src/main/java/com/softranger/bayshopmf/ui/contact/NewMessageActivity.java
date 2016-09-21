package com.softranger.bayshopmf.ui.contact;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SpinnerAdapter;
import com.softranger.bayshopmf.model.chat.MailCategory;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.SpinnerObj;

import java.util.ArrayList;

public class NewMessageActivity extends ParentActivity implements MenuItem.OnMenuItemClickListener,
        SpinnerAdapter.OnCountryClickListener {

    private static final int UPLOAD_RESULT_CODE = 13;
    private SpinnerAdapter<MailCategory> mSpinnerAdapter;
    private Spinner mSpinner;
    private TextView mCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MailCategory orders = new MailCategory("Orders", 0);
        MailCategory packageMails = new MailCategory("Package", 1);
        MailCategory personal = new MailCategory("Personal", 2);
        ArrayList<MailCategory> categories = new ArrayList<>();
        categories.add(orders);
        categories.add(packageMails);
        categories.add(personal);
        mSpinnerAdapter = new SpinnerAdapter<>(this, R.layout.country_spinner_item, categories);
        mSpinnerAdapter.setOnCountryClickListener(this);
        mSpinner = (Spinner) findViewById(R.id.categorySelectorSpinner);
        mSpinner.setAdapter(mSpinnerAdapter);

        mCategoryName = (TextView) findViewById(R.id.categoryNameLabel);
    }

    public void showSpinner(View view) {
        mSpinner.performClick();
    }

    //------------------- Menu -------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_message, menu);
        MenuItem attachItem = menu.findItem(R.id.attachFile);
        MenuItem sendItem = menu.findItem(R.id.sendMessage);
        attachItem.setOnMenuItemClickListener(this);
        sendItem.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.attachFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, UPLOAD_RESULT_CODE);
                return true;
            case R.id.sendMessage:

                break;
        }
        return false;
    }

    @Override
    public <T extends SpinnerObj> void onCountryClick(T country, int position) {
        mCategoryName.setText(country.getName());
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
    public void replaceFragment(Fragment fragment) {

    }

    @Override
    public void onBackStackChanged() {

    }
}
