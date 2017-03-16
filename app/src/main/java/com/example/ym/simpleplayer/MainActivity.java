package com.example.ym.simpleplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static Boolean IS_PLAY = false;    //判断当前是否播放

    private ImageButton pre;
    private ImageButton play;
    private ImageButton next;
    private SeekBar seekBar;
    private Toolbar toolbar;
    private TextView showName;
    private TextView musicNumber;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private TextView artist;
    public MyService musicService;
    private ObjectAnimator discAnimation;
    private ObjectAnimator needleAnimation;
    private ImageView disc;
    private ImageView needle;


    private static final String TAG = "MainActivity";

    private int position = -1;

    private int length;                 //得到songlist的大小

    private List<Song> songList;

    private int isPlay = 0 ;
    private int isPause = 1;
    private int isReumse = 2;
    private int isNext = 3;
    private int isPre = 4;


    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ( ( MyService.MusicService ) service ).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    private void init(){
        pre = (ImageButton)findViewById(R.id.pre_button);
        next = (ImageButton)findViewById(R.id.next_button);
        play = (ImageButton)findViewById(R.id.play_button);
        showName = (TextView)findViewById(R.id.show_name);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        artist = (TextView)findViewById(R.id.textView2);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerview);
        musicNumber = (TextView)findViewById(R.id.number);
        disc = (ImageView) findViewById(R.id.disc);
        needle = (ImageView) findViewById(R.id.needle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setAnimations();

        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setOnClickListener(this);


        musicService = new MyService();

        /**
         * 运行时权限
         */
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            bindConnect();
            initView();
        }
    }

    private void setAnimations() {

        discAnimation = ObjectAnimator.ofFloat(disc, "rotation", 0, 360);
        discAnimation.setDuration(20000);
        discAnimation.setInterpolator(new LinearInterpolator());
        discAnimation.setRepeatCount(ValueAnimator.INFINITE);

        /*
        needleAnimation = ObjectAnimator.ofFloat(needle, "rotation", 0, 25);
        needle.setPivotX(0);
        needle.setPivotY(0);
        needleAnimation.setDuration(800);
        needleAnimation.setInterpolator(new LinearInterpolator());
        */
    }


    private void bindConnect(){
        Intent intent = new Intent(this,MyService.class);
        startService(intent);
        bindService(intent , sc , BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    /**
     * 初始化界面
     */
    private void initView(){

        listView = (ListView) findViewById(R.id.list_view);
        final List<Song> songList = Util.scanMusic(getApplicationContext());
        SongAdapter adapter = new SongAdapter(this, songList);
        listView.setAdapter(adapter);
        final Intent intent = new Intent(this,MyService.class);
        length = songList.size();
        setSongList(songList);
        String stemp = String.valueOf(length);
        musicNumber.setText(stemp);

         /*
        * listView显示的歌曲点击播放功能
        * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                /**
                 *
                 * 判断是否点击同一个item
                 */
                if (position != i){

                    intent.putExtra("position",i);
                    intent.putExtra("msg",isPlay);
                    play();
                    setText(i);
                    startService(intent);

                }
                ChangeIcon(true);
                position = i;
                drawerLayout.closeDrawers();

            }
        });


    }

    public void ChangeIcon(boolean isPlay){
        if (isPlay){
            play.setImageResource(R.drawable.stop);
            IS_PLAY = isPlay;
        }else {
            play.setImageResource(R.drawable.play);
            IS_PLAY = isPlay;
        }

    }

    @Override
    protected void onDestroy() {
        unbindService(sc);
        super.onDestroy();


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

        Intent intent = new Intent(this,MyService.class);
        switch (view.getId()){
            case R.id.play_button:
                /**
                 * 第一次打开app，如果是第一次打开，则点击播放按钮从第一首歌开始播放
                 */
                if (position == -1){
                    position = 0;
                    intent.putExtra("position",position);
                    intent.putExtra("msg",isPlay);
                    ChangeIcon(true);
                    setText(position);
                    play();

                }else {
                    if (IS_PLAY){
                        intent.putExtra("msg",isPause);
                        ChangeIcon(false);
                        discAnimation.pause();         //暂停动画


                    }else{
                        intent.putExtra("msg",isReumse);
                        ChangeIcon(true);
                        discAnimation.resume();        //重新开始运行动画

                    }
                }
                break;
            case R.id.pre_button:
                preMusic(intent);

                break;
            case R.id.next_button:
                nextMusic(intent);

                break;
            default:
                break;
        }

        startService(intent);
    }

    public void play(){
        //needleAnimation.start();
        discAnimation.start();

    }

    private void nextMusic(Intent intent){
        if (position < length - 1) {
            position = position + 1;
            intent.putExtra("position", position);
            intent.putExtra("msg", isNext);
            setText(position);
            ChangeIcon(true);
            if(discAnimation != null) {
                discAnimation.end();
                play();
            }

        }else {
            Toast.makeText(this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
        }

    }

    private void preMusic(Intent intent){
        if (position > 0 ) {
            position = position - 1;

            intent.putExtra("position", position);
            intent.putExtra("msg", isPre);
            setText(position);
            ChangeIcon(true);
            if(discAnimation != null) {
                discAnimation.end();
                play();
            }
        }else {
            Toast.makeText(this, "已经没有上一首了", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSongList(List<Song> songList){
        this.songList = songList;
    }

    private void setText(int i){
        showName.setText(songList.get(i).getTitle());
        artist.setText(songList.get(i).getArtist());

    }

}
