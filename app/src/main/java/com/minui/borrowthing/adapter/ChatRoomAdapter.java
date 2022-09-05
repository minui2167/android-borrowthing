package com.minui.borrowthing.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.ChatActivity;
import com.minui.borrowthing.R;
import com.minui.borrowthing.model.ChatRoom;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    Context context;
    List<ChatRoom> chatRoomList;
    String opponentNickname;
    public ChatRoomAdapter(Context context, List<ChatRoom> chatRoomList) {
        this.context = context;
        this.chatRoomList = chatRoomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_room_row, parent, false);

        return new ChatRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomList.get(position);

        holder.txtTitle.setText(chatRoom.getTitle());
        Log.i("sellerNickname", chatRoom.getSellerNickname());
        Log.i("buyerNickname", chatRoom.getBuyerNickname());
        if(chatRoom.getBuyerId() == chatRoom.getMyId()){
            holder.txtNick.setText(chatRoom.getSellerNickname());

        } else {
            holder.txtNick.setText(chatRoom.getBuyerNickname());
        }



    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView txtTitle;
        TextView txtNick;
        String nick = "";
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtTitle = itemView.findViewById(R.id.txtKeyword);
            txtNick = itemView.findViewById(R.id.txtNick);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatRoom chatRoom = chatRoomList.get(getAdapterPosition());

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("type", "");
                    intent.putExtra("chatRoom", chatRoom);
                    intent.putExtra("opponentNickname", txtNick.getText());

                    context.startActivity(intent);
                }
            });

        }
    }
}

