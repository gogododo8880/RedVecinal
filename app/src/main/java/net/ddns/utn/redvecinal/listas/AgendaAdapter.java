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
 * Created by miguel on 23/10/2017.
 */

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {
    private List<Agenda> items;
    private Context context;

    public static class AgendaViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_status;
        TextView tv_asunto,
                tv_hora_inicio,
                tv_hora_fin,
                tv_fecha;

        public AgendaViewHolder(View v) {
            super(v);
            iv_status = (ImageView) v.findViewById(R.id.iv_status);
            tv_asunto = (TextView) v.findViewById(R.id.tv_asunto);
            tv_hora_inicio = (TextView) v.findViewById(R.id.tv_hora_inicio);
            tv_hora_fin = (TextView) v.findViewById(R.id.tv_hora_fin);
            tv_fecha = (TextView) v.findViewById(R.id.tv_fecha);
        }
    }
    public AgendaAdapter(List<Agenda> items){
        this.items = items;
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lista_agenda,viewGroup,false);
        context = viewGroup.getContext();
        return new AgendaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AgendaViewHolder viewHolder,int i){
        viewHolder.tv_asunto.setText(items.get(i).getAsunto());
        viewHolder.tv_fecha.setText(items.get(i).getFecha_reunion());
        viewHolder.tv_hora_fin.setText(items.get(i).getHora_fin());
        viewHolder.tv_hora_inicio.setText(items.get(i).getHora_inicio());
        if(items.get(i).getStatus()==0){
            viewHolder.iv_status.setImageResource(R.drawable.information_icon);
        }else{
            viewHolder.iv_status.setImageResource(R.drawable.confirm_icon);
        }
    }
}
