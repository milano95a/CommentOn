package com.example.aj.commenton.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.aj.commenton.R;
import com.example.aj.commenton.network.retrofit.model.Album;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{

    private ArrayList<Album> mAlbumList;

    public AlbumAdapter(ArrayList<Album> albums) {
        this.mAlbumList = albums;
    }

    @NonNull
    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.album_list_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.AlbumViewHolder holder, int position) {
        holder.name.setText(mAlbumList.get(position).getName());
        holder.songCount.setText(String.valueOf(mAlbumList.get(position).getSongCount()));
        holder.releaseDate.setText(mAlbumList.get(position).getReleaseDate());
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView songCount;
        TextView releaseDate;

        AlbumViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            songCount = (TextView) itemView.findViewById(R.id.txt_song_count);
            releaseDate = (TextView) itemView.findViewById(R.id.txt_release_date);
        }
    }
}
