package com.example.lucasmaciel.testevoice.genericalarmclock.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.lucasmaciel.testevoice.R;

/**
 * Created by cezar on 12/5/16.
 */
public class WeekListAdapter extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] itens;
    private int id_Marcado = 0;
    private boolean[] _vetMarcados;

    public WeekListAdapter(Activity context, String[] a_VetItens)
    {
        super(context, R.layout.list_of_days,a_VetItens);
        _vetMarcados = new boolean[7];
        this.context = context;
        this.itens = a_VetItens;
    }
    public View getView(int position, View view, ViewGroup parent)
    {
        View rowView = null;
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.list_of_days, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtNomeDia);
        txtTitle.setText(itens[position]);

        CheckBox chkDia = (CheckBox) rowView.findViewById(R.id.chkDia);
        chkDia.setTag(R.id.myId,position);
        chkDia.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox)v.findViewById(R.id.chkDia);
                id_Marcado = (int) cb.getTag(R.id.myId);
                for (int i = 0; i< getVetMarcados().length; i++)
                    if(id_Marcado == i)  _vetMarcados[i] = cb.isChecked();
            }
        });
        return rowView;
    }
    public boolean[] getVetMarcados()
    {
        return _vetMarcados;
    }
}