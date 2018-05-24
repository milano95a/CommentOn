package com.example.aj.commenton.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aj.commenton.Constants;
import com.example.aj.commenton.R;
import com.example.aj.commenton.UI.AlbumItemActivity;
import com.example.aj.commenton.model.Album;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{

    private ArrayList<Album> mAlbumList;
    private Context mContext;

    public AlbumAdapter(Context context, ArrayList<Album> albums) {
        this.mAlbumList = albums;
        this.mContext = context;
    }

    @NonNull
    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.album_list_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.AlbumViewHolder holder, final int position) {

        if(mAlbumList.get(position).isLast){
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.name.setVisibility(View.GONE);
            holder.songCount.setVisibility(View.GONE);
            holder.releaseDate.setVisibility(View.GONE);
        }else{
            holder.progressBar.setVisibility(View.GONE);
            holder.name.setVisibility(View.VISIBLE);
            holder.songCount.setVisibility(View.VISIBLE);
            holder.releaseDate.setVisibility(View.VISIBLE);

            holder.name.setText(mAlbumList.get(position).getName());
            holder.songCount.setText(String.valueOf(mAlbumList.get(position).getSongCount()));
            holder.releaseDate.setText(mAlbumList.get(position).getReleaseDate().substring(0,4));

            holder.albumItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Album currentAlbum = mAlbumList.get(position);
                    Gson gson = new Gson();
                    String albumDataObjectAsAString = gson.toJson(currentAlbum);

                    Intent intent = new Intent(mContext, AlbumItemActivity.class);
                    intent.putExtra(Constants.ALBUM_KEY, albumDataObjectAsAString);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView songCount;
        TextView releaseDate;
        ProgressBar progressBar;
        LinearLayout albumItem;

        AlbumViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            songCount = (TextView) itemView.findViewById(R.id.txt_song_count);
            releaseDate = (TextView) itemView.findViewById(R.id.txt_release_date);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            albumItem = (LinearLayout)itemView.findViewById(R.id.album_item);
        }
    }
}
