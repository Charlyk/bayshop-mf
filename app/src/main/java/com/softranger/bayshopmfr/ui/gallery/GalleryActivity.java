package com.softranger.bayshopmfr.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.GalleryPagerAdapter;
import com.softranger.bayshopmfr.model.product.Photo;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.util.ArrayList;

public class GalleryActivity extends ParentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent = getIntent();
        ArrayList<Photo> images = intent.getExtras().getParcelableArrayList("images");
        int position = intent.getExtras().getInt("position");
        ViewPager viewPager = (ViewPager) findViewById(R.id.galleryViewPager);
        viewPager.setOffscreenPageLimit(3);
        GalleryPagerAdapter adapter = new GalleryPagerAdapter(this, getSupportFragmentManager());
        String from = getString(R.string.from);
        for (int i = 0; i < images.size(); i++) {
            adapter.addFragment(GalleryImageFragment.newInstance(images.get(i)), (i + 1) + " " + from + " " + images.size());
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    public void onBackButtonClick(View view) {
        onBackPressed();
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
    public void onBackStackChanged() {

    }
}
