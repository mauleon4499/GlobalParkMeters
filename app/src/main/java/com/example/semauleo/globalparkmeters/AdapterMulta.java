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

public class AdapterMulta extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<multa> items;

    public AdapterMulta (Activity activity, ArrayList<multa> items) {
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

    public void addAll(ArrayList<multa> category) {
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
            v = inf.inflate(R.layout.item_multa, null);
        }

        multa dir = items.get(position);

        TextView fecha = (TextView) v.findViewById(R.id.mtFecha);
        fecha.setText(dir.getFecha());

        TextView importe = (TextView) v.findViewById(R.id.mtImporte);
        importe.setText(dir.getImporte() + " euros");

        TextView matricula = (TextView) v.findViewById(R.id.mtMatricula);
        matricula.setText(dir.getMatricula());

        TextView motivo = (TextView) v.findViewById(R.id.mtMotivo);
        motivo.setText(dir.getMotivo());

        TextView pagada = (TextView) v.findViewById(R.id.mtPagada);
        pagada.setText(dir.getPagada());

        return v;
    }
}
