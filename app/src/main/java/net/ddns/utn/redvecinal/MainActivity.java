package net.ddns.utn.redvecinal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.ddns.utn.redvecinal.lib.Constantes;
import net.ddns.utn.redvecinal.listas.DetalleDirectorioActivity;
import net.ddns.utn.redvecinal.listas.Directorio;
import net.ddns.utn.redvecinal.listas.DirectorioAdapter;
import net.ddns.utn.redvecinal.listas.Inicio;
import net.ddns.utn.redvecinal.listas.InicioAdapter;

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout panel;
    private View child;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private View header;
    private NavigationView navigationView;
    private long lastPressedTime;
    private static final int PERIOD = 1000;
    private Animation animation;

    private TextView subtitulo;
    private TextView uNombre;
    private ImageView fotoPerfil;

    RecyclerView rv_directorio;
    private RecyclerView rv_inicio;

    //variables preferencias
    private String TOKEN;
    private String NOMBRE;
    private String AP_PAT;
    private String AP_MAT;
    private String IMAGEN;
    private String PIN;

    //variables json
    private JSONObject jsonObj;
    private JSONArray contactos;

    ArrayList<Directorio> items;

    TextView tv_fecha_agenda;
    TextView tv_hora_inicio;
    TextView tv_hora_fin;

    String fecha_agenda;
    String hora_inicio;
    String hora_fin;
    String descripcion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.startService(new Intent(
                MainActivity.this, UpdateService.class));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_red2);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_in);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);

        SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NOM, MODE_PRIVATE);

        TOKEN = settings.getString("token", "");
        NOMBRE = settings.getString("nombre","");
        AP_PAT = settings.getString("ap_pat","");
        AP_MAT = settings.getString("ap_mat","");
        IMAGEN = settings.getString("foto","");
        PIN = settings.getString("pin","");

        subtitulo = (TextView) header.findViewById(R.id.subtitulo);
        uNombre = (TextView) header.findViewById(R.id.u_nombre);
        fotoPerfil = (ImageView) header.findViewById(R.id.iv_usuario);
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.removeAllViews();
                child = getLayoutInflater().inflate(R.layout.child_inicio, null);
                panel.addView(child);
                construye();
            }
        });

        cargainfoUsuario();

        panel = (LinearLayout) findViewById(R.id.child);
        child = getLayoutInflater().inflate(R.layout.child_inicio, null);
        panel.addView(child);
        construye();
        evalua();
    }

    private void cargainfoUsuario() {
        uNombre.setText(NOMBRE+" "+AP_PAT+" "+AP_MAT);
        if(IMAGEN.equals("null")){
            fotoPerfil.setImageResource(R.drawable.directorio);
            fotoPerfil.setLayoutParams(new LinearLayout.LayoutParams(120,120));
        }else{
            IMAGEN = Constantes.URL_PRIMARY + IMAGEN;
            Picasso.with(this).load(IMAGEN).resize(120,120).into(fotoPerfil);
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_emergencia) {
            Intent i = new Intent(this, AlertaActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_informacion) {
            return true;
        }
        if (id == R.id.action_legal) {
            return true;
        }
        if(id == R.id.action_salir){
            SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NOM, MODE_PRIVATE);

            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = settings.edit();
            editor.clear().commit();
            Intent i = new Intent(MainActivity.this,SplashActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_violencia) {
            panel.removeAllViews();
            child = getLayoutInflater().inflate(R.layout.child_violencia_genero, null);
            panel.addView(child);
            iniciaViolencia();
        } else if (id == R.id.nav_directorio) {
            panel.removeAllViews();
            child = getLayoutInflater().inflate(R.layout.child_directorio, null);
            panel.addView(child);
            iniciaDirectorio();
        } else if (id == R.id.nav_agenda) {
            panel.removeAllViews();
            child = getLayoutInflater().inflate(R.layout.child_agenda, null);
            panel.addView(child);
            iniciaAgenda();
        } else if (id == R.id.nav_denuncia) {
            panel.removeAllViews();
            child = getLayoutInflater().inflate(R.layout.child_denuncia, null);
            panel.addView(child);
            iniciaDenuncia();
        } else if (id == R.id.nav_contactos) {
            panel.removeAllViews();
            child = getLayoutInflater().inflate(R.layout.child_contactos, null);
            panel.addView(child);
            iniciaContactos();
        } else if (id == R.id.nav_sitio) {
            iniciaSitio();
        } else if (id == R.id.nav_facebook) {
            iniciaFacebook();
        } else if (id == R.id.nav_twitter) {
            iniciaTwitter();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // componentes de la seccion principal del menu principal

    private void construye() {
        tema_azul(R.string.inicio);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        rv_inicio = (RecyclerView) findViewById(R.id.rv_inicio);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_inicio.setLayoutManager(linearLayoutManager);

        final ArrayList<Inicio> items = new ArrayList<>();

        items.add(new Inicio(R.string.violencia_genero, R.drawable.violencia));
        items.add(new Inicio(R.string.directorio, R.drawable.directorio));
        items.add(new Inicio(R.string.agenda_vecinal, R.drawable.agenda_vecinal));
        items.add(new Inicio(R.string.denuncia, R.drawable.denuncia));
        items.add(new Inicio(R.string.contactos, R.drawable.contactos));
        items.add(new Inicio(R.string.sitio, R.drawable.seguridad));
        items.add(new Inicio(R.string.facebook, R.drawable.facebook));
        items.add(new Inicio(R.string.twitter, R.drawable.twitter));

        InicioAdapter adapter = new InicioAdapter(items);
        rv_inicio.setAdapter(adapter);
        rv_inicio.setLayoutManager(new GridLayoutManager(this, 2));

        rv_inicio.addOnItemTouchListener(new RecyclerTouchListener(this,
                rv_inicio, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                switch (items.get(position).getTitulo()) {
                    case R.string.violencia_genero:
                        panel.startAnimation(animation);
                        panel.removeAllViews();
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        child = getLayoutInflater().inflate(R.layout.child_violencia_genero, null);
                        panel.addView(child);
                        iniciaViolencia();
                        break;
                    case R.string.directorio:
                        panel.startAnimation(animation);
                        panel.removeAllViews();
                        child = getLayoutInflater().inflate(R.layout.child_directorio, null);
                        panel.addView(child);
                        iniciaDirectorio();
                        break;
                    case R.string.agenda_vecinal:
                        panel.startAnimation(animation);
                        panel.removeAllViews();
                        child = getLayoutInflater().inflate(R.layout.child_agenda, null);
                        panel.addView(child);
                        iniciaAgenda();
                        break;
                    case R.string.denuncia:
                        panel.startAnimation(animation);
                        panel.removeAllViews();
                        child = getLayoutInflater().inflate(R.layout.child_denuncia, null);
                        panel.addView(child);
                        iniciaDenuncia();
                        break;
                    case R.string.contactos:
                        panel.startAnimation(animation);
                        panel.removeAllViews();
                        child = getLayoutInflater().inflate(R.layout.child_contactos, null);
                        panel.addView(child);
                        iniciaContactos();
                        break;
                    case R.string.sitio:
                        iniciaSitio();
                        break;
                    case R.string.facebook:
                        iniciaFacebook();
                        break;
                    case R.string.twitter:
                        iniciaTwitter();
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }

    private void iniciaViolencia() {
        tema_rosa(R.string.violencia_genero);
        //decaracion de botones de la vista violencia de genero
        Button btn_nom,
                btn_procedimiento,
                btn_caso,
                btn_protocolo,
                btn_decreto1,
                btn_decreto2,
                btn_test;

        //inicializacion de variables con la vista
        btn_nom = (Button) findViewById(R.id.btn_nom);
        btn_procedimiento = (Button) findViewById(R.id.btn_procedimiento);
        btn_caso = (Button) findViewById(R.id.btn_caso);
        btn_protocolo = (Button) findViewById(R.id.btn_protocolo);
        btn_decreto1 = (Button) findViewById(R.id.btn_decreto1);
        btn_decreto2 = (Button) findViewById(R.id.btn_decreto2);
        btn_test = (Button) findViewById(R.id.btn_test);

        //realizacion de acciones para los botones en la vista

        btn_nom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyReadAssets("norma_violencia_sexual.pdf");
            }
        });
        btn_procedimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyReadAssets("procedimiento_violencia_sexual.pdf");
            }
        });
        btn_caso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyReadAssets("caso_feminicidio.pdf");
            }
        });
        btn_protocolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyReadAssets("protocolo.pdf");
            }
        });
        btn_decreto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyReadAssets("decreto_desap1.pdf");
            }
        });
        btn_decreto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyReadAssets("decreto_desap2.pdf");
            }
        });
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "Iniciando el Test...", Snackbar.LENGTH_LONG).show();
                Intent i = new Intent(MainActivity.this, TestActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

    private void iniciaDirectorio() {
        tema_azul(R.string.directorio);
        if(Constantes.compuebaConexion(MainActivity.this)){
            new ConsultaContactos().execute();
            rv_directorio = (RecyclerView) findViewById(R.id.rv_directorio2);

            rv_directorio.addOnItemTouchListener(new RecyclerTouchListener(this,
                    rv_directorio, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    String nombre,
                            telefono,
                            clave,
                            sitio,
                            imagen;

                    nombre = items.get(position).getNombre_contacto();
                    telefono = items.get(position).getTelefono();
                    clave = items.get(position).getClave_atencion();
                    sitio = items.get(position).getSitio();
                    imagen = items.get(position).getFoto();

                    Intent i = new Intent(MainActivity.this, DetalleDirectorioActivity.class);
                    i.putExtra("nombre", nombre);
                    i.putExtra("telefono", telefono);
                    i.putExtra("clave", clave);
                    i.putExtra("sitio", sitio);
                    i.putExtra("imagen",imagen);
                    startActivity(i);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }

                @Override
                public void onLongClick(View view, int position) {
                    String telefono;

                    telefono = items.get(position).getTelefono();

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);

                        return;
                    }

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + telefono));
                    startActivity(callIntent);
                }
            }));
        }
    }

    private void iniciaAgenda() {
        tema_azul(R.string.agenda_vecinal);

        Button btn_fecha_agenda = (Button) findViewById(R.id.btn_fecha_agenda);
        Button btn_hora_inicio = (Button) findViewById(R.id.btn_hora_inicio);
        Button btn_hora_fin = (Button) findViewById(R.id.btn_hora_fin);
        Button btn_ver_agenda = (Button) findViewById(R.id.btn_ver_agenda);
        Button btn_guardar = (Button) findViewById(R.id.btn_guardar);

        btn_fecha_agenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {popupMuestraCalendario();
            }
        });
        btn_hora_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {popupMuestraReloj("inicio");
            }
        });
        btn_hora_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMuestraReloj("fin");
            }
        });
        btn_ver_agenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verDetalleAgenda("Ver");
            }
        });
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verDetalleAgenda("guardar");
            }
        });
    }

    private void verDetalleAgenda(final String funcion){
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        input.setGravity(Gravity.CENTER);
        input.setHint("Ingresa Pin...");
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("¡Atención!")
                .setIcon(R.drawable.ic_alerta)
                .setMessage("Pin requerido para esta acción.")
                .setView(input).setPositiveButton("Ver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pin =  Constantes.getCifrado(input.getText().toString(),"SHA1");
                if(PIN.equals(pin)){
                    if(funcion == "guardar"){
                        EditText et_descripcion =(EditText) findViewById(R.id.et_descripcion);
                        descripcion = et_descripcion.getText().toString().trim().toUpperCase();
                        new GuardarAgenda().execute();
                    }else{

                        Intent intent = new Intent(MainActivity.this,DetalleAgendaActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Pin incorrecto",Toast.LENGTH_LONG).show();
                }
            }
        }).show();
    }

    private void popupMuestraReloj(final String funcion){
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);

                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                if(funcion.equals("inicio")){
                    tv_hora_inicio = (TextView) findViewById(R.id.tv_hora_inicio);
                    tv_hora_inicio.setText("Hora inicio: "+sdf.format(calendar.getTime()));
                    hora_inicio = sdf.format(calendar.getTime());
                }else {
                    tv_hora_fin = (TextView) findViewById(R.id.tv_hora_fin);
                    tv_hora_fin.setText("Hora fin: "+sdf.format(calendar.getTime()));
                    hora_fin = sdf.format(calendar.getTime());
                }
            }
        };

        new TimePickerDialog(this,
                time,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)).show();
    }

    private void popupMuestraCalendario() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd MMMM yyyy"; // your format
                String formato = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                SimpleDateFormat formatoInsert = new SimpleDateFormat(formato, Locale.getDefault());

                tv_fecha_agenda =(TextView) findViewById(R.id.tv_fecha_agenda);
                tv_fecha_agenda.setText(sdf.format(calendar.getTime()));
                fecha_agenda = formatoInsert.format(calendar.getTime());

            }

        };
        new DatePickerDialog(this,date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void iniciaDenuncia() {
        tema_azul(R.string.denuncia);
    }

    private void iniciaContactos() {
        tema_azul(R.string.contactos);
    }

    //componentes de la seccion comunicate del menu principal

    private void iniciaSitio() {
        String urlPage = "http://www.seguridadneza.gob.mx/portal/";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
    }

    private void iniciaTwitter() {
        Intent intent = null;
        try {

            this.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=seguridadneza"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/seguridadneza"));
        }
        startActivity(intent);
    }

    private void iniciaFacebook() {
        String facebookId = "fb://page/1649948185241636";
        String urlPage = "https://www.facebook.com/NezaSeguridad/";

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)));
        } catch (Exception e) {
            //Abre url de pagina.
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
        }
    }

    //metodos de configuracion y de uso de librerias

    /**
     * metodo que funciona para copiar y abrir un archivo pdf alojado en a aplicacion
     *
     * @param archivo parametro de entrada que recibe el nombre del archivo que se invocara para ser abierto
     */
    private void copyReadAssets(String archivo) {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;

        String strDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "Pdfs";
        File fileDir = new File(strDir);
        fileDir.mkdirs();   // crear la ruta si no existe
        File file = new File(fileDir, archivo);


        try {

            in = assetManager.open(archivo);  //leer el archivo de assets
            out = new BufferedOutputStream(new FileOutputStream(file)); //crear el archivo


            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "Pdfs" + "/" + archivo), "application/pdf");
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * metodo que sirve para copiar el archivo directamente de la apk al almacenamiento interno
     *
     * @param in  parametro que recibe el stream desde el metodo copyReadAssets
     * @param out parametro que enviar el resultado del stream al metodo copyReadAssets
     * @throws IOException recibe error si no es ejecutado correctamente el comando (evita ARN)
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * metodo que sirve para evaluar los permisos de configuracion de la app
     */
    private void evalua() {
        //verifica la version de android
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            //si es menor que lollipop
        } else {
            //si es mayor que lollipop
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

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

    private void tema_azul(int mensaje) {
        // cambia el fondo y el titulo del toolbar
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setTitle(mensaje);

        // cambia a azul el fondo y el contenido del texto del subtitulo en el encabezado del menu
        TextView subtitulo = (TextView) header.findViewById(R.id.subtitulo);
        subtitulo.setText(mensaje);
        LinearLayout lnav_bar = (LinearLayout) header.findViewById(R.id.lnav_bar);
        lnav_bar.setBackgroundResource(R.drawable.background_blue);

        // verifica la version de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //si es mayor a lollipop

            //cambia el color de la barra de estado y navegacion a azul
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void tema_rosa(int mensaje) {
        // cambia el color de la barra de herramientas y su titulo
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        actionBar.setTitle(mensaje);

        // cambia el contenido del texto y la imagen de fondo del menu principal
        subtitulo.setText(mensaje);
        LinearLayout lnav_bar = (LinearLayout) header.findViewById(R.id.lnav_bar);
        lnav_bar.setBackgroundResource(R.drawable.background_pink);

        // verifica version de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //si es mayor a lollipop

            //cambia la imagen de la barra de estado y navegacion
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccentligth));
            window.setNavigationBarColor(getResources().getColor(R.color.colorAccentligth));
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < PERIOD) {
                        finish();
                    } else {
                        //Snackbar.make(header,"Vuelve a presionar para salir",Snackbar.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), "Vuelve a presionar para salir",
                          //      Toast.LENGTH_SHORT).show();

                        panel.removeAllViews();
                        child = getLayoutInflater().inflate(R.layout.child_inicio, null);
                        panel.addView(child);
                        construye();
                        Toast.makeText(MainActivity.this,"Presione nuevamente para salir.",Toast.LENGTH_LONG).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }

    class ConsultaContactos extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =  new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setTitle("espere...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(MainActivity.this)){
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
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
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
                            rv_directorio.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));

                        }else{
                            Toast.makeText(MainActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    class GuardarAgenda extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =new ProgressDialog(MainActivity.this);
            dialog.setTitle("espere...");
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(MainActivity.this)){
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constantes.URL_AGENDA);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                entityBuilder.addTextBody("metodo","insertaAgenda");
                entityBuilder.addTextBody("token",TOKEN);
                entityBuilder.addTextBody("descripcion",Constantes.quitarCaracteres(descripcion));
                entityBuilder.addTextBody("fecha_reunion",fecha_agenda);
                entityBuilder.addTextBody("hora_fin",hora_fin);
                entityBuilder.addTextBody("hora_inicio",hora_inicio);
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
                        Boolean error = jsonObj.getBoolean("error");
                        if(error == false ){
                            Intent intent = new Intent(MainActivity.this,DetalleAgendaActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            Toast.makeText(MainActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
