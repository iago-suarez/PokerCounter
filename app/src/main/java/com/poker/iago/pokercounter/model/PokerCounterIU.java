package com.poker.iago.pokercounter.model;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import com.poker.iago.pokercounter.R;

public class PokerCounterIU extends PokerCounter {

	private View view;
	private TableLayout blinds_table;
	private Button startPauseButt;
	private Button nextLevelButt;
	private DigitalClock digitalClock;
	private ProgressBar progressBar;

	private Handler prBarHandler = new Handler();

	public PokerCounterIU(View pokerCounterView) {
		super();
		view = pokerCounterView;
		blinds_table = (TableLayout) view.findViewById(R.id.blinds_table);
		startPauseButt = (Button) view.findViewById(R.id.startPauseButt);
		nextLevelButt = (Button) view.findViewById(R.id.nextLevelButt);
		digitalClock = (DigitalClock) view.findViewById(R.id.digitalClock1);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
	}

	public PokerCounterIU(View pokerCounterView, BlindsDistribution distribution) {
		super(distribution);
		view = pokerCounterView;
		blinds_table = (TableLayout) view.findViewById(R.id.blinds_table);
		startPauseButt = (Button) view.findViewById(R.id.startPauseButt);
		nextLevelButt = (Button) view.findViewById(R.id.nextLevelButt);
		digitalClock = (DigitalClock) view.findViewById(R.id.digitalClock1);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
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
				progressBar.setProgress(mProgressStatus);
			}
		});

		//TODO revisar
		digitalClock.setText(Integer.toString(new Long(
				(millisUntilFinished / 1000)).intValue()));

	}
}
