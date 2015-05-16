package com.example.dell.myapplication;

/**
 * Created by Dell on 10/05/2015.
 */
public class Aong {
    private long id;
    private String title;
    private String artist;

    public Aong(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
}
