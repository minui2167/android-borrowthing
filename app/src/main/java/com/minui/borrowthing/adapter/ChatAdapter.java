package com.minui.borrowthing.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.R;
import com.minui.borrowthing.model.ChatData;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatData> mDataset;
    private String myNickName;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtMsg;
        public TextView txtTime;
        public ImageView imgProfile;
        public CardView cardView;
        public View rootView;
        public MyViewHolder(View v) {
            super(v);
            txtMsg = v.findViewById(R.id.txtMsg);
            txtTime = v.findViewById(R.id.txtTime);
            imgProfile = v.findViewById(R.id.imgProfile);
            cardView = v.findViewById(R.id.cardView);
            rootView = v;

        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatAdapter(List<ChatData> myDataset, Context context, String myNickName) {
        //{"1","2"}
        mDataset = myDataset;
        this.myNickName = myNickName;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChatData chat = mDataset.get(position);

        holder.txtMsg.setText(chat.getMsg());
        holder.txtTime.setText(chat.getUpdatedAt());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //채팅 친 사람의 닉네임이 나의 닉네임이면 오른쪽 정렬
        if(chat.getNickname().equals(this.myNickName)) {
            params.weight = 1.0f;
            params.gravity = Gravity.RIGHT;
            holder.cardView.setLayoutParams(params);
            holder.imgProfile.setVisibility(View.GONE);
        }
        else {
            params.weight = 1.0f;
            params.gravity = Gravity.LEFT;
            holder.cardView.setLayoutParams(params);
            holder.imgProfile.setVisibility(View.VISIBLE);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        //삼항 연산자
        return mDataset == null ? 0 :  mDataset.size();
    }

    public ChatData getChat(int position) {
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addChat(ChatData chat) {
        mDataset.add(chat);
    }

}
