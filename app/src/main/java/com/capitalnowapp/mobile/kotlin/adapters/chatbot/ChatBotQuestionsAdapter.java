package com.capitalnowapp.mobile.kotlin.adapters.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.kotlin.activities.ChatActivity;

import java.util.ArrayList;

public class ChatBotQuestionsAdapter extends RecyclerView.Adapter {
    private ArrayList<String> messageList;
    private ChatActivity chatActivity;

    public ChatBotQuestionsAdapter(ArrayList<String> messageList, ChatActivity chatActivity) {
        this.messageList = messageList;
        this.chatActivity = chatActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cb_question, parent, false);
        return new CBVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CBVH) {
            ((CBVH) holder).tvQuestion.setText(messageList.get(position));

            ((CBVH) holder).tvQuestion.setOnClickListener(v -> {
                setSelectedQuestion(messageList.get(position));
            });

            if(position == messageList.size() -1){
                ((CBVH) holder).view.setVisibility(View.GONE);
            }
        }
    }

    private void setSelectedQuestion(String msg) {
        chatActivity.selectedQuestion(msg);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    private class CBVH extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvQuestion;

        public CBVH(View view) {
            super(view);
            tvQuestion = view.findViewById(R.id.tvQuestion);
            this.view = view.findViewById(R.id.view);
        }
    }
}
