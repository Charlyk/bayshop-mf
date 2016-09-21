package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.box.AwaitingArrival;
import com.softranger.bayshopmf.model.box.AwaitingArrivalDetails;
import com.softranger.bayshopmf.model.product.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAwaitingFragment extends ParentFragment implements View.OnClickListener {

    private static final String PRODUCT_ARG = "product argument";

    private EditText mNameInput, mTrackingInput, mUrlInput, mPriceInput;
    private Button mSaveButton;
    private static AwaitingArrivalDetails product;
    private MainActivity mActivity;
    private View mRootView;
    private CustomTabsIntent mTabsIntent;

    public EditAwaitingFragment() {
        // Required empty public constructor
    }

    public static EditAwaitingFragment newInstance(AwaitingArrivalDetails awaitingArrivalDetails) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, awaitingArrivalDetails);
        EditAwaitingFragment fragment = new EditAwaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_edit_awaiting, container, false);
        mActivity = (MainActivity) getActivity();
        product = getArguments().getParcelable(PRODUCT_ARG);
        bindViews(mRootView);

        mNameInput.setText(product.getTitle());
        mTrackingInput.setText(product.getTracking());
        mUrlInput.setText(product.getUrl());
        mPriceInput.setText(product.getPrice());

        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(mActivity.getResources().getColor(R.color.colorPrimary));
        mTabsIntent = tabsBuilder.build();

        return mRootView;
    }

    private void bindViews(View view) {
        mNameInput = (EditText) view.findViewById(R.id.editAwaitingNameInput);
        mTrackingInput = (EditText) view.findViewById(R.id.editAwaitingTrackingInput);
        mUrlInput = (EditText) view.findViewById(R.id.editAwaitingUrlInput);
        mPriceInput = (EditText) view.findViewById(R.id.editAwaitingPriceInput);

        mSaveButton = (Button) view.findViewById(R.id.editAwaitingSaveButton);
        mSaveButton.setOnClickListener(this);

        ImageButton openUrlBtn = (ImageButton) view.findViewById(R.id.openUrlArrivalBtn);
        openUrlBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String urlToProduct = String.valueOf(mUrlInput.getText());
        if (urlToProduct == null || urlToProduct.equals("")) {
            mUrlInput.setError("please specify a url for this item");
            return;
        }

        if (v.getId() == R.id.openUrlArrivalBtn) {
            mTabsIntent.launchUrl(mActivity, Uri.parse(urlToProduct));
        } else {
            String productName = String.valueOf(mNameInput.getText());
            if (productName == null || productName.equals("")) {
                mNameInput.setError("please specify a name for this item");
                return;
            }

            String trackingNumber = String.valueOf(mTrackingInput.getText());
            if (trackingNumber == null || trackingNumber.equals("")) {
                mTrackingInput.setError("please specify a track number for this item");
                return;
            }

            String price = String.valueOf(mPriceInput.getText());
            if (price == null || price.equals("")) {
                mPriceInput.setError("please specify a price for this item");
                return;
            }

            product.setTitle(productName);
            product.setTrackingNumber(trackingNumber);
            product.setProductUrl(urlToProduct);
            product.setPackagePrice(price);
            RequestBody body = new FormBody.Builder()
                    .add("storage", Constants.USA)
                    .add("tracking", product.getTracking())
                    .add("title", product.getTitle())
                    .add("url", product.getUrl())
                    .add("packagePrice", product.getPrice())
                    .build();
            ApiClient.getInstance().postRequest(body, Constants.Api.urlEditWaitingArrivalItem(String.valueOf(product.getId())), mHandler);
            mActivity.toggleLoadingProgress(true);
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
//        JSONObject data = response.getJSONObject("data");
//        product.setTitle(data.getString("packageName"));
//        product.set(Integer.parseInt(data.getString("id")));
//        product.setDeposit(data.getString("storage").toLowerCase());
//        product.setTrackingNumber(data.getString("tracking"));
//        product.setProductPrice(data.getString("packagePrice"));
//        product.setProductUrl(data.getString("url"));
//        product.setBarcode(data.getString("barCode"));

        Snackbar.make(mRootView, getString(R.string.saved_succesfuly), Snackbar.LENGTH_SHORT).show();
        Intent update = new Intent(AwaitingArrivalProductFragment.ACTION_UPDATE);
        mActivity.sendBroadcast(update);
        mActivity.onBackPressed();
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.edit_details);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.edit_awaiting_arrival;
    }
}
