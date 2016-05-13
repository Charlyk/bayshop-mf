package com.softranger.bayshopmf.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.GalleryPagerAdapter;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent = getIntent();
        ArrayList<Integer> images = intent.getExtras().getIntegerArrayList("images");
        int position = intent.getExtras().getInt("position");
        ViewPager viewPager = (ViewPager) findViewById(R.id.galleryViewPager);
        GalleryPagerAdapter adapter = new GalleryPagerAdapter(this, images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    public void onBackButtonClick(View view) {
        onBackPressed();
    }
}
