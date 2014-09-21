package com.poker.iago.pokercounter.model;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.TableLayout;
import android.widget.TextView;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.poker.iago.pokercounter.R;

public class PokerCounterIU extends PokerCounter {

	private View view;
	private TableLayout blinds_table;
	private Button startPauseButt;
	private Button nextLevelButt;
	private TextView digitalClock;
	private CircularSeekBar seekBar;

	private Handler prBarHandler = new Handler();

    /**
     * Por defecto se asigna una distribución IagoDistribution
     */
	public PokerCounterIU(View pokerCounterView) {
		this(pokerCounterView, new IagoDistribution());
	}

	public PokerCounterIU(View pokerCounterView, BlindsDistribution distribution) {
		super(distribution);
		view = pokerCounterView;
		blinds_table = (TableLayout) view.findViewById(R.id.blinds_table);
		startPauseButt = (Button) view.findViewById(R.id.startPauseButt);
		nextLevelButt = (Button) view.findViewById(R.id.nextLevelButt);
		digitalClock = (TextView) view.findViewById(R.id.digitalClock1);
        seekBar = (CircularSeekBar) view.findViewById(R.id.levelProgressBar);
        //Inutilizamos la funcion de click
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
	}

	@Override
	public void startCounter() {
		/* Start lengthy operation in a background thread */
		super.startCounter();
//		new Thread(new Runnable() {
//			public void run() {
//				while (mProgressStatus < 100) {
//					mProgressStatus = doWork();
//				}
//			}
//		}).start();
//	
	}

	public void nextLevel() {
		super.nextLevel();
		// TODO Resetear contador y progressbar
	}

	@Override
	public void actualizarContador(long millisUntilFinished) {

		long maxMs = getBlindsLevel().getMinutes() * 60000;
		long actualMs = maxMs - getMillisUntilFinished();
		final int mProgressStatus = new Long(((actualMs / maxMs) * 100))
				.intValue();

		System.out.println(mProgressStatus);

		// Update the progress bar
		prBarHandler.post(new Runnable() {
			public void run() {
                seekBar.setProgress(mProgressStatus);
			}
		});

		//TODO revisar
		digitalClock.setText(Integer.toString(new Long(
				(millisUntilFinished / 1000)).intValue()));

	}
}
