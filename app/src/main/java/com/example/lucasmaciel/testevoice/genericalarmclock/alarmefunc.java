package com.example.lucasmaciel.testevoice.genericalarmclock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucasmaciel.testevoice.R;
import com.example.lucasmaciel.testevoice.genericalarmclock.adapters.AlarmListAdapter;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.Alarm;
import com.example.lucasmaciel.testevoice.genericalarmclock.ui.AlarmManagerActivity;

import io.realm.Realm;

public class alarmefunc extends AppCompatActivity
{
    private AlarmListAdapter adapter;
    private ListView list;
    private Realm realm;
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

        preparaBancoDados();

        adapter = new AlarmListAdapter(this,realm.where(Alarm.class).findAll());
        list=(ListView)findViewById(R.id.lstAlarmesCadastrados);
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
}
