package com.minui.borrowthing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        public TextView TextView_msg;
        public TextView TextView_time;
        public ImageView imgProfile;
        public View rootView;
        public MyViewHolder(View v) {
            super(v);
            TextView_msg = v.findViewById(R.id.TextView_msg);
            TextView_time = v.findViewById(R.id.textView_time);
            imgProfile = v.findViewById(R.id.imgProfile);
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

        holder.TextView_msg.setText(chat.getMsg());
        holder.TextView_time.setText(chat.getUpdatedAt());

        if(chat.getNickname().equals(this.myNickName)) {
            holder.TextView_msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.TextView_time.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.imgProfile.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.imgProfile.setVisibility(View.GONE);
        }
        else {
            holder.TextView_msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.TextView_time.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.imgProfile.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.imgProfile.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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
        notifyItemInserted(mDataset.size()-1); //갱신
    }

}
