package com.example.ym.simpleplayer;

/**
 * Created by YM on 2017/3/17.
 */

public class Time {
    public static String  format(long duration){
        long min = 0;
        long sec = 0;
        min = duration / 1000 / 60;
        sec = ( duration / 1000 ) % 60;

        String time = String.valueOf( min + ":" + sec);


        return time;
    }
}
