package com.softranger.bayshopmf.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.MainActivity;

public class SplashActivity extends AppCompatActivity {

    public static SplashActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        instance = this;
        final ImageView logoImage = (ImageView) findViewById(R.id.logo_imageViewCircle);
        final ImageView lettersImage = (ImageView) findViewById(R.id.logo_imageViewLetters);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(logoImage, lettersImage);
            }
        }, 2000);
    }

    private void startActivity(ImageView imageView, ImageView lettersLogoImage) {
        Intent intent = new Intent(this, LoginActivity.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Pair<View, String> circleLogo = new Pair<View, String>(imageView, getString(R.string.transition_image_view));
//            ActivityOptionsCompat options;
//            if (lettersLogoImage != null) {
//                Pair<View, String> lettersLogo = new Pair<View, String>(lettersLogoImage, "lettersTransitionName");
//                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, circleLogo, lettersLogo);
//            } else {
//                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, getString(R.string.transition_image_view));
//            }
//            startActivity(intent, options.toBundle());
//        }
//        else {
            startActivity(intent);
//        }
    }
}
