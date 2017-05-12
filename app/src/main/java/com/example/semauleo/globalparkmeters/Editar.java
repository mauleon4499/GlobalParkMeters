package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;

public class Editar extends AppCompatActivity {

    EditText UserName;
    EditText PassWord;
    EditText RePassWord;
    EditText Nombre;
    EditText Apellido1;
    EditText Apellido2;
    EditText Email;

    private Button modificar;
    private String email;
    private String user;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        id = getIntent().getStringExtra("id");
        System.out.println("id: " + id);

        UserName = (EditText) findViewById(R.id.EditUserNane);
        Nombre = (EditText) findViewById(R.id.EditNombre);
        Apellido1 = (EditText) findViewById(R.id.EditApel1);
        Apellido2 = (EditText) findViewById(R.id.EditApel2);
        Email = (EditText) findViewById(R.id.EditEmail);

        new Editar.obtenerDatos().execute("http://"+getString(R.string.ip)+"/movil/datosPersonales.php?id="+id.toString().trim());

         modificar = (Button) findViewById(R.id.btnModificar);
         modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Editar.comprobarDatos().execute("http://"+getString(R.string.ip)+"/movil/verificar.php?usuario="+UserName.getText().toString().trim()+"&email="+Email.getText().toString().trim());

            }
        });
    }

    //Método para enviar datos a la base de datos
    private class guardarDatos extends AsyncTask<String, Void, String> {

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

    //Método para comprobar el email y el nombre de usuario
    private class comprobarDatos extends AsyncTask<String, Void, String> {

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

            boolean error = false;

            if(UserName.getText().toString().trim().equals("")){
                error = true;
                UserName.setError("Este campo es requerido");
            }

            if(!isAlphaNumeric(UserName.getText().toString().trim())){
                error = true;
                UserName.setError("Solo números y caracteres");
            }

            if(Nombre.getText().toString().trim().equals("")){
                error = true;
                Nombre.setError("Este campo es requerido");
            }

            if(Apellido1.getText().toString().trim().equals("")){
                error = true;
                Apellido1.setError("Este campo es requerido");
            }

            if(Apellido2.getText().toString().trim().equals("")){
                error = true;
                Apellido2.setError("Este campo es requerido");
            }

            if(Email.getText().toString().trim().equals("")){
                error = true;
                Email.setError("Este campo es requerido");
            }

            if(!isEmail(Email.getText().toString().trim())){
                error = true;
                Email.setError("Introduzca un email correcto");
            }

            JSONObject ja = null;

            try {
                ja = new JSONObject(result);
                boolean esta_email = ja.getBoolean("esta_email");
                boolean esta_user = ja.getBoolean("esta_user");

                if((esta_email)&&(!email.equals(Email.getText().toString()))){
                    error = true;
                    Email.setError("El email ya está registrado");
                }

                if((esta_user)&&(!user.equals(UserName.getText().toString()))){
                    error = true;
                    UserName.setError("El usuario ya está registrado");
                }

                if(!error){
                    String hash;

                    new Editar.guardarDatos().execute("http://"+getString(R.string.ip)+"/movil/editar.php?username="+UserName.getText().toString()+"&nombre="+Nombre.getText().toString()+"&apellido1="+Apellido1.getText().toString()+"&apellido2="+Apellido2.getText().toString()+"&email="+Email.getText().toString()+"&id="+id);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

            JSONArray ja = null;

            try {
                ja = new JSONArray(result);
                UserName.setText(ja.getString(1));
                user = ja.getString(1);
                Nombre.setText(ja.getString(3));
                Apellido1.setText(ja.getString(4));
                Apellido2.setText(ja.getString(5));
                Email.setText(ja.getString(6));
                email = ja.getString(6);

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
        reader = new InputStreamReader(stream, StandardCharsets.ISO_8859_1);
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    //Método para comprobar que una cadena es alfanumérica
    public  static boolean isAlphaNumeric(String cadena){
        Pattern patron = Pattern.compile("^[a-zA-Z0-9]+$");
        if (!patron.matcher(cadena).matches()) {
            return false;
        } else {
            return true;
        }
    }

    //Método para comprobar que una cadena es un email
    public static boolean isEmail(String cadena){
        Pattern patron = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
        if (!patron.matcher(cadena).matches()) {
            return false;
        } else {
            return true;
        }
    }
}
