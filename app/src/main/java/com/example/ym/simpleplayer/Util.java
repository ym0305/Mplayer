package com.example.ym.simpleplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YM on 2017/3/14.
 */

public class Util {
    private static final String TAG = "Util";


    public static List<Song> scanMusic(Context context){




        List<Song> songList  = new ArrayList<Song>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null ){
            return null;
        }
        if (cursor.moveToFirst()){
            do {




                Song song = new Song();
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));


                /**
                 * 判断是否为音乐，不是音乐则跳过记录数据阶段
                 */
                if (isMusic == 0){
                    continue;
                }



                song.setDuration(duration);
                song.setTitle(title);
                song.setFileName(fileName);
                song.setAlbum(album);
                song.setArtist(artist);
                song.setUri(uri);
                songList.add(song);


                //Log.d(TAG, "scanMusic: title " + song.getTitle());
                //Log.d(TAG, "scanMusic: artist " + song.getArtist());
                //Log.d(TAG, "scanMusic: album " + song.getAlbum());
                //Log.d(TAG, "scanMusic: uri " + song.getUri());
                //Log.d(TAG, "scanMusic: filename " + song.getFileName());
            }while (cursor.moveToNext());





        }

        cursor.close();
        return songList;



    }
}
