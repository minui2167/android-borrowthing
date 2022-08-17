package com.minui.borrowthing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.R;
import com.minui.borrowthing.model.Community;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    Context context;
    List<Community> communityList;

    public CommunityAdapter(Context context, List<Community> communityList) {
        this.context = context;
        this.communityList = communityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Community community = communityList.get(position);

        holder.txtTitle.setText(community.getTitle());
        holder.txtContent.setText(community.getContent());
    }


    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtContent;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);
            linearLayout = itemView.findViewById(R.id.linearLayout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
