package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.ChatMessage;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 7/21/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatMessage> mChatMessages;
    private OnMessageClickListener mOnMessageClickListener;

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
            mAuthorLabel = (TextView) itemView.findViewById(R.id.incomingMessageAuthorLabel);
        }

        @Override
        public void onClick(View v) {
            mMessageLabel.setSelected(!mMessageLabel.isSelected());
            boolean isSelected = mMessageLabel.isSelected();
            mDateLabel.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            mAuthorLabel.setVisibility(isSelected ? View.VISIBLE : View.GONE);
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

    public interface OnMessageClickListener {
        void onMessageClicked(ChatMessage message, int position);
        void onMessageLongClick(ChatMessage message, int position);
    }
}
