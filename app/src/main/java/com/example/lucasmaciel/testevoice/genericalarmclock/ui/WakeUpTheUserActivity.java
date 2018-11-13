package com.example.lucasmaciel.testevoice.genericalarmclock.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucasmaciel.testevoice.R;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.DeezerInformation;

/**
 * Created by cezar on 12/8/16.
 */
public class WakeUpTheUserActivity extends AppCompatActivity
{
    private DeezerInformation vo_despertador;
    private TextView txtResultado;
    private Button pausar;

    private int idCorreto;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wakeuptheuser);

        pausar = (Button) findViewById(R.id.btnPausar);
        txtResultado = (TextView) findViewById(R.id.txtResultado);
        vo_despertador = new DeezerInformation();

        Intent intent = new Intent ("ALARME_DISPARADO");
        PendingIntent p = PendingIntent.getBroadcast (this, 0 ,intent, 0);

        mediaPlayer = MediaPlayer.create(this, R.raw.deezer);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        Toast.makeText(getApplicationContext (), "ALARM!! ALARM!!", Toast.LENGTH_SHORT).show();

        findViewById(R.id.btnPausar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stop alarm manager
                stopPlaying();
            }
        });
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
