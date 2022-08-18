package com.minui.borrowthing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.R;
import com.minui.borrowthing.model.itemImage;
import com.minui.borrowthing.model.item;

import java.util.List;

public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.ViewHolder> {

    Context context;
    List<item> itemList;
    List<itemImage> itemImageList;

    public BorrowAdapter(Context context, List<item> itemList, List<itemImage> itemImageList) {
        this.context = context;
        this.itemList = itemList;
        this.itemImageList = itemImageList;
    }

    @NonNull
    @Override
    public BorrowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_row, parent, false);
        return new BorrowAdapter.ViewHolder(view);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull BorrowAdapter.ViewHolder holder, int position) {
        item item = itemList.get(position);
        // itemImage itemImage = itemImageList.get(position);

        // todo 이미지 불러오기
        holder.imgBorrow.setImageResource(R.drawable.ic_photo);
        holder.txtTitle.setText(item.getTitle());
        holder.txtPrice.setText(item.getPrice() + "");
        //holder.txtTag.setText(item.getTag);
        holder.txtTrade.setVisibility(item.getStatus());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBorrow;
        TextView txtTitle;
        TextView txtPrice;
        TextView txtTag;
        TextView txtTrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBorrow = itemView.findViewById(R.id.imgBorrow);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtTag = itemView.findViewById(R.id.txtTag);
            txtTrade = itemView.findViewById(R.id.txtTrade);
        }
    }
}
