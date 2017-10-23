package net.ddns.utn.redvecinal;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.ddns.utn.redvecinal.lib.Constantes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private Animation animation;

    LinearLayout panel;
    private View child;

    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath = null;
    private ImageButton btn_cargaImagen;
    private long lastPressedTime;
    private static final int PERIOD = 1000;

    private EditText et_nombre;
    private EditText et_ap_pat;
    private EditText et_ap_mat;
    private EditText et_telefono;
    private EditText et_correo;
    private EditText et_pin;
    private EditText et_pin_conf;

    String nombre,
            ap_pat,
            ap_mat,
            telefono,
            correo,
            pin,
            pin_conf;

    private boolean error;
    private JSONObject jsonObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.left_in);

        panel = (LinearLayout) findViewById(R.id.child);
        child = getLayoutInflater().inflate(R.layout.child_login, null);
        panel.addView(child);
        iniciaLogin();

    }

    private void iniciaLogin(){
        Button btn_entrar = (Button) findViewById(R.id.btn_entrar);
        et_correo = (EditText) findViewById(R.id.et_correo);
        et_pin = (EditText) findViewById(R.id.et_pin);
        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = et_correo.getText().toString().trim();
                pin = et_pin.getText().toString().trim();

                if(correo.equals("") || pin.equals("")){
                    if(correo.equals("")){
                        et_correo.setError("Ingresa correo.");
                    }
                    if(pin.equals("")){
                        et_pin.setError("Ingresa pin.");
                    }
                }
                else{
                    new Login().execute();
                }
            }
        });

        Button btn_registrar = (Button) findViewById(R.id.btn_registrar);
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                panel.startAnimation(animation);
                panel.removeAllViews();
                child = getLayoutInflater().inflate(R.layout.child_registro, null);
                panel.addView(child);
                iniciaRegistro();
            }
        });
    }

    private void iniciaRegistro() {
        Button btn_guardar = (Button)  findViewById(R.id.btn_guardar);
        Button btn_correo = (Button) findViewById(R.id.btn_cargar_correo);
        btn_cargaImagen = (ImageButton) findViewById(R.id.ibtn_carga_imagen);

        et_nombre =  (EditText) findViewById(R.id.et_nombre);
        et_ap_pat = (EditText) findViewById(R.id.et_ap_pat);
        et_ap_mat = (EditText) findViewById(R.id.et_ap_mat);
        et_telefono = (EditText) findViewById(R.id.et_telefono);
        et_correo = (EditText) findViewById(R.id.et_correo);
        et_pin = (EditText) findViewById(R.id.et_pin);
        et_pin_conf = (EditText) findViewById(R.id.et_pin_conf);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
                }
        });
        btn_cargaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    //si es menor que lollipop
                } else {
                    //si es mayor que lollipop
                    if (ContextCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    }else{
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Selecciona una Imagen de perfil"), PICK_IMAGE_REQUEST);
                    }
                }

            }
        });
        btn_correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //verifica la version de android
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    //si es menor que lollipop
                } else {
                    //si es mayor que lollipop
                    if (ContextCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.GET_ACCOUNTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.GET_ACCOUNTS},
                                1);
                    }
                }
                Account[] accounts = AccountManager.get(LoginActivity.this).getAccounts();
                for (Account account : accounts) {
                    et_correo.setText(account.name);
                    et_correo.setEnabled(false);
                    break;
                }
            }
        });
        et_correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_correo.setText("");
            }
        });
    }

    private void registrar() {
        if(et_nombre.getText().length() == 0 ||
                et_ap_pat.getText().length() == 0 ||
                et_ap_mat.getText().length() == 0 ||
                et_telefono.getText().length() == 0 ||
                et_correo.getText().length() == 0 ||
                et_pin.getText().length() == 0 ||
                et_pin_conf.getText().length() == 0){
            if(et_nombre.getText().length() == 0){
                et_nombre.setError("Ingresa el nombre");
            }
            if(et_ap_pat.getText().length() == 0){
                et_ap_pat.setError("Ingresa apellido paterno");
            }
            if(et_ap_mat.getText().length() == 0){
                et_ap_mat.setError("Ingresa apellido materno");
            }
            if(et_telefono.getText().length() == 0){
                et_telefono.setError("Ingresa telefono");
            }
            if(et_correo.getText().length() == 0){
                et_correo.setError("Ingresa correo");
            }
            if(et_pin.getText().length() == 0){
                et_pin.setError("Ingresa pin");
            }
            if(et_pin_conf.getText().length() == 0){
                et_pin_conf.setError("Ingresa pin de confirmación");
            }

        }else{

            pin = et_pin.getText().toString().trim().toUpperCase();
            pin_conf = et_pin_conf.getText().toString().trim().toUpperCase();

            if(pin.equals(pin_conf)){
                nombre = et_nombre.getText().toString().trim().toUpperCase();
                ap_pat = et_ap_pat.getText().toString().trim().toUpperCase();
                ap_mat = et_ap_mat.getText().toString().trim().toUpperCase();
                telefono = et_telefono.getText().toString().trim().toUpperCase();
                correo = et_correo.getText().toString().trim();
                new RegistraUsuario().execute();
            }else{
                et_pin_conf.setError("Confirmación incorrecta");
            }
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                btn_cargaImagen.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    class Login extends AsyncTask<String,String,String>{
        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Espere...");
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            if(Constantes.compuebaConexion(LoginActivity.this) == true){

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httPost = new HttpPost(Constantes.URL_LOGIN);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                try {

                    entityBuilder.addTextBody("correo",correo);
                    entityBuilder.addTextBody("pin",pin);
                    entityBuilder.addTextBody("metodo","login");
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
                    if(Constantes.compuebaConexion(LoginActivity.this)==true){
                        try {
                            Boolean error = jsonObj.getBoolean("error");

                            if(error == false){
                                SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NOM, MODE_PRIVATE);
                                // Writing data to SharedPreferences
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("nombre", jsonObj.getString("nombre"));
                                editor.putString("ap_pat", jsonObj.getString("ap_pat"));
                                editor.putString("ap_mat",jsonObj.getString("ap_mat"));
                                editor.putString("token",jsonObj.getString("token"));
                                editor.putString("foto",jsonObj.getString("foto"));
                                editor.putString("pin",Constantes.getCifrado(pin, "SHA1"));
                                editor.commit();

                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Sin conexión a internet",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    class RegistraUsuario extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Guardando");
            dialog.setIcon(R.drawable.ic_alerta);
            dialog.setMessage("Espere...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(Constantes.compuebaConexion(LoginActivity.this)){
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httPost = new HttpPost(Constantes.URL_LOGIN);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                if(filePath == null){
                    try {
                        entityBuilder.addTextBody("nombre",nombre);
                        entityBuilder.addTextBody("ap_pat",ap_pat);
                        entityBuilder.addTextBody("ap_mat",ap_mat);
                        entityBuilder.addTextBody("telefono",telefono);
                        entityBuilder.addTextBody("correo",correo);
                        entityBuilder.addTextBody("pin",pin);
                        entityBuilder.addTextBody("metodo","registrar");
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
                }else{
                    try {
                        String path = getPath(filePath);
                        File file = new File(path);

                        entityBuilder.addTextBody("nombre",nombre);
                        entityBuilder.addTextBody("ap_pat",ap_pat);
                        entityBuilder.addTextBody("ap_mat",ap_mat);
                        entityBuilder.addTextBody("telefono",telefono);
                        entityBuilder.addTextBody("correo",correo);
                        entityBuilder.addTextBody("pin",pin);
                        entityBuilder.addTextBody("metodo","registrar");
                        entityBuilder.addPart("foto", new FileBody(file));
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }

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
                    if(Constantes.compuebaConexion(LoginActivity.this)){
                        try {
                            error = jsonObj.getBoolean("error");
                            if(error == false){
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("¡Información!")
                                        .setMessage("¡Registrado con exito!\n\n  Le recomendamos guardar su correo y pin en un papel.\n \nCorreo: "+correo+"\nPin: "+pin)
                                        .setCancelable(false)
                                        .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                panel.startAnimation(animation);
                                                panel.removeAllViews();
                                                child = getLayoutInflater().inflate(R.layout.child_login, null);
                                                panel.addView(child);
                                                iniciaLogin();
                                                et_correo.setText(correo);
                                                et_pin.setText(pin);
                                            }
                                        }).show();

                            }
                            else{
                                Toast.makeText(LoginActivity.this,jsonObj.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this,"Sin conexión",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }



    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
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
                        child = getLayoutInflater().inflate(R.layout.child_login, null);
                        panel.addView(child);
                        iniciaLogin();

                        Toast.makeText(LoginActivity.this,"Presione nuevamente para salir.",Toast.LENGTH_LONG).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }
}
