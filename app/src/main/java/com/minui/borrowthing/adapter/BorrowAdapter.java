package com.minui.borrowthing.adapter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
import com.minui.borrowthing.BorrowByCategoryActivity;
import com.minui.borrowthing.BorrowDetailActivity;
import com.minui.borrowthing.BorrowDetailStatusIngActivity;
import com.minui.borrowthing.EvaluationActivity;
import com.minui.borrowthing.FirstFragment;
import com.minui.borrowthing.MainActivity;
import com.minui.borrowthing.R;
import com.minui.borrowthing.SearchFragment;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.model.item;

import java.util.List;

public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.ViewHolder> {

    Context context;
    List<item> itemList;
    String calledContext;

    public BorrowAdapter(Context context, List<item> itemList, String calledContext) {
        this.context = context;
        this.itemList = itemList;
        this.calledContext = calledContext;
    }

    @NonNull
    @Override
    public BorrowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_row, parent, false);
//        if (calledContext.equals("purchaseHistoryStatus2NotRating"))
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_not_rating_row, parent, false);
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
        holder.txtPrice.setText(item.getPrice() + "원");

        String tagList = "";
        for (int i = 0;i < item.getTag().size();i++) {
            tagList += " #" + item.getTag().get(i).getTagName();
        }
        try {
//            tagList = tagList.substring(0, tagList.length() - 2);
        } catch (Exception e) {

        }
        holder.txtTag.setText(tagList);
        holder.txtEmdName.setText(item.getEmdName());
        holder.txtnickName.setText(item.getNickname());
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
        TextView txtEmdName;
        TextView txtnickName;
        TextView txtTag;
        TextView txtTrade;
        ImageView imgConcerned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            imgBorrow = itemView.findViewById(R.id.imgBorrow);
            txtTitle = itemView.findViewById(R.id.txtKeyword);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtnickName = itemView.findViewById(R.id.txtNickname);
            txtTag = itemView.findViewById(R.id.txtTag);
            txtEmdName = itemView.findViewById(R.id.txtEmdName);
            txtTrade = itemView.findViewById(R.id.txtTrade);
            imgConcerned = itemView.findViewById(R.id.imgConcerned);


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(calledContext.equals("firstFragment") || calledContext.equals("borrowByCategory") || calledContext.equals("searchFragment")) {
                        int index = getAdapterPosition();
                        Intent intent = new Intent(context, BorrowDetailActivity.class);
                        intent.putExtra("item", itemList.get(index));
                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                    }
                    else if(calledContext.equals("purchaseHistoryStatus2NotRating")){
                        int index = getAdapterPosition();
                        Intent intent = new Intent(context, EvaluationActivity.class);
                        intent.putExtra("item", itemList.get(index));
                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                    }
                    else if(calledContext.equals("purchaseHistoryStatus1") || calledContext.equals("salesHistoryStatus1")){
                        int index = getAdapterPosition();
                        Intent intent = new Intent(context, BorrowDetailStatusIngActivity.class);
                        intent.putExtra("item", itemList.get(index));
                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                    }
                    else if(calledContext.equals("salesHistoryStatus0")){
                        int index = getAdapterPosition();
                        Intent intent = new Intent(context, BorrowDetailActivity.class);
                        intent.putExtra("item", itemList.get(index));
                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                    }
                }

            });

            imgConcerned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    if(calledContext.equals("firstFragment"))
                        ((FirstFragment) ((MainActivity) context).firstFragment).setConcerned(index);
                    else if(calledContext.equals("borrowByCategory"))
                        ((BorrowByCategoryActivity)context).setConcerned(index);
                    else if(calledContext.equals("searchFragment"))
                        ((SearchFragment) ((MainActivity) context).searchFragment).setConcerned(index);

                }
            });


        }
    }
}
