package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.regex.Pattern;


/**
 * Created by semauleo on 23/02/2017.
 */

public class Registro extends AppCompatActivity{

    EditText UserName;
    EditText PassWord;
    EditText RePassWord;
    EditText Nombre;
    EditText Apellido1;
    EditText Apellido2;
    EditText Email;
    EditText Telefono;

    private Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        UserName = (EditText) findViewById(R.id.txtUserNane);
        PassWord = (EditText) findViewById(R.id.txtPwd);
        RePassWord = (EditText) findViewById(R.id.txtRePwd);
        Nombre = (EditText) findViewById(R.id.txtNombre);
        Apellido1 = (EditText) findViewById(R.id.txtApel1);
        Apellido2 = (EditText) findViewById(R.id.txtApel2);
        Email = (EditText) findViewById(R.id.txtEmail);
        Telefono = (EditText) findViewById(R.id.txtTlf);

        //Método para enviar los datos a la base de datos.
        //IMPORTANTE: La ip puesta cambian en fución de la ip del pc, además hace falta el archivo de conexion.php
        guardar = (Button) findViewById(R.id.txtRegistrarse);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new comprobarDatos(v).execute("http://"+getString(R.string.ip)+"/movil/verificar.php?usuario="+UserName.getText().toString().trim()+"&email="+Email.getText().toString().trim());

            }
        });
    }

    //Método para comprobar el email y el nombre de usuario
    private class comprobarDatos extends AsyncTask<String, Void, String> {

        View v;

        public comprobarDatos(View vista) {
            this.v = vista;
        }

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

            if(PassWord.getText().toString().trim().equals("")){
                error = true;
                PassWord.setError("Este campo es requerido");
            }

            if(PassWord.getText().toString().trim().length() < 8){
                error = true;
                PassWord.setError("La contraseña debe tener al menos 8 caracteres");
            }

            if(RePassWord.getText().toString().trim().equals("")){
                error = true;
                RePassWord.setError("Este campo es requerido");
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

            if(Telefono.getText().toString().trim().equals("")){
                error = true;
                Telefono.setError("Este campo es requerido");
            }

            if(Telefono.getText().toString().trim().length() != 9){
                error = true;
                Telefono.setError("El teléfono es incorrecto");
            }

            if(!PassWord.getText().toString().trim().equals(RePassWord.getText().toString().trim())){
                error = true;
                PassWord.setError("Las contraseñas son diferentes");
                RePassWord.setError("Las contraseñas son diferentes");
            }

            JSONObject ja = null;

            try {
                ja = new JSONObject(result);
                boolean esta_email = ja.getBoolean("esta_email");
                boolean esta_user = ja.getBoolean("esta_user");

                if(esta_email){
                    error = true;
                    Email.setError("El email ya está registrado");
                }

                if(esta_user){
                    error = true;
                    UserName.setError("El usuario ya está registrado");
                }

               if(!error){
                    String hash= getMd5Key(PassWord.getText().toString());
                    new guardarDatos().execute("http://"+getString(R.string.ip)+"/movil/guardar.php?username="+UserName.getText().toString()+"&password="+hash+"&nombre="+Nombre.getText().toString()+"&apellido1="+Apellido1.getText().toString()+"&apellido2="+Apellido2.getText().toString()+"&email="+Email.getText().toString()+"&telefono="+Telefono.getText().toString());
                    Intent intent = new Intent (v.getContext(), Login.class);
                    startActivityForResult(intent, 0);
               }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    //Función para generación del hash
    public static String getMd5Key(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Digest(in hex format):: " + sb.toString());

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            System.out.println("Digest(in hex format):: " + hexString.toString());

            return hexString.toString();

        } catch (Exception e) {

        }
        return "";
    }
}
