package com.example.semauleo.globalparkmeters;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Matriculas extends AppCompatActivity {

    Button addMatricula;
    Button btnSaveMatriculas;
    TextView txtMatricula;
    ListView lvMatriculas;
    private ArrayList<String> matriculas;
    private ArrayAdapter<String> adaptador;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matriculas);
        id = getIntent().getStringExtra("id");

        addMatricula = (Button)findViewById(R.id.addMatricula);
        btnSaveMatriculas = (Button) findViewById(R.id.btnSaveMatriculas);
        txtMatricula = (TextView) findViewById(R.id.txtMatricula);

        matriculas=new ArrayList<String>();
        adaptador=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,matriculas);
        lvMatriculas = (ListView) findViewById(R.id.lvMatriculas);
        lvMatriculas.setAdapter(adaptador);

        new Matriculas.obtenerMatriculas().execute("http://"+getString(R.string.ip)+"/movil/datosMatriculas.php?id="+id.toString().trim());

        //Eliminar matriculas pulsando sobre ellas
        lvMatriculas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion=i;

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Matriculas.this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Eliminar esta matrícula ?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        matriculas.remove(posicion);
                        adaptador.notifyDataSetChanged();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();

                return false;
            }
        });

        //Método para enviar los datos a la base de datos.
        //IMPORTANTE: La ip puesta cambian en fución de la ip del pc, además hace falta el archivo de conexion.php
        btnSaveMatriculas = (Button) findViewById(R.id.btnSaveMatriculas);
        btnSaveMatriculas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valores = null;
                for(int i=0;i<matriculas.size();i++){
                    if(i==0){
                        valores = matriculas.get(i);
                    }else{
                        valores = valores + ',' + matriculas.get(i);
                    }
                }
                new Matriculas.guardarMatriculas().execute("http://"+getString(R.string.ip)+"/movil/guardarMatriculas.php?id="+id+"&matriculas="+valores);
            }
        });
    }

    //Añadir matricula
    public void agregar(View v) {
        Pattern patron = Pattern.compile("(^[0-9]{4}[a-zA-Z]{2}$)||(^[a-zA-Z]{1,2}[0-9]{4}[a-zA-Z]{1,2}$)");
            if((txtMatricula.getText().toString().length()!=7)&&(!patron.matcher(txtMatricula.getText().toString()).matches())){
                txtMatricula.setError("Matrícula incorrecta");
            }else{
                matriculas.add(txtMatricula.getText().toString());
                adaptador.notifyDataSetChanged();
                txtMatricula.setText("");
            }
    }

    //Método para enviar datos a la base de datos
    private class guardarMatriculas extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();
        }
    }

    //Método para comprobar el nombre de usuario y la contraseña
    private class obtenerMatriculas extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {

            JSONArray ja = null;

            try {
                ja = new JSONArray(result);
                if(ja.length() != 0){
                    for(int i =0;i<ja.length();i++){
                        matriculas.add(ja.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //Método para la conexión con la base de datos
    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;

        int len = 50000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            String contentAsString = readIt(is, len);
            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    //Nétodo para leer los datos que envia el servidor
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
