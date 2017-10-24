package net.ddns.utn.redvecinal;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import net.ddns.utn.redvecinal.lib.Constantes;
import net.ddns.utn.redvecinal.listas.Agenda;
import net.ddns.utn.redvecinal.listas.AgendaAdapter;

import java.util.ArrayList;

public class DetalleAgendaActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView rv_agenda;
    private ArrayList<Agenda> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_agenda);
        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_red2);
        iniciaAgenda();
    }

    private void iniciaAgenda(){
        if(Constantes.compuebaConexion(this)){
            rv_agenda = (RecyclerView) findViewById(R.id.rv_agenda);
            new ConsultaAgenda().execute();
        }else{
            Toast.makeText(this,"No hay conexión a internet",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class ConsultaAgenda extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DetalleAgendaActivity.this);
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.information_icon);
            dialog.setTitle("Espere...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
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

                    items.add(new Agenda("un asunto","08:00","09:00","01-02-2017",0));
                    items.add(new Agenda("otro asunto","11:00","12:00","01-03-2017",1));

                    AgendaAdapter adapter = new AgendaAdapter(items);
                    rv_agenda.setAdapter(adapter);
                    rv_agenda.setLayoutManager(new GridLayoutManager(DetalleAgendaActivity.this,1));
                }
            });
        }


    }
}
