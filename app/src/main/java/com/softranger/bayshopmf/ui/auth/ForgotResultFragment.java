package com.softranger.bayshopmf.ui.auth;


import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotResultFragment extends ParentFragment {

    private View.OnClickListener mOnClickListener;
    private String mTopTitle;
    private String mSecondTitle;
    private String mDescription;

    @DrawableRes
    private int mImageId;

    public ForgotResultFragment() {
        // Required empty public constructor
    }

    public static ForgotResultFragment newInstance(String topTitle, @DrawableRes int image,
                                                   String secondTitle, String description,
                                                   View.OnClickListener onClickListener)
    {
        Bundle args = new Bundle();
        ForgotResultFragment fragment = new ForgotResultFragment();
        fragment.setArguments(args);
        fragment.mOnClickListener = onClickListener;
        fragment.mTopTitle = topTitle;
        fragment.mImageId = image;
        fragment.mSecondTitle = secondTitle;
        fragment.mDescription = description;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_result, container, false);
        Button button = (Button) view.findViewById(R.id.registerResultDoneButton);
        button.setOnClickListener(mOnClickListener);
        TextView topTitle = (TextView) view.findViewById(R.id.resultFragmentTopTitle);
        TextView secondTitle = (TextView) view.findViewById(R.id.resultFragmentSecondTitle);
        TextView description = (TextView) view.findViewById(R.id.resultFragmentDescription);
        ImageView imageView = (ImageView) view.findViewById(R.id.resultFragmentImage);
        topTitle.setText(mTopTitle);
        secondTitle.setText(mSecondTitle);
        description.setText(mDescription);
        imageView.setImageResource(mImageId);
        return view;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {

    }

    @Override
    public String getFragmentTitle() {
        return null;
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return null;
    }
}
