package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.PackageDetailsAdapter;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckDeclarationFragment extends ParentFragment implements View.OnClickListener,
        PackageDetailsAdapter.OnEditClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String IN_FORMING_ARG = "in forming object argument";
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private InForming mInForming;
    private ArrayList<Product> mProducts;
    private ArrayList<InStockDetailed> mInStock;
    private PackageDetailsAdapter mAdapter;
    private TextView mTotalPriceLabel;
    private TextView mTotalWeightLabel;
    private EditText mGeneralDescriptionInput;
    private AlertDialog mEditPriceDialog;

    public CheckDeclarationFragment() {
        // Required empty public constructor
    }

    public static CheckDeclarationFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        CheckDeclarationFragment fragment = new CheckDeclarationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_declaration, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.check_list), true);
        mProducts = new ArrayList<>();
        mInStock = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.buildFourthStepDeclarationList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mGeneralDescriptionInput = (EditText) view.findViewById(R.id.buildFourthStepGeneralDescription);
        mAdapter = new PackageDetailsAdapter(mInStock);
        mAdapter.setOnEditClickListener(this);
        mTotalPriceLabel = (TextView) view.findViewById(R.id.buildFirstFragmentTotalPriceLabel);
        mTotalWeightLabel = (TextView) view.findViewById(R.id.buildFirstFragmentTotalWeightLabel);
        RadioGroup declarationSelector = (RadioGroup) view.findViewById(R.id.checkDeclarationMethodSelector);
        declarationSelector.setOnCheckedChangeListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mInForming = getArguments().getParcelable(IN_FORMING_ARG);
        mInForming.setAutoFilling(true);
        mGeneralDescriptionInput.setText(mInForming.getGeneralDescription());
        ImageButton next = (ImageButton) view.findViewById(R.id.buildFirstStepNextButton);
        next.setOnClickListener(this);
        mActivity.toggleLoadingProgress(true);
        RequestBody body = new FormBody.Builder()
                .add("shipperMeasureId", String.valueOf(mInForming.getShippingMethod().getId()))
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlBuildStep(4, String.valueOf(mInForming.getId())), mHandler);
        return view;
    }

    private void setTotals() {
        int weight = 0;
        double price = 0;

        for (Product product : mInForming.getProducts()) {
            weight += Double.parseDouble(product.getWeight());
            price += Double.parseDouble(product.getProductPrice());
        }

        mTotalPriceLabel.setText("$" + String.valueOf(price));
        mTotalWeightLabel.setText(String.valueOf(((float) (weight / 1000))) + "kg.");
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.check_list), true);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }

    /**
     * Here we save all the data an go to the insurance screen
     */
    @Override
    public void onClick(View v) {
        String description = String.valueOf(mGeneralDescriptionInput.getText());
        if (description.equals("") && !mInForming.isAutoFilling()) {
            Snackbar.make(mRecyclerView, getString(R.string.fill_general_description), Snackbar.LENGTH_SHORT).show();
            mGeneralDescriptionInput.setError(getString(R.string.fill_general_description));
            return;
        }

        if (description.length() < 6 && !mInForming.isAutoFilling()) {
            Snackbar.make(mRecyclerView, getString(R.string.at_least_six), Snackbar.LENGTH_SHORT).show();
            mGeneralDescriptionInput.setError(getString(R.string.at_least_six));
            return;
        }

        if (mInForming.isAutoFilling()) description = "";
        mInForming.setGeneralDescription(description);
        mActivity.addFragment(InsuranceFragment.newInstance(mInForming), true);
        mActivity.hideKeyboard();
    }

    /**
     * Here we give the opportunity to change the price for a product within the list
     * this is implemented in an alert dialog
     * @param product for which to edit the price
     * @param position within the list
     */
    @Override
    public void onEditClicked(final Product product, final int position) {
        mEditPriceDialog = mActivity.getEditDialog(getString(R.string.price_edit), product.getProductPrice(), R.mipmap.ic_cash_24dpi,
                getString(R.string.save), getString(R.string.cancel), InputType.TYPE_NUMBER_FLAG_DECIMAL, new MainActivity.OnEditDialogClickListener() {
                    @Override
                    public void onPositiveClick(String newInput) {
                        product.setProductPrice(newInput);
                        setTotals();
                        mEditPriceDialog.dismiss();
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onNegativeClick() {
                        mEditPriceDialog.dismiss();
                    }
                });
        mEditPriceDialog.show();
    }


    /**
     * Here we turn on or off auto packing
     * if it is turned on we hide the list and general description input so user could just
     * go forward and save some time
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.checkDeclarationAutoFillingSelector:
                mInForming.setAutoFilling(true);
                mGeneralDescriptionInput.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                break;
            case R.id.checkDeclarationManualFillingSelector:
                mGeneralDescriptionInput.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mInForming.setAutoFilling(false);
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");

        JSONArray jsonBoxes = data.getJSONArray("boxes");
        for (int i = 0; i < jsonBoxes.length(); i++) {
            JSONObject box = jsonBoxes.getJSONObject(i);
            InStockDetailed detailed = new InStockDetailed();
            detailed.setID(box.getInt("id"));
            detailed.setParcelId(box.getString("uid"));
            mInStock.add(detailed);
        }

        String declaration = data.getString("declaration");
        if (declaration != null && !declaration.equals("null")) {
            mInForming.setGeneralDescription(declaration);
        }

        JSONArray jsonDec = data.getJSONArray("declarationItems");
        for (InStockDetailed detailed : mInStock) {
            ArrayList<Product> products = new ArrayList<>();
            for (int i = 0; i < jsonDec.length(); i++) {
                JSONObject jsonProd = jsonDec.getJSONObject(i);
                Product product = new Product.Builder()
                        .id(jsonProd.getInt("id"))
                        .productName(jsonProd.getString("title"))
                        .productQuantity(jsonProd.getString("quantity"))
                        .productPrice(jsonProd.getString("price"))
                        .productUrl(jsonProd.getString("url"))
                        .orderStorageId(jsonProd.getString("orderStorageId"))
                        .weight(jsonProd.getString("weight"))
                        .declarationId(jsonProd.getString("declarationId"))
                        .build();
                final int boxId = Integer.parseInt(product.getOrderStorageId());
                if (boxId == detailed.getID()) {
                    products.add(product);
                }

            }
            detailed.setProducts(products);
            mProducts.addAll(products);
        }
        mInForming.setProducts(mProducts);
        if (mInForming.getGeneralDescription() != null && !mInForming.getGeneralDescription().equals("")) {
            mGeneralDescriptionInput.setText(String.valueOf(mInForming.getGeneralDescription()));
        }
        mAdapter.addItems(mInStock);
        setTotals();
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public void onServerError(String message) {
        mActivity.toggleLoadingProgress(false);
    }
}
