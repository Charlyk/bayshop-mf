package com.softranger.bayshopmf.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassFragment extends ParentFragment implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText mCurrentPassInput;
    private EditText mNewPassInput;
    private EditText mConfirmPassInput;
    private SettingsActivity mActivity;

    private View mCurrentIndicator, mNewIndicator, mConfirmIndicator;

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

        mCurrentPassInput = (EditText) view.findViewById(R.id.currentPassInput);
        mNewPassInput = (EditText) view.findViewById(R.id.newPassInput);
        mConfirmPassInput = (EditText) view.findViewById(R.id.confirmPassInput);

        mCurrentPassInput.setOnFocusChangeListener(this);
        mNewPassInput.setOnFocusChangeListener(this);
        mConfirmPassInput.setOnFocusChangeListener(this);

        mCurrentIndicator = view.findViewById(R.id.currentPassInputFocusIndicator);
        mNewIndicator = view.findViewById(R.id.newPassInputFocusIndicator);
        mConfirmIndicator = view.findViewById(R.id.confirmPassInputFocusIndicator);

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
        mActivity.setToolbarTitle(mActivity.getString(R.string.settings), true);
        mActivity.hideKeyboard();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            mCurrentIndicator.setVisibility(View.GONE);
            mNewIndicator.setVisibility(View.GONE);
            mConfirmIndicator.setVisibility(View.GONE);
        }
        switch (v.getId()) {
            case R.id.currentPassInput:
                mCurrentIndicator.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                break;
            case R.id.newPassInput:
                mNewIndicator.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                break;
            case R.id.confirmPassInput:
                mConfirmIndicator.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                break;
        }
    }
}
