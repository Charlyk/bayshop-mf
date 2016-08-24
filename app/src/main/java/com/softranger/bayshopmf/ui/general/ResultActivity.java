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

    public static final String TOP_TITLE = "topTitle";
    public static final String SECOND_TITLE = "secondTitle";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_ID = "imageId";

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

        if (intent.hasExtra(TOP_TITLE) && intent.hasExtra(SECOND_TITLE)
                && intent.hasExtra(DESCRIPTION) && intent.hasExtra(IMAGE_ID)) {
            mTopTitle = intent.getExtras().getString(TOP_TITLE);
            mSecondTitle = intent.getExtras().getString(SECOND_TITLE);
            mDescription = intent.getExtras().getString(DESCRIPTION);
            mImageId = intent.getExtras().getInt(IMAGE_ID);
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
