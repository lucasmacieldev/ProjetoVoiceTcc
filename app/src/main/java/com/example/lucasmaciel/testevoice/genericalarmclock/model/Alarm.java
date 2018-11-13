package com.example.lucasmaciel.testevoice.genericalarmclock.model;

import com.google.gson.Gson;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cezar on 12/1/16.
 */
public class Alarm extends RealmObject
{
    @PrimaryKey
    private int _id;
    private String _diasMarcadosJson;
    private int _hora;
    private int _minutos;
    public Alarm(){}
    public boolean[] get_vetDiasMarcados()
    {
        Gson gson = new Gson();
        ListOfDays vo_lista = gson.fromJson(this._diasMarcadosJson,ListOfDays.class);
        return vo_lista.get_dias();
    }
    public void set_diasMarcadosJson(boolean[] ab_Dias)
    {
        Gson gson = new Gson();
        ListOfDays lista = new ListOfDays();
        lista.set_dias(ab_Dias);
        this._diasMarcadosJson = gson.toJson(lista);
    }
    public void excluiTodosAlarmes(Realm realm)
    {
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                for (Alarm vo_alarm : realm.where(Alarm.class).findAll())
                    vo_alarm.deleteFromRealm();
            }
        });
    }
    // Verifica se no alarme cadastrado para este horário há alguma restrição de dia de semana
    public Alarm recuperaAlarmesParaHoje(int diaDeHoje, int horaAtual, int MinutoAtual,Realm realm)
    {
        Alarm vo_alarmeRetorno = null;
        for (Alarm vo_alarm : realm.where(Alarm.class).
                between("_minutos",MinutoAtual -1,MinutoAtual + 1).
                equalTo("_hora",horaAtual).findAll())
                if (vo_alarm.get_vetDiasMarcados()[diaDeHoje])
                    vo_alarmeRetorno = vo_alarm;

        return vo_alarmeRetorno;
    }
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public int get_hora() {
        return _hora;
    }
    public void set_hora(int _hora) {
        this._hora = _hora;
    }
    public int get_minutos() {
        return _minutos;
    }
    public void set_minutos(int _minutos) {
        this._minutos = _minutos;
    }
}