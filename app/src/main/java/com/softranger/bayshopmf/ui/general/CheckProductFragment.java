package com.softranger.bayshopmf.ui.general;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.instock.DetailsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckProductFragment extends Fragment implements View.OnClickListener {

    public static final String ACTION_CHECK_IN_PROCESSING = "ACTION CHECK IN PROCESSING";
    public static final String ACTION_CANCEL_CHECK_PRODUCT = "ACTION CANCEL CHECK PRODUCT";
    private static final String PRODUCT_ARG = "PRODUCT ARGUMENT";

    private EditText mCommentInput;
    private Button mLeaveComment;
    private MainActivity mActivity;

    public CheckProductFragment() {
        // Required empty public constructor
    }

    public static CheckProductFragment newInstance(Product product) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, product);
        CheckProductFragment fragment = new CheckProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_product, container, false);
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
            case R.id.check_product_confirmBtn: {
                Intent intent = new Intent(ACTION_CHECK_IN_PROCESSING);
                mActivity.sendBroadcast(intent);
                mActivity.onBackPressed();
                break;
            }
        }
    }
}
