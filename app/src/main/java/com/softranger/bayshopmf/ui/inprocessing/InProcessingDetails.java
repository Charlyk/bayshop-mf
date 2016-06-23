package com.softranger.bayshopmf.ui.inprocessing;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ImagesAdapter;
import com.softranger.bayshopmf.adapter.InProcessingDetailsAdapter;
import com.softranger.bayshopmf.model.Photo;
import com.softranger.bayshopmf.model.packages.Package;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.gallery.GalleryActivity;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProcessingDetails extends Fragment implements ImagesAdapter.OnImageClickListener {

    private static final String PRODUCT_ARG = "in processing arguments";

    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private ArrayList<Object> mObjects;

    public InProcessingDetails() {
        // Required empty public constructor
    }

    public static InProcessingDetails newInstance(@NonNull Package product) {
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
        Package aPackage = getArguments().getParcelable(PRODUCT_ARG);
        mObjects = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.inProcessingDetailsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        InProcessingDetailsAdapter adapter = new InProcessingDetailsAdapter(mObjects, this);
        mRecyclerView.setAdapter(adapter);
        ApiClient.getInstance().sendRequest(Constants.Api.urlViewParcelDetails(String
                .valueOf(aPackage.getId())), mDetailsHandler);
        return view;
    }

    @Override
    public void onImageClick(ArrayList<Photo> images, int position) {
        Intent intent = new Intent(mActivity, GalleryActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("position", position);
        mActivity.startActivity(intent);
    }

    private Handler mDetailsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {

                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };
}
