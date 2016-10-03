package com.softranger.bayshopmf.ui.addresses;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.softranger.bayshopmf.R;

public class SearchAddressActivity extends AppCompatActivity {

    public static final String ADDRESSES = "ADDRESSES TO SEARCH IN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
    }
}
