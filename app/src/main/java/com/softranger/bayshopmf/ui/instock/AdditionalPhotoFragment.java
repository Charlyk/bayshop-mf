package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalPhotoFragment extends Fragment implements View.OnClickListener {

    private EditText mCommentInput;
    private Button mLeaveComment;
    private MainActivity mActivity;

    public AdditionalPhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_additional_photo, container, false);
        mActivity = (MainActivity) getActivity();
        mCommentInput = (EditText) view.findViewById(R.id.check_product_commentInput);
        mLeaveComment = (Button) view.findViewById(R.id.check_product_leaveCommentBtn);
        Button confirm = (Button) view.findViewById(R.id.check_product_confirmBtn);
        mLeaveComment.setOnClickListener(this);
        confirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_product_leaveCommentBtn:
                if (mCommentInput.getVisibility() == View.GONE) {
                    mCommentInput.setVisibility(View.VISIBLE);
                    mLeaveComment.setText("Hide comment");
                } else {
                    mCommentInput.setVisibility(View.GONE);
                    mLeaveComment.setText("Leave comment");
                }
                break;
            case R.id.check_product_confirmBtn:
                Intent intent = new Intent(DetailsFragment.ACTION_PHOTO_IN_PROCESSING);
                mActivity.sendBroadcast(intent);
                mActivity.onBackPressed();
                break;
        }
    }
}
