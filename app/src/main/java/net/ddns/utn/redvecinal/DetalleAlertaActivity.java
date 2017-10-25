package net.ddns.utn.redvecinal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.ddns.utn.redvecinal.lib.Constantes;
import net.ddns.utn.redvecinal.listas.DetalleDirectorioActivity;
import net.ddns.utn.redvecinal.listas.Directorio;
import net.ddns.utn.redvecinal.listas.DirectorioAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.duration;

public class DetalleAlertaActivity extends AppCompatActivity {

    RecyclerView rv_directorio;

    int idA;

    //variables json
    private JSONObject jsonObj;
    private JSONArray contactos;

    ArrayList<Directorio> items;
    public String valorBoton = "media";
    public String valorTexto = "No hay texto";


    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_alerta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_red2);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        idA = bundle.getInt("idA");

        InicioDirec();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_ma);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText valorTexto2 = (EditText) findViewById(R.id.txt_alerta);
                valorTexto = valorTexto2.getText().toString().trim().toUpperCase();
                new ModificarAlerta().execute();
            }
        });


        Button btnVerde = (Button) findViewById(R.id.btn_verde);
        Button btnAmarillo = (Button) findViewById(R.id.btn_amarillo);
        Button btnRojo = (Button) findViewById(R.id.btn_rojo);

        btnVerde.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                valorBoton = "Baja";

                Toast toast = Toast.makeText(getApplicationContext(), "Prioridad Baja", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        btnAmarillo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                valorBoton = "Media";

                Toast.makeText(getApplicationContext(), "Prioridad Media", Toast.LENGTH_SHORT).show();
            }
        });

        btnRojo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                valorBoton = "Alta";

                Toast.makeText(getApplicationContext(), "Prioridad Alta", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void InicioDirec(){
        if(Constantes.compuebaConexion(DetalleAlertaActivity.this)){
            rv_directorio = (RecyclerView) findViewById(R.id.div_numeros);
            new ConsultaContactos().execute();


            rv_directorio.addOnItemTouchListener(new RecyclerTouchListener(this,
                    rv_directorio, new ClickListener(){
                @Override
                public void onClick(View view, final int position) {
                    String telefono;

                    telefono = items.get(position).getTelefono();

                    if (ActivityCompat.checkSelfPermission(DetalleAlertaActivity.this, Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(DetalleAlertaActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);

                        return;
                    }

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + telefono));
                    startActivity(callIntent);
                }

                @Override
                public void onLongClick(View view, int position) {
                    //vacio
                }
            }));
        }
    }


    class ConsultaContactos extends AsyncTask<String,String,String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =  new ProgressDialog(DetalleAlertaActivity.this);
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setTitle("espere...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(DetalleAlertaActivity.this)){
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constantes.URL_CONTACTOS);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                entityBuilder.addTextBody("metodo","consultaTodo");
                HttpEntity entity = entityBuilder.build();
                httpPost.setEntity(entity);

                HttpResponse response = null;

                try {
                    response = httpClient.execute(httpPost);
                    HttpEntity httpEntity = response.getEntity();
                    String result = EntityUtils.toString(httpEntity);
                    jsonObj = new JSONObject(result);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetalleAlertaActivity.this);
                        rv_directorio.setLayoutManager(linearLayoutManager);

                        items = new ArrayList<>();

                        boolean error = jsonObj.getBoolean("error");
                        if(error == false){
                            contactos = jsonObj.getJSONArray("contactos");

                            for(int i=0;i<contactos.length();i++){
                                JSONObject objetoJson = contactos.getJSONObject(i);
                                items.add(new Directorio(objetoJson.getString("contacto"),objetoJson.getString("telefono"), objetoJson.getString("sitio"), objetoJson.getString("clave"),Constantes.URL_PRIMARY+objetoJson.getString("imagen")));
                            }

                            DirectorioAdapter adapter = new DirectorioAdapter(items);
                            rv_directorio.setAdapter(adapter);
                            rv_directorio.setLayoutManager(new GridLayoutManager(DetalleAlertaActivity.this, 1));

                        }else{
                            Toast.makeText(DetalleAlertaActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private DetalleAlertaActivity.ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final DetalleAlertaActivity.ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    class ModificarAlerta extends AsyncTask<String,String,String>{

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DetalleAlertaActivity.this);
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

            //String TOKEN = settings.getString("token", "");
            String COD_ALERTA = settings.getString("cod_alerta", String.valueOf(idA));
            String DESCRIPCION = settings.getString("descripcion", valorTexto);
            String PRIORIDAD = settings.getString("prioridad", valorBoton );

            try {
                //entityBuilder.addTextBody("token", TOKEN);
                entityBuilder.addTextBody("cod_alerta", COD_ALERTA);
                entityBuilder.addTextBody("descripcion", DESCRIPCION);
                entityBuilder.addTextBody("prioridad", PRIORIDAD);
                entityBuilder.addTextBody("metodo","p_modificarAlerta");
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
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Constantes.compuebaConexion(DetalleAlertaActivity.this)==true){
                        try {
                            Boolean error = jsonObj.getBoolean("error");

                            if(error == false){
                                String mensaje = jsonObj.getString("mensaje");
                                Toast.makeText(DetalleAlertaActivity.this,mensaje,Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else{
                                Toast.makeText(DetalleAlertaActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(DetalleAlertaActivity.this,"Sin conexión a internet", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


}
