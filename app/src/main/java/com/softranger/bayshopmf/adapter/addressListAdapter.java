package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eduard Albu on 5/26/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> implements SectionTitleProvider {

    private ArrayList<Address> mAddresses;
    private OnAddressClickListener mOnAddressClickListener;

    public AddressListAdapter(ArrayList<Address> addresses) {
        mAddresses = addresses;
        sortListByName();
    }

    private void sortListByName() {
        Collections.sort(mAddresses, (lhs, rhs) -> lhs.getClientName().compareTo(rhs.getClientName()));
    }

    public void refreshList(ArrayList<Address> addresses) {
        mAddresses.clear();
        mAddresses.addAll(addresses);
        sortListByName();
        notifyDataSetChanged();
    }

    public void replaceList(ArrayList<Address> addresses) {
        mAddresses = addresses;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mAddresses.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnAddressClickListener(OnAddressClickListener onAddressClickListener) {
        mOnAddressClickListener = onAddressClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mAddressObj = mAddresses.get(position);
        holder.mStreet.setText(holder.mAddressObj.getStreet());
        holder.mPhoneNumber.setText(holder.mAddressObj.getPhoneNumber());
        holder.mCity.setText(holder.mAddressObj.getCity());
        holder.mCountry.setText(holder.mAddressObj.getCountry());
        holder.mPostalCode.setText(holder.mAddressObj.getPostalCode());
        holder.mClientName.setText(holder.mAddressObj.getClientName());
    }

    @Override
    public int getItemCount() {
        return mAddresses.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return mAddresses.get(position).getClientName().substring(0, 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AddressSpinnerAdapter.OnItemClickListener {

        @BindView(R.id.addressItemNameLabel) TextView mClientName;
        @BindView(R.id.addressStreetLabel) TextView mStreet;
        @BindView(R.id.addressPhoneNumberLabel) TextView mPhoneNumber;
        @BindView(R.id.addressCityLabel) TextView mCity;
        @BindView(R.id.addressCountryLabel) TextView mCountry;
        @BindView(R.id.addressPostalCodeLabel) TextView mPostalCode;
        @BindView(R.id.addressAddToFavoritesButton) ImageButton mAddToFavorite;
        @BindView(R.id.addressItemSpinner)
        Spinner mSpinner;

        final Address.AddressAction[] mActions = {
                new Address.AddressAction(R.string.edit_address, R.mipmap.ic_edit_30dp),
                new Address.AddressAction(R.string.delete, R.mipmap.ic_delete_30dp)
        };

        Address mAddressObj;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            AddressSpinnerAdapter adapter = new AddressSpinnerAdapter(Application.getInstance(),
                    R.layout.spinner_list_item, mActions);
            adapter.setOnItemClickListener(this);

            mSpinner.setAdapter(adapter);
        }

        @OnClick(R.id.addressEditButton)
        void editAddress() {
            mSpinner.performClick();
        }

        @OnClick(R.id.addressAddToFavoritesButton)
        void addAddressToFavorites() {
            if (mOnAddressClickListener != null) {
                mOnAddressClickListener.onAddToFavoritesClick(mAddressObj, getAdapterPosition(), mAddToFavorite);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnAddressClickListener != null) {
                mOnAddressClickListener.onSelectAddressClick(mAddressObj, getAdapterPosition());
            }
        }

        @Override
        public void onItemClicked(int position) {
            if (mOnAddressClickListener == null) return;
            switch (position) {
                case 0:
                    mOnAddressClickListener.onEditAddressClick(mAddressObj, getAdapterPosition());
                    break;
                case 1:
                    mOnAddressClickListener.onDeleteAddressClick(mAddressObj, getAdapterPosition());
                    break;
            }
        }
    }

    public interface OnAddressClickListener {
        void onSelectAddressClick(Address address, int position);

        void onAddToFavoritesClick(Address address, int position, ImageButton button);

        void onEditAddressClick(Address address, int position);

        void onDeleteAddressClick(Address address, int position);
    }

    public enum ButtonType {
        none, select, delete
    }
}
