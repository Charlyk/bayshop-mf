package com.softranger.bayshopmf.util;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;

/**
 * Created by Eduard Albu on 7/1/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public abstract class ParentActivity extends AppCompatActivity {

    public abstract void setToolbarTitle(String title, boolean showIcon);

    public abstract void addFragment(Fragment fragment, boolean showAnimation);

    public abstract void toggleLoadingProgress(boolean show);

    public abstract void replaceFragment(Fragment fragment);

    /**
     * Create an alert dialog with BayShop design
     *
     * @param title                         which will be shown in the dialog header
     * @param message                       will be shown in dialog body
     * @param imageResource                 will be shown at the left of title
     * @param positiveButtonText            text for right side button
     * @param onPositiveButtonClickListener click listener for positive button(can be null)
     * @param negativeButtonText            text for left side button
     * @param onNegativeButtonClickListener click listener for negative button(can be null)
     * @param color                         color for top dialog bar
     * @return an Alert Dialog with specified data to be shown on the screen
     */
    public AlertDialog getDialog(@NonNull String title, @NonNull String message, @DrawableRes int imageResource,
                                 @Nullable String positiveButtonText,
                                 @Nullable View.OnClickListener onPositiveButtonClickListener,
                                 @Nullable String negativeButtonText, @Nullable View.OnClickListener onNegativeButtonClickListener,
                                 @ColorRes int color) {
        // inflate dialog layout and bind all views
        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null, false);
        LinearLayout topDialogBar = (LinearLayout) dialogLayout.findViewById(R.id.topDialogBarLayout);
        ImageView dialogImage = (ImageView) dialogLayout.findViewById(R.id.alertDialogImageLabel);
        TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.alertDialogTitleLabel);
        TextView dialogMessage = (TextView) dialogLayout.findViewById(R.id.alertDialogMessageLabel);
        Button negativeButton = (Button) dialogLayout.findViewById(R.id.alertDialogNegativeButton);
        Button positiveButton = (Button) dialogLayout.findViewById(R.id.alertDialogPositiveButton);

        // set top bar background
        if (color == 0) color = R.color.colorAccent;
        topDialogBar.setBackgroundColor(getResources().getColor(color));

        // set not null data, as text and image for dialog
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogImage.setImageResource(imageResource);

        // check and set buttons either text and listener or visibility to GONE
        if (positiveButtonText != null) {
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(onPositiveButtonClickListener);
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        if (negativeButtonText != null) {
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(onNegativeButtonClickListener);
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        // Create the dialog with the given layout and return it
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setView(dialogLayout);
        return dialogBuilder.create();
    }

    public AlertDialog getEditDialog(@NonNull String title, @NonNull String message, @DrawableRes int imageResource,
                                     @Nullable String positiveButtonText,
                                     @Nullable String negativeButtonText, int inputType,
                                     @Nullable final MainActivity.OnEditDialogClickListener onEditDialogClickListener) {
        // inflate dialog layout and bind all views
        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.edit_text_dialog, null, false);
        ImageView dialogImage = (ImageView) dialogLayout.findViewById(R.id.editDialogImageLabel);
        TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.editDialogTitleLabel);
        final EditText dialogMessage = (EditText) dialogLayout.findViewById(R.id.editDialogInput);
        Button negativeButton = (Button) dialogLayout.findViewById(R.id.editDialogNegativeButton);
        Button positiveButton = (Button) dialogLayout.findViewById(R.id.editDialogPositiveButton);

        // set not null data, as text and image for dialog
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogImage.setImageResource(imageResource);

        // check and set buttons either text and listener or visibility to GONE
        if (positiveButtonText != null) {
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditDialogClickListener != null) {
                        onEditDialogClickListener.onPositiveClick(String.valueOf(dialogMessage.getText()));
                    }
                }
            });
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        if (negativeButtonText != null) {
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditDialogClickListener != null) {
                        onEditDialogClickListener.onNegativeClick();
                    }
                }
            });
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        // Create the dialog with the given layout and return it
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setView(dialogLayout);
        return dialogBuilder.create();
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
