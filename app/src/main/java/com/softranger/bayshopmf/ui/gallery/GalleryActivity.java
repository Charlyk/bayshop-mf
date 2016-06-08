package com.softranger.bayshopmf.ui.gallery;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.GalleryPagerAdapter;
import com.softranger.bayshopmf.model.Photo;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent = getIntent();
        ArrayList<Photo> images = intent.getExtras().getParcelableArrayList("images");
        int position = intent.getExtras().getInt("position");
        ViewPager viewPager = (ViewPager) findViewById(R.id.galleryViewPager);
        viewPager.setOffscreenPageLimit(3);
        GalleryPagerAdapter adapter = new GalleryPagerAdapter(this, getSupportFragmentManager());
        for (int i = 0; i < images.size(); i++) {
            adapter.addFragment(GalleryImageFragment.newInstance(images.get(i)), (i + 1) + " from " + images.size());
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    public void onBackButtonClick(View view) {
        onBackPressed();
    }
}
