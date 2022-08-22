package com.minui.borrowthing.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.R;

public class CommunityCommentAdapter extends RecyclerView.Adapter {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNickname;
        TextView txtComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNickname = itemView.findViewById(R.id.txtNickname);
            txtComment = itemView.findViewById(R.id.txtComment);
        }
    }
}
