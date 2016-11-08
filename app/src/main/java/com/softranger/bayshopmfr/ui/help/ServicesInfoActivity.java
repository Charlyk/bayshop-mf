package com.softranger.bayshopmfr.ui.help;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;

import butterknife.ButterKnife;

public class ServicesInfoActivity extends AppCompatActivity {

    public static final String TITLE = "com.softranger.bayshopmfr.ui.help.TITLE";
    public static final String CONTENT_TEXT = "com.softranger.bayshopmfr.ui.help.CONTENT_TEXT";
    public static final String IMAGE_ID = "com.softranger.bayshopmfr.ui.help.IMAGE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_info);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.servicesInfoToolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        TextView content = ButterKnife.findById(this, R.id.servicesContentText);
        ImageView imageView = ButterKnife.findById(this, R.id.servicesImageView);
        TextView title = ButterKnife.findById(this, R.id.servicesInfoToolbarTitle);

        Intent intent = getIntent();

        if (intent != null) {
            String text = intent.getExtras().getString(CONTENT_TEXT);
            int imageId = intent.getExtras().getInt(IMAGE_ID);
            String titleText = intent.getExtras().getString(TITLE);

            content.setText(text);
            imageView.setImageResource(imageId);
            title.setText(titleText);
        }
    }
}
