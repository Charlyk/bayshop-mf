package com.softranger.bayshopmf.util;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by macbook on 6/28/16.
 */
public abstract class ParentFragment extends Fragment {

    private ParentActivity mActivity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (ParentActivity) getActivity();
    }


    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            onServerResponse(response);
                        } else {
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            onServerError(message);
                        }
                    } catch (Exception e) {
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        onServerError(e.getMessage());
                    } finally {
                        finallyMethod();
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
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    onServerError(message);
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
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    onServerError(message);
                    break;
                }
            }
            onHandleMessageEnd();
        }
    };

    public abstract void onServerResponse(JSONObject response) throws Exception;

    public void onServerError(String message) {

    }

    public void finallyMethod() {

    }

    public void onHandleMessageEnd() {

    }

    public abstract String getFragmentTitle();

    public abstract ParentActivity.SelectedFragment getSelectedFragment();
}
