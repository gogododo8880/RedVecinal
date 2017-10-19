package net.ddns.utn.redvecinal.listas;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.ddns.utn.redvecinal.R;

import java.util.List;

/**
 * Created by miguel on 28/09/2017.
 */

public class InicioAdapter extends  RecyclerView.Adapter<InicioAdapter.InicioViewHolder>  {
    private List<Inicio> items;
    private Context context;

    public static class InicioViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_r_imagen_seccion;
        TextView tv_r_seccion_titulo;

        public InicioViewHolder(View v){
            super(v);
            iv_r_imagen_seccion = (ImageView) v.findViewById(R.id.iv_r_imagen_seccion);
            tv_r_seccion_titulo = (TextView) v.findViewById(R.id.tv_r_seccion_titulo);
        }
    }

    public InicioAdapter (List<Inicio> items){
        this.items = items;
    }

    @Override
    public InicioViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_inicio,viewGroup,false);
        context = viewGroup.getContext();
        return new InicioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InicioViewHolder viewHolder, int i) {
        viewHolder.tv_r_seccion_titulo.setText(items.get(i).getTitulo());
        viewHolder.iv_r_imagen_seccion.setImageResource(items.get(i).getImagen());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
