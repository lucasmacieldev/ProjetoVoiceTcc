package com.example.lucasmaciel.testevoice.genericalarmclock.model;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.lucasmaciel.testevoice.R;

import java.io.IOException;
import java.util.Random;

/**
 * Created by cezar on 12/9/16.
 */
public class DeezerInformation
{
    private MediaPlayer mPlayer;
    public void tocaMP3(String urlMP3) throws IOException
    {
        try {
            mPlayer = new MediaPlayer ();
            String url = urlMP3;
            mPlayer.setDataSource(url);
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
        }catch(Exception exc){
            throw exc;
        }
    }
    public void tocaMP3Local(Context ctx)
    {
        mPlayer = MediaPlayer.create(ctx, R.raw.deezer);
        mPlayer.start();
    }
    public String recuperaQualquerTom()
    {
        String[] vetTons = {"c","d","e","f","g","a","b",
                "c min","d min","e min","f min","g min","a min","b min"};
        Random numRand = new Random ();
        return vetTons[numRand.nextInt(vetTons.length- 1)];
    }
    public void paraMp3()
    {
        mPlayer.stop();
    }
}
