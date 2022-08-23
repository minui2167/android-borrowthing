package com.minui.borrowthing.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minui.borrowthing.CommunityDetailActivity;
import com.minui.borrowthing.R;
import com.minui.borrowthing.model.CommunityComment;

import java.util.List;

public class CommunityCommentAdapter extends RecyclerView.Adapter<CommunityCommentAdapter.ViewHolder> {

    Context context;
    List<CommunityComment> communityCommentList;

    public CommunityCommentAdapter(Context context, List<CommunityComment> communityCommentList) {
        this.context = context;
        this.communityCommentList = communityCommentList;
    }

    @NonNull
    @Override
    public CommunityCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        return new CommunityCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityCommentAdapter.ViewHolder holder, int position) {
        CommunityComment communityComment = communityCommentList.get(position);

        holder.txtNickname.setText(communityComment.getNickname());
        holder.txtComment.setText(communityComment.getComment());
        if (communityComment.getIsAuthor() == 0) {
            holder.btnRevise.setVisibility(View.GONE);
            holder.btnTrash.setVisibility(View.GONE);
        } else {
            holder.btnRevise.setVisibility(View.VISIBLE);
            holder.btnRevise.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return communityCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNickname;
        TextView txtComment;
        ImageView btnRevise;
        ImageView btnTrash;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNickname = itemView.findViewById(R.id.txtNickname);
            txtComment = itemView.findViewById(R.id.txtComment);
            btnRevise = itemView.findViewById(R.id.btnRevise);
            btnTrash = itemView.findViewById(R.id.btnTrash);

            btnRevise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText editText = new EditText(context);

                    AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                    dlg.setTitle("댓글 수정");
                    dlg.setView(editText);
                    dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int index = getAdapterPosition();
                            ((CommunityDetailActivity) context).reviseComment(editText.getText().toString().trim(), index);
                        }
                    });
                    dlg.setNegativeButton("취소", null);
                    dlg.show();

                }
            });

            btnTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                    dlg.setTitle("댓글 삭제");
                    dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int index = getAdapterPosition();
                            ((CommunityDetailActivity) context).deleteComment(index);
                        }
                    });
                    dlg.setNegativeButton("취소", null);
                    dlg.show();
                }
            });
        }
    }
}
