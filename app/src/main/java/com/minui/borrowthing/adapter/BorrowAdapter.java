package com.minui.borrowthing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.R;
import com.minui.borrowthing.model.Borrow;

import java.util.List;

public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.ViewHolder> {

    Context context;
    List<Borrow> borrowList;

    public BorrowAdapter(Context context, List<Borrow> borrowList) {
        this.context = context;
        this.borrowList = borrowList;
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
        Borrow borrow = borrowList.get(position);

        // todo 이미지 불러오기
        holder.imgBorrow.setImageResource(R.drawable.ic_photo);
        holder.txtTitle.setText(borrow.getTxtTitle());
        holder.txtPrice.setText(borrow.getTxtPrice());
        holder.txtTag.setText(borrow.getTxtTag());
        holder.txtTrade.setVisibility(borrow.getTraded());
    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgBorrow;
        TextView txtTitle;
        TextView txtPrice;
        TextView txtTag;
        TextView txtTrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imgBorrow = itemView.findViewById(R.id.imgBorrow);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtTag = itemView.findViewById(R.id.txtTag);
            txtTrade = itemView.findViewById(R.id.txtTrade);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
