package com.softranger.bayshopmf.ui.contact;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ChatAdapter;
import com.softranger.bayshopmf.model.chat.ChatMessage;
import com.softranger.bayshopmf.model.chat.MailMessage;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

public class ChatActivity extends ParentActivity implements TextWatcher, ChatAdapter.OnMessageClickListener,
        MenuItem.OnMenuItemClickListener {

    private static final int UPLOAD_RESULT_CODE = 12;
    private TextView mToolbarTitle;
    private ImageButton mSendButton;
    private EditText mMessageInput;
    private ChatAdapter mAdapter;
    private ArrayList<ChatMessage> mChatMessages;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent chat = getIntent();
        MailMessage message = chat.getExtras().getParcelable("message");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mMessageInput = (EditText) findViewById(R.id.sendMessageInput);
        mMessageInput.addTextChangedListener(this);
        mSendButton = (ImageButton) findViewById(R.id.sendMessageGoBtn);
        mSendButton.setClickable(false);

        mChatMessages = new ArrayList<>();

        ChatMessage chatMessage = new ChatMessage.Builder()
                .message("Ce vrei de la noi?")
                .author("Eduard albu")
                .date("21 Jul 2016")
                .messageType(ChatMessage.MessageType.income)
                .build();
        mChatMessages.add(chatMessage);

        mAdapter = new ChatAdapter(mChatMessages);
        mAdapter.setOnMessageClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void sendMessage(View view) {
        String messageText = String.valueOf(mMessageInput.getText());
        if (messageText.length() <= 0) return;
        String fullName = Application.user.getFirstName() + " " + Application.user.getLastName();
        ChatMessage message = new ChatMessage.Builder()
                .message(messageText)
                .author(fullName)
                .date("21 Jul 2016")
                .messageType(ChatMessage.MessageType.outgoing)
                .build();
        mAdapter.addNewMessage(message);
        mMessageInput.setText("");
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    //------------------- Edit text listener -------------------//
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            mSendButton.setClickable(true);
            mSendButton.setImageResource(R.mipmap.ic_send_red_24dp);
        } else {
            mSendButton.setClickable(false);
            mSendButton.setImageResource(R.mipmap.ic_send_24dp);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //------------------- Menu -------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        MenuItem closeTicketItem = menu.findItem(R.id.closeTicket);
        MenuItem attachItem = menu.findItem(R.id.attachFile);
        attachItem.setOnMenuItemClickListener(this);
        closeTicketItem.setOnMenuItemClickListener(this);

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.closeTicket:

                break;
            case R.id.attachFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, UPLOAD_RESULT_CODE);
                return true;
        }
        return false;
    }

    //------------------- Adapter listener -------------------//
    @Override
    public void onMessageClicked(ChatMessage message, int position) {

    }

    @Override
    public void onMessageLongClick(ChatMessage message, int position) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", message.getMessage());
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    //------------------- Parent activity methods -------------------//
    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {

    }

    @Override
    public void toggleLoadingProgress(boolean show) {

    }

    @Override
    public void replaceFragment(ParentFragment fragment) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackStackChanged() {

    }
}
