package com.poker.iago.pokercounter.iu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.poker.iago.pokercounter.R;
import com.poker.iago.pokercounter.model.BlindsDistribution;
import com.poker.iago.pokercounter.model.BlindsLevel;
import com.poker.iago.pokercounter.model.IagoDistribution;
import com.poker.iago.pokercounter.model.PokerCounter;

/**
 * Created by iago on 21/09/14.
 */
public class TimerFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private PokerCounterIU pokerCounterIU;

    private View rootView;
    private Button startPauseButt;
    private Button nextLevelButt;
    private TableLayout blindsTable;

    public TimerFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TimerFragment newInstance(int sectionNumber) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_naw_draver, container, false);


        // TODO Si queremos emplear una distribución especial debemos
        // pasarsela aqui al constructor de pokerCounterIU
        pokerCounterIU = new PokerCounterIU();
        blindsTable = (TableLayout) rootView
                .findViewById(R.id.blinds_table);


        generateTableRow(inflater);

		/* Asignamos lo necesario al boton de Start/Pause */

        startPauseButt = (Button) rootView.findViewById(R.id.startPauseButt);
        startPauseButt.setText(getString(R.string.start_txt));
        startPauseButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Si el contador está parado lo arrancamos sino lo paramos
                if (startPauseButt.getText() == getString(R.string.start_txt) ||
                        startPauseButt.getText() == getString(R.string.continue_txt)) {
                    pokerCounterIU.startCounter();
                    startPauseButt.setText(getString(R.string.pause_txt));
                } else {
                    pokerCounterIU.pauseCounter();
                    startPauseButt.setText(getString(R.string.continue_txt));
                }
            }
        });

		/* Asignamos lo necesario al boton de Next Level */
        nextLevelButt = (Button) rootView
                .findViewById(R.id.nextLevelButt);

        nextLevelButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pokerCounterIU.nextLevel();
            }

        });

			/* TODO Iniciar progressbar */

			/* TODO Iniciar DigitalClock */


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainNawDraver) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Recorremos los nieveles de ciegas añadiendo la TableRow correspondiente a cada uno
     */
    private void generateTableRow(LayoutInflater inflater){
        for (BlindsLevel bl : pokerCounterIU.getDistribution()
                .getBlindsLevels())
            addTableRow(bl, blindsTable, inflater);
    }
    /**
     * Dado un nivel de ciegas, se añade a la tabla parent la vista
     * correspondiente al nivel de ciegas bl que obtenemos de blinds_table_item
     *
     * @param bl
     * @param parent
     * @param inflater
     * @return
     */
    private static void addTableRow(BlindsLevel bl, TableLayout parent,
                                    LayoutInflater inflater) {

        View row = inflater.inflate(R.layout.blinds_table_item, parent, false);
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Manejar el click en la fila de la tabla
                System.out.println("Click Row");
            }
        });

        TextView bb = (TextView) row.findViewById(R.id.bb_table_row);
        bb.setText(Integer.toString(bl.getBb()));

        TextView sb = (TextView) row.findViewById(R.id.sb_table_row);
        sb.setText(Integer.toString(bl.getSb()));

        TextView time = (TextView) row.findViewById(R.id.time_table_row);
        time.setText(Integer.toString(bl.getMinutes()));

        parent.addView(row);
    }


    class PokerCounterIU extends PokerCounter {

        private TextView clockTextView;
        private CircularSeekBar seekBar;

        private Handler prBarHandler = new Handler();

        /**
         * Por defecto se asigna una distribución IagoDistribution
         */
        public PokerCounterIU() {
            this(new IagoDistribution());
        }

        public PokerCounterIU( BlindsDistribution distribution) {
            super(distribution);

            clockTextView = (TextView) rootView.findViewById(R.id.digitalClock1);
            seekBar = (CircularSeekBar) rootView.findViewById(R.id.levelProgressBar);
            //Inutilizamos la funcion de click
            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
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

            Log.i(this.getClass().toString(), Integer.toString(mProgressStatus));

            // Update the progress bar
            prBarHandler.post(new Runnable() {
                public void run() {
                    seekBar.setProgress(mProgressStatus);
                }
            });

            clockTextView.setText(Integer.toString(new Long(
                    (millisUntilFinished / 1000)).intValue()));

        }

        @Override
        protected void levelFinished(BlindsLevel finishedLevel) {
            //TODO
        }
    }
}
