package com.example.lucasmaciel.testevoice.genericalarmclock;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucasmaciel.testevoice.R;
import com.example.lucasmaciel.testevoice.genericalarmclock.adapters.AlarmListAdapter;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.Alarm;
import com.example.lucasmaciel.testevoice.genericalarmclock.ui.AlarmManagerActivity;

import java.util.Locale;

import io.realm.Realm;

public class alarmefunc extends AppCompatActivity
{
    private AlarmListAdapter adapter;
    private GridView list;
    private Realm realm;
    TextToSpeech textToSpeech;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private Button btnVoltar;
    @Override
    public void onStart()
    {
        super.onStart();
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmefunc);


        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        textToSpeech = new TextToSpeech (getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        preparaBancoDados();

        adapter = new AlarmListAdapter(this,realm.where(Alarm.class).findAll());
        list=(GridView)findViewById(R.id.lstAlarmesCadastrados);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String vs_id = (String) ((TextView) view.findViewById(R.id.txtIdAlarme)).getText();
                abreAlarme(Integer.parseInt(vs_id));
            }
        });

        list.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                String horarioAlarme = (String) ((TextView) view.findViewById(R.id.txtHorarioAlarme)).getText();
                String diasAlarme = (String) ((TextView) view.findViewById(R.id.txtDiasMarcados)).getText();

                String falar = "Horario do alarme " +horarioAlarme + " nos dias " + diasAlarme;
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return  true;
            }
        });
    }
    private void preparaBancoDados()
    {
        try {
            Realm.init(getApplicationContext());
            realm = Realm.getDefaultInstance();

        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                            exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void adicionarAlarme(View v)
    {
        abreAlarme(-1);
    }
    private void abreAlarme(int id_Alarme)
    {
        Intent intentAddAlarm = new Intent (alarmefunc.this, AlarmManagerActivity.class);
        intentAddAlarm.putExtra("id_alarme",id_Alarme);
        alarmefunc.this.startActivity(intentAddAlarm);
    }

    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
}
