package com.example.ym.simpleplayer;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static Boolean IS_PLAY = false;    //判断当前是否播放

    private ImageButton pre;

    private ImageButton play;

    private ImageButton next;

    private TextView showName;

    private static final String TAG = "MainActivity";

    private ListView listView;

    private MediaPlayer mp = new MediaPlayer();

    private Song song ;

    private int position = -1;

    private int length;                 //得到songlist的大小

    private List<Song> songList;








    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pre = (ImageButton)findViewById(R.id.pre_button);
        next = (ImageButton)findViewById(R.id.next_button);
        play = (ImageButton)findViewById(R.id.play_button);
        showName = (TextView)findViewById(R.id.show_name);


        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setOnClickListener(this);


        /**
         * 运行时权限
         */
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }else {
            initView();

        }


        /**
         * 设置播放完后，自动播放下一曲
         */
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                NextMusic(position);
                position = position + 1;
            }
        });



    }


    /**
     * 初始化界面
     */
    private void initView(){

        listView = (ListView) findViewById(R.id.listview);
        final List<Song> songList = Util.scanMusic(getApplicationContext());
        SongAdapter adapter = new SongAdapter(this, songList);
        listView.setAdapter(adapter);
        setSongList(songList);

         /*
        * listView显示的歌曲点击播放功能
        * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                song = songList.get(i);
                setSongList(songList);

                /**
                 *
                 * 判断是否点击同一个item
                 */
                if (position == i){
                    if (IS_PLAY){
                        mp.start();
                        ChangeIcon(true);

                    }else {
                        mp.pause();
                        ChangeIcon(false);
                    }

                }else {
                    mp.reset();
                    PlayMusic(song);
                    ChangeIcon(true);
                }
                position = i;

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "allow", Toast.LENGTH_SHORT).show();
                    initView();

                }else {
                    Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }




    /*
    * 点击事件
    * */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play_button:
                /**
                 * 第一次打开app，如果是第一次打开，则点击播放按钮从第一首歌开始播放
                 */
                if (position == -1){
                    position = 0;
                    song = songList.get(position);
                    ChangeIcon(true);

                    PlayMusic(song);
                }else {
                    if (IS_PLAY){

                        ChangeIcon(true);
                        mp.start();
                    }else{

                        ChangeIcon(IS_PLAY);
                        mp.pause();

                    }
                }
                break;
            case R.id.pre_button:


                PreMusic(position);
                if (position > 0) {
                    position = position - 1;
                }
                break;


            case R.id.next_button:


                NextMusic(position);
                if (position < (length -1)) {
                    position = position + 1;
                }
                break;


            default:
                break;
        }
    }


    /*
    * 判断是否播放来改变播放图标
    * */
    public void ChangeIcon(boolean isPlay){
        if (isPlay){
            play.setImageResource(R.drawable.stop);
            IS_PLAY = false;
        }else {
            play.setImageResource(R.drawable.play);
            IS_PLAY = true;
        }

    }


    /*
    *
    * 播放音乐
    * */
    private void PlayMusic(Song song) {

        showName.setText(song.getTitle());
        try {
            mp.setDataSource(song.getUri());
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mp.start();




    }

    /**
     * 下一首音乐
     * @param position
     */
    private void NextMusic(int position){
        Song song;

        if (position < (length - 1)){
            song = songList.get(position + 1);
            mp.reset();
            PlayMusic(song);
            ChangeIcon(true);
        }else {
            Toast.makeText(this, "已经是最后一首歌了", Toast.LENGTH_SHORT).show();
        }



    }


    /**
     * 上一首音乐
     */
    private void PreMusic(int position){
        Song song;

        if (position > 0){
            song = songList.get(position - 1);
            mp.reset();
            PlayMusic(song);
            ChangeIcon(true);
        }else {
            Toast.makeText(this, "已经到第一首歌了", Toast.LENGTH_SHORT).show();
        }

    }


    private void setSongList(List<Song> songList1){
        songList = songList1;
        length = songList.size();

    }
}
