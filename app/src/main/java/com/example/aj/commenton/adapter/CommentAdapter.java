package com.example.aj.commenton.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aj.commenton.R;
import com.example.aj.commenton.model.Comment;
import com.example.aj.commenton.util.Utils;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private ArrayList<Comment> mCommentList;
    private Context mContext;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.mCommentList = comments;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.comment_list_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, final int position) {

        holder.comment.setText(mCommentList.get(position).getText());
        holder.author.setText(mCommentList.get(position).getAuthor());
        holder.time.setText(Utils.convertStringDate(mCommentList.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView comment;
        TextView author;
        TextView time;

        CommentViewHolder(View itemView) {
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.txt_comment);
            author = (TextView) itemView.findViewById(R.id.txt_author);
            time = (TextView) itemView.findViewById(R.id.txt_time);
        }
    }

}
