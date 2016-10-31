package com.softranger.bayshopmfr.ui.payment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.payment.History;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.ButterKnife;

public class PaymentDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Intent intent = getIntent();
        History history = intent.getExtras().getParcelable("history");

        if (history == null) {
            finish();
            return;
        }

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setNavigationOnClickListener((view) -> {
            onBackPressed();
        });

        TextView description = ButterKnife.findById(this, R.id.paymentDetailsDescriptionLabel);
        TextView totalAmount = ButterKnife.findById(this, R.id.paymentDetailsTotalAmountLabel);
        TextView dateTime = ButterKnife.findById(this, R.id.paymentDetailsDateTimeLabel);
        TextView amount = ButterKnife.findById(this, R.id.paymentDetailsAmountLabel);
        TextView transactionId = ButterKnife.findById(this, R.id.paymentDetailsTransactionIdLabel);

        description.setText(history.getComment());
        String total = history.getCurrency() + history.getTotalAmmount();
        String summ = history.getCurrency() + history.getSumm();
        totalAmount.setText(total);

        if (history.getTotalAmmount() < 0) {
            totalAmount.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            totalAmount.setTextColor(getResources().getColor(R.color.colorGreenAction));
        }

        SimpleDateFormat friendlyFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        dateTime.setText(friendlyFormat.format(history.getDate()));

        amount.setText(summ);
        if (history.getPaymentType() == History.PaymentType.minus) {
            amount.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            amount.setTextColor(getResources().getColor(R.color.colorGreenAction));
        }

        transactionId.setText(history.getTransactionId());
    }
}
