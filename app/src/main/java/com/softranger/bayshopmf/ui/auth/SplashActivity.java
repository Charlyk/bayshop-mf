package com.softranger.bayshopmf.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(logoImage);

            }
        }, 2000);
    }

    private void startActivity(ImageView imageView) {
        Intent intent = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, imageView, getString(R.string.transition_image_view));
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
