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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryImageFragment extends Fragment {


    private static final String PHOTOS_ARG = "photos argument";

    @BindView(R.id.galleryImageProgress)
    ProgressBar mProgressBar;
    @BindView(R.id.galleryImageLabel)
    SubsamplingScaleImageView mImageView;
    private GalleryActivity mGalleryActivity;
    private Unbinder mUnbinder;
    private DownloadThread mDownloadThread;

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
        mUnbinder = ButterKnife.bind(this, view);
        Photo photo = getArguments().getParcelable(PHOTOS_ARG);
        if (photo != null) {
            mDownloadThread = new DownloadThread(photo);
            mDownloadThread.start();
            toggleLoading();
        }
        return view;
    }

    private void toggleLoading() {
        if (mProgressBar == null) return;
        mGalleryActivity.runOnUiThread(() -> {
            if (mProgressBar.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
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
                URL bigImageUrl = new URL(mPhoto.getBigImage());
                File image = getImageFile(bigImageUrl);
                mPhoto.setBigBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));
                if (mImageView == null) return;
                mGalleryActivity.runOnUiThread(() -> {
                    if (mPhoto.getBigBitmap() != null) {
                        mImageView.setImage(ImageSource.bitmap(mPhoto.getBigBitmap()));
                    } else {
                        mImageView.setImage(ImageSource.resource(R.drawable.no_image));
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

    private File getImageFile(URL url) throws Exception {
        File image = new File(mGalleryActivity.getCacheDir(), urlToFileName(url.toString()));
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
        return image;
    }

    private String urlToFileName(String url) {
        return url.replace("/", "").replace(".", "") + ".png";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
