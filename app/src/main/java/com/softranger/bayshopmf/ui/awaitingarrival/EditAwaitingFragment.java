package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.DeclarationListAdapter;
import com.softranger.bayshopmf.model.box.AwaitingArrivalDetails;
import com.softranger.bayshopmf.model.box.Product;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAwaitingFragment extends ParentFragment implements
        DeclarationListAdapter.OnActionButtonsClick {

    private static final String PRODUCT_ARG = "product argument";

    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private CustomTabsIntent mTabsIntent;
    private DeclarationListAdapter mAdapter;
    private ArrayList<Product> mProducts;

    @BindView(R.id.editAwaitingRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.editAwaitingTrackingNumInput)
    EditText mTrackingNumField;


    public EditAwaitingFragment() {
        // Required empty public constructor
    }

    public static EditAwaitingFragment newInstance(AwaitingArrivalDetails awaitingArrivalDetails) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, awaitingArrivalDetails);
        EditAwaitingFragment fragment = new EditAwaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_awaiting, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        mActivity = (MainActivity) getActivity();

        mProducts = new ArrayList<>();
        mAdapter = new DeclarationListAdapter(mProducts, true);
        mAdapter.setOnActionButtonsClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);

        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(mActivity.getResources().getColor(R.color.colorPrimary));
        mTabsIntent = tabsBuilder.build();

        return rootView;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.editAwaitingAddFieldButton)
    void addNewField() {
        mAdapter.addNewProductCard();
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.editAwaitingSaveButton)
    void saveParcelDetails() {
        mActivity.onBackPressed();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.edit_details);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.edit_awaiting_arrival;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mActivity.hideKeyboard();
    }

    @Override
    public void onDeleteClick(Product product, int position) {
        mAdapter.removeItem(position);
    }

    @Override
    public void onOpenUrl(String url, int position) {
        mTabsIntent.launchUrl(mActivity, Uri.parse(url));
    }
}
