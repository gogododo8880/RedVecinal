package net.ddns.utn.redvecinal.listas;

/**
 * Created by miguel on 23/10/2017.
 */

public class Agenda {
    String asunto,hora_inicio,hora_fin,fecha_reunion;
    int idAgenda,status;

    public Agenda(int idAgenda, String asunto, String hora_inicio, String hora_fin, String fecha_reunion, int status) {
        this.asunto = asunto;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.fecha_reunion = fecha_reunion;
        this.idAgenda = idAgenda;
        this.status = status;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getFecha_reunion() {
        return fecha_reunion;
    }

    public void setFecha_reunion(String fecha_reunion) {
        this.fecha_reunion = fecha_reunion;
    }

    public int getIdAgenda() {
        return idAgenda;
    }

    public void setIdAgenda(int idAgenda) {
        this.idAgenda = idAgenda;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
