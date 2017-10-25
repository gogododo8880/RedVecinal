package net.ddns.utn.redvecinal.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by miguel on 04/10/2017.
 */

public class Constantes {
    public static final String PREFS_NOM = "login";

    //public static final String URL_LOGIN = "http://192.168.1.80/av_app/usuarios.php";
    //public static final String URL_PRIMARY= "http://192.168.1.80/av_app/";
    //public static final String URL_CONTACTOS= "http://192.168.1.80/av_app/contactos.php";
    //public static final String URL_TEST = "http://192.168.1.80/av_app/test.php";
    //public static final String URL_AGENDA = "http://192.168.1.80/av_app/agenda.php";




    public static final String URL_LOGIN = "http://172.16.28.4:8080/DGSC/appRV/usuarios.php";
    public static final String URL_PRIMARY= "http://172.16.28.4:8080/DGSC/appRV/";
    public static final String URL_CONTACTOS= "http://172.16.28.4:8080/DGSC/appRV/contactos.php";
    public static final String URL_TEST = "http://172.16.28.4:8080/DGSC/appRV/test.php";
    public static final String URL_AGENDA = "http://172.16.28.4:8080/DGSC/appRV/agenda.php";
    public static final String URL_ALERTAS= "http://172.16.28.4:8080/DGSC/appRV/alertas.php";

    public static boolean compuebaConexion(Context context){
        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public static String getCifrado(String texto, String hashType) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(hashType);
            byte[] array = md.digest(texto.getBytes());
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.err.println("Error "+e.getMessage());
        }
        return "";
    }
    public static String quitarCaracteres(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }
}
