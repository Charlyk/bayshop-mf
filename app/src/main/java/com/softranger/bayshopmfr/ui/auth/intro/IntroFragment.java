package com.softranger.bayshopmfr.ui.auth.intro;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragment extends Fragment {

    private static final String IMAGE = "com.softranger.bayshopmf.ui.auth.intro.IMAGE";
    private static final String TITLE = "com.softranger.bayshopmf.ui.auth.intro.TITLE";
    private static final String MESSAGE = "com.softranger.bayshopmf.ui.auth.intro.MESSAGE";
    private static final String COLOR = "com.softranger.bayshopmf.ui.auth.intro.COLOR";

    @BindView(R.id.introFragmentImageView)
    ImageView mImageView;
    @BindView(R.id.introFragmentTitleLabel)
    TextView mTitle;
    @BindView(R.id.introFragmentDescriptionLabel)
    TextView mDescription;
    @BindView(R.id.introFragmentLayout)
    RelativeLayout mRelativeLayout;

    private Unbinder mUnbinder;
    private View.OnClickListener mOnClickListener;

    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance(@DrawableRes int imageId, String title, String message, @ColorRes int color) {
        Bundle args = new Bundle();
        args.putInt(IMAGE, imageId);
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putInt(COLOR, color);
        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof View.OnClickListener) {
            mOnClickListener = (View.OnClickListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        int imageId = getArguments().getInt(IMAGE);
        String title = getArguments().getString(TITLE);
        String message = getArguments().getString(MESSAGE);
        int color = getArguments().getInt(COLOR);

        mImageView.setImageResource(imageId);
        mTitle.setText(title);
        mDescription.setText(message);
        mRelativeLayout.setBackgroundColor(getResources().getColor(color));

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
