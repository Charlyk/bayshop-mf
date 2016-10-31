package com.softranger.bayshopmfr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.address.Country;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eduard Albu on 9/30/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CountriesDialogAdapter extends RecyclerView.Adapter<CountriesDialogAdapter.ViewHolder> {

    private ArrayList<Country> mCountries;
    private OnCoutryClickListener mOnCoutryClickListener;

    public CountriesDialogAdapter(ArrayList<Country> countries) {
        mCountries = countries;
    }

    public void setOnCoutryClickListener(OnCoutryClickListener onCoutryClickListener) {
        mOnCoutryClickListener = onCoutryClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.countries_dialog_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCountry = mCountries.get(position);
        holder.mNameLabel.setText(holder.mCountry.getName());
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.countryItemNameLabel) TextView mNameLabel;
        Country mCountry;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnCoutryClickListener != null) {
                mOnCoutryClickListener.onCountryClicked(mCountry, getAdapterPosition());
            }
        }
    }

    public interface OnCoutryClickListener {
        void onCountryClicked(Country country, int position);
    }
}
