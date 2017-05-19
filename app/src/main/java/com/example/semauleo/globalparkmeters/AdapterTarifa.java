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

public class AdapterTarifa extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<tarifa> items;

    public AdapterTarifa (Activity activity, ArrayList<tarifa> items) {
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

    public void addAll(ArrayList<tarifa> category) {
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
            v = inf.inflate(R.layout.item_tarifa, null);
        }

        tarifa dir = items.get(position);

        TextView titulo = (TextView) v.findViewById(R.id.aTitulo);
        titulo.setText(dir.getZona());

        TextView precio = (TextView) v.findViewById(R.id.ttPrecio);
        precio.setText(dir.getPrecio() + " euros/hora");

        TextView tiempo = (TextView) v.findViewById(R.id.ttTiempo);
        tiempo.setText(dir.getTiempo());

        return v;
    }
}