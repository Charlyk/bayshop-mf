package com.softranger.bayshopmfr.adapter;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.chat.MailMessage;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.ViewAnimator;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 7/20/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class MailAdapter extends RecyclerView.Adapter<MailAdapter.ViewHolder> {

    private ArrayList<MailMessage> mMessages;
    private OnMailClickListener mOnMailClickListener;

    public MailAdapter(ArrayList<MailMessage> mailMessages) {
        mMessages = mailMessages;
    }

    public void setOnMailClickListener(OnMailClickListener onMailClickListener) {
        mOnMailClickListener = onMailClickListener;
    }

    public void addMessages(ArrayList<MailMessage> mailMessages) {
        final int initIndex = mMessages.size() - 1;
        mMessages.addAll(mailMessages);
        notifyItemRangeInserted(initIndex, mailMessages.size());
    }

    public void deleteMessages(ArrayList<MailMessage> mailMessages) {
        for (MailMessage message : mailMessages) {
            final int index = mMessages.indexOf(message);
            mMessages.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void deleteMessage(MailMessage message) {
        final int itemIndex = mMessages.indexOf(message);
        mMessages.remove(itemIndex);
        notifyItemRemoved(itemIndex);
    }

    public void insertMessage(MailMessage message) {
        mMessages.add(0, message);
        notifyItemInserted(0);
    }

    public void refreshList(ArrayList<MailMessage> mailMessages) {
        mMessages.clear();
        mMessages.addAll(mailMessages);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    } // TODO: 7/20/16 set to list size

    @DrawableRes
    public int getMailStatusImage(boolean isRead) {
        return isRead ? R.mipmap.ic_read_mail_35dp : R.mipmap.ic_open_mail_35dp;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,
            ViewAnimator.AnimationListener {

        final ImageView mMailIcon;
        final TextView mTitleLabel;
        final TextView mMessageLabel;
        final TextView mTimeLabel;
        MailMessage mMailMessage;
        ViewAnimator mViewAnimator;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mMailIcon = (ImageView) itemView.findViewById(R.id.mailItemImageView);
            mMailIcon.setOnClickListener(this);

            mTitleLabel = (TextView) itemView.findViewById(R.id.mailItemTitleLabel);
            mMessageLabel = (TextView) itemView.findViewById(R.id.mailItemMessageLabel);
            mTimeLabel = (TextView) itemView.findViewById(R.id.mailItemTimeLabel);

            mViewAnimator = new ViewAnimator();
            mViewAnimator.setAnimationListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnMailClickListener == null) return;
            switch (v.getId()) {
                case R.id.mailItemImageView:
//                    mMailMessage.setSelected(!mMailMessage.isSelected());
                    mOnMailClickListener.onMessageIconClicked(mMailMessage, getAdapterPosition(), true);
                    mViewAnimator.flip(mMailIcon);
                    break;
                default:
                    mOnMailClickListener.onMessageClicked(mMailMessage, getAdapterPosition(), true);
                    break;
            }
        }

        @Override
        public void onAnimationStarted() {
            if (mMailMessage != null) {
                @ColorInt int color = mMailMessage.isSelected() ? Application.getInstance().getResources().getColor(R.color.colorSelection) :
                        Application.getInstance().getResources().getColor(R.color.colorPrimary);
                itemView.setBackgroundColor(color);
            }
        }

        @Override
        public void onAnimationFinished() {
            if (mMailMessage != null)
                mMailIcon.setImageResource(
                        mMailMessage.isSelected() ? R.mipmap.ic_check_35dp : getMailStatusImage(mMailMessage.isRead())
                );
        }

        @Override
        public boolean onLongClick(View v) {
            if (mMailMessage != null && mOnMailClickListener != null) {
                mMailMessage.setSelected(!mMailMessage.isSelected());
                mOnMailClickListener.onMessageClicked(mMailMessage, getAdapterPosition(), mMailMessage.isSelected());
            }
            mViewAnimator.flip(mMailIcon);
            return true;
        }
    }

    public interface OnMailClickListener {
        void onMessageIconClicked(MailMessage message, int position, boolean isSelected);

        void onMessageClicked(MailMessage message, int position, boolean isSelected);
    }
}
