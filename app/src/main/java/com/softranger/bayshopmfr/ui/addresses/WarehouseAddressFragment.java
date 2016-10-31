package com.softranger.bayshopmfr.ui.addresses;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.WarehouseAddress;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarehouseAddressFragment extends ParentFragment implements View.OnClickListener {


    private static final String STORAGE = "storage argument";

    @BindView(R.id.addressesFullNameLabel)
    TextView mFullName;
    @BindView(R.id.addressesLineOneLabel)
    TextView mLineOne;
    @BindView(R.id.addressesLineTwoLabel)
    TextView mLineTwo;
    @BindView(R.id.addressesCityLabel)
    TextView mCity;
    @BindView(R.id.addressesStateLabel)
    TextView mState;
    @BindView(R.id.addressesPostalCodeLabel)
    TextView mPostalCode;
    @BindView(R.id.addressesCountryLabel)
    TextView mCountry;
    @BindView(R.id.addressesPhoneNumberLabel)
    TextView mPhone;
    @BindView(R.id.warningItemLabel)
    TextView mWarningLabel;
    @BindView(R.id.warehouseAddressLayout)
    LinearLayout mFragmentLayout;

    private WarehouseAddressesActivity mActivity;
    private Unbinder mUnbinder;
    private Call<ServerResponse<ArrayList<WarehouseAddress>>> mCall;

    private String mStorage;

    public WarehouseAddressFragment() {
        // Required empty public constructor
    }

    public static WarehouseAddressFragment newInstance(String storage) {
        Bundle args = new Bundle();
        args.putString(STORAGE, storage);
        WarehouseAddressFragment fragment = new WarehouseAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_warehouse_address, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (WarehouseAddressesActivity) getActivity();

        mWarningLabel.setText(R.string.click_on_field_to_copy);

        mFragmentLayout.setVisibility(View.GONE);

        mStorage = getArguments().getString(STORAGE);

        mCall = Application.apiInterface().getWarehouseAddress();
        mCall.enqueue(mResponseCallback);
        mActivity.toggleLoadingProgress(true);
        return view;
    }

    private ResponseCallback<ArrayList<WarehouseAddress>> mResponseCallback = new ResponseCallback<ArrayList<WarehouseAddress>>() {
        @Override
        public void onSuccess(ArrayList<WarehouseAddress> data) {
            if (data == null || data.size() <= 0) return;
            WarehouseAddress address = data.get(0);
            mFullName.setText(address.getFullName());
            mLineOne.setText(address.getAddressLine1());
            mLineTwo.setText(address.getAddressLine2());
            mCity.setText(address.getCity());
            mState.setText(address.getState());
            mPostalCode.setText(address.getPostalCode());
            mCountry.setText(address.getCountryTitle());
            mPhone.setText(address.getPhone());
            mFragmentLayout.setVisibility(View.VISIBLE);
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<WarehouseAddress>>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @OnClick({R.id.warehouseAddressFullName, R.id.warehouseAddressAddressLineOne, R.id.warehouseAddressAddressLineTwo,
            R.id.warehouseAddressCity, R.id.warehouseAddressState, R.id.warehouseAddressPostalCode, R.id.warehouseAddressCountry,
            R.id.warehouseAddressPhoneNumber})
    public void onClick(View v) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager)
                mActivity.getSystemService(Context.CLIPBOARD_SERVICE);

        String copiedText = "";

        switch (v.getId()) {
            case R.id.warehouseAddressFullName:
                copiedText = String.valueOf(mFullName.getText());
                break;
            case R.id.warehouseAddressAddressLineOne:
                copiedText = String.valueOf(mLineOne.getText());
                break;
            case R.id.warehouseAddressAddressLineTwo:
                copiedText = String.valueOf(mLineTwo.getText());
                break;
            case R.id.warehouseAddressCity:
                copiedText = String.valueOf(mCity.getText());
                break;
            case R.id.warehouseAddressState:
                copiedText = String.valueOf(mState.getText());
                break;
            case R.id.warehouseAddressPostalCode:
                copiedText = String.valueOf(mPostalCode.getText());
                break;
            case R.id.warehouseAddressCountry:
                copiedText = String.valueOf(mCountry.getText());
                break;
            case R.id.warehouseAddressPhoneNumber:
                copiedText = String.valueOf(mPhone.getText());
                break;
        }

        if (copiedText.length() > 0) {
            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("simple text", copiedText);
            // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip);

            Toast.makeText(mActivity, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.warehouse_addresses);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.warehouse_address;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
    }
}
