package net.ddns.utn.redvecinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.ddns.utn.redvecinal.listas.Directorio;
import net.ddns.utn.redvecinal.listas.DirectorioAdapter;

import java.util.ArrayList;

public class DirectorioActivity extends AppCompatActivity {

    RecyclerView rv_directorio;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directorio);

        rv_directorio = (RecyclerView) findViewById(R.id.rv_directorio);

        linearLayoutManager = new LinearLayoutManager(this);
        rv_directorio.setLayoutManager(linearLayoutManager);

        ArrayList<Directorio> items = new ArrayList<>();

        items.add(new Directorio("Bomberos","263738372","http://www.neza.gob.mx/","lol","https://img1.etsystatic.com/186/0/13221305/il_570xN.1275201211_lsye.jpg"));
        items.add(new Directorio("Bomberos","263738372","http://www.neza.gob.mx/","lol","https://img1.etsystatic.com/186/0/13221305/il_570xN.1275201211_lsye.jpg"));

        try{
            DirectorioAdapter adapter = new DirectorioAdapter(items);
            rv_directorio.setAdapter(adapter);
            rv_directorio.setLayoutManager(new GridLayoutManager(this,1));
        }catch (Exception e){
            System.out.print(e);
        }


    }
}
