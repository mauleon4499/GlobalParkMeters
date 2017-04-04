package com.example.semauleo.globalparkmeters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.example.semauleo.globalparkmeters.R.id.textView;

public class Pagos extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    TabHost TbH;

    private EditText etPaypal;

    String id;
    Spinner spCiudades;
    Spinner spZonas;
    Spinner spMatricula;

    private ArrayList<String> ZonasHora = new ArrayList<String>();
    private ArrayList<Double> ZonasPrecio = new ArrayList<Double>();
    private String horaZonaE;
    private Double precioZonaE;

    private ArrayList<String> matriculas = new ArrayList<String>();
    private ArrayAdapter<String> adaptadorMatriculas;
    private ArrayList<String> ciudades = new ArrayList<String>();
    private ArrayAdapter<String> adaptadorCiudades;
    private ArrayList<String> zonas = new ArrayList<String>();
    private ArrayAdapter<String> adaptadorZonas;
    private JSONObject datos;

    private Button pagar;
    private EditText tiempo;
    private EditText horaMax;
    private TextView importe;

    public static final int PAYPAL_REQUEST_CODE = 123;


    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        TbH = (TabHost) findViewById(R.id.tabHost);
        TbH.setup();

        TabHost.TabSpec tab1 = TbH.newTabSpec("tab1");
        TabHost.TabSpec tab2 = TbH.newTabSpec("tab2");

        tab1.setIndicator("Datos parking");
        tab1.setContent(R.id.Parking);

        tab2.setIndicator("Tipo de pago");
        tab2.setContent(R.id.Forma_Pago);

        TbH.addTab(tab1);
        TbH.addTab(tab2);

        tiempo = (EditText) findViewById(R.id.tiempo);
        horaMax = (EditText) findViewById(R.id.textoHora);
        importe = (TextView) findViewById(R.id.txtImporte);

        id = getIntent().getStringExtra("id");
        spMatricula = (Spinner) findViewById(R.id.spMatricula);
        spCiudades = (Spinner) findViewById(R.id.spCiudades);
        spZonas = (Spinner) findViewById(R.id.spZonas);
        new Pagos.obtenerDatos().execute("http://"+getString(R.string.ip)+"/movil/datosZonas.php?id="+id.toString().trim());

        spCiudades.setOnItemSelectedListener(this);
        spZonas.setOnItemSelectedListener(this);

        horaMax = (EditText) findViewById(R.id.textoHora);
        horaMax.setFocusable(false);
        horaMax.setEnabled(false);
        horaMax.setCursorVisible(false);
        horaMax.setKeyListener(null);
        horaMax.setBackgroundColor(Color.TRANSPARENT);

        tiempo.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.equals("") && s.length()>3 && s.toString().indexOf(":")!=-1) {
                    String t = tiempo.getText().toString();
                    Pattern patron = Pattern.compile("^[0-9]{1,2}:[0-9]{2}$");
                    if (patron.matcher(t).matches()) {
                        int zonaE = spZonas.getSelectedItemPosition();
                        if (zonaE != -1) {
                            boolean correcto = true;

                            String[] tiempoMax = horaZonaE.split(":");
                            int hM = Integer.parseInt(tiempoMax[0]);
                            int mM = Integer.parseInt(tiempoMax[1]);

                            String[] tiempoP = t.split(":");
                            int h = Integer.parseInt(tiempoP[0]);
                            int m = Integer.parseInt(tiempoP[1]);

                            //Comprobaciones de teimpo máximo
                            //La hora puesta es mayor que la permitida
                            if(h>hM){
                                correcto = false;
                            }
                            //La hora puesta es igual que la permitida pero los minutos puestos son mayores que los permitidos
                            if((h==hM)&&(m>mM)){
                                correcto = false;
                            }

                            if (correcto) {
                                Double coste = ((60 * h) + m) * precioZonaE;
                                DecimalFormat f = new DecimalFormat("0.00");
                                importe.setText(f.format(coste));
                            }else{
                                tiempo.setError("Dato incorrecto");
                                importe.setText("00.00");
                            }
                        } else {
                            importe.setText("00.00");
                        }

                    }else{
                        tiempo.setError("Dato incorrecto");
                        importe.setText("00.00");
                    }
                }else{
                    tiempo.setError("Dato incorrecto");
                    importe.setText("00.00");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etPaypal = (EditText) findViewById(R.id.etPaypal);
        pagar = (Button) findViewById(R.id.btnPagar);
        pagar.setOnClickListener(this);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        getPayment();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void getPayment() {
        String precio = importe.getText().toString();

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(precio)), "EUR", "Simplified Coding Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, DatosPagoActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", importe.getText().toString()));
                        //Pasar datos del pago de la zona
                        //IMPORTANTE: Pasar la ciudad, zona y tiempo

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            //Caso de Spinner de ciudades
            case R.id.spCiudades:
                ArrayList<String> listZonas = new ArrayList<String>();
                String nomCiudad = parent.getSelectedItem().toString();
                zonas = new ArrayList<String>();

                JSONArray jc = null;
                int pos = 100;
                try {
                    jc = datos.getJSONArray("ciudades");
                    for(int i =0;i < jc.length();i++){
                        if (nomCiudad.equals(jc.getJSONObject(i).getString("ciudad"))) {
                            pos = i;
                        }
                    }

                    JSONArray jz = jc.getJSONObject(pos).getJSONArray("zonas");
                    for(int i =0;i < jz.length();i++){
                        zonas.add(jz.getJSONObject(i).getString("nombre"));
                        ZonasPrecio.add(jz.getJSONObject(i).getDouble("precio"));
                        ZonasHora.add(jz.getJSONObject(i).getString("tiempo"));
                    }
                    horaZonaE = ZonasHora.get(0);
                    precioZonaE = ZonasPrecio.get(0);
                    horaMax.setText("Tiempo max: "+horaZonaE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adaptadorZonas = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,zonas);
                spZonas = (Spinner) findViewById(R.id.spZonas);
                spZonas.setAdapter(adaptadorZonas);

            break;

            //Caso de Spinner de Zonas
            case R.id.spZonas:
                String zonaE = parent.getSelectedItem().toString();
                for(int i =0;i < zonas.size();i++){
                    if (zonaE.equals(zonas.get(i))) {
                       horaZonaE = ZonasHora.get(i);
                       precioZonaE = ZonasPrecio.get(i);
                   }
                }
                horaMax.setText("Tiempo max: "+horaZonaE);

                //Actualizar el tiempo si cumple condiciones
                int numeroZona = spZonas.getSelectedItemPosition();
                String tiempoPuesto = tiempo.getText().toString();

                if((numeroZona!=-1)&&(!tiempoPuesto.equals(""))){
                    String t = tiempo.getText().toString();
                    Pattern patron = Pattern.compile("^[0-9]{1,2}:[0-9]{2}$");

                    if (patron.matcher(t).matches()) {
                        boolean correcto = true;

                        String[] tiempoMax = horaZonaE.split(":");
                        int hM = Integer.parseInt(tiempoMax[0]);
                        int mM = Integer.parseInt(tiempoMax[1]);

                        String[] tiempoP = t.split(":");
                        int h = Integer.parseInt(tiempoP[0]);
                        int m = Integer.parseInt(tiempoP[1]);

                        //Comprobaciones de teimpo máximo
                        //La hora puesta es mayor que la permitida
                        if(h>hM){
                            correcto = false;
                        }
                        //La hora puesta es igual que la permitida pero los minutos puestos son mayores que los permitidos
                        if((h==hM)&&(m>mM)){
                            correcto = false;
                        }

                        if (correcto) {
                            Double coste = ((60 * h) + m) * precioZonaE;
                            DecimalFormat f = new DecimalFormat("0.00");
                            importe.setText(f.format(coste));
                        }else{
                            tiempo.setError("Dato incorrecto");
                            importe.setText("00.00");
                        }
                    }else{
                        tiempo.setError("Dato incorrecto");
                        importe.setText("00.00");
                    }
                }else{
                    importe.setText("00.00");
                }
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Método para comprobar el nombre de usuario y la contraseña
    private class obtenerDatos extends AsyncTask<String, Void, String> {

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

            JSONObject ja = null;

            try {
                ja = new JSONObject(result);
                datos = new JSONObject(result);
                //Cargar matrículas del usuario
                JSONArray jm = ja.getJSONArray("matriculas");
                for(int i =0;i < jm.length();i++){
                    matriculas.add(jm.getString(i));
                }
                adaptadorMatriculas = new ArrayAdapter<String>(Pagos.this, android.R.layout.simple_list_item_1,matriculas);
                spMatricula.setAdapter(adaptadorMatriculas);

                //Cargar ciudades
                JSONArray jc = ja.getJSONArray("ciudades");
                for(int i =0;i < jc.length();i++){
                    ciudades.add(jc.getJSONObject(i).getString("ciudad"));
                }
                System.out.println(ciudades);
                adaptadorCiudades = new ArrayAdapter<String>(Pagos.this, android.R.layout.simple_list_item_1,ciudades);
                spCiudades.setAdapter(adaptadorCiudades);

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

    //Nétodo para leer los datos que envia el servidor
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
