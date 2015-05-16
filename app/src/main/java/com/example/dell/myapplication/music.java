package com.example.dell.myapplication;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import java.util.ArrayList;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by Dell on 10/05/2015.
 */


    public class music extends Service implements
            MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Aong> songs;
    //current position
    private int songPosn;
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;


    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }
        @Override
        public void onCreate(){
        //create the service
            super.onCreate();
//initialize position
            songPosn=0;
//create player
            rand=new Random();
            player = new MediaPlayer();
            initMusicPlayer();
        }





    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }
    public void setList(ArrayList<Aong> theSongs){
        songs=theSongs;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
//
//            return false;

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class MusicBinder extends Binder {
        music getService() {
            return music.this;
        }
    }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if(player.getCurrentPosition()>0){
                mp.reset();
                playNext();

        }}

//        @Override
//        public boolean onError(MediaPlayer mp, int what, int extra) {
//            mp.reset();
//
//            return false;
//        }
//
//        @Override
//        public void onPrepared(MediaPlayer mp) {
//            mp.start();
//
//        }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }
    private final IBinder musicBind = new MusicBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void playSong(){
        //play a song
        player.reset();
        //get song
        Aong playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
//get id
        long currSong = playSong.getID();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void go(){
        player.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
        .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }


    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){

            if(shuffle){
                int newSong = songPosn;
                while(newSong==songPosn){
                    newSong=rand.nextInt(songs.size());
                }
                songPosn=newSong;
            }
            else{
                songPosn++;
                if(songPosn>=songs.size()) songPosn=0;
            }
            playSong();
        }


    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    }
