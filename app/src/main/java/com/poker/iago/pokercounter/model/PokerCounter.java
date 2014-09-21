package com.poker.iago.pokercounter.model;

import android.content.Context;
import android.os.CountDownTimer;

public abstract class PokerCounter {

    private Context context;
    /**
	 * Es la distribución de niveles de ciegas que sigue el contador
	 */
	private BlindsDistribution distribution;

	/**
	 * Es el nivel actual en elque se encuentra el contador
	 */
	private int blindsLevel = 0;
	private int segundosRestantes;
	private CountDownTimer countDownTimer;

	public PokerCounter(BlindsDistribution distribution) {
		this.distribution = distribution;
	}

	public void startCounter() {
		segundosRestantes = distribution.getBlindsLevels().get(blindsLevel).getMinutes() * 60;
		countDownTimer = new CountDownTimer(segundosRestantes * 1000, 1000) {

			public void onTick(long millisUntilFinished) {
				segundosRestantes--;
				actualizarContador(millisUntilFinished);
			}

			public void onFinish() {
                levelFinished(distribution.getBlindsLevels().get(blindsLevel));
			}
		}.start();
	}

	/**
	 * Pausará el contador hasta que se llame a startCounter o a nextLevel
	 */
	public void pauseCounter() {

		countDownTimer.cancel();
	}

	/**
	 * Avanza un nivel de ciegas Pararemos el contador para esto, de forma que
	 * para que vuelva a correr es necesaria una llamada a startCounter
	 */
	public void nextLevel() {
		blindsLevel++;
		countDownTimer.cancel();
		segundosRestantes = distribution.getBlindsLevels().get(blindsLevel)
				.getMinutes() * 60;
	}

	/**
	 * Impone el nivel de ciegas level
	 * 
	 * @param blindsLevel
	 */
	public void setBlindsLevel(int blindsLevel) {
		this.blindsLevel = blindsLevel;
	}

	public BlindsLevel getBlindsLevel() {
		return distribution.getBlindsLevels().get(blindsLevel);
	}
	
	public BlindsDistribution getDistribution() {
		return distribution;
	}

	protected long getMillisUntilFinished (){
		return segundosRestantes*1000;
	}
	/**
	 * Este método ha de ser redefinir en las subclases para actualizar relojes,
	 * progressbars ...
	 */
	protected abstract void actualizarContador(long millisUntilFinished);

    protected abstract void levelFinished(BlindsLevel finishedLevel);

}
