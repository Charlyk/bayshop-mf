package com.softranger.bayshopmf.ui.inprocessing;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ImagesAdapter;
import com.softranger.bayshopmf.adapter.InProcessingDetailsAdapter;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.ui.GalleryActivity;
import com.softranger.bayshopmf.ui.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProcessingDetails extends Fragment implements ImagesAdapter.OnImageClickListener {


    private static final String PRODUCT_ARG = "in processing arguments";

    private MainActivity mActivity;

    public InProcessingDetails() {
        // Required empty public constructor
    }

    public static InProcessingDetails newInstance(InProcessingParcel product) {

        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, product);
        InProcessingDetails fragment = new InProcessingDetails();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_processing_details, container, false);
        mActivity = (MainActivity) getActivity();
        InProcessingParcel processingParcel = getArguments().getParcelable(PRODUCT_ARG);
        processingParcel.setProducts(buildTestProducts());
        processingParcel.setShippingBy("BayShop Air Post");
        processingParcel.setTrackingNumber("tracking123123124231");
        processingParcel.setGoodsPrice("$ 245");
        processingParcel.setCustomsClearance("$ 3");
        processingParcel.setShippingPrice("$ 24");
        processingParcel.setTotalPrice("$ 272");

        Address address = new Address.Builder()
                .clientName("Eduard Albu")
                .street("Florilor")
                .buildingNumber("16/1")
                .city("Chisinau")
                .country("Moldova")
                .postalCode("MD-2088")
                .phoneNumber("(79) 466-284")
                .build();
        processingParcel.setAddress(address);

        ArrayList<Object> listItems = new ArrayList<>();
        listItems.add(processingParcel);
        listItems.addAll(processingParcel.getProducts());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.inProcessingDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        InProcessingDetailsAdapter adapter = new InProcessingDetailsAdapter(listItems, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private ArrayList<Product> buildTestProducts() {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Product product = new Product.Builder()
                    .trackingNumber("tracking13123123123")
                    .productUrl("http://ebay.com")
                    .productQuantity("2")
                    .productPrice("$ 250")
                    .productId("MF342342352")
                    .productName("Comp comp")
                    .images(images())
                    .build();
            products.add(product);
        }
        return products;
    }

    private ArrayList<Integer> images() {
        ArrayList<Integer> images = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            images.add(R.drawable.computer_mac_image);
        }
        return images;
    }

    @Override
    public void onImageClick(ArrayList<Integer> images, int position) {
        Intent intent = new Intent(mActivity, GalleryActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("position", position);
        mActivity.startActivity(intent);
    }
}
