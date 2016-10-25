package com.softranger.bayshopmf.ui.pus;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveFeedbackFragment extends ParentFragment implements RatingBar.OnRatingBarChangeListener,
        LoadingDialogFragment.OnDoneListener {

    private static final String DETAILED_PARCEL = "detailed parcel arg";
    private static final int UPLOAD_RESULT_CODE = 12;
    private static final int TAKE_PICTURE_CODE = 13;
    private static final int CAMERA_PERMISSION_CODE = 14;

    private ParentActivity mActivity;
    private Unbinder mUnbinder;
    private Call<ServerResponse> mCall;
    private PUSParcelDetailed mParcelDetailed;

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

    private HashMap<Float, Integer> mRatingStrings = new HashMap<Float, Integer>() {{
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
            mRatingLabel.setText(getString(mRatingStrings.get((float) mParcelDetailed.getRating())));
            mCommentInput.setText(mParcelDetailed.getComment());
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
            if (requestCode == UPLOAD_RESULT_CODE) {
                Uri path = data.getData();
                File selectedFile = new File(path.getPath());
                Log.d(this.getClass().getSimpleName(), selectedFile.getAbsolutePath());
            } else if (requestCode == TAKE_PICTURE_CODE) {

                Log.d(this.getClass().getSimpleName(), "Take picture done");
            }
            LoadingDialogFragment dialogFragment = LoadingDialogFragment.newInstance(requestCode);
            dialogFragment.setOnDoneListener(this);
            dialogFragment.show(mActivity.getFragmentManager(), "Loading dialog fragment");
        }
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

    }

    void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, UPLOAD_RESULT_CODE);
    }

    void takePhoto() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(mActivity, permissions, CAMERA_PERMISSION_CODE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri outputFileUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Bitmap mphoto = BitmapFactory.decodeFile(image.getAbsolutePath());
        Log.d(this.getClass().getSimpleName(), "Take picture done");
        return image;
    }

    @OnClick(R.id.leaveFeedbackSendCommentBtn)
    void sendFeedback() {
        String comment = String.valueOf(mCommentInput.getText());
        float rating = mRatingBar.getRating();

        mCall = Application.apiInterface().leaveFeedback(
                mParcelDetailed.getId(), comment, String.valueOf(rating));
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
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDone(int action) {

    }
}
