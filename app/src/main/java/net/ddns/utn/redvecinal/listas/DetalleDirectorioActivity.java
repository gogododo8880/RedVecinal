package net.ddns.utn.redvecinal.listas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.ddns.utn.redvecinal.R;

public class DetalleDirectorioActivity extends AppCompatActivity {

    String nombre,
            telefono,
            clave,
            sitio,
            imagen;

    private ActionBar actionBar;

    private TextView tv_nombre,
            tv_telefono,
            tv_sitio;

    private ImageView iv_foto_contacto;

    private FloatingActionButton fab_telefono,
            fab_sitio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_directorio);
        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_red2);
        actionBar.setTitle(R.string.directorio);

        Bundle bundle = getIntent().getExtras();

        nombre = bundle.getString("nombre");
        telefono = bundle.getString("telefono");
        clave = bundle.getString("clave");
        sitio = bundle.getString("sitio");
        imagen = bundle.getString("imagen");

        tv_nombre = (TextView) findViewById(R.id.tv_nombre);
        tv_telefono = (TextView) findViewById(R.id.tv_telefono);
        tv_sitio = (TextView) findViewById(R.id.tv_sitio);
        iv_foto_contacto = (ImageView) findViewById(R.id.iv_foto_contacto);

        tv_nombre.setText(nombre);
        tv_telefono.setText(telefono);
        tv_sitio.setText(sitio);

        if (imagen == null) {
            iv_foto_contacto.setImageResource(R.drawable.logo_redvecinal);
        } else {
            Picasso.with(this).load(imagen).into(iv_foto_contacto);
        }

        fab_telefono = (FloatingActionButton) findViewById(R.id.fab_telefono);
        fab_sitio = (FloatingActionButton) findViewById(R.id.fab_sitio);

        fab_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(DetalleDirectorioActivity.this, Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(DetalleDirectorioActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + telefono));
                startActivity(callIntent);
            }
        });

        fab_sitio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sitio)));
            }
        });

    }
}
