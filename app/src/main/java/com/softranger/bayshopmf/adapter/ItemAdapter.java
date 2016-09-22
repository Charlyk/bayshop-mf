package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.box.AwaitingArrival;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.packages.InProcessing;
import com.softranger.bayshopmf.model.packages.LocalDepot;
import com.softranger.bayshopmf.model.packages.Packed;
import com.softranger.bayshopmf.util.ViewAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by eduard on 28.04.16.
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Object> mInStockItems;
    private OnItemClickListener mOnItemClickListener;
    private SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat output = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

    private static final int HEADER = -1, IN_STOCK_ITEM = 0, PRODUCT = 1, PUS_PARCEL = 2, IN_FORMING = 3;

    public ItemAdapter(Context context) {
        mContext = context;
        mInStockItems = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mInStockItems.get(position) instanceof PUSParcel) {
            return PUS_PARCEL;
        } else if (mInStockItems.get(position) instanceof InForming) {
            return IN_FORMING;
        }
        return HEADER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case PUS_PARCEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_procesing_list_item, parent, false);
                return new InProcessingViewHolder(view);
            case IN_FORMING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_procesing_list_item, parent, false);
                return new InFormingViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.additional_services_layout, parent, false);
                return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mInStockItems.get(position) instanceof PUSParcel) {
            InProcessingViewHolder processingHolder = (InProcessingViewHolder) holder;
            processingHolder.mProduct = (PUSParcel) mInStockItems.get(position);
            // check if item is an instance of local depot object
            // and if it's true then we need to show the checkbox used to select multiple items
            // to order a home delivery otherwise hide that check box
            if (mInStockItems.get(position) instanceof LocalDepot) {
                processingHolder.mCreatedDateTitle.setText(mContext.getString(R.string.created_on) + " ");
            }

            // check if the item is an instance of either  or packed object
            // if true we need to show the progress bar and percentage text which shows
            // how much time it needs to finish packing, other wise hide the progressbar and
            // percentage text
            if (mInStockItems.get(position) instanceof InProcessing) {
                InProcessing inProcessing = (InProcessing) mInStockItems.get(position);
                processingHolder.mProgress.setText(inProcessing.getPercentage() + "%");
                processingHolder.mProcessingProgressBar.setProgress(inProcessing.getPercentage());
                processingHolder.mProgressLayout.setVisibility(View.VISIBLE);
                processingHolder.mProcessingProgressBar.setVisibility(View.VISIBLE);
            } else if (mInStockItems.get(position) instanceof Packed) {
                Packed packed = (Packed) mInStockItems.get(position);
                processingHolder.mProgress.setText(packed.getPercentage() + "%");
                processingHolder.mProcessingProgressBar.setProgress(packed.getPercentage());
                processingHolder.mProgressLayout.setVisibility(View.VISIBLE);
                processingHolder.mProcessingProgressBar.setVisibility(View.VISIBLE);
            } else {
                processingHolder.mProgressLayout.setVisibility(View.GONE);
                processingHolder.mProcessingProgressBar.setVisibility(View.GONE);
            }

            // set name id and date in position
            processingHolder.mParcelId.setText(String.valueOf(processingHolder.mProduct.getCodeNumber()));
            String name = processingHolder.mProduct.getGeneralDescription();
            if (name == null || name.equals("") || name.equals("null")) {
                processingHolder.mProductName.setText(mContext.getString(R.string.no_description));
                processingHolder.mProductName.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
            } else {
                processingHolder.mProductName.setText(processingHolder.mProduct.getGeneralDescription());
                processingHolder.mProductName.setTextColor(mContext.getResources().getColor(android.R.color.black));
            }
            processingHolder.mCreatedDate.setText(getFormattedDate(processingHolder.mProduct.getFieldTime()));

            // compute kilos from grams and set the result in weight label
            double realWeight = Double.parseDouble(processingHolder.mProduct.getRealWeight());
            double kg = realWeight / 1000;
            processingHolder.mWeight.setText(kg + "kg.");

        } else if (mInStockItems.get(position) instanceof InForming) {
            InFormingViewHolder processingHolder = (InFormingViewHolder) holder;
            processingHolder.mProduct = (InForming) mInStockItems.get(position);
            if (processingHolder.mProduct.getName() == null || processingHolder.mProduct.getName().equals("null"))
                processingHolder.mProduct.setName("");
            processingHolder.mParcelId.setText(processingHolder.mProduct.getCodeNumber());
            String name = processingHolder.mProduct.getName();
            if (name == null || name.equals("") || name.equals("null")) {
                processingHolder.mProductName.setText(mContext.getString(R.string.no_description));
                processingHolder.mProductName.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
            } else {
                processingHolder.mProductName.setText(processingHolder.mProduct.getName());
                processingHolder.mProductName.setTextColor(mContext.getResources().getColor(android.R.color.black));
            }
            processingHolder.mCreatedDate.setText(getFormattedDate(processingHolder.mProduct.getCreatedDate()));
            double kg = processingHolder.mProduct.getWeight() / 1000;
            processingHolder.mWeight.setText(kg + "kg.");
            processingHolder.mProcessingProgressBar.setVisibility(View.INVISIBLE);
            processingHolder.mProgressTitle.setVisibility(View.INVISIBLE);
        }
    }

    private String getFormattedDate(String createdDate) {
        Date today  = new Date();
        Date date = new Date();
        String formattedDate = "";
        try {
            if (createdDate != null && !createdDate.equals(""))
                date = input.parse(createdDate);
            formattedDate = output.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long diff = today.getTime() - date.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (days > 0) {
                formattedDate = formattedDate + " (" + days + " " + mContext.getString(R.string.days_ago) + ")";
            } else {
                formattedDate = formattedDate + " (" + mContext.getString(R.string.today) + ")";
            }
        }
        return formattedDate;
    }

    @Override
    public int getItemCount() {
        return mInStockItems.size();
    }

    public void refreshList(ArrayList<Object> objects) {
        mInStockItems.clear();
        mInStockItems.addAll(objects);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        mInStockItems.remove(position);
        notifyItemRemoved(position);
    }

    class InProcessingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mParcelId, mProductName, mCreatedDate, mProgress, mWeight, mProgressTitle, mWeightTitle, mCreatedDateTitle;
        final ProgressBar mProcessingProgressBar;
        final LinearLayout mProgressLayout;
        PUSParcel mProduct;

        public InProcessingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mParcelId = (TextView) itemView.findViewById(R.id.inProcessingParcelId);
            mProductName = (TextView) itemView.findViewById(R.id.inProcessingProductName);
            mCreatedDate = (TextView) itemView.findViewById(R.id.inProcessingCreatedDate);
            mProgress = (TextView) itemView.findViewById(R.id.inProcessingProgress);
            mWeight = (TextView) itemView.findViewById(R.id.inProcessingWeight);
            mProgressTitle = (TextView) itemView.findViewById(R.id.inProcessingProgressTitle);
            mProcessingProgressBar = (ProgressBar) itemView.findViewById(R.id.inProcessingProgressBar);
            mProgressLayout = (LinearLayout) itemView.findViewById(R.id.inProcessingItemCompletionLayout);
            mWeightTitle = (TextView) itemView.findViewById(R.id.inProcessingWeightTitle);
            mCreatedDateTitle = (TextView) itemView.findViewById(R.id.inProcessingCreatedDateTitle);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onInProcessingProductClick(mProduct, getAdapterPosition());
            }
        }
    }

    class InFormingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mParcelId, mProductName, mCreatedDate, mProgress, mWeight, mProgressTitle;
        final ProgressBar mProcessingProgressBar;
        InForming mProduct;

        public InFormingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mParcelId = (TextView) itemView.findViewById(R.id.inProcessingParcelId);
            mProductName = (TextView) itemView.findViewById(R.id.inProcessingProductName);
            mCreatedDate = (TextView) itemView.findViewById(R.id.inProcessingCreatedDate);
            mProgress = (TextView) itemView.findViewById(R.id.inProcessingProgress);
            mWeight = (TextView) itemView.findViewById(R.id.inProcessingWeight);
            mProgressTitle = (TextView) itemView.findViewById(R.id.inProcessingProgressTitle);
            mProcessingProgressBar = (ProgressBar) itemView.findViewById(R.id.inProcessingProgressBar);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onInFormingClick(mProduct, getAdapterPosition());
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Button mCombineBtn;
        final Button mCheckBtn;
        final Button mPhotosBtn;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mCombineBtn = (Button) itemView.findViewById(R.id.inStockAddsCombineParcelsBtn);
            mCheckBtn = (Button) itemView.findViewById(R.id.inStockAddsOrderCheckBtn);
            mPhotosBtn = (Button) itemView.findViewById(R.id.inStockAddsAdditionalPhotosBtn);

            mCombineBtn.setOnClickListener(this);
            mCheckBtn.setOnClickListener(this);
            mPhotosBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.inStockAddsCombineParcelsBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onCombineClick();
                    }
                    break;
                case R.id.inStockAddsOrderCheckBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onCheckOrderClick();
                    }
                    break;
                case R.id.inStockAddsAdditionalPhotosBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onAdditionalPhotosClick();
                    }
                    break;
            }
        }
    }

    public interface OnItemClickListener {

        void onInProcessingProductClick(PUSParcel processingPackage, int position);

        void onInFormingClick(InForming inForming, int position);

        void onCombineClick();

        void onCheckOrderClick();

        void onAdditionalPhotosClick();
    }
}
