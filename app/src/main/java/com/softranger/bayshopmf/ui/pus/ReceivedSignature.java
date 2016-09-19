package com.softranger.bayshopmf.ui.pus;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.PUSParcelDetailed;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedSignature extends ParentFragment implements OnMapReadyCallback {

    private static final String RECEIVED_ARG = "received argument";

    private Unbinder mUnbinder;
    private ParentActivity mActivity;
    @BindView(R.id.receivedSignatureClientSignature) ImageView mClientSignature;
    @BindView(R.id.receivedSignatureParcelUid) TextView mUidLabel;
    @BindView(R.id.receivedSignatureParcelDescription) TextView mDescriptionlabel;
    @BindView(R.id.receivedSignatureWaitingDateLabel) TextView mReceivedDateLabel;
    @BindView(R.id.receivedSignatureWeightLabel) TextView mWeightLabel;
    @BindView(R.id.receivedSignatureClientNameLagel) TextView mClinetNameLabel;
    @BindView(R.id.receivedSignatureStreetLabel) TextView mStreetLabel;
    @BindView(R.id.receivedSignaturePhoneLabel) TextView mPhoneNumberLabel;
    @BindView(R.id.secondStepCityLabel) TextView mCityLabel;
    @BindView(R.id.receivedSignatureCountryLabel) TextView mCountryLabel;
    @BindView(R.id.receivedSignaturepostalCodeLabel) TextView mPostalCodeLabel;
    @BindView(R.id.receivedSignatureMapView) MapView mMapView;
    @BindView(R.id.clientSignatureDownloadProgress) ProgressBar mDownloadProgress;

    private static SimpleDateFormat serverFormat;
    private static SimpleDateFormat friendlyFormat;

    private static double latitude = 47.043252904877306;
    private static double longitude = 28.868207931518555;

    public ReceivedSignature() {
        // Required empty public constructor
    }

    public static ReceivedSignature newInstance(PUSParcelDetailed received) {
        Bundle args = new Bundle();
        args.putParcelable(RECEIVED_ARG, received);
        ReceivedSignature fragment = new ReceivedSignature();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_received_signature, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();

        serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        friendlyFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

        // map view

        PUSParcelDetailed received = getArguments().getParcelable(RECEIVED_ARG);
        if (received != null) {
            setDataOnPosition(received);
        }

        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.setClickable(false);

        return view;
    }

    private void setDataOnPosition(@NonNull PUSParcelDetailed received) {

        if (received.getCoordinates() != null) {
            latitude = received.getCoordinates().getLatitude();
            longitude = received.getCoordinates().getLongitude();
        }

        mUidLabel.setText(received.getCodeNumber());
        mDescriptionlabel.setText(received.getGeneralDescription());

        Date date = new Date();
        try {
            date = serverFormat.parse(received.getReceivedTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String strDate = friendlyFormat.format(date);
        mReceivedDateLabel.setText(strDate);

        // compute kilos from grams and set the result in weight label
        double realWeight = Double.parseDouble(received.getRealWeight());
        double kg = realWeight / 1000;
        mWeightLabel.setText(kg + "kg.");

        // concatenate client first name and last name to set full name in label
        String clientFullName = received.getAddress().getFirstName() + " "
                + received.getAddress().getLastName();
        mClinetNameLabel.setText(clientFullName);
        mStreetLabel.setText(received.getAddress().getStreet());
        mPhoneNumberLabel.setText(received.getAddress().getPhoneNumber());
        mCityLabel.setText(received.getAddress().getCity());
        mCountryLabel.setText(received.getAddress().getCountry());
        mPostalCodeLabel.setText(received.getAddress().getPostalCode());

        if (received.getSignatureUrl() != null) {
            mDownloadProgress.setVisibility(View.VISIBLE);
            mDownloadSignatureTask.execute(received.getSignatureUrl());
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng latLng = new LatLng(latitude, longitude);

        // disable marker click
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        final Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Received location"));
        // googleMap in the Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
        }, 300);
    }

    private AsyncTask<String, Void, Bitmap> mDownloadSignatureTask = new AsyncTask<String, Void, Bitmap>() {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mClientSignature.setImageBitmap(bitmap);
            mDownloadProgress.setVisibility(View.GONE);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(Constants.Api.BASE_URL + strings[0]);
                return downloadImage(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    };

    private Bitmap downloadImage(URL url) throws Exception {
        File image = new File(mActivity.getCacheDir(), urlToFileName(url.toString()));
        if (!image.exists()) {
            image.createNewFile();
            BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteCount);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        return BitmapFactory.decodeFile(image.getAbsolutePath());
    }

    private String urlToFileName(String url) {
        return url.replace("/", "").replace(".", "") + ".png";
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {

    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.signature_of_geolocation);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.signature_and_location;
    }
}
