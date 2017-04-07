package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatosPagoActivity extends AppCompatActivity implements View.OnClickListener {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_pago);

        id = getIntent().getStringExtra("user_id");

        //Getting Intent
        Intent intent = getIntent();

        try {
            JSONObject jsonDetails = new JSONObject(intent.getStringExtra("PaymentDetails"));
            //Displaying payment details
            showDetails(jsonDetails.getJSONObject("response"), jsonDetails.getJSONObject("client"), intent.getStringExtra("PaymentAmount"));
            //Mandar datos del pago a la base de datos
            //IMPORTANTE: Queda poner el mandar los datos del pago a la base de datos
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetails(JSONObject jsonDetails, JSONObject jsonClient, String paymentAmount) throws JSONException {
        //Views
        TextView textViewId = (TextView) findViewById(R.id.paymentId);
        TextView textViewStatus= (TextView) findViewById(R.id.paymentStatus);
        TextView textViewAmount = (TextView) findViewById(R.id.paymentAmount);

        TextView txtCiudad = (TextView) findViewById(R.id.txtCiudad);
        TextView txtZona= (TextView) findViewById(R.id.txtZona);
        TextView txtMatricula = (TextView) findViewById(R.id.txtMatricula);
        TextView txtFechaInicio = (TextView) findViewById(R.id.txtFechaInicio);
        TextView txtFechaFin = (TextView) findViewById(R.id.txtFechaFin);
        Button btnVolver = (Button) findViewById(R.id.btnVolver);

        //Showing the details from json object
        textViewId.setText(jsonDetails.getString("id"));
        textViewStatus.setText(jsonDetails.getString("state"));
        textViewAmount.setText(paymentAmount+" Euros");

        txtCiudad.setText(getIntent().getStringExtra("ciudad"));
        //txtCiudadId.setText(getIntent().getStringExtra("ciudad_id"));
        txtZona.setText(getIntent().getStringExtra("zona"));
        //txtZonaId.setText(getIntent().getStringExtra("zona_id"));
        txtMatricula.setText(getIntent().getStringExtra("matricula"));
        btnVolver.setOnClickListener(this);

        //Calcular fecha de inicio y fecha de fin
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 2);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha_inicio = df.format(c.getTime());
        String tiempo = getIntent().getStringExtra("tiempo");
        String[] tiempoP = tiempo.split(":");
        int h = Integer.parseInt(tiempoP[0]);
        int m = Integer.parseInt(tiempoP[1]);
        c.add(Calendar.HOUR, h);
        c.add(Calendar.MINUTE, m);
        String fecha_fin = df.format(c.getTime());

        txtFechaInicio.setText(fecha_inicio);
        txtFechaFin.setText(fecha_fin);

        new DatosPagoActivity.guardarPago().execute("http://"+getString(R.string.ip)+"/movil/guardarPago.php?idUsuario="+id+"&idCiudad="+getIntent().getStringExtra("ciudad_id")+"&idZona="+getIntent().getStringExtra("zona_id")+"&idCodigo="+jsonDetails.getString("id")+"&matricula="+getIntent().getStringExtra("matricula")+"&importe="+paymentAmount+"&duracion="+getIntent().getStringExtra("tiempo")+"&formaPago="+jsonClient.getString("product_name")+"&fechaInicio="+fecha_inicio+"&fechaFin="+fecha_fin+"&estado="+jsonDetails.getString("state"));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent (v.getContext(), Principal.class);
        intent.putExtra("id", getIntent().getStringExtra("user_id"));
        startActivityForResult(intent, 0);
    }

    //Método para enviar datos a la base de datos
    private class guardarPago extends AsyncTask<String, Void, String> {

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

    //Método para la conexión con la base de datos
    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;

        int len = 500;

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

    //Método para leer los datos que envia el servidor
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
