package net.ddns.utn.redvecinal.listas;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.ddns.utn.redvecinal.R;

import java.util.List;

/**
 * Created by miguel on 27/09/2017.
 */

public class DirectorioAdapter extends RecyclerView.Adapter<DirectorioAdapter.DirectorioViewHolder> {
    private List<Directorio> items;
    private Context context;

    public static class DirectorioViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_r_foto;
        TextView tv_r_nombre,
                tv_r_telefono;
        public DirectorioViewHolder(View v){
            super(v);
            iv_r_foto = (ImageView) v.findViewById(R.id.iv_r_foto);
            tv_r_nombre = (TextView) v.findViewById(R.id.tv_r_nombre);
            tv_r_telefono = (TextView) v.findViewById(R.id.tv_r_telefono);
        }
    }

    public  DirectorioAdapter(List<Directorio> items){
        this.items = items;
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    @Override
    public DirectorioViewHolder onCreateViewHolder(ViewGroup viewGroup , int i ){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lista_directorio, viewGroup, false);
        context = viewGroup.getContext();
        return new DirectorioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DirectorioViewHolder viewHolder,int i){
        viewHolder.tv_r_nombre.setText(items.get(i).getNombre_contacto());
        viewHolder.tv_r_telefono.setText(items.get(i).getTelefono());
        String urlImagen = items.get(i).getFoto();
        try {
            if (urlImagen.equals("")) {
                viewHolder.iv_r_foto.setImageResource(R.drawable.logo_redvecinal);
            } else {
                Picasso.with(context).load(urlImagen).into(viewHolder.iv_r_foto);
            }
        }catch (Exception e){
            viewHolder.iv_r_foto.setImageResource(R.drawable.logo_redvecinal);
        }

    }
}
