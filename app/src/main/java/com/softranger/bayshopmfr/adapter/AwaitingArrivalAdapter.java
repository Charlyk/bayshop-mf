package com.softranger.bayshopmfr.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.box.AwaitingArrival;
import com.softranger.bayshopmfr.model.tracking.Checkpoint;
import com.softranger.bayshopmfr.model.tracking.Courier;
import com.softranger.bayshopmfr.model.tracking.CourierService;
import com.softranger.bayshopmfr.model.tracking.TrackApiResponse;
import com.softranger.bayshopmfr.model.tracking.TrackingResult;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class AwaitingArrivalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<AwaitingArrival> mAwaitingArrivals;
    private OnAwaitingClickListener mOnAwaitingClickListener;
    SparseArray<ParcelStatusBarView.BarColor> mBarColorSparseArray;

    public AwaitingArrivalAdapter(ArrayList<AwaitingArrival> awaitingArrivals,
                                  SparseArray<ParcelStatusBarView.BarColor> barColorSparseArray) {
        mAwaitingArrivals = awaitingArrivals;
        mBarColorSparseArray = barColorSparseArray;
    }

    public void setOnAwaitingClickListener(OnAwaitingClickListener onAwaitingClickListener) {
        mOnAwaitingClickListener = onAwaitingClickListener;
    }

    public void refreshList(ArrayList<AwaitingArrival> awaitingArrivals) {
        Stream.of(awaitingArrivals)
                .filterNot(value -> mAwaitingArrivals.contains(value))
                .forEach(awaitingArrival -> mAwaitingArrivals.add(awaitingArrival));

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arrival_list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.mAwaitingArrival = mAwaitingArrivals.get(position);
            itemHolder.mUidLabel.setText(itemHolder.mAwaitingArrival.getUid());
            itemHolder.mDescriptionLabel.setText(itemHolder.mAwaitingArrival.getTracking());
            itemHolder.mDateLabel.setText(Application.getFormattedDate(itemHolder.mAwaitingArrival.getCreatedDate()));
            itemHolder.mPriceLabel.setText(itemHolder.mAwaitingArrival.getPrice());
            itemHolder.mWeightLabel.setText("---");
            if (itemHolder.mAwaitingArrival.getTrackingResult() == null) {
                getTrackingCourierService(itemHolder.mAwaitingArrival);
                itemHolder.mStatusBarView.setProgress(0, Application.getInstance().getString(R.string.geting_status));
            } else {
                ArrayList<Checkpoint> checkpoints = itemHolder.mAwaitingArrival.getTrackingResult().getCheckpoints();
                if (checkpoints != null && checkpoints.size() > 0) {
                    Checkpoint checkpoint = checkpoints.get(0);

                    String statusName;
                    if (checkpoint.getCheckpointStatus() != Checkpoint.CheckpointStatus.other) {
                        statusName = Application.getInstance()
                                .getString(checkpoint.getCheckpointStatus().translatedStatus());
                    } else {
                        statusName = checkpoint.getStatusName();
                    }

                    int statusStep;
                    if (checkpoint.getCheckpointStatus() == Checkpoint.CheckpointStatus.delivered) {
                        statusStep = 2;
                    } else {
                        statusStep = 1;
                    }

                    itemHolder.mStatusBarView.setProgress(statusStep, statusName);
                }
            }
        }
    }

    private void getTrackingCourierService(AwaitingArrival data) {
        String trackingNumber = data.getTracking();
        // get courier service by tracking number
        Application.trackApiInterface().detectCourierService(trackingNumber)
                .subscribeOn(Schedulers.io())
                .subscribe(arrayListTrackApiResponse -> {
                    // on response get first courier service from the list
                    ArrayList<CourierService> services = arrayListTrackApiResponse.getData();
                    if (services != null && services.size() > 0) {
                        CourierService service = services.get(0);
                        Courier courier = service.getCourier();
                        if (courier != null) {
                            getTrackingInfo(data, courier.getSlug(), trackingNumber);
                        }
                    }
                });
    }

    private void getTrackingInfo(AwaitingArrival awaitingArrival, String slug, String trackingNumber) {
        Application.trackApiInterface().trackParcelByCourierAndTracking(slug, trackingNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackingResultTrackApiResponse -> {
                    if (trackingResultTrackApiResponse.getResult() == TrackApiResponse.Result.waiting) {
                        getTrackingInfo(awaitingArrival, slug, trackingNumber);
                    } else {
                        // when we have an response show the progressbar
                        TrackingResult result = trackingResultTrackApiResponse.getData();
                        awaitingArrival.setTrackingResult(result);
                        if (result != null) {
                            notifyItemChanged(mAwaitingArrivals.indexOf(awaitingArrival));
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mAwaitingArrivals.size();
    }

    public void deleteItem(int position) {
        notifyItemRemoved(position);
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ParcelStatusBarView.OnStatusBarReadyListener {

        @BindView(R.id.awaitingUidLabel) TextView mUidLabel;
        @BindView(R.id.awaitingDescriptionLabel) TextView mDescriptionLabel;
        @BindView(R.id.awaitingDateLabel) TextView mDateLabel;
        @BindView(R.id.awaitingWeightLabel) TextView mWeightLabel;
        @BindView(R.id.awaitingPriceLabel) TextView mPriceLabel;
        @BindView(R.id.awaitingTrackingStatusBarView)
        ParcelStatusBarView mStatusBarView;
        AwaitingArrival mAwaitingArrival;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mStatusBarView.setNewColorsMap(mBarColorSparseArray);
            mStatusBarView.setOnStatusBarReadyListener(this);

            itemView.setOnClickListener(this);
        }

        @OnClick(R.id.awaitingDeleteButton)
        void deleteAwaitingParcel() {
            if (mOnAwaitingClickListener != null) {
                mOnAwaitingClickListener.onDeleteAwaitingClick(mAwaitingArrival, getAdapterPosition());
            }
        }

        @Override
        public void onClick(View view) {
            if (mOnAwaitingClickListener != null) {
                mOnAwaitingClickListener.onAwaitingClick(mAwaitingArrival, getAdapterPosition());
            }
        }

        @Override
        public void onStatusBarReady() {
            if (mAwaitingArrival.getTrackingResult() != null) {
                TrackingResult result = mAwaitingArrival.getTrackingResult();
                Checkpoint checkpoint = result.getCheckpoints().get(0);
                String statusName = checkpoint.getCheckpointStatus() == Checkpoint.CheckpointStatus.other ?
                        checkpoint.getStatusName() : Application.getInstance().getString(checkpoint.getCheckpointStatus().translatedStatus());

                int statusStep;
                if (checkpoint.getCheckpointStatus() == Checkpoint.CheckpointStatus.delivered) {
                    statusStep = 2;
                } else {
                    statusStep = 1;
                }

                mStatusBarView.setProgress(statusStep, statusName);
            } else {
                mStatusBarView.setProgress(0, Application.getInstance().getString(R.string.geting_status));
            }
        }
    }

    public interface OnAwaitingClickListener {
        void onAwaitingClick(AwaitingArrival awaitingArrival, int position);
        void onDeleteAwaitingClick(AwaitingArrival awaitingArrival, int position);
    }
}
