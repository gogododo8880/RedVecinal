package net.ddns.utn.redvecinal;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class UpdateService extends Service {
    int bandera = 0;
    Handler m_handler;
    Runnable m_handlerTask ;
    int timeleft = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new Receiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try{
            boolean screenOn = intent.getBooleanExtra("screen_state", false);
            if (!screenOn) {
                // your code
                Log.e("nota:","encendido: "+ bandera);
                if(bandera == 2) {
                    Intent i = new Intent(this, AlertaActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    bandera = 0;
                }else {
                    bandera++;
                }
            } else {
                // your code
                Log.e("nota:","apagado");
                ejecutaCron();
            }
        }catch(Exception e){
            bandera = 0;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void ejecutaCron(){
        m_handler = new Handler();
        m_handlerTask = new Runnable()
        {
            @Override
            public void run() {
                if(timeleft>=0)
                {
                    // do stuff
                    Log.i("timeleft",""+timeleft);
                    if(timeleft == 0){
                        bandera = 0;
                    }
                    timeleft--;
                }
                else
                {
                    m_handler.removeCallbacks(m_handlerTask); // cancel run
                }
                m_handler.postDelayed(m_handlerTask, 1000);
            }
        };
        m_handlerTask.run();
        timeleft = 5;
    }
}
