package com.softranger.bayshopmfr.ui.help;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Eduard Albu on 11/8/16, 11, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class HelpDialog extends DialogFragment {

    private Unbinder mUnbinder;

    private String mContentText;

    int style = DialogFragment.STYLE_NO_TITLE;
    int theme = R.style.MyDialog;

    @BindView(R.id.helpDialogToolbar)
    Toolbar mToolbar;
    @BindView(R.id.helpDialogToolbarTitle)
    TextView mToolbarTitle;
    @BindView(R.id.helpDialogContentLabel)
    TextView mContentLabel;

    public static HelpDialog newInstance() {
        Bundle args = new Bundle();
        HelpDialog fragment = new HelpDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_dialog_layout, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mToolbar.setNavigationOnClickListener(v -> dismiss());

        if (mContentText != null) mContentLabel.setText(mContentText);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public static void showDialog(Activity activity) {
        HelpDialog dialog = HelpDialog.newInstance();
        dialog.show(activity.getFragmentManager(), dialog.getClass().getSimpleName());
    }

    public static void showDialog(Activity activity, String contentText) {
        HelpDialog dialog = HelpDialog.newInstance();
        dialog.setContentText(contentText);
        dialog.show(activity.getFragmentManager(), dialog.getClass().getSimpleName());
    }

    public void setContentText(String contentText) {
        mContentText = contentText;
    }
}
