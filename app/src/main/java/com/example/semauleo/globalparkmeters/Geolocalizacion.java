package com.example.semauleo.globalparkmeters;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class Geolocalizacion extends FragmentActivity implements OnMapReadyCallback {

    private TextView nuevaTxtLatitud;
    private TextView nuevaTxtLongitud;
    private TextView bdTxtLatitud;
    private TextView bdTxtLongitud;

    private double actualLatitud;
    private double actualLongitud;

    private GoogleMap mMap;
    private Button btnPos;

    private  String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocalizacion);

        id = getIntent().getStringExtra("id");

        //Consulta para obtener la localización anterior
        new Geolocalizacion.obtenerLocalizacion().execute("http://"+getString(R.string.ip)+"/movil/datosLocalizacion.php?id="+id.toString().trim());

        bdTxtLatitud = (TextView) findViewById(R.id.txtLatitudDato);
        bdTxtLongitud = (TextView) findViewById(R.id.txtLongitudDato);
        nuevaTxtLatitud = (TextView) findViewById(R.id.txtLatitudActual);
        nuevaTxtLongitud = (TextView) findViewById(R.id.txtLongitudActual);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnPos = (Button) findViewById(R.id.btnPosicion);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new Geolocalizacion.guardarLocalizacion().execute("http://"+getString(R.string.ip)+"/movil/guardarLocalizacion.php?id="+id.toString().trim()+"&latitud="+actualLatitud+"&longitud="+actualLongitud);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location loc = service.getLastKnownLocation(provider);
        actualLatitud = loc.getLatitude();
        actualLongitud = loc.getLongitude();
        nuevaTxtLatitud.setText(String.valueOf(loc.getLatitude()));
        nuevaTxtLongitud.setText(String.valueOf(loc.getLongitude()));
        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

        //Código para usarlo con el emulador
        mMap.addMarker(new MarkerOptions().position(pos).title("Posición actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }
    //Método para obtener los datos de geolocalizacion de la base de datos
    private class obtenerLocalizacion extends AsyncTask<String, Void, String> {

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
                    System.out.println("Datos: "+ja);
                    Double lat = ja.getDouble(1);
                    Double lon = ja.getDouble(2);
                    LatLng pos = new LatLng(lat,lon);
                    bdTxtLatitud.setText(lat.toString());
                    bdTxtLongitud.setText(lon.toString());
                    //Código para usarlo con el emulador
                    mMap.addMarker(new MarkerOptions().position(pos).title("Posición anterior"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //Método para enviar datos a la base de datos
    private class guardarLocalizacion extends AsyncTask<String, Void, String> {

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

    //Nétodo para leer los datos que envia el servidor
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}