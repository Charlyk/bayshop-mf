package com.softranger.bayshopmf.ui.addresses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.AddressListAdapter;
import com.softranger.bayshopmf.model.address.Address;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAddressActivity extends AppCompatActivity implements AddressListAdapter.OnAddressClickListener, TextWatcher {

    public static final String ADDRESSES = "ADDRESSES TO SEARCH IN";

    private AddressListAdapter mAdapter;
    private ArrayList<Address> mSearchResults;
    private ArrayList<Address> mAddresses;
    @BindView(R.id.searchAddressRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_title)
    EditText mSearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (intent != null && intent.hasExtra(ADDRESSES)) {
            mAddresses = intent.getExtras().getParcelableArrayList(ADDRESSES);
            if (mAddresses == null || mAddresses.size() <= 0) {
                finish();
                return;
            }
            mSearchResults = new ArrayList<>();
            mAdapter = new AddressListAdapter(mAddresses);
            mAdapter.setOnAddressClickListener(this);
            mAdapter.setShowOptions(false);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mSearchInput.addTextChangedListener(this);
        } else {
            finish();
        }
    }

    @Override
    public void onSelectAddressClick(Address address, int position) {
        Intent sendAddress = new Intent(AddressesListFragment.ACTION_ADDRESS_SELECT);
        sendAddress.putExtra("address", address);
        sendBroadcast(sendAddress);
        finish();
    }

    @Override
    public void onAddToFavoritesClick(Address address, int position, ImageButton button) {
        // for now we just change button icon
        if (address.isInFavorites()) {
            button.setImageResource(R.mipmap.ic_star_silver_24dpi);
            address.setInFavorites(false);
        } else {
            button.setImageResource(R.mipmap.ic_favorit_24dp);
            address.setInFavorites(true);
        }
    }

    @Override
    public void onEditAddressClick(Address address, int position) {
        Intent editAddress = new Intent(this, EditAddressActivity.class);
        editAddress.putExtra(EditAddressActivity.ADDRESS_ID_EXTRA, address.getId());
        startActivity(editAddress);
    }

    @Override
    public void onDeleteAddressClick(Address address, int position) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mSearchResults.clear();
        if (charSequence.length() <= 0) {
            mAdapter.replaceList(mAddresses);
            return;
        }

        String newText = charSequence.toString();
        for (Address address : mAddresses) {
            String name = address.getClientName().toLowerCase();
            if (name.contains(newText.toLowerCase())) {
                mSearchResults.add(address);
            }
        }

        mAdapter.replaceList(mSearchResults);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
