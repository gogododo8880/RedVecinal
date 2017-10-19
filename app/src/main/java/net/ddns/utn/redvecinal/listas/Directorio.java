package net.ddns.utn.redvecinal.listas;

/**
 * Created by miguel on 27/09/2017.
 */

public class Directorio {

    String nombre_contacto;
    String telefono;
    String sitio;
    String clave_atencion;
    String foto;

    public Directorio(String nombre_contacto, String telefono, String sitio, String clave_atencion, String foto) {
        this.nombre_contacto = nombre_contacto;
        this.telefono = telefono;
        this.sitio = sitio;
        this.clave_atencion = clave_atencion;
        this.foto = foto;
    }

    public String getNombre_contacto() {
        return nombre_contacto;
    }

    public void setNombre_contacto(String nombre_contacto) {
        this.nombre_contacto = nombre_contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public String getClave_atencion() {
        return clave_atencion;
    }

    public void setClave_atencion(String clave_atencion) {
        this.clave_atencion = clave_atencion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
