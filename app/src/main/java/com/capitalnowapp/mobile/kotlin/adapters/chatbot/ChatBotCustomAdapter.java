package com.capitalnowapp.mobile.kotlin.adapters.chatbot;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.kotlin.activities.ChatActivity;
import com.capitalnowapp.mobile.models.chatbot.ChatBotMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatBotCustomAdapter extends RecyclerView.Adapter {
    private List<ChatBotMessage> messageList;
    private ChatActivity chatActivity;

    public ChatBotCustomAdapter(List<ChatBotMessage> messageList, ChatActivity chatActivity) {
        this.messageList = messageList;
        this.chatActivity = chatActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cb, parent, false);
        return new CBVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CBVH) {
            ChatBotMessage msg = messageList.get(position);

            if (Objects.equals(msg.getMessageType(), Constants.CHATBOT.SAY_HELLO)) {
                ((CBVH) holder).llBotMsg.setVisibility(View.VISIBLE);
                ((CBVH) holder).llQuestions.setVisibility(View.GONE);
                ((CBVH) holder).llUserMsg.setVisibility(View.GONE);
                ((CBVH) holder).llOptions.setVisibility(View.GONE);

                String text1 = "Hey " + msg.getMessage();
                String text2 = holder.itemView.getContext().getString(R.string.cb_hello);

                SpannableString span1 = new SpannableString(text1);
                span1.setSpan(new AbsoluteSizeSpan(40), 0, text1.length(), SPAN_INCLUSIVE_INCLUSIVE);
                span1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text1.length(), SPAN_INCLUSIVE_INCLUSIVE);

                SpannableString span2 = new SpannableString(text2);
                span2.setSpan(new AbsoluteSizeSpan(35), 0, text2.length(), SPAN_INCLUSIVE_INCLUSIVE);

                CharSequence finalText = TextUtils.concat(span1, " ", span2);

                ((CBVH) holder).tvBotMessage.setText(finalText);
            } else if (Objects.equals(msg.getMessageType(), Constants.CHATBOT.QUESTIONS)) {
                ((CBVH) holder).llBotMsg.setVisibility(View.GONE);
                ((CBVH) holder).llUserMsg.setVisibility(View.GONE);
                ((CBVH) holder).llOptions.setVisibility(View.GONE);
                ((CBVH) holder).llQuestions.setVisibility(View.VISIBLE);

                ((CBVH) holder).rvQuestions.setAdapter(new ChatBotQuestionsAdapter(msg.getQuestionsList(), chatActivity));

            } else if (Objects.equals(msg.getMessageType(), Constants.CHATBOT.SELECTED_QUESTION)) {
                ((CBVH) holder).llBotMsg.setVisibility(View.GONE);
                ((CBVH) holder).llUserMsg.setVisibility(View.VISIBLE);
                ((CBVH) holder).llQuestions.setVisibility(View.GONE);
                ((CBVH) holder).llOptions.setVisibility(View.GONE);

                ((CBVH) holder).tvUserMessage.setText(msg.getMessage());
                if (chatActivity.userDetails.getFirstName() != null && !chatActivity.userDetails.getFirstName().equals("")
                        && chatActivity.userDetails.getFirstName().length() > 1) {
                    ((CBVH) holder).tvUserName.setText(chatActivity.userDetails.getFirstName().substring(0, 2).toUpperCase(Locale.ROOT));
                } else {

                }
            } else if (Objects.equals(msg.getMessageType(), Constants.CHATBOT.BOT_MESSAGE)) {
                ((CBVH) holder).llBotMsg.setVisibility(View.VISIBLE);
                ((CBVH) holder).llUserMsg.setVisibility(View.GONE);
                ((CBVH) holder).llQuestions.setVisibility(View.GONE);
                ((CBVH) holder).llOptions.setVisibility(View.VISIBLE);

                ((CBVH) holder).tvBotMessage.setText(msg.getMessage());

                if (msg.getOptionCount() == 1) {
                    ((CBVH) holder).tvOption1.setVisibility(View.VISIBLE);
                    ((CBVH) holder).tvOption1.setText(msg.getOption1());
                    ((CBVH) holder).tvOption1.setOnClickListener(v -> {
                        msg.setSelectedOption(msg.getOption1());
                        chatActivity.selectedOption(msg);
                    });
                }
               else if (msg.getOptionCount() == 2) {
                    ((CBVH) holder).tvOption1.setVisibility(View.VISIBLE);
                    ((CBVH) holder).tvOption1.setText(msg.getOption1());
                    ((CBVH) holder).tvOption1.setOnClickListener(v -> {
                        msg.setSelectedOption(msg.getOption1());
                        chatActivity.selectedOption(msg);
                    });

                    ((CBVH) holder).tvOption2.setVisibility(View.VISIBLE);
                    ((CBVH) holder).tvOption2.setText(msg.getOption2());
                    ((CBVH) holder).tvOption2.setOnClickListener(v -> {
                        msg.setSelectedOption(msg.getOption2());
                        chatActivity.selectedOption(msg);
                    });
                }
                else if (msg.getOptionCount() == 3) {

                    ((CBVH) holder).tvOption1.setVisibility(View.VISIBLE);
                    ((CBVH) holder).tvOption1.setText(msg.getOption1());
                    ((CBVH) holder).tvOption1.setOnClickListener(v -> {
                        msg.setSelectedOption(msg.getOption1());
                        chatActivity.selectedOption(msg);
                    });

                    ((CBVH) holder).tvOption2.setVisibility(View.VISIBLE);
                    ((CBVH) holder).tvOption2.setText(msg.getOption2());
                    ((CBVH) holder).tvOption2.setOnClickListener(v -> {
                        msg.setSelectedOption(msg.getOption2());
                        chatActivity.selectedOption(msg);
                    });

                    ((CBVH) holder).tvOption3.setVisibility(View.VISIBLE);
                    ((CBVH) holder).tvOption3.setText(msg.getOption3());
                    ((CBVH) holder).tvOption3.setOnClickListener(v -> {
                        msg.setSelectedOption(msg.getOption3());
                        chatActivity.selectedOption(msg);
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void reloadMsgs(@NotNull ArrayList<ChatBotMessage> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    private class CBVH extends RecyclerView.ViewHolder {
        private LinearLayout llBotMsg, llQuestions, llUserMsg, llOptions;
        private TextView tvBotMessage, tvUserMessage, tvUserName, tvOption1, tvOption2, tvOption3, tvOption4;
        private RecyclerView rvQuestions;

        public CBVH(View view) {
            super(view);
            llBotMsg = view.findViewById(R.id.llBotMsg);
            llQuestions = view.findViewById(R.id.llQuestion);
            llUserMsg = view.findViewById(R.id.llUserMsg);
            llOptions = view.findViewById(R.id.llOptions);
            tvBotMessage = view.findViewById(R.id.tvBotMessage);
            rvQuestions = view.findViewById(R.id.rvQuestions);
            tvUserMessage = view.findViewById(R.id.tvUserMessage);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvOption1 = view.findViewById(R.id.tvOption1);
            tvOption2 = view.findViewById(R.id.tvOption2);
            tvOption3 = view.findViewById(R.id.tvOption3);
            tvOption4 = view.findViewById(R.id.tvOption4);

            rvQuestions.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
