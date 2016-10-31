package com.softranger.bayshopmfr.ui.awaitingarrival;

import android.os.AsyncTask;
import android.widget.Toast;

import com.softranger.bayshopmfr.adapter.AwaitingArrivalAdapter;
import com.softranger.bayshopmfr.model.box.AwaitingArrival;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class DeleteAsyncTask extends AsyncTask<AwaitingArrival, Void, Void> {

    private int mItemPosition;
    private ArrayList<AwaitingArrival> mAwaitingArrivals;
    private MainActivity mActivity;
    private AwaitingArrivalAdapter mAdapter;
    private OnDeleteListener mOnDeleteListener;

    public DeleteAsyncTask(int itemPosition, ArrayList<AwaitingArrival> awaitingArrivals,
                           MainActivity activity, AwaitingArrivalAdapter adapter)
    {
        mItemPosition = itemPosition;
        mAwaitingArrivals = awaitingArrivals;
        mActivity = activity;
        mAdapter = adapter;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        mOnDeleteListener = onDeleteListener;
    }

    @Override
    protected Void doInBackground(AwaitingArrival... awaitingArrivals) {
        // send delete request to server
        Call<ServerResponse> call = Application.apiInterface().deleteAwaitingParcel(awaitingArrivals[0].getId());
        try {
            // send request
            Response<ServerResponse> response = call.execute();
            // check if response is successful
            if (response.body().getMessage().equals(Constants.ApiResponse.OK_MESSAGE)) {
                mAwaitingArrivals.remove(mItemPosition);
            } else {
                Toast.makeText(mActivity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAdapter.deleteItem(mItemPosition);
        mActivity.toggleLoadingProgress(false);
        // get parcels count
        int count = Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL);
        // decrease it by one
        count -= 1;
        // put it back
        Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, count);
        // update counters from menu
        mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
        if (mOnDeleteListener != null) mOnDeleteListener.onItemDeleted();
    }

    public interface OnDeleteListener {
        void onItemDeleted();
    }
}
