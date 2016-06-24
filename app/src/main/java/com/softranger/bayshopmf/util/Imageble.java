package com.softranger.bayshopmf.util;

import android.graphics.Bitmap;

/**
 * Created by macbook on 6/24/16.
 */
public interface Imageble {
    void setImage(Bitmap bitmap);
    Bitmap getImage();
    String getImageUrl();
}
