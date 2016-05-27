package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoNeedInsuranceFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;

    public NoNeedInsuranceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_insurance, container, false);
        mActivity = (MainActivity) getActivity();
        Button select = (Button) view.findViewById(R.id.noInsuranceSelectButton);
        select.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        View deleteDialog = LayoutInflater.from(mActivity).inflate(R.layout.delete_dialog, null, false);
        TextView message = (TextView) deleteDialog.findViewById(R.id.dialog_message_text);
        message.setText(getString(R.string.message_example));
        Button cancel = (Button) deleteDialog.findViewById(R.id.dialog_cancel_buttonn);
        Button delete = (Button) deleteDialog.findViewById(R.id.dialog_delete_button);
        delete.setText(getString(R.string.i_am_sure));
        final AlertDialog dialog = new AlertDialog.Builder(mActivity).setView(deleteDialog).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.addFragment(new ConfirmationFragment(), true);
                dialog.dismiss();
            }
        });
    }
}
