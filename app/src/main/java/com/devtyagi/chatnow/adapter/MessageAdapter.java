package com.devtyagi.chatnow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devtyagi.chatnow.R;
import com.devtyagi.chatnow.databinding.ItemReceiveBinding;
import com.devtyagi.chatnow.databinding.ItemSendBinding;
import com.devtyagi.chatnow.model.Message;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;
    final int ITEM_SENT = 101;
    final int ITEM_RECEIVED = 102;

    String senderRoom, receiverRoom;

    public MessageAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass() == SentViewHolder.class) {
                SentViewHolder sentViewHolder = (SentViewHolder) holder;
                sentViewHolder.binding.imgReactionReceived.setImageResource(reactions[pos]);
                sentViewHolder.binding.imgReactionReceived.setVisibility(View.VISIBLE);
            } else {
                ReceivedViewHolder receivedViewHolder = (ReceivedViewHolder) holder;
                receivedViewHolder.binding.imgReactionSent.setImageResource(reactions[pos]);
                receivedViewHolder.binding.imgReactionSent.setVisibility(View.VISIBLE);
            }

            message.setReaction(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true;
        });


        if(holder.getClass() == SentViewHolder.class) {
            SentViewHolder sentViewHolder = (SentViewHolder) holder;
            sentViewHolder.binding.txtSentMsg.setText(message.getMessage());

            if(message.getReaction() >= 0) {
                sentViewHolder.binding.imgReactionReceived.setImageResource(reactions[message.getReaction()]);
                sentViewHolder.binding.imgReactionReceived.setVisibility(View.VISIBLE);
            } else {
                sentViewHolder.binding.imgReactionReceived.setVisibility(View.GONE);
            }

            sentViewHolder.binding.txtSentMsg.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });
        } else {
            ReceivedViewHolder receivedViewHolder = (ReceivedViewHolder) holder;
            receivedViewHolder.binding.txtReceivedMsg.setText(message.getMessage());

            if(message.getReaction() >= 0) {
                receivedViewHolder.binding.imgReactionSent.setImageResource(reactions[message.getReaction()]);
                receivedViewHolder.binding.imgReactionSent.setVisibility(View.VISIBLE);
            } else {
                receivedViewHolder.binding.imgReactionSent.setVisibility(View.GONE);
            }

            receivedViewHolder.binding.txtReceivedMsg.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSendBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    class ReceivedViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

}
