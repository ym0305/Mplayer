package com.example.ym.simpleplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MyService extends Service {

    private static final String TAG = "MyService";

    private List<Song> songList;

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    private int position = -1;

    private boolean isPause;

    private int msg = -1;
    private int isplay = 0 ;
    private int ispause = 1;
    private int isreumse = 2;


    /*
    public IBinder binder = new MusicService();



    public class MusicService extends Binder{
        MyService getService(){
            return MyService.this;
        }

    }

    */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public MyService() {

    }



    @Override
    public void onCreate() {
        super.onCreate();


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (position < songList.size() - 1) {
                    position = position + 1;
                    play(position);
                }else if (position == songList.size()){
                    position = 0;
                    play(position);

                }
                Intent intent = new Intent("com.example.broadcast.UPDATE");
                intent.putExtra("position",position);
                sendBroadcast(intent);
            }
        });


        songList = Util.scanMusic(MyApplication.getContext());


        try{

            mediaPlayer.reset();
            mediaPlayer.setDataSource(songList.get(0).getUri());
            mediaPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();

        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            position = intent.getIntExtra("position", -1);
            msg = intent.getIntExtra("msg", -1);

        }catch(NullPointerException e){
            e.printStackTrace();
        }
        if (msg == isplay){
            play(position);

        }else if (msg == ispause){
            pause();

        }else if (msg == isreumse) {
            resume();

        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    private void play(int position){
        Log.d(TAG, "play: !!!!");


        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(songList.get(position).getUri());
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
        mediaPlayer.start();

        Log.d(TAG, "play: " + position);
    }

    private void pause(){

        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause = true;

        }
    }

    private void resume(){

        if (isPause){
            mediaPlayer.start();
            isPause = false;
        }
    }

    private void pre(){
        Log.d(TAG, "pre: test");
        play(position);

    }

    private void next(){
        Log.d(TAG, "next: test");
        play(position);


    }

    public static int druation(){
        int dura = -1;
        if (mediaPlayer != null) {
            dura = mediaPlayer.getDuration();
            Log.d(TAG, "druation: " + dura );
        }
        return  dura;
    }

}
