package com.example.semauleo.globalparkmeters;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class EditarPassword extends AppCompatActivity {

    public EditText Password;
    public EditText RePassword;
    Button btnEditPassword;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_password);

        Password = (EditText) findViewById(R.id.editPassword);
        RePassword = (EditText) findViewById(R.id.editRePassword);

        id = getIntent().getStringExtra("id");

        btnEditPassword = (Button) findViewById(R.id.btnEditPass);
        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean error = false;

                if(Password.getText().toString().trim().equals("")){
                    error = true;
                    Password.setError("Este campo es requerido");
                }

                if(Password.getText().toString().trim().length() < 8){
                    error = true;
                    Password.setError("La contraseña debe tener al menos 8 caracteres");
                }

                if(RePassword.getText().toString().trim().equals("")){
                    error = true;
                    RePassword.setError("Este campo es requerido");
                }

                if(!Password.getText().toString().trim().equals(RePassword.getText().toString().trim())){
                    error = true;
                    Password.setError("Las contraseñas son diferentes");
                    RePassword.setError("Las contraseñas son diferentes");
                }

                if(!error) {
                    String hash = getMd5Key(Password.getText().toString());
                    new EditarPassword.guardarPass().execute("http://" + getString(R.string.ip) + "/movil/editarPass.php?id=" + id + "&password=" + hash);
                }
            }
        });
    }

    //Método para enviar datos a la base de datos
    private class guardarPass extends AsyncTask<String, Void, String> {

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

    //Método para leer los datos que envia el servidor
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
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
