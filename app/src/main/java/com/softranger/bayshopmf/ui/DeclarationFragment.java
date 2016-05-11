package com.softranger.bayshopmf.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.DeclarationListAdapter;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.Product;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclarationFragment extends Fragment implements DeclarationListAdapter.OnActionButtonsClick {

    private static final String IN_STOCK_ARG = "in stock argument";

    private MainActivity mActivity;
    private DeclarationListAdapter mDeclarationAdapter;
    private RecyclerView mRecyclerView;

    public DeclarationFragment() {
        // Required empty public constructor
    }

    public static DeclarationFragment newInstance(InStockDetailed inStockDetailed) {
        Bundle args = new Bundle();
        args.putParcelable(IN_STOCK_ARG, inStockDetailed);
        DeclarationFragment fragment = new DeclarationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_declaration, container, false);
        mActivity = (MainActivity) getActivity();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.declarationItemsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        InStockDetailed is = getArguments().getParcelable(IN_STOCK_ARG);
        mDeclarationAdapter = new DeclarationListAdapter(is);
        mDeclarationAdapter.setOnActionButtonsClickListener(this);
        mRecyclerView.setAdapter(mDeclarationAdapter);
        return view;
    }

    @Override
    public void onAddFieldsClick() {
        mDeclarationAdapter.addNewProductCard();
        mRecyclerView.smoothScrollToPosition(mDeclarationAdapter.getItemCount() - 1);
    }

    @Override
    public void onSaveItemsClick(ArrayList<Product> products) {
        mActivity.onBackPressed();
        Snackbar.make(mRecyclerView, "Saved " + products.size() + " products", Snackbar.LENGTH_SHORT).show();
    }
}
