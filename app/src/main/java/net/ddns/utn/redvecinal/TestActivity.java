package net.ddns.utn.redvecinal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import net.ddns.utn.redvecinal.lib.Constantes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TestActivity extends AppCompatActivity{

    private ActionBar actionBar;
    private int currentTab = 0;
    private Button btn_enviar;
    private TabHost tabs;

    EditText et_nombre,
             et_ap_pat,
             et_ap_mat,
             et_curp,
             et_edad,
             et_telefono,
             et_direccion;

    Integer rb_p1,
            rb_p2,
            rb_p3,
            rb_p4,
            rb_p5,
            rb_p6,
            rb_p7,
            rb_p8;

    String nombre,
            ap_pat,
            ap_mat,
            curp,
            edad,
            telefono,
            direccion;

    private String test_resultado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        Resources res = getResources();
        tabs = (TabHost) findViewById(R.id.th_cuestionario);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("Test");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",res.getDrawable(R.drawable.ic_test));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("Datos");
        spec.setContent(R.id.tab2);
        spec.setIndicator("",res.getDrawable(R.drawable.ic_edit));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId)
            {
                View currentView = tabs.getCurrentView();
                if (tabs.getCurrentTab() > currentTab)
                {
                    currentView.setAnimation( inFromRightAnimation() );
                }
                else
                {
                    currentView.setAnimation( outToRightAnimation() );
                }

                currentTab = tabs.getCurrentTab();
                if(tabs.getCurrentTab()<1){
                    btn_enviar.setText("Siguente");
                    btn_enviar.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_siguiente), null);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }else{
                    btn_enviar.setText("Enviar");
                    btn_enviar.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_enviar), null);
                }
            }
        });

        btn_enviar = (Button) findViewById(R.id.btn_enviar);
        btn_enviar.setText("Siguente");
        btn_enviar.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_siguiente), null);
        btn_enviar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                int bandera =  tabs.getCurrentTab();
                if(bandera<1){
                    tabs.setCurrentTab(1);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                else{
                    if(valida()==true){
                        Snackbar.make(view,"Enviando test...",Snackbar.LENGTH_LONG).show();
                        sumar();
                    }
                }
            }
        });
        iniciaTest();
    }

    private void sumar() {
        int total = rb_p1+
                rb_p2+
                rb_p3+
                rb_p4+
                rb_p5+
                rb_p6+
                rb_p7+
                rb_p8;

        if(total <= 8){
            test_resultado = "poca probabilidad";
        }
        if(total >8 && total <= 11){
            test_resultado = "cierta probabilidad";
        }
        if(total>12){
            test_resultado = "mucha probabilidad";
        }
        if(rb_p6 ==3){
            test_resultado = "mucha probabilidad";
        }
        if(rb_p8 == 3){
            test_resultado = "mucha probabilidad";
        }

        nombre = Constantes.quitarCaracteres(et_nombre.getText().toString().trim().toUpperCase());
        ap_pat = Constantes.quitarCaracteres(et_ap_pat.getText().toString().trim().toUpperCase());
        ap_mat = Constantes.quitarCaracteres(et_ap_mat.getText().toString().trim().toUpperCase());
        curp = Constantes.quitarCaracteres(et_curp.getText().toString().trim().toUpperCase());
        edad = Constantes.quitarCaracteres(et_edad.getText().toString().trim());
        telefono = Constantes.quitarCaracteres(et_telefono.getText().toString().trim());
        direccion = Constantes.quitarCaracteres(et_direccion.getText().toString().trim().toUpperCase());

        Intent i = new Intent(TestActivity.this,InfoViolenciaActivity.class);
        i.putExtra("nombre",nombre);
        i.putExtra("ap_pat",ap_pat);
        i.putExtra("ap_mat",ap_mat);
        i.putExtra("curp",curp);
        i.putExtra("edad",edad);
        i.putExtra("telefono",telefono);
        i.putExtra("direccion",direccion);
        i.putExtra("test_resultado",test_resultado);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();


    }

    private boolean valida() {
        boolean respuesta;
        if(rb_p1==null || rb_p2==null || rb_p3==null || rb_p4==null || rb_p5==null || rb_p6==null || rb_p7==null || rb_p8==null){
            muestraError("¡Termine completamente el Test!");
            tabs.setCurrentTab(0);
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            respuesta = false;
        }
        else{
            respuesta = true;
        }
        if(et_nombre.getText().toString().equals("")|| et_ap_pat.getText().toString().equals("")
                || et_ap_mat.getText().toString().equals("") || et_curp.getText().toString().equals("")
                || et_edad.getText().toString().equals("") || et_telefono.getText().toString().equals("")
                || et_direccion.getText().toString().equals("")){
            if(et_nombre.getText().toString().equals("")){
                et_nombre.setError("campo obligatorio");
            }
            if(et_ap_pat.getText().toString().equals("")){
                et_ap_pat.setError("campo obligatorio");
            }
            if(et_ap_mat.getText().toString().equals("")){
                et_ap_mat.setError("campo obligatorio");
            }
            if(et_curp.getText().toString().equals("")){
                et_curp.setError("campo obligatorio");
            }
            if(et_edad.getText().toString().equals("")){
                et_edad.setError("campo obligatorio");
            }
            if(et_telefono.getText().toString().equals("")){
                et_telefono.setError("campo obligatorio");
            }
            if(et_direccion.getText().toString().equals("")){
                et_direccion.setError("campo obligatorio");
            }
            respuesta = false;
        }
        return respuesta;
    }

    private void iniciaTest() {
        RadioGroup rg_p1 = (RadioGroup) findViewById(R.id.rg_p1);
        RadioGroup rg_p2 = (RadioGroup) findViewById(R.id.rg_p2);
        RadioGroup rg_p3 = (RadioGroup) findViewById(R.id.rg_p3);
        RadioGroup rg_p4 = (RadioGroup) findViewById(R.id.rg_p4);
        RadioGroup rg_p5 = (RadioGroup) findViewById(R.id.rg_p5);
        RadioGroup rg_p6 = (RadioGroup) findViewById(R.id.rg_p6);
        RadioGroup rg_p7 = (RadioGroup) findViewById(R.id.rg_p7);
        RadioGroup rg_p8 = (RadioGroup) findViewById(R.id.rg_p8);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_ap_pat = (EditText) findViewById(R.id.et_ap_pat);
        et_ap_mat = (EditText) findViewById(R.id.et_ap_mat);
        et_curp = (EditText) findViewById(R.id.et_curp);
        et_edad = (EditText) findViewById(R.id.et_edad);
        et_telefono = (EditText) findViewById(R.id.et_telefono);
        et_direccion = (EditText) findViewById(R.id.et_direccion);

        rg_p1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p1_mucha:
                        rb_p1 = 3;
                        break;
                    case R.id.rb_p1_alguna:
                        rb_p1 = 2;
                        break;
                    case R.id.rb_p1_sin:
                        rb_p1 = 1;
                        break;
                }
            }
        });

        rg_p2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p2_mucha:
                        rb_p2 = 3;
                        break;
                    case R.id.rb_p2_alguna:
                        rb_p2 = 2;
                        break;
                    case R.id.rb_p2_sin:
                        rb_p2 = 1;
                        break;
                }
            }
        });

        rg_p3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p3_mucha:
                        rb_p3 = 3;
                        break;
                    case R.id.rb_p3_alguna:
                        rb_p3 = 2;
                        break;
                    case R.id.rb_p3_ninguna:
                        rb_p3 = 1;
                        break;
                }
            }
        });

        rg_p4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p4_mucha:
                        rb_p4 = 3;
                        break;
                    case R.id.rb_p4_alguna:
                        rb_p4 = 2;
                        break;
                    case R.id.rb_p4_ninguna:
                        rb_p4 = 1;
                        break;
                }
            }
        });

        rg_p5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p5_mucha:
                        rb_p5 = 3;
                        break;
                    case R.id.rb_p5_alguna:
                        rb_p5 = 2;
                        break;
                    case R.id.rb_p5_ninguna:
                        rb_p5 = 1;
                        break;
                }
            }
        });

        rg_p6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p6_mucha:
                        rb_p6 = 3;
                        break;
                    case R.id.rb_p6_alguna:
                        rb_p6 = 2;
                        break;
                    case R.id.rb_p6_ninguna:
                        rb_p6 = 1;
                        break;
                }
            }
        });

        rg_p7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p7_mucha:
                        rb_p7 = 3;
                        break;
                    case R.id.rb_p7_alguna:
                        rb_p7 = 2;
                        break;
                    case R.id.rb_p7_ninguna:
                        rb_p7 = 1;
                        break;
                }
            }
        });

        rg_p8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_p8_mucha:
                        rb_p8 = 3;
                        break;
                    case R.id.rb_p8_alguna:
                        rb_p8 = 2;
                        break;
                    case R.id.rb_p8_ninguna:
                        rb_p8 = 1;
                        break;
                }
            }
        });
    }

    public Animation inFromRightAnimation(){
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(240);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }
    public Animation outToRightAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(240);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private void muestraError(String mensaje){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle( "Alerta" )
                .setIcon(R.drawable.ic_alerta)
                .setMessage(mensaje)
                .show();
    }

}
