package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InStockDetailed;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Photo;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/25/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class FirstStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<InStockItem> mInStockItems;
    private OnItemClickListener mOnItemClickListener;

    private static final int ALERT = 0, ITEM = 1;

    public FirstStepAdapter(ArrayList<InStockItem> inStockItems) {
        mInStockItems = inStockItems;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public InStockItem removeItem(int position) {
        InStockItem item = mInStockItems.get(position - 1);
        mInStockItems.remove(position - 1);
        notifyItemRemoved(position);
        return item;
    }

    public ArrayList<InStockItem> getList() {
        return mInStockItems;
    }

    public void insertItem(int position, InStockItem inStockItem) {
        mInStockItems.add(position, inStockItem);
        notifyItemInserted(position);
    }

    public void addItems(ArrayList<InStockItem> items) {
        mInStockItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return ALERT;
            default:
                return ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ALERT: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.warning_item,
                        parent, false);
                return new WarningHolder(view);
            }
            default: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.build_parcel_first_step_list_item, parent, false);
                return new ViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mInStockItem = mInStockItems.get(position - 1);
            viewHolder.mMfLabel.setText(viewHolder.mInStockItem.getParcelId());
            viewHolder.mNameLabel.setText(viewHolder.mInStockItem.getName());
            viewHolder.mWeightLabel.setText(String.valueOf(((float) viewHolder.mInStockItem.getWeight() / 1000)) + "kg.");
            viewHolder.mPriceLabel.setText(String.valueOf(viewHolder.mInStockItem.getCurrency()
                    + viewHolder.mInStockItem.getPrice()));
        } else if (holder instanceof WarningHolder) {
            WarningHolder warningHolder = (WarningHolder) holder;
            // TODO: 7/18/16 replace the text below with the correct one
            warningHolder.mLabel.setText("This is some warning for this screen, please pay attention to what is writen here.");
        }
    }

    @Override
    public int getItemCount() {
        return mInStockItems.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mMfLabel;
        final TextView mNameLabel;
        final TextView mPriceLabel;
        final TextView mWeightLabel;
        final ImageButton mDeleteButton;
        InStockItem mInStockItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mMfLabel = (TextView) itemView.findViewById(R.id.buildFirstStepMfIdLabel);
            mNameLabel = (TextView) itemView.findViewById(R.id.buildFirstStepNameLabel);
            mPriceLabel = (TextView) itemView.findViewById(R.id.buildFirstStepPriceLabel);
            mWeightLabel = (TextView) itemView.findViewById(R.id.buildFirstStepWeghtLabel);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.buildFirstDeleteButton);
            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onDeleteClick(mInStockItem, getAdapterPosition());
            }
        }
    }

    public class WarningHolder extends RecyclerView.ViewHolder {
        final TextView mLabel;
        public WarningHolder(View itemView) {
            super(itemView);
            mLabel = (TextView) itemView.findViewById(R.id.warningItemLabel);
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(InStockItem inStockItem, int position);
    }
}
