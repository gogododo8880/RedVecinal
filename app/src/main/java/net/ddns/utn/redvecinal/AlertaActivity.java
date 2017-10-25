package net.ddns.utn.redvecinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.ddns.utn.redvecinal.lib.Constantes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AlertaActivity extends AppCompatActivity {

    private ActionBar actionBar;

    private JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);
        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_red2);

        Button btn_zlerta = (Button) findViewById(R.id.btn_alerta);
        btn_zlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             new  MandarAlerta().execute();
            }
        });
    }

    class MandarAlerta extends AsyncTask<String,String,String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AlertaActivity.this);
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setMessage("Espere...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httPost = new HttpPost(Constantes.URL_ALERTAS);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NOM, MODE_PRIVATE);

            String TOKEN = settings.getString("token", "");

            try {

                entityBuilder.addTextBody("token", TOKEN);
                entityBuilder.addTextBody("metodo","mandarAlerta");
                HttpEntity entity = entityBuilder.build();
                httPost.setEntity(entity);
                HttpResponse response = null;

                response = httpclient.execute(httPost);
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);
                jsonObj = new JSONObject(result);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Constantes.compuebaConexion(AlertaActivity.this)==true){
                        try {
                            Boolean error = jsonObj.getBoolean("error");

                            if(error == false){
                                SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NOM, MODE_PRIVATE);
                                int idA = Integer.parseInt(jsonObj.getString("mensaje"));
                                Intent i = new Intent(AlertaActivity.this,DetalleAlertaActivity.class);
                                i.putExtra("idA",idA);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Toast.makeText(AlertaActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(AlertaActivity.this,"Sin conexión a internet", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

}
