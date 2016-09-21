package com.softranger.bayshopmf.ui.payment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.payment.History;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailsFragment extends ParentFragment {

    private static final String HISTORY_ARG = "history argument";

    public PaymentDetailsFragment() {
        // Required empty public constructor
    }

    public static PaymentDetailsFragment newInstance(History history) {

        Bundle args = new Bundle();
        args.putParcelable(HISTORY_ARG, history);
        PaymentDetailsFragment fragment = new PaymentDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_details, container, false);
        History history = getArguments().getParcelable(HISTORY_ARG);
        TextView description = (TextView) view.findViewById(R.id.paymentDetailsDescriptionLabel);
        TextView totalAmount = (TextView) view.findViewById(R.id.paymentDetailsTotalAmountLabel);
        TextView dateTime = (TextView) view.findViewById(R.id.paymentDetailsDateTimeLabel);
        TextView amount = (TextView) view.findViewById(R.id.paymentDetailsAmountLabel);
        TextView transactionId = (TextView) view.findViewById(R.id.paymentDetailsTransactionIdLabel);

        description.setText(history.getComment());
        String total = history.getCurrency() + history.getTotalAmmount();
        String summ = history.getCurrency() + history.getSumm();
        totalAmount.setText(total);

        if (history.getTotalAmmount() < 0) {
            totalAmount.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            totalAmount.setTextColor(getResources().getColor(R.color.colorGreenAction));
        }

        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat friendlyFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        try {
            date = serverFormat.parse(history.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dateTime.setText(friendlyFormat.format(date));
        }

        amount.setText(summ);
        if (history.getPaymentType() == History.PaymentType.minus) {
            amount.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            amount.setTextColor(getResources().getColor(R.color.colorGreenAction));
        }

        transactionId.setText(history.getTransactionId());


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
    public MainActivity.SelectedFragment getSelectedFragment() {
        return null;
    }
}
