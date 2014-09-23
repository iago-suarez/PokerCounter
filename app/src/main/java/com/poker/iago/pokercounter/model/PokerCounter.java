package com.poker.iago.pokercounter.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.poker.iago.pokercounter.R;
import com.poker.iago.pokercounter.iu.MainNawDraver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Representa un contador de ciegas, que en segundo plano, realiza las acciones necesariar, tanto
 * cuando se actualiza el tiempo restante (casa segundo), como cuando finaliza un nivel de ciegas.
 */
public class PokerCounter {

    private static PokerCounter INSTANCE = new PokerCounter();

    /**
     * Diferentes estados que sirven para indicar el estado actual del Contador
     */
    public static enum State {
        RUNNING,// El contador está corriendo actualmente
        PAUSED, // El contador del tiempo ha sido pausado sin que se complete el nivel de ciegas
        STOPED};// El contador está parado, bien porque no se ha pulsado comenzar, bien porque el
                // nivel de ciegas se ha acabado

    /**
     * Guarda el estado actual del contador de ciegas
     */
    private State state = State.STOPED;

    private Context context;
    /**
	 * Es la distribución de niveles de ciegas que sigue el contador, por defecto IagoDistribution
	 */
	private BlindsDistribution distribution = new IagoDistribution();

	/**
	 * Es el nivel actual en elque se encuentra el contador
	 */
	private int blindsLevel = 0;
    /**
     * This counter count the seconds in a blind level and call to updateCounter and levelFinished
     */
	private CountDownTimer countDownTimer;
    private CircularSeekBar circularSeekBar;
    private TextView clockTextView;
    private NotificationCompat.Builder mBuilder;

    private static Calendar clockCalendar = Calendar.getInstance();
    private SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss");

    static{
        clockCalendar.setTimeInMillis(0);
    }
    /**
     * This class is a Songelton, whith this methor return a PokerCounter that update the rootView
     * progressBar and TextView
     * @return the unique instance of this class
     */
    public static PokerCounter getInstance(Context context, View rootView){
        INSTANCE.setView(context, rootView);
        return INSTANCE;
    }

    /**
     * Imprecindible para poder actualizar el progreso
     * @param rootView
     */
	public void setView(Context context, View rootView) {
        this.context = context;

        //Guardamos las vistas que vamos a utilizar
        clockTextView = (TextView) rootView.findViewById(R.id.digitalClock1);
        circularSeekBar = (CircularSeekBar) rootView.findViewById(R.id.levelProgressBar);

        //Inutilizamos la funcion de click
        circularSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //Generamos la notificación para lanzarla más adelante
        if(mBuilder == null) mBuilder =generateNotification();

        //Si el contador no estaba corriendo, asignamos el primer tiempo
        if (state == State.STOPED)
            clockCalendar.set(Calendar.MINUTE, getBlindsLevel().getMinutes());

        //Metemos en la progressBar y en el tiempo, los datos del clockCalendar
        updateCounter();
	}

	public void startCounter() {
        //Si segundos restantes ya tiene un valor esto quiere decir que ha sido detenido, y que
        // debemos lanzarlo desde donde nos habíamos quedado.
		countDownTimer = new MyCountDownTimer(clockCalendar.getTimeInMillis(), 1000).start();
        state = State.RUNNING;
	}

	/**
	 * Pausará el contador hasta que se llame a startCounter o a nextLevel
	 */
	public void pauseCounter() {
		countDownTimer.cancel();
        state = State.PAUSED;
	}

	/**
	 * Avanza un nivel de ciegas Pararemos el contador para esto, de forma que
	 * para que vuelva a correr es necesaria una llamada a startCounter
	 */
	public void nextLevel() {
		blindsLevel++;
		countDownTimer.cancel();
        state = State.STOPED;

        //Borramos el reloj del anterior nivel, y establecemos el del siguiente
        clockCalendar.setTimeInMillis(0);
        clockCalendar.set(Calendar.MINUTE, getBlindsLevel().getMinutes());

        //TODO Cambiar el boton de continue a paused

        updateCounter();
	}

    /**
     * Restaura el nivel de ciegas a su estado inicial
     */
    public void resetBlindsLevel(){
        countDownTimer.cancel();
        state = State.STOPED;

        //Borramos el reloj del anterior nivel, y establecemos el del siguiente
        clockCalendar.setTimeInMillis(0);
        clockCalendar.set(Calendar.MINUTE, getBlindsLevel().getMinutes());

        updateCounter();
    }

	public BlindsLevel getBlindsLevel() {
		return distribution.getBlindsLevels().get(blindsLevel);
	}

    public void setDistribution(BlindsDistribution distribution) {
        this.distribution = distribution;
    }

    public BlindsDistribution getDistribution() {
		return distribution;
	}

	/**
	 * Este método ha de ser redefinir en las subclases para actualizar relojes,
	 * progressbars ...
	 */
	public void updateCounter(){
        //Calculamos el nivel de progreso en funcion de los segundosRestantes
        int levelSeconds = (getBlindsLevel().getMinutes() * 60);
        int segundosRestantes = (int) clockCalendar.getTimeInMillis()/1000;
        int progress = ((levelSeconds-segundosRestantes)*100)/levelSeconds;

        circularSeekBar.setProgress(progress);

        clockTextView.setText(dataFormat.format(clockCalendar.getTime()));
    };

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

    public State getState(){
        return state;
    }

    /**
     * Es un countDownTimer que en cada click resta un segundo al clockCalendar y actualiza la
     * interfaz. Cuando ha acabado se encarga de lanzar la notificacion de mBuilder y actualizar la
     * interfaz.
     */
    private class MyCountDownTimer extends CountDownTimer {
        private static final int NOTIF_ALERTA_ID = 1;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUntilFinished) {
            //Restamos un segundo al reloj
            clockCalendar.add(Calendar.SECOND, -1);
            updateCounter();
        }

        public void onFinish() {
            //Restamos el último segundo al reloj
            clockCalendar.add(Calendar.SECOND, -1);

            updateCounter();
            //TODO lo que podemos hacer es darle al usuario opción de pasar al siguiente nivel (tecla next)
            // O bien comenzar de nuevo el actual (Boton de start, pero con la etiqueta cambiada a
            // reestart y q llama a la funcion de reestart)

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
        }
    }

}
