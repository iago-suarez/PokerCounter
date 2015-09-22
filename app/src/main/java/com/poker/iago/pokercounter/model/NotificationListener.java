package com.poker.iago.pokercounter.model;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;

import com.poker.iago.pokercounter.R;
import com.poker.iago.pokercounter.iu.MainNawDraver;

public class NotificationListener implements PokerCounterListener{

    private static final int NOTIF_ALERTA_ID = 1;
    private Context context;
    private NotificationCompat.Builder mBuilder;

    public NotificationListener(Context context) {
        this.context = context;
        //Generamos la notificación para lanzarla más adelante
        if(mBuilder == null) mBuilder =generateNotification();
    }

    @Override
    public void updateCounter() {

    }

    @Override
    public void levelFinish() {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
    }

    @Override
    public void levelChange() {

    }

    /**
     * Crea un NotificationCompat.Builder con la notificación de que se ha finalizado el actual
     * nivel de ciegas
     * @return
     */
    public NotificationCompat.Builder generateNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .setLargeIcon((((BitmapDrawable) context.getResources()
                                .getDrawable(R.drawable.ic_launcher)).getBitmap()))
                        .setContentTitle(context.getString(R.string.level_finished))
                        .setContentText(context.getString(R.string.level_finished_ext))
                        .setTicker(context.getString(R.string.level_finished_ext))
                        .setAutoCancel(true);
        Intent notIntent =
                new Intent(context, MainNawDraver.class);
        notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contIntent =
                PendingIntent.getActivity(
                        context, 0, notIntent, 0);

        mBuilder.setContentIntent(contIntent);

        return mBuilder;
    }

}
