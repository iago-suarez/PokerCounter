package com.poker.iago.pokercounter.model;

import android.content.Context;
import android.os.CountDownTimer;

import com.poker.iago.pokercounter.exception.InvalidStateError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Representa un contador de ciegas, que en segundo plano, realiza las acciones necesariar, tanto
 * cuando se actualiza el tiempo restante (casa segundo), como cuando finaliza un nivel de ciegas.
 */
public class PokerCounter {

    private static PokerCounter INSTANCE = new PokerCounter();

    /**
     * Diferentes estados que sirven para indicar el estado actual del Contador
     */
    public enum State {

        RUNNING,// El contador está corriendo actualmente
        PAUSED, // El contador del tiempo ha sido pausado sin que se complete el nivel de ciegas
        STOPPED_START,// El contador está parado porque no se ha pulsado comenzar
        STOPPED_END;// El contador está parado porque el nivel de ciegas se ha acabado
        public boolean isRunnig(){
            return this == State.RUNNING;
        }
        public boolean isPaused(){
            return this == State.PAUSED;
        }
        public boolean isStoppedStart(){
            return this == State.STOPPED_START;
        }
        public boolean isStoppedEnd(){
            return this == State.STOPPED_END;
        }
    };
    /**
     * Guarda el estado actual del contador de ciegas
     */
    private State state = State.STOPPED_START;

    private List<PokerCounterListener> listeners = new ArrayList<PokerCounterListener>();

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
	private MyCountDownTimer countDownTimer;

    /**
     * Contiene los milisegundos restantes para que finalize en nivel de ciegas
     */
    private static Calendar clockCalendar = Calendar.getInstance();

    private Context context;

    static{
        //Se ejecuta cuando iniciamos el programa
        clockCalendar.setTimeInMillis(0);
    }
    /**
     * This class is a Songelton, whith this methor return a PokerCounter that update the rootView
     * progressBar and TextView
     * @return the unique instance of this class
     */
    public static PokerCounter getInstance(Context context){
        //Se ejecuta al solicitar una in
        INSTANCE.init(context);
        return INSTANCE;
    }

	public void init(Context context) {
        this.context = context;

        //Si el contador no estaba corriendo, asignamos el primer tiempo
        if (state.isStoppedStart())
            clockCalendar.set(Calendar.MINUTE, getBlindsLevel().getMinutes());

        //Metemos en los listeners
        for (PokerCounterListener listener: listeners) {
            listener.updateCounter();
        }
    }

    /**
     * @throws InvalidStateError si el sistema ya se ha iniciado,
     */
	public void startCounter() {
        if (state.isRunnig()){
            throw new InvalidStateError("Counter is alredy started.");
        }
        //Si segundos restantes ya tiene un valor esto quiere decir que ha sido detenido, y que
        // debemos lanzarlo desde donde nos habíamos quedado.
        addListener(new NotificationListener(context));
        countDownTimer = (MyCountDownTimer)
                new MyCountDownTimer(clockCalendar.getTimeInMillis(), 1000).start();
        state = State.RUNNING;
	}

    /**
     * Pausará el contador hasta que se llame a startCounter o a nextLevel
     *
     * @throws InvalidStateError si el sistema ya esta parado o no se ha iniciado
     */
	public void pauseCounter() {
        if(!state.isRunnig()){
            throw new InvalidStateError("Counter is alredy paused.");
        }
		countDownTimer.cancel();
        state = State.PAUSED;
	}

	/**
	 * Avanza un nivel de ciegas Pararemos el contador para esto, de forma que
	 * para que vuelva a correr es necesaria una llamada a startCounter
	 */
	public void nextLevel() throws NoMoreLevelsException {

        if (blindsLevel+1 >= distribution.getBlindsLevels().size()){
            throw new NoMoreLevelsException();
        }

        blindsLevel++;
		if(countDownTimer != null){
            countDownTimer.cancel();
        }
        state = State.STOPPED_START;

        //Borramos el reloj del anterior nivel, y establecemos el del siguiente
        clockCalendar.setTimeInMillis(0);
        clockCalendar.set(Calendar.MINUTE, getBlindsLevel().getMinutes());

        for (PokerCounterListener listener: listeners) {
            listener.levelChange();
        }

        for (PokerCounterListener listener: listeners) {
            listener.updateCounter();
        }
    }

    /**
     * Restaura el nivel de ciegas a su estado inicial
     */
    public void resetBlindsLevel(){
        countDownTimer.cancel();
        state = State.STOPPED_START;

        //Borramos el reloj del anterior nivel, y establecemos el del siguiente
        clockCalendar.setTimeInMillis(0);
        clockCalendar.set(Calendar.MINUTE, getBlindsLevel().getMinutes());

        for (PokerCounterListener listener: listeners) {
            listener.updateCounter();
        }
    }

	public BlindsLevel getBlindsLevel(){
        return distribution.getBlindsLevels().get(blindsLevel);
    }

    public void setDistribution(BlindsDistribution distribution) {
        this.distribution = distribution;
    }

    public BlindsDistribution getDistribution() {
		return distribution;
	}

    public State getState(){
        return state;
    }

    public Calendar getClockCalendar() {
        return clockCalendar;
    }

    public void addListener(PokerCounterListener listener){
        this.listeners.add(listener);
    }

    public void removeListener(PokerCounterListener listener){
        this.listeners.remove(listener);
    }

    public List<PokerCounterListener> getListeners() {
        return listeners;
    }

    /**
     * Es un countDownTimer que en cada click resta un segundo al clockCalendar y actualiza la
     * interfaz. Cuando ha acabado se encarga de lanzar la notificacion de mBuilder y actualizar la
     * interfaz.
     */
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUntilFinished) {
            //Restamos un segundo al reloj
            clockCalendar.add(Calendar.SECOND, -1);
            for (PokerCounterListener listener: listeners) {
                listener.updateCounter();
            }

        }

        public void onFinish() {
            //Restamos el último segundo al reloj
            clockCalendar.add(Calendar.SECOND, -1);
            for (PokerCounterListener listener: listeners) {
                listener.updateCounter();
            }
            for (Iterator<PokerCounterListener> iterator = listeners.iterator(); iterator.hasNext();) {
                PokerCounterListener pcListener = iterator.next();
                pcListener.levelFinish();
                if (pcListener instanceof NotificationListener ) {
                    // Remove the current element from the iterator and the list.
                    iterator.remove();
                }
            }
            state = State.STOPPED_END;

            //TODO lo que podemos hacer es darle al usuario opción de pasar al siguiente nivel (tecla next)
            // O bien comenzar de nuevo el actual (Boton de start, pero con la etiqueta cambiada a
            // reestart y q llama a la funcion de reestart)
        }
    }
}
