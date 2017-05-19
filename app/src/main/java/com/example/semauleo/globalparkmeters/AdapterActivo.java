package com.example.semauleo.globalparkmeters;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Doc on 19/05/2017.
 */

public class AdapterActivo extends BaseAdapter{
    protected Activity activity;
    protected ArrayList<activo> items;

    public AdapterActivo (Activity activity, ArrayList<activo> items) {
        this.activity = activity;
        this.items = items;
    }

    public Activity getActivity(){
        return this.activity;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<activo> category) {
        for (int i = 0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_activo, null);
        }

        activo dir = items.get(position);

        TextView id = (TextView) v.findViewById(R.id.atId);
        id.setText(dir.getId());

        TextView importe = (TextView) v.findViewById(R.id.atImporte);
        importe.setText(dir.getImporte() + " euros");

        TextView ciudad = (TextView) v.findViewById(R.id.atCiudad);
        ciudad.setText(dir.getCiudad());

        TextView zona = (TextView) v.findViewById(R.id.atZona);
        zona.setText(dir.getZona());

        TextView matricula = (TextView) v.findViewById(R.id.atMatricula);
        matricula.setText(dir.getMatricula());

        TextView fecha_inicio = (TextView) v.findViewById(R.id.atFechaInicio);
        fecha_inicio.setText(dir.getFechainicio());

        TextView fecha_fin = (TextView) v.findViewById(R.id.atFechaFin);
        fecha_fin.setText(dir.getFechafin());

        final TextView temporizador = (TextView) v.findViewById(R.id.ttTemporizador);
        new CountDownTimer(dir.getTiempo(), 1000) {

            public void onTick(long millisUntilFinished) {
                long segundos = millisUntilFinished/1000;
                long horas = segundos/3600;
                long minutos = ((segundos-(horas*3600)))/60;
                segundos = segundos - (horas*3600) - (minutos *60);
                temporizador.setText(String.format("%02d",horas) +":"+String.format("%02d",minutos)+":"+String.format("%02d",segundos));
            }

            public void onFinish() {
                temporizador.setText("Tiempo terminado");
            }
        }.start();

        return v;
    }
}
