package com.minui.borrowthing.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.model.item;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.BorrowDetailActivity;
import com.minui.borrowthing.R;


import java.util.List;


public class BorrowRecommendAdapter extends RecyclerView.Adapter<BorrowRecommendAdapter.ViewHolder> {

    Context context;
    List<item> itemList;

    public BorrowRecommendAdapter(Context context, List<item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public BorrowRecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_row, parent, false);
        return new BorrowRecommendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowRecommendAdapter.ViewHolder holder, int position) {
       item item = itemList.get(position);

        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(0).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(context).load(url).into(holder.imgRecommend);
        } catch (Exception e) {
            holder.imgRecommend.setImageResource(R.drawable.nolmage);
        }
       holder.txtTitle.setText(item.getTitle());
       holder.txtPrice.setText(item.getPrice()+"원");
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgRecommend;
        TextView txtTitle;
        TextView txtPrice;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecommend = itemView.findViewById(R.id.imgRecommend);
            txtTitle = itemView.findViewById(R.id.txtKeyword);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            linearLayout = itemView.findViewById(R.id.linearLayout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    Intent intent = new Intent(context, BorrowDetailActivity.class);
                    intent.putExtra("item", itemList.get(index));
                    context.startActivity(intent);
                }
            });
        }
    }
}
