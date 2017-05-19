package com.example.semauleo.globalparkmeters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Doc on 19/05/2017.
 */

public class AdapterPago extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<pago> items;

    public AdapterPago (Activity activity, ArrayList<pago> items) {
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

    public void addAll(ArrayList<pago> category) {
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
            v = inf.inflate(R.layout.item_pago, null);
        }

        pago dir = items.get(position);

        TextView id = (TextView) v.findViewById(R.id.ptId);
        id.setText(dir.getId());

        TextView importe = (TextView) v.findViewById(R.id.ptImporte);
        importe.setText(dir.getImporte() + " euros");

        TextView ciudad = (TextView) v.findViewById(R.id.ptCiudad);
        ciudad.setText(dir.getCiudad());

        TextView zona = (TextView) v.findViewById(R.id.ptZona);
        zona.setText(dir.getZona());

        TextView matricula = (TextView) v.findViewById(R.id.ptMatricula);
        matricula.setText(dir.getMatricula());

        TextView fecha_inicio = (TextView) v.findViewById(R.id.ptFechaInicio);
        fecha_inicio.setText(dir.getFechainicio());

        TextView fecha_fin = (TextView) v.findViewById(R.id.ptFechaFin);
        fecha_fin.setText(dir.getFechafin());

        return v;
    }
}