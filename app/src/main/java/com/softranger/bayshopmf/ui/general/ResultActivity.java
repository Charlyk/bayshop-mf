package com.softranger.bayshopmf.ui.general;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private String mTopTitle;
    private String mSecondTitle;
    private String mDescription;
    @DrawableRes private int mImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        if (intent == null) {
            finish();
            return;
        }

        if (intent.hasExtra("topTitle") && intent.hasExtra("secondTitle")
                && intent.hasExtra("description") && intent.hasExtra("imageId")) {
            mTopTitle = intent.getExtras().getString("topTitle");
            mSecondTitle = intent.getExtras().getString("secondTitle");
            mDescription = intent.getExtras().getString("description");
            mImageId = intent.getExtras().getInt("imageId");
        } else {
            finish();
        }

        Button button = (Button) findViewById(R.id.registerResultDoneButton);
        button.setOnClickListener(this);
        TextView topTitle = (TextView) findViewById(R.id.resultFragmentTopTitle);
        TextView secondTitle = (TextView) findViewById(R.id.resultFragmentSecondTitle);
        TextView description = (TextView) findViewById(R.id.resultFragmentDescription);
        ImageView imageView = (ImageView) findViewById(R.id.resultFragmentImage);

        topTitle.setText(mTopTitle);
        secondTitle.setText(mSecondTitle);
        description.setText(mDescription);
        imageView.setImageResource(mImageId);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
