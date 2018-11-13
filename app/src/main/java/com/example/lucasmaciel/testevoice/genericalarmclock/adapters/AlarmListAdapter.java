package com.example.lucasmaciel.testevoice.genericalarmclock.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lucasmaciel.testevoice.R;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.Alarm;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.WeekInformation;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by cezar on 12/5/16.
 */

public class AlarmListAdapter extends ArrayAdapter<Alarm>
{
    private final Activity context;
    private List<Alarm> lst_Itens;

    public AlarmListAdapter(Activity context, RealmResults<Alarm> a_VetItens)
    {
        super(context, R.layout.list_of_alarms, (List<Alarm>) a_VetItens);
        lst_Itens = (List<Alarm>) a_VetItens;
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        View rowView = null;
        String vs_ValoresMarcados = "";
        int contDias = 0;
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.list_of_alarms, null, true);

        TextView txtId = (TextView) rowView.findViewById(R.id.txtIdAlarme);
        int id = ((Alarm) lst_Itens.toArray()[position]).get_id();
        txtId.setText(String.valueOf(id));

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtHorarioAlarme);
        txtTitle.setText(String.format("%02d",((Alarm) lst_Itens.toArray()[position]).get_hora()) + ":" +
                         String.format("%02d", ((Alarm) lst_Itens.toArray()[position]).get_minutos()));

        TextView txtDays = (TextView) rowView.findViewById(R.id.txtDiasMarcados);
        for (int i = 0; i < ((Alarm) lst_Itens.toArray()[position]).get_vetDiasMarcados().length; i++)
        {
            if(((Alarm) lst_Itens.toArray()[position]).get_vetDiasMarcados()[i])
            {
                vs_ValoresMarcados += WeekInformation.get_vetReduzido()[i] + ".";
                contDias++;
            }
        }
        if(contDias== 7)
            vs_ValoresMarcados = this.context.getResources().getString(R.string.todosDias);

        txtDays.setText(vs_ValoresMarcados);
        return rowView;
    }
}