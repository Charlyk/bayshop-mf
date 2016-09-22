package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.DeclarationListAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.Declaration;
import com.softranger.bayshopmf.model.box.InStockDetailed;
import com.softranger.bayshopmf.model.box.Product;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclarationFragment extends ParentFragment implements DeclarationListAdapter.OnActionButtonsClick {

    private static final String IN_STOCK_ARG = "in stock argument";

    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private DeclarationListAdapter mDeclarationAdapter;
    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    private InStockDetailed mInStockDetailed;
    private CustomTabsIntent mTabsIntent;
    private Declaration mDeclaration;
    private Call<ServerResponse<Declaration>> mDeclarationCall;
    private Call<ServerResponse> mSaveCall;

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
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mInStockDetailed = getArguments().getParcelable(IN_STOCK_ARG);

        // create tabs intent for products url
        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(mActivity.getResources().getColor(R.color.colorPrimary));
        mTabsIntent = tabsBuilder.build();

        // get declaration from server
        mDeclarationCall = Application.apiInterface().getInStockItemDeclaration(Application.currentToken, mInStockDetailed.getId());
        mActivity.toggleLoadingProgress(true);
        mDeclarationCall.enqueue(mDeclarationResponseCallback);

        return view;
    }

    @Override
    public void onAddFieldsClick() {
        mDeclarationAdapter.addNewProductCard();
        mRecyclerView.smoothScrollToPosition(mDeclarationAdapter.getItemCount() - 1);
    }

    @Override
    public void onSaveItemsClick(Declaration declaration, ArrayList<Product> products) {
        JSONArray jsonArray = new JSONArray();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        for (Product product : products) {
            // check if all data was specified
            int productIndex = products.indexOf(product);
            if (productIndex < (products.size() - 1)) {
                if (!isAllDataProvided(product)) {
                    if (!product.getTitle().equals(""))
                        Snackbar.make(mRecyclerView, getString(R.string.specify_all_data) + " "
                                + product.getTitle(), Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(mRecyclerView, getString(R.string.specify_all_data) + " item "
                                + (products.indexOf(product) + 1), Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
            // create a json array for server
            try {
                String object = ow.writeValueAsString(product);
                JSONObject jsonObject = new JSONObject(object);
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (declaration.getTitle().equals("")) {
            Snackbar.make(mRecyclerView, getString(R.string.please_enter_description), Snackbar.LENGTH_SHORT).show();
            return;
        }
        mSaveCall = Application.apiInterface().saveInStockItemDeclaration(Application.currentToken,
                mInStockDetailed.getId(), declaration.getTitle(), jsonArray.toString());
        mSaveCall.enqueue(mSaveCallback);
    }

    private ResponseCallback<Declaration> mDeclarationResponseCallback = new ResponseCallback<Declaration>() {
        @Override
        public void onSuccess(Declaration data) {
            mDeclaration = data;
            mDeclarationAdapter = new DeclarationListAdapter(mDeclaration, mInStockDetailed.getDeclarationFilled() != 0);
            mDeclarationAdapter.setOnActionButtonsClickListener(DeclarationFragment.this);
            mRecyclerView.setAdapter(mDeclarationAdapter);
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            mActivity.toggleLoadingProgress(false);
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call<ServerResponse<Declaration>> call, Throwable t) {
            mActivity.toggleLoadingProgress(false);
        }
    };

    private Callback<ServerResponse> mSaveCallback = new Callback<ServerResponse>() {
        @Override
        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
            if (response.body() != null) {
                if (response.body().getMessage().equalsIgnoreCase(Constants.ApiResponse.OK_MESSAGE)) {
                    Intent update = new Intent(AddAwaitingFragment.ACTION_ITEM_ADDED);
                    mActivity.sendBroadcast(update);
                    mActivity.onBackPressed();
                    mActivity.hideKeyboard();
                    Snackbar.make(mRecyclerView, getString(R.string.declaration_saved), Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(mActivity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                ServerResponse serverResponse;
                try {
                    serverResponse = new ObjectMapper().readValue(response.errorBody().string(), ServerResponse.class);
                    Toast.makeText(mActivity, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(Call<ServerResponse> call, Throwable t) {
            // TODO: 9/22/16 handle error
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public void onDeleteClick(int position) {
        mActivity.hideKeyboard();
    }

    @Override
    public void onOpenUrl(String url, int position) {
        mTabsIntent.launchUrl(mActivity, Uri.parse(url));
    }

    private boolean isAllDataProvided(Product product) {
        return !product.getTitle().equals("") && !product.getUrl().equals("")
                && !product.getPrice().equals("") && !product.getQuantity().equals("")
                && !product.getQuantity().equals("0") && !product.getPrice().equals("0");
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.edit_declaration);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.edit_declaration;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSaveCall != null) mSaveCall.cancel();
        if (mDeclarationCall != null) mDeclarationCall.cancel();
        mUnbinder.unbind();
    }
}
