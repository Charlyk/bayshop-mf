package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Eduard Albu on 5/26/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class SecondStepAdapter extends RecyclerView.Adapter<SecondStepAdapter.ViewHolder> implements SectionIndexer {

    private ArrayList<Address> mAddresses;
    private OnAddressClickListener mOnAddressClickListener;

    public SecondStepAdapter(ArrayList<Address> addresses) {
        mAddresses = addresses;
        sortListByName();
    }

    public void sortListByName() {
        Collections.sort(mAddresses, new Comparator<Address>() {
            @Override
            public int compare(Address lhs, Address rhs) {
                return lhs.getClientName().compareTo(rhs.getClientName());
            }
        });
    }

    public void refreshList(ArrayList<Address> addresses) {
        mAddresses.clear();
        mAddresses.addAll(addresses);
        sortListByName();
        notifyDataSetChanged();
    }

    public void setOnAddressClickListener(OnAddressClickListener onAddressClickListener) {
        mOnAddressClickListener = onAddressClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_step_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mAddressObj = mAddresses.get(position);
        String addressBUilder = holder.mAddressObj.getStreet() + " " +
                holder.mAddressObj.getCity() + "\n" +
                holder.mAddressObj.getCountry() + "\n" +
                holder.mAddressObj.getPostalCode();
        holder.mAddress.setText(addressBUilder);
        holder.mClientName.setText(holder.mAddressObj.getClientName());
    }

    @Override
    public int getItemCount() {
        return mAddresses.size();
    }

    @Override
    public Object[] getSections() {
        return Constants.ALPHABET;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position == mAddresses.size()) {
            position = mAddresses.size() - 1;
        }
        char c = mAddresses.get(position).getClientName().charAt(0);
        int groupPosition = 0;
        for (int i = 0; i < Constants.ALPHABET.length; i++) {
            if (c == Constants.ALPHABET[i]) {
                groupPosition = i;
            }
        }

        return groupPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mClientName;
        final TextView mAddress;
        final Button mSelectButton;
        Address mAddressObj;
        public ViewHolder(View itemView) {
            super(itemView);
            mClientName = (TextView) itemView.findViewById(R.id.secondStepItemNameLabel);
            mAddress = (TextView) itemView.findViewById(R.id.secondStepAddressText);
            mSelectButton = (Button) itemView.findViewById(R.id.secondStepSelectBtn);
            mSelectButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnAddressClickListener != null) {
                mOnAddressClickListener.onAddressClick(mAddressObj, getAdapterPosition());
            }
        }
    }

    public interface OnAddressClickListener {
        void onAddressClick(Address address, int position);
    }
}
