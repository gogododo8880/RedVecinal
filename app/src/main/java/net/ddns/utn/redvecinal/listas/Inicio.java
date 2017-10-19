package net.ddns.utn.redvecinal.listas;

/**
 * Created by miguel on 28/09/2017.
 */

public class Inicio {
    int titulo;
    int imagen;

    public Inicio(int titulo, int imagen) {
        this.titulo = titulo;
        this.imagen = imagen;
    }

    public int getTitulo() {
        return titulo;
    }

    public void setTitulo(int titulo) {
        this.titulo = titulo;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}
