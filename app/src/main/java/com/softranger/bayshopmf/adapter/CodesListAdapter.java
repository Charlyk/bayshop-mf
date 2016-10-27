package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.address.CountryCode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eduard Albu on 9/30/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CodesListAdapter extends RecyclerView.Adapter<CodesListAdapter.ViewHolder> {

    private ArrayList<CountryCode> mCountryCodes;
    private OnCodeClickListener mOnCodeClickListener;
    private Context mContext;

    public CodesListAdapter(Context context, ArrayList<CountryCode> countryCodes) {
        mCountryCodes = countryCodes;
        mContext = context;
    }

    public void setOnCodeClickListener(OnCodeClickListener onCodeClickListener) {
        mOnCodeClickListener = onCodeClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_code_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCountryCode = mCountryCodes.get(position);

        holder.mCodeLabel.setText(holder.mCountryCode.getCode());
        holder.mCountrylabel.setText(holder.mCountryCode.getName());

        Picasso.with(mContext).load(holder.mCountryCode.getImageUrl()).into(holder.mFlagImage);
    }

    @Override
    public int getItemCount() {
        return mCountryCodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.spinnerFlagLabel) ImageView mFlagImage;
        @BindView(R.id.spinnerCodeLabel) TextView mCodeLabel;
        @BindView(R.id.spinnerCountryLabel) TextView mCountrylabel;
        CountryCode mCountryCode;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnCodeClickListener != null) {
                mOnCodeClickListener.onCodeClicked(mCountryCode, getAdapterPosition());
            }
        }
    }

    public interface OnCodeClickListener {
        void onCodeClicked(CountryCode countryCode, int position);
    }
}
