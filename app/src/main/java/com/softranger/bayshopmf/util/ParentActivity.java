package com.softranger.bayshopmf.util;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
import com.softranger.bayshopmf.ui.general.ResultActivity;

import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

/**
 * Created by Eduard Albu on 7/1/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public abstract class ParentActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener,
        PullToRefreshLayout.PullToRefreshListener {

    public static SelectedFragment selectedFragment;

    public abstract void setToolbarTitle(String title);

    public abstract void addFragment(ParentFragment fragment, boolean showAnimation);

    public abstract void toggleLoadingProgress(boolean show);

    public abstract void replaceFragment(ParentFragment fragment);

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getFragmentManager().addOnBackStackChangedListener(this);
    }

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

    public void expandTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", 300);
        animation.setDuration(200).start();
    }

    public void collapseTextView(TextView tv, int numLines) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", numLines);
        animation.setDuration(200).start();
    }

    public void stopRereshing() {

    }

    public void showResultActivity(@NonNull String topTitle, @NonNull String secondTitle,
                                   @DrawableRes int image, @NonNull String descriptiom) {
        // build intent for result activity
        Intent showResult = new Intent(this, ResultActivity.class);
        showResult.putExtra(ResultActivity.TOP_TITLE, topTitle);
        showResult.putExtra(ResultActivity.SECOND_TITLE, secondTitle);
        showResult.putExtra(ResultActivity.IMAGE_ID, image);
        showResult.putExtra(ResultActivity.DESCRIPTION, descriptiom);
        // show result activity
        startActivity(showResult);
    }

    public enum SelectedFragment {
        in_stock(R.string.warehouse_usa),
        awaiting_arrival(R.string.awaiting_arrival),
        parcels(R.string.parcels),
        account_replenishment(R.string.account_replenishment),
        add_awaiting_parcel(R.string.add_awaiting_package),
        addresses_list(R.string.addresses_list),
        add_address(R.string.add_new_address),
        edit_awaiting_arrival(R.string.edit_details),
        edit_declaration(R.string.edit_declaration),
        in_stock_details(R.string.details),
        parcel_details(R.string.parcel_details),
        leave_feedback(R.string.leave_feedback),
        signature_and_location(R.string.signature_of_geolocation),
        return_to_seller(R.string.return_to_seller_s_address),
        check_product(R.string.check_product),
        additional_photos(R.string.additional_photo),
        autopacking(R.string.autopacking),
        change_password(R.string.change_password),
        notifications(R.string.notifications),
        regional(R.string.regional),
        settings(R.string.settings),
        user_data(R.string.user_data),
        forgot_password(R.string.forgot_password),
        check_declaration(R.string.declaration),
        confirmation(R.string.check_parcel_details),
        register_user(R.string.register),
        insurance(R.string.insurance),
        items_list(R.string.list_items),
        shipping_method(R.string.shipping_method),
        received(R.string.received),
        storage(R.string.storage),
        login_fragment(R.string.login),
        select_address(R.string.select_address),
        warehouse_address(R.string.warehouse_addresses);

        @StringRes
        private int mFragmentName;

        SelectedFragment(int fragmentName) {
            mFragmentName = fragmentName;
        }

        @StringRes
        public int fragmentName() {
            return mFragmentName;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

    }
}
