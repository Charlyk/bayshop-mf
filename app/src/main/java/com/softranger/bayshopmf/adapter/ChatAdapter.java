package com.softranger.bayshopmf.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.chat.ChatMessage;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 7/21/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatMessage> mChatMessages;
    private OnMessageClickListener mOnMessageClickListener;

    private static final float DATAILS_H = 49f;

    private static final int INCOMING = 0, OUTGOING = 1, UNKNOWN = -1;

    public ChatAdapter(ArrayList<ChatMessage> chatMessages) {
        mChatMessages = chatMessages;
    }

    public void setOnMessageClickListener(OnMessageClickListener onMessageClickListener) {
        mOnMessageClickListener = onMessageClickListener;
    }

    public void addNewMessage(ChatMessage message) {
        mChatMessages.add(message);
        int index = mChatMessages.indexOf(message);
        notifyItemInserted(index);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = mChatMessages.get(position);
        switch (message.getMessageType()) {
            case income:
                return INCOMING;
            case outgoing:
                return OUTGOING;
            default:
                return UNKNOWN;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case INCOMING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_message, parent, false);
                break;
            case OUTGOING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_message, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = mChatMessages.get(position);
        holder.mChatMessage = message;
        holder.mDateLabel.setText(message.getDate());
        holder.mMessageLabel.setText(message.getMessage());
        holder.mAuthorLabel.setText(message.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView mDateLabel;
        final TextView mMessageLabel;
        final TextView mAuthorLabel;
        ChatMessage mChatMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            mDateLabel = (TextView) itemView.findViewById(R.id.incomingMessageDateLabel);
            mMessageLabel = (TextView) itemView.findViewById(R.id.incomingMessageTextLabel);
            mMessageLabel.setOnClickListener(this);
            mMessageLabel.setOnLongClickListener(this);
            mDateLabel.setVisibility(View.GONE);
            mAuthorLabel = (TextView) itemView.findViewById(R.id.incomingMessageAuthorLabel);
            mAuthorLabel.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            mMessageLabel.setSelected(!mMessageLabel.isSelected());
            boolean isSelected = mMessageLabel.isSelected();

            if (isSelected) {
                expandDetails(mAuthorLabel, mDateLabel, true);
            } else {
                expandDetails(mAuthorLabel, mDateLabel, false);
            }

            if (mOnMessageClickListener != null) {
                mOnMessageClickListener.onMessageClicked(mChatMessage, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnMessageClickListener != null) {
                mOnMessageClickListener.onMessageLongClick(mChatMessage, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    /**
     * Expand or collapse details texts
     * it can expand and collapse for now just two text views
     * @param first text view to be animated
     * @param second text view to be animated
     * @param expand if true texts will be expanded and set as visible else texts will
     *               be collapsed and set as gone
     */
    private void expandDetails(final TextView first, final TextView second, final boolean expand) {
        final float fromValue = expand ? 0 : first.getMeasuredHeight();
        final float toValue = expand ? DATAILS_H : 0;
        ValueAnimator animator = ValueAnimator.ofFloat(fromValue, toValue);
        animator.setDuration(300);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float height = (float) animation.getAnimatedValue();
                first.setHeight((int) height);
                second.setHeight((int) height);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (expand) {
                    first.setVisibility(View.VISIBLE);
                    second.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!expand) {
                    first.setVisibility(View.GONE);
                    second.setVisibility(View.GONE);
                }
                // set default height
                first.setHeight((int) (expand ? toValue : fromValue));
                second.setHeight((int) (expand ? toValue : fromValue));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public interface OnMessageClickListener {
        void onMessageClicked(ChatMessage message, int position);
        void onMessageLongClick(ChatMessage message, int position);
    }
}
