package net.ddns.utn.redvecinal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import net.ddns.utn.redvecinal.lib.Constantes;
import net.ddns.utn.redvecinal.listas.Agenda;
import net.ddns.utn.redvecinal.listas.AgendaAdapter;

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

public class DetalleAgendaActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView rv_agenda;
    private ArrayList<Agenda> items;
    private String TOKEN;
    private JSONObject jsonObj;
    private JSONArray agenda;
    private int id_agenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_agenda);
        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_red2);

        SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NOM, MODE_PRIVATE);
        TOKEN = settings.getString("token", "");

        iniciaAgenda();
    }

    private void iniciaAgenda(){
        if(Constantes.compuebaConexion(this)){
            rv_agenda = (RecyclerView) findViewById(R.id.rv_agenda);
            new ConsultaAgenda().execute();
            rv_agenda.addOnItemTouchListener(new RecyclerTouchListener(this, rv_agenda, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    if(items.get(position).getStatus() == 0){
                        new AlertDialog.Builder(DetalleAgendaActivity.this)
                                .setIcon(R.drawable.ic_alerta)
                                .setTitle("¡Alerta!")
                                .setMessage("¿Desea ver la lista de acuerdos o marcar la reunión como terminada?")
                                .setPositiveButton("Terminar reunion", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        id_agenda = items.get(position).getIdAgenda();
                                        new ModificaAgenda().execute();
                                    }
                                })
                                .setNegativeButton("Ver lista de acuerdos", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }

                }

                @Override
                public void onLongClick(View view, int position) {}
            }));
        }else{
            Toast.makeText(this,"No hay conexión a internet",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private DetalleAgendaActivity.ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final DetalleAgendaActivity.ClickListener clicklistener){

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
    private class ConsultaAgenda extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DetalleAgendaActivity.this);
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setTitle("Espere...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(DetalleAgendaActivity.this)){
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constantes.URL_AGENDA);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                entityBuilder.addTextBody("metodo","consultaAgenda");
                entityBuilder.addTextBody("token",TOKEN);
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
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetalleAgendaActivity.this);
                    rv_agenda.setLayoutManager(linearLayoutManager);

                    items = new ArrayList<>();

                    boolean error;
                    try {
                        error = jsonObj.getBoolean("error");
                        if(error == false){
                            agenda = jsonObj.getJSONArray("agenda");

                            for(int i=0;i<agenda.length();i++){
                                JSONObject objetoJson = agenda.getJSONObject(i);
                                items.add(new Agenda(objetoJson.getInt("age_cod"),objetoJson.getString("age_descripcion"),objetoJson.getString("age_horaini"),objetoJson.getString("age_horafin"),objetoJson.getString("age_fechareunion"),objetoJson.getInt("age_status")));
                            }
                            AgendaAdapter adapter = new AgendaAdapter(items);
                            rv_agenda.setAdapter(adapter);
                            rv_agenda.setLayoutManager(new GridLayoutManager(DetalleAgendaActivity.this,1));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }
    private class ModificaAgenda extends AsyncTask<String, String, String >{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DetalleAgendaActivity.this);
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setTitle("Espere...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(DetalleAgendaActivity.this)){
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constantes.URL_AGENDA);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                entityBuilder.addTextBody("metodo","actualizaAgenda");
                entityBuilder.addTextBody("id_agenda",id_agenda+"");
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
                    iniciaAgenda();
                }
            });
        }
    }

}
