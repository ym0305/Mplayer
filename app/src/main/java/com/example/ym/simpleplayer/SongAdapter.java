package com.example.ym.simpleplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by YM on 2017/3/14.
 */

public class SongAdapter extends BaseAdapter {
    private Context context;
    private List<Song> songList;

    public void setListItem(List<Song> songList){
        this.songList =songList;

    }

    public SongAdapter(Context context, List<Song> songList){
        this.context = context;
        this.songList = songList;

    }
    @Override
    public int getCount() {
        return songList.size();
    }


    @Override
    public Object getItem(int i) {
        return songList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item,null);

        }
        ImageView imageView = (ImageView)view.findViewById(R.id.imageview);
        Song song = songList.get(i);
        TextView filename = (TextView)view.findViewById(R.id.list_name);
        filename.setText(song.getTitle());
        TextView artist = (TextView)view.findViewById(R.id.list_artist);
        artist.setText(song.getArtist());

        return view;
    }

}
