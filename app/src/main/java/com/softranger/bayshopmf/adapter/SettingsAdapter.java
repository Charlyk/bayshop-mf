package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Setting;

import java.util.ArrayList;

/**
 * Created by macbook on 6/28/16.
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private ArrayList<Setting> mSettings;
    private OnSettingClickListener mOnSettingClickListener;

    public SettingsAdapter(ArrayList<Setting> settings) {
        mSettings = settings;
    }

    public void setOnSettingClickListener(OnSettingClickListener onSettingClickListener) {
        mOnSettingClickListener = onSettingClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSetting = mSettings.get(position);
        holder.mSettingIcon.setImageResource(holder.mSetting.getIconId());
        holder.mSettingTitle.setText(holder.mSetting.getTitle());
    }

    @Override
    public int getItemCount() {
        return mSettings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mSettingIcon;
        final TextView mSettingTitle;
        Setting mSetting;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mSettingIcon = (ImageView) itemView.findViewById(R.id.settingsItemIconLabel);
            mSettingTitle = (TextView) itemView.findViewById(R.id.settingsItemTitleLabel);
        }

        @Override
        public void onClick(View v) {
            if (mOnSettingClickListener != null) {
                mOnSettingClickListener.onSettingClick(mSetting, getAdapterPosition());
            }
        }
    }

    public interface OnSettingClickListener {
        void onSettingClick(Setting setting, int position);
    }
}
