package com.softranger.bayshopmf.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassFragment extends ParentFragment implements View.OnClickListener {

    private TextInputEditText mCurrentPassInput;
    private TextInputEditText mNewPassInput;
    private TextInputEditText mConfirmPassInput;
    private SettingsActivity mActivity;

    public ChangePassFragment() {
        // Required empty public constructor
    }

    public static ChangePassFragment newInstance() {

        Bundle args = new Bundle();

        ChangePassFragment fragment = new ChangePassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);
        mActivity = (SettingsActivity) getActivity();

        mCurrentPassInput = (TextInputEditText) view.findViewById(R.id.changePassWordCurrentPassInput);
        mNewPassInput = (TextInputEditText) view.findViewById(R.id.changePassWordNewPassInput);
        mConfirmPassInput = (TextInputEditText) view.findViewById(R.id.changePassWordConfirmPassInput);

        Button save = (Button) view.findViewById(R.id.changePassSaveButton);
        save.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        String currentPass = String.valueOf(mCurrentPassInput.getText());
        String newPass = String.valueOf(mNewPassInput.getText());
        String confirmPass = String.valueOf(mConfirmPassInput.getText());

        mActivity.onBackPressed();

        if (currentPass.length() < 6) {
            mCurrentPassInput.setError(getString(R.string.enter_valid_password));
            return;
        }

        if (newPass.length() < 6) {
            mNewPassInput.setError(getString(R.string.must_contain));
            return;
        }

        if (!confirmPass.equals(newPass)) {
            mConfirmPassInput.setError(getString(R.string.passwords_does_not_match));
            return;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        mActivity.onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.changeToolbarTitle(mActivity.getString(R.string.settings));
    }
}
