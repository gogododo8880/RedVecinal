package net.ddns.utn.redvecinal;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.utn.redvecinal.lib.Constantes;
import net.ddns.utn.redvecinal.listas.Directorio;
import net.ddns.utn.redvecinal.listas.DirectorioAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

public class InfoViolenciaActivity extends AppCompatActivity {

    String nombre,
            ap_pat,
            ap_mat,
            curp,
            edad,
            telefono,
            direccion,
            contacto;

    private String test_resultado;


    private boolean error;
    private JSONObject jsonObj;
    private ActionBar actionBar;
    private RecyclerView rv_directorio;

    //variables json
    private JSONArray contactos;

    ArrayList<Directorio> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_violencia);
        Bundle bundle = getIntent().getExtras();

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_red2);

        // cambia el color de la barra de herramientas y su titulo
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        actionBar.setTitle("Test violencia de genero");

        // verifica version de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //si es mayor a lollipop

            //cambia la imagen de la barra de estado y navegacion
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccentligth));
            window.setNavigationBarColor(getResources().getColor(R.color.colorAccentligth));
        }

        nombre = bundle.getString("nombre");
        ap_pat = bundle.getString("ap_pat");
        ap_mat = bundle.getString("ap_mat");
        curp = bundle.getString("curp");
        edad = bundle.getString("edad");
        telefono = bundle.getString("telefono");
        direccion = bundle.getString("direccion");
        test_resultado = bundle.getString("test_resultado");
        contacto = "0";

        TextView tv_test_resultado = (TextView) findViewById(R.id.tv_test_resultado);
        tv_test_resultado.setText("Usted tiene "+ test_resultado + " de que sufra violencia de género");

        rv_directorio = (RecyclerView) findViewById(R.id.rv_contactos);
        new ConsultaContactos().execute();
        rv_directorio.addOnItemTouchListener(new RecyclerTouchListener(this,
                rv_directorio, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                String telefono;

                telefono = items.get(position).getTelefono();
                contacto = items.get(position).getNombre_contacto();

                if (ActivityCompat.checkSelfPermission(InfoViolenciaActivity.this, Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(InfoViolenciaActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + telefono));
                startActivity(callIntent);
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    class ConsultaContactos extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =  new ProgressDialog(InfoViolenciaActivity.this);
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setTitle("espere...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(InfoViolenciaActivity.this)){
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constantes.URL_CONTACTOS);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                entityBuilder.addTextBody("metodo","consultaEspecifica");
                entityBuilder.addTextBody("busqueda","MUJER");

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
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InfoViolenciaActivity.this);
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
                            rv_directorio.setLayoutManager(new GridLayoutManager(InfoViolenciaActivity.this, 1));

                        }else{
                            Toast.makeText(InfoViolenciaActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    class InsertaTest extends AsyncTask<String,String,String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(InfoViolenciaActivity.this);
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setTitle("Espere...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(InfoViolenciaActivity.this)){
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httPost = new HttpPost(Constantes.URL_TEST);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                try {
                    entityBuilder.addTextBody("metodo","insertaTest");
                    entityBuilder.addTextBody("nombre",nombre);
                    entityBuilder.addTextBody("ap_pat",ap_pat);
                    entityBuilder.addTextBody("ap_mat",ap_mat);
                    entityBuilder.addTextBody("curp",curp);
                    entityBuilder.addTextBody("edad",edad);
                    entityBuilder.addTextBody("telefono",telefono);
                    entityBuilder.addTextBody("direccion",direccion);
                    entityBuilder.addTextBody("descripcion_resultado",test_resultado);
                    entityBuilder.addTextBody("contacto",contacto);

                    HttpEntity entity = entityBuilder.build();
                    httPost.setEntity(entity);
                    HttpResponse response = null;

                    response = httpclient.execute(httPost);
                    HttpEntity httpEntity = response.getEntity();
                    String result = EntityUtils.toString(httpEntity);

                    jsonObj = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                    if(Constantes.compuebaConexion(InfoViolenciaActivity.this)){
                        try {
                            boolean error = jsonObj.getBoolean("error");
                            if(error==false){
                                Toast.makeText(InfoViolenciaActivity.this,"gracias por contestar el test",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(InfoViolenciaActivity.this,"ocurrio un error al registrar el test",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            finish();
                        }
                    }
                }
            });
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    new InsertaTest().execute();
                    return true;
            }
        }
        return false;
    }

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private InfoViolenciaActivity.ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final InfoViolenciaActivity.ClickListener clicklistener){

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
}
