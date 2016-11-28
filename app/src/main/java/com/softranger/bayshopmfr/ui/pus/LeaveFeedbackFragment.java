package com.softranger.bayshopmfr.ui.pus;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveFeedbackFragment extends ParentFragment implements RatingBar.OnRatingBarChangeListener {

    private static final String TAG = "LeaveFeedbackFragment";
    private static final String DETAILED_PARCEL = "detailed parcel arg";
    private static final int UPLOAD_RESULT_CODE = 12;
    private static final int TAKE_PICTURE_CODE = 13;
    public static final int CAMERA_PERMISSION_CODE = 14;
    private static final int CAMERA_RQ = 1725;

    private ParentActivity mActivity;
    private Unbinder mUnbinder;
    private Call<ServerResponse> mCall;
    private PUSParcelDetailed mParcelDetailed;
    private AlertDialog mUploadDialog;
    private File mUserPhoto;

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private static SimpleDateFormat friendlyFormat;

    @BindView(R.id.leaveFeedbackUidLabel)
    TextView mUidLabel;
    @BindView(R.id.leaveFeedbackDescriptionLabel)
    TextView mDescriptionLabel;
    @BindView(R.id.leaveFeedbackDateLabel)
    TextView mDateLabel;
    @BindView(R.id.leaveFeedbackRatingBar)
    RatingBar mRatingBar;
    @BindView(R.id.leaveFeedbackRatingLabel)
    TextView mRatingLabel;
    @BindView(R.id.leaveFeedbackCommentLabel)
    EditText mCommentInput;
    @BindView(R.id.leaveFeedbackCommentImage)
    ImageView mImageView;

    private HashMap<Float, Integer> mRatingStrings = new HashMap<Float, Integer>() {{
        put(0f, R.string.empty_string);
        put(1f, R.string.hated_it);
        put(2f, R.string.disliked_it);
        put(3f, R.string.it_is_ok);
        put(4f, R.string.liked_it);
        put(5f, R.string.loved_it);
    }};

    public LeaveFeedbackFragment() {
        // Required empty public constructor
    }

    public static LeaveFeedbackFragment newInstance(PUSParcelDetailed pusParcelDetailed) {
        Bundle args = new Bundle();
        args.putParcelable(DETAILED_PARCEL, pusParcelDetailed);
        LeaveFeedbackFragment fragment = new LeaveFeedbackFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leave_feedback, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();
        mRatingBar.setOnRatingBarChangeListener(this);

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        friendlyFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        mParcelDetailed = getArguments().getParcelable(DETAILED_PARCEL);

        if (mParcelDetailed != null) {
            mDateLabel.setText(friendlyFormat.format(mParcelDetailed.getReceivedTime()));
            mUidLabel.setText(mParcelDetailed.getCodeNumber());
            mDescriptionLabel.setText(mParcelDetailed.getGeneralDescription());
        }

        if (mParcelDetailed != null) {
            mRatingBar.setRating(mParcelDetailed.getRating());
            int rating;
            if (mParcelDetailed.getRating() > 5) rating = 5;
            else if (mParcelDetailed.getRating() < 0) rating = 0;
            else rating = mParcelDetailed.getRating();
            mRatingLabel.setText(getString(mRatingStrings.get((float) rating)));
            mCommentInput.setText(mParcelDetailed.getComment());
            if (mParcelDetailed.getPhoto() != null) {
                Picasso.with(mActivity).load(Uri.parse(mParcelDetailed.getPhoto().getSmallImage())).into(mImageView);
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            mUserPhoto = new File(mActivity.getCacheDir().getPath(), "product_photo.jpg");
            try {
                // get image from intent
                writeResultFromIntentToFile(data);
                decreaseFileSize(mUserPhoto);
                Bitmap original = BitmapFactory.decodeFile(mUserPhoto.getAbsolutePath());
                if (original.getWidth() > original.getHeight()) {
                    Bitmap rotated = rotateImage(original, 90);
                    FileOutputStream fos = new FileOutputStream(mUserPhoto);
                    rotated.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    rotated.recycle();
                }
                original.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int width = Application.getPixelsFromDp(160);
            int height = Application.getPixelsFromDp(120);
            Picasso.with(mActivity).load(mUserPhoto).resize(width, height).into(mImageView);
        } else if (data != null) {
            Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
            if (e != null) {
                e.printStackTrace();
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void writeResultFromIntentToFile(Intent data) {
        try {
            // get image from intent
            InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            OutputStream outputStream = new FileOutputStream(mUserPhoto);
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.leave_feedback);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.leave_feedback;
    }

    @OnClick(R.id.leaveFeedbackUploadImageBtn)
    void uploadImageFile() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            showPhotoDialog();
        } else {
            ArrayList<String> perms = new ArrayList<>();
            perms.add(Manifest.permission.CAMERA);
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            String[] permissions = perms.toArray(new String[perms.size()]);

            ActivityCompat.requestPermissions(mActivity, permissions, CAMERA_PERMISSION_CODE);
        }
    }

    private void showPhotoDialog() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.upload_image_dialog, null, false);
        view.findViewById(R.id.uploadDialogTakePhoto).setOnClickListener(v -> {
//            takePhoto();
            new MaterialCamera(this)
                    .forceCamera1()
                    /** all the previous methods can be called, but video ones would be ignored */
                    .stillShot() // launches the Camera in stillshot mode
                    .start(CAMERA_RQ);
            mUploadDialog.dismiss();
        });

        view.findViewById(R.id.uploadDialogUploadPhoto).setOnClickListener(v -> {
            uploadImage();
            mUploadDialog.dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setView(view);
        mUploadDialog = builder.create();
        mUploadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mUploadDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN
                        && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                showPhotoDialog();
            }
        }
    }

    void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, UPLOAD_RESULT_CODE);
    }

    @OnClick(R.id.leaveFeedbackSendCommentBtn)
    void sendFeedback() {
        String comment = String.valueOf(mCommentInput.getText());
        int rating = (int) mRatingBar.getRating();

        if (mUserPhoto != null) {
            MultipartBody.Part part = MultipartBody.Part.createFormData("service_photo", mUserPhoto.getName(),
                    RequestBody.create(MEDIA_TYPE_PNG, mUserPhoto));
            mCall = Application.apiInterface().leaveFeedback(
                    mParcelDetailed.getId(), comment, rating, part);
        } else {
            mCall = Application.apiInterface().leaveFeedback(
                    mParcelDetailed.getId(), comment, rating);
        }

        mActivity.toggleLoadingProgress(true);
        mCall.enqueue(mResponseCallback);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    sendFeedback();
                    break;
            }
        }
    };

    private void decreaseFileSize(File src) {
        try {
            Bitmap resized = decreaseBitmapSize(BitmapFactory.decodeFile(src.getAbsolutePath()));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] compressedImage = stream.toByteArray();
            OutputStream outputStream = new FileOutputStream(src);
            outputStream.write(compressedImage);
            outputStream.flush();
            outputStream.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap decreaseBitmapSize(Bitmap src) {
        int size = 700;
        return ThumbnailUtils.extractThumbnail(src, size, size, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.showResultActivity(getString(R.string.feedback_added),
                    R.mipmap.ic_expediated_comment_250dp, getString(R.string.feedback_comment));
            Intent update = new Intent(ReceivedFragment.ACTION_UPDATE);
            mActivity.sendBroadcast(update);
            mActivity.onBackPressed();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        if (v < 1.0f) {
            ratingBar.setRating(1.0f);
            v = 1.0f;
        }
        mRatingLabel.setText(getString(mRatingStrings.get(v)));
    }
}
