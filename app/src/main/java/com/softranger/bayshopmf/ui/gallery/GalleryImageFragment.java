package com.softranger.bayshopmf.ui.gallery;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.product.Photo;
import com.softranger.bayshopmf.util.Constants;

import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryImageFragment extends Fragment {


    private static final String PHOTOS_ARG = "photos argument";

    private ProgressBar mProgressBar;
    private SubsamplingScaleImageView mImageView;
    private GalleryActivity mGalleryActivity;

    public GalleryImageFragment() {
        // Required empty public constructor
    }

    public static GalleryImageFragment newInstance(Photo photo) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTOS_ARG, photo);
        GalleryImageFragment fragment = new GalleryImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_galery_image, container, false);
        mGalleryActivity = (GalleryActivity) getActivity();
        mProgressBar = (ProgressBar) view.findViewById(R.id.galleryImageProgress);
        mImageView = (SubsamplingScaleImageView) view.findViewById(R.id.galleryImageLabel);
        Photo photo = getArguments().getParcelable(PHOTOS_ARG);
        if (photo != null) {
            new DownloadThread(photo).start();
            toggleLoading();
        }
        return view;
    }

    private void toggleLoading() {
        mGalleryActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    class DownloadThread extends Thread {

        private Photo mPhoto;

        public DownloadThread(Photo photo) {
            mPhoto = photo;
        }

        @Override
        public void run() {
            Looper.prepare();
            try {
                URL biImageUlr = new URL(Constants.Api.BASE_URL + mPhoto.getBigImage());
                mPhoto.setBigBitmap(BitmapFactory.decodeStream(biImageUlr.openStream()));
                mGalleryActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImage(ImageSource.bitmap(mPhoto.getBigBitmap()));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                toggleLoading();
            }
            Looper.loop();
        }
    }
}
