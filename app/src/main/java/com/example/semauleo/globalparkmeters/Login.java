package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Login extends AppCompatActivity {

    Button btnRegistrase;
    Button btnAcceder;
    EditText txtUser;
    EditText txtPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPwd = (EditText) findViewById(R.id.EditPwd);

        //Método para cambiar al activity Registro
        btnRegistrase = (Button) findViewById(R.id.btnRegistrarse);
        btnRegistrase.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Registro.class);
                startActivityForResult(intent, 0); }
        });

        //Método para loguearse
        btnAcceder = (Button) findViewById(R.id.btnAcceder);
        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String hash= getMd5Key(txtPwd.getText().toString());
                new acceso(v).execute("http://"+getString(R.string.ip)+"/movil/login.php?usuario="+txtUser.getText().toString()+"&password="+hash);
            }
        });
    }

    //Método para comprobar el nombre de usuario y la contraseña
    private class acceso extends AsyncTask<String, Void, String> {

        View v;

        public acceso(View vista) {
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

            JSONObject ja = null;

            try {
                ja = new JSONObject(result);
                boolean login_correcto = ja.getBoolean("login_correcto");

                if(login_correcto){
                    String id = ja.getString("id");
                    Intent intent = new Intent (v.getContext(), Principal.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }else{
                    txtUser.setError("Usuario o contraseña incorrectos");
                    txtPwd.setError("Usuario o contraseña incorrectos");
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
