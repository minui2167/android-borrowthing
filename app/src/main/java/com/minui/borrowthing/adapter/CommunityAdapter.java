package com.minui.borrowthing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.minui.borrowthing.MainActivity;
import com.minui.borrowthing.R;
import com.minui.borrowthing.config.Config;
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
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_row, parent, false);
        return new CommunityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Community community = communityList.get(position);

        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + community.getImgUrl().get(0).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(context).load(url).into(holder.imgCommunity);
        } catch (Exception e) {
            holder.imgCommunity.setImageResource(R.drawable.ic_photo);
        }

        holder.txtContent.setText(community.getContent());
        holder.txtLikes.setText(community.getLikesCount() + "");
        if (community.getIsLike() == 0) {
            holder.imgThumb.setImageResource(R.drawable.ic_thumb_outline);
        } else {
            holder.imgThumb.setImageResource(R.drawable.ic_thumb);
        }
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView txtContent;
        ImageView imgCommunity;
        ImageView imgThumb;
        TextView txtLikes;
        LinearLayout linearLayoutLike;
        LinearLayout linearLayoutComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            txtContent = itemView.findViewById(R.id.txtContent);
            imgCommunity = itemView.findViewById(R.id.imgCommunity);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtLikes = itemView.findViewById(R.id.txtLikes);
            linearLayoutLike = itemView.findViewById(R.id.linearLayoutLike);
            linearLayoutComment = itemView.findViewById(R.id.linearLayoutComment);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            linearLayoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    ((MainActivity) context).setLike(index);
                }
            });

            linearLayoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
