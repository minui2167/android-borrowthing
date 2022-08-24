package com.minui.borrowthing.adapter;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.minui.borrowthing.BorrowDetailActivity;
import com.minui.borrowthing.FirstFragment;
import com.minui.borrowthing.MainActivity;
import com.minui.borrowthing.R;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.model.item;

import java.util.List;

public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.ViewHolder> {

    Context context;
    List<item> itemList;

    public BorrowAdapter(Context context, List<item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public BorrowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_row, parent, false);
        return new BorrowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowAdapter.ViewHolder holder, int position) {
        item item = itemList.get(position);

        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(0).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(context).load(url).into(holder.imgBorrow);
        } catch (Exception e) {
            holder.imgBorrow.setImageResource(R.drawable.nolmage);
        }
        holder.txtTitle.setText(item.getTitle());
        holder.txtPrice.setText(item.getPrice() + "");

        String tagList = "";
        for (int i = 0;i < item.getTag().size();i++) {
            tagList += item.getTag().get(i).getTagName() + ", ";
        }
        tagList = tagList.substring(0, tagList.length() - 2);
        holder.txtTag.setText(tagList);

        if (item.getStatus() == 1) {
            holder.txtTrade.setVisibility(View.VISIBLE);
            holder.txtTrade.setText("거래중");
        } else if (item.getStatus() == 0){
            holder.txtTrade.setVisibility(View.INVISIBLE);
        } else {
            holder.txtTrade.setVisibility(View.VISIBLE);
            holder.txtTrade.setText("거래완료");
        }

        if (item.getIsWish() == 0) {
            holder.imgConcerned.setImageResource(R.drawable.ic_heart);
        } else {
            holder.imgConcerned.setImageResource(R.drawable.heart_red);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        ImageView imgBorrow;
        TextView txtTitle;
        TextView txtPrice;
        TextView txtTag;
        TextView txtTrade;
        ImageView imgConcerned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            imgBorrow = itemView.findViewById(R.id.imgBorrow);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtTag = itemView.findViewById(R.id.txtTag);
            txtTrade = itemView.findViewById(R.id.txtTrade);
            imgConcerned = itemView.findViewById(R.id.imgConcerned);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    Intent intent = new Intent(context, BorrowDetailActivity.class);
                    intent.putExtra("item", itemList.get(index));
                    context.startActivity(intent);
                }
            });

            imgConcerned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    ((FirstFragment) ((MainActivity) context).firstFragment).setConcerned(index);
                }
            });
        }
    }
}
