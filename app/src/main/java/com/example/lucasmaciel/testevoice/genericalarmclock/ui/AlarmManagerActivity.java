package com.example.lucasmaciel.testevoice.genericalarmclock.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lucasmaciel.testevoice.R;
import com.example.lucasmaciel.testevoice.genericalarmclock.adapters.WeekListAdapter;
import com.example.lucasmaciel.testevoice.genericalarmclock.alarmefunc;
import com.example.lucasmaciel.testevoice.genericalarmclock.core.AlarmReceiver;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.Alarm;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.WeekInformation;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by cezar on 12/8/16.
 */
public class AlarmManagerActivity extends AppCompatActivity
{
    private Alarm vo_AlarmGeral;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private TimePicker clock;
    private ListView lstWeekDays;
    private WeekListAdapter adapterWeek;
    private int etapa = 0;
    private int vi_hora;
    private int vi_minuto;
    private Realm realm;
    private Button btnGerenciar;
    private Button btnExcluir;
    private Bundle bundle;
    private int vi_Alarme;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);
        btnGerenciar = (Button) findViewById(R.id.btnAvancar);
        btnExcluir = (Button) findViewById(R.id.btnExcluir);

        preparaBancoDados();

        bundle = getIntent().getExtras();
        vi_Alarme = bundle.getInt("id_alarme");

        // Carrega o relógio
        clock = (TimePicker) findViewById(R.id.clock);
        clock.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (vi_Alarme == -1)
        {
            btnExcluir.setVisibility(View.GONE);
        }else {
            btnExcluir.setVisibility(View.VISIBLE);
            vo_AlarmGeral = realm.where(Alarm.class).equalTo("_id",vi_Alarme).findFirst();
            if(vo_AlarmGeral != null) {
                clock.setCurrentHour(vo_AlarmGeral.get_hora());
                clock.setCurrentMinute(vo_AlarmGeral.get_minutos());
            }
        }
        // Cria a lista de dias da semana.
        lstWeekDays = (ListView) findViewById(R.id.lstDias);
        adapterWeek = new WeekListAdapter(this, WeekInformation.get_vetDiasSemana());
        lstWeekDays.setAdapter(adapterWeek);
    }
    private void preparaBancoDados()
    {
        try {
            realm = Realm.getDefaultInstance();
        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                    exc.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void gerenciaAlarme(View v)
    {
        if(etapa == 0)
        {
            vi_hora =  clock.getCurrentHour();
            vi_minuto = clock.getCurrentMinute();
            alternaView();
            etapa++;
        }else{
            criaAlarme();
            alternaView();
            etapa = 0;
        }
    }
    public void cadastrar(final int a_hora, final int a_minuto, final boolean[] ab_diasMarcados)
    {
        realm.beginTransaction();
        criaIdSeNecessario();
        try
        {
            vo_AlarmGeral.set_hora(a_hora);
            vo_AlarmGeral.set_minutos(a_minuto);
            vo_AlarmGeral.set_diasMarcadosJson(ab_diasMarcados);
            realm.copyToRealmOrUpdate(vo_AlarmGeral);
        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                            exc.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        realm.commitTransaction();

        Intent j = new Intent (this, alarmefunc.class);
        startActivity (j);

        this.finish();
    }
    private void criaIdSeNecessario() {
        int id = 0;
        if(vi_Alarme == -1)
        {
            RealmResults<Alarm> lastAlarms = realm.where(Alarm.class).findAll();
            if (!lastAlarms.isEmpty()) id = lastAlarms.last().get_id() + 1;
            final int finalId = id;
            vo_AlarmGeral = new Alarm();
            vo_AlarmGeral.set_id(finalId);
        }
    }

    public void excluiAlarme(View v)
    {
        try
        {
            realm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    for (Alarm vo_alarm:realm.where(Alarm.class).equalTo("_id",vi_Alarme).findAll())
                          vo_alarm.deleteFromRealm();

                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.alarmeExcluido),
                            Toast.LENGTH_SHORT).show();
                }
            });
            this.finish();
        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                    exc.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void criaAlarme()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,vi_hora);
        calendar.set(Calendar.MINUTE,vi_minuto);
        Intent myIntent = new Intent (AlarmManagerActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmManagerActivity.this, 0, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
        cadastrar(vi_hora,vi_minuto,adapterWeek.getVetMarcados());
    }
    private void alternaView()
    {
        if(etapa == 0)
        {
            lstWeekDays.setVisibility(View.VISIBLE);
            clock.setVisibility(View.GONE);
            btnGerenciar.setText(getResources().getString(R.string.concluir));
        }else{
            lstWeekDays.setVisibility(View.GONE);
            clock.setVisibility(View.VISIBLE);
            btnGerenciar.setText(getResources().getString(R.string.avancar));
        }
    }
}
