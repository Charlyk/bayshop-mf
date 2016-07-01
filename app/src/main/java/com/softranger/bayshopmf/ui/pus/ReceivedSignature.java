package com.softranger.bayshopmf.ui.pus;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.packages.Received;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedSignature extends Fragment implements OnMapReadyCallback {

    private static final String RECEIVED_ARG = "received argument";
    private TextView mUidLabel, mDescriptionlabel, mReceivedDateLabel, mWeightLabel, mClinetNameLabel,
            mStreetLabel, mPhoneNumberLabel, mCityLabel, mCountryLabel, mPostalCodeLabel;
    private MapView mMapView;

    private static SimpleDateFormat serverFormat;
    private static SimpleDateFormat friendlyFormat;

    private static final double LATITUDE = 47.043252904877306;
    private static final double LONGITUDE = 28.868207931518555;

    public ReceivedSignature() {
        // Required empty public constructor
    }

    public static ReceivedSignature newInstance(Received received) {

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

        serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        friendlyFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

        mUidLabel = (TextView) view.findViewById(R.id.receivedSignatureParcelUid);
        mDescriptionlabel = (TextView) view.findViewById(R.id.receivedSignatureParcelDescription);
        mReceivedDateLabel = (TextView) view.findViewById(R.id.receivedSignatureWaitingDateLabel);
        mWeightLabel = (TextView) view.findViewById(R.id.receivedSignatureWeightLabel);
        mClinetNameLabel = (TextView) view.findViewById(R.id.receivedSignatureClientNameLagel);
        mStreetLabel = (TextView) view.findViewById(R.id.receivedSignatureStreetLabel);
        mPhoneNumberLabel = (TextView) view.findViewById(R.id.receivedSignaturePhoneLabel);
        mCityLabel = (TextView) view.findViewById(R.id.secondStepCityLabel);
        mCountryLabel = (TextView) view.findViewById(R.id.receivedSignatureCountryLabel);
        mPostalCodeLabel = (TextView) view.findViewById(R.id.receivedSignaturepostalCodeLabel);
        // map view
        mMapView = (MapView) view.findViewById(R.id.receivedSignatureMapView);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.setClickable(false);

        Received received = getArguments().getParcelable(RECEIVED_ARG);
        setDataOnPosition(received);
        return view;
    }

    private void setDataOnPosition(Received received) {
        mUidLabel.setText(received.getCodeNumber());
        mDescriptionlabel.setText(received.getName());

        Date date = new Date();
        try {
            date = serverFormat.parse(received.getReceivedTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String strDate = friendlyFormat.format(date);
        mReceivedDateLabel.setText(strDate);

//        mWeightLabel.setText(received.getRealWeght());
        mClinetNameLabel.setText(received.getAddress().getClientName());
        mStreetLabel.setText(received.getAddress().getStreet());
        mPhoneNumberLabel.setText(received.getAddress().getPhoneNumber());
        mCityLabel.setText(received.getAddress().getCity());
        mCountryLabel.setText(received.getAddress().getCountry());
        mPostalCodeLabel.setText(received.getAddress().getPostalCode());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng latLng = new LatLng(LATITUDE, LONGITUDE);

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
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
        }, 300);
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
