package com.softranger.bayshopmf.ui.settings;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassFragment extends ParentFragment implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText mCurrentPassInput;
    private EditText mNewPassInput;
    private EditText mConfirmPassInput;

    private RelativeLayout mCurrentPassLayout;
    private RelativeLayout mNewPassLayout;
    private RelativeLayout mConfirmPassLayout;

    private SettingsActivity mActivity;
    private View mFocusIndicator;

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

        mCurrentPassLayout = (RelativeLayout) view.findViewById(R.id.currentPassLayout);
        mNewPassLayout = (RelativeLayout) view.findViewById(R.id.newPassLayout);
        mConfirmPassLayout = (RelativeLayout) view.findViewById(R.id.confirmPassLayout);

        mFocusIndicator = view.findViewById(R.id.changePassFocusIndicator);

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
    public String getFragmentTitle() {
        return getString(R.string.change_password);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.change_password;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.hideKeyboard();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.currentPassInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mCurrentPassLayout.getY()).setDuration(300).start();
                break;
            case R.id.newPassInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mNewPassLayout.getY()).setDuration(300).start();
                break;
            case R.id.confirmPassInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mConfirmPassLayout.getY()).setDuration(300).start();
                break;
        }
    }
}
