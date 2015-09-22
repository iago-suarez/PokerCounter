package com.poker.iago.pokercounter.iu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.poker.iago.pokercounter.R;
import com.poker.iago.pokercounter.model.BlindsLevel;
import com.poker.iago.pokercounter.model.PokerCounter;
import com.poker.iago.pokercounter.model.PokerCounterListener;

import java.text.SimpleDateFormat;


public class BlindsCounterFrag extends Fragment implements PokerCounterListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private PokerCounter pokerCounter;

    private View rootView;
    private Button startPauseButt;
    private Button nextLevelButt;
    private TableLayout blindsTable;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss");

    private CircularSeekBar circularSeekBar;
    private TextView clockTextView;

    public BlindsCounterFrag() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BlindsCounterFrag newInstance(int sectionNumber) {
        BlindsCounterFrag fragment = new BlindsCounterFrag();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        System.out.println("*************** Nuevo Fragment ************+");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.blinds_counter_fragment, container, false);
        System.out.println("*************** Nuevo Fragment Creado ************+");
        pokerCounter = PokerCounter.getInstance(getActivity().getApplicationContext());

        if(savedInstanceState == null){
            pokerCounter.addListener(this);
        }

        //En caso de que la vista del fragment haya sido recargado reponemos los onjetos que hemos
        // guardado en el bundle
        else{
            //TODO Reponer los objetos
        }

        blindsTable = (TableLayout) rootView.findViewById(R.id.blinds_table);

        generateTableRow(inflater);

        startPauseButt = (Button) rootView.findViewById(R.id.startPauseButt);
        startPauseButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // En funcion de lo que ponga la etiqueta hacemos una cosa u otra
                if (startPauseButt.getText().equals(getString(R.string.pause_txt))) {
                    pokerCounter.pauseCounter();
                    startPauseButt.setText(getString(R.string.continue_txt));
                } else if (startPauseButt.getText().equals(getString(R.string.start_txt))) {
                    pokerCounter.startCounter();
                    startPauseButt.setText(getString(R.string.pause_txt));
                } else if (startPauseButt.getText().equals(getString(R.string.restart_txt))){
                    pokerCounter.resetBlindsLevel();
                    pokerCounter.startCounter();
                    startPauseButt.setText(getString(R.string.pause_txt));
                } else if (startPauseButt.getText().equals(getString(R.string.continue_txt))) {
                    pokerCounter.startCounter();
                    startPauseButt.setText(getString(R.string.pause_txt));
                }
            }
        });

		/* Asignamos lo necesario al boton de Next Level */
        nextLevelButt = (Button) rootView.findViewById(R.id.nextLevelButt);
        nextLevelButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pokerCounter.nextLevel();
            }
        });

        //Guardamos las vistas que luego vamos a utilizar
        clockTextView = (TextView) rootView.findViewById(R.id.digitalClock1);
        circularSeekBar = (CircularSeekBar) rootView.findViewById(R.id.levelProgressBar);

        //Inutilizamos la funcion de click
        circularSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        updateCounter();
        return rootView;
    }

    /**
     * Eliminamos el fragment para que ya no reciva actualizaciones de progreso
     */
    @Override
    public void onPause() {
        super.onPause();
        pokerCounter.removeListener(this);
        System.out.println("*************** Nuevo Fragment Pausado ************+");

    }

    public void updateCounter(){
        //Calculamos el nivel de progreso en funcion de los segundosRestantes
        int levelSeconds = (pokerCounter.getBlindsLevel().getMinutes() * 60);
        int segundosRestantes = (int) pokerCounter.getClockCalendar().getTimeInMillis()/1000;
        int progress = ((levelSeconds-segundosRestantes)*100)/levelSeconds;

        circularSeekBar.setProgress(progress);


        clockTextView.setText(dataFormat.format(pokerCounter.getClockCalendar().getTime()));
        if(!pokerCounter.getState().isStoppedEnd() && (clockTextView.getAnimation() != null)){
            clockTextView.getAnimation().cancel();
        }
        //TODO Actualizamos la tabla

        //TODO Actualizamos las entradas de texto

        /* Asignamos lo el estado al boton de Start/Pause */
        startPauseButt = (Button) rootView.findViewById(R.id.startPauseButt);

        if (pokerCounter.getState().isRunnig()) {
            //Contador corriendo
            startPauseButt.setText(getString(R.string.pause_txt));
        }else if(pokerCounter.getState().isPaused()){
            //Contador pausado
            startPauseButt.setText(getString(R.string.continue_txt));
        }else if(pokerCounter.getState().isStoppedStart()){
            //Contador parado
            startPauseButt.setText(getString(R.string.start_txt));
        }else{
            startPauseButt.setText(getString(R.string.restart_txt));
        }

    }

    @Override
    public void levelFinish() {

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the time of the blink with this parameter
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        clockTextView.startAnimation(anim);

        startPauseButt.setText(getString(R.string.restart_txt));
    }

    @Override
    public void levelChange() {
    }

    /**
     * Dado un nivel de ciegas, se añade a la tabla parent la vista
     * correspondiente al nivel de ciegas bl que obtenemos de blinds_table_item
     *
     * @param bl
     * @param parent
     * @param inflater
     */
    private void addTableRow(BlindsLevel bl, TableLayout parent,
                                    LayoutInflater inflater, Boolean greyStyle) {

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

        if(greyStyle){
            row.setBackgroundColor(getResources().getColor(R.color.superdarkgreen));
        }
        parent.addView(row);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainNawDraver) context).updateTitle(
                getArguments().getInt(ARG_SECTION_NUMBER));
        System.out.println("*************** Nuevo Fragment Attacheado ************+");

    }

    /**
     * Save the actual center point of the map to be restored when the Activity be restored.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO Guardar el estado de los objetos importantes
    }

    /**
     * Recorremos los nieveles de ciegas añadiendo la TableRow correspondiente a cada uno
     */
    private void generateTableRow(LayoutInflater inflater) {
        boolean isDarkRow = false;
        for (BlindsLevel bl : pokerCounter.getDistribution()
                .getBlindsLevels()) {
            addTableRow(bl, blindsTable, inflater, isDarkRow);
            isDarkRow = !isDarkRow;
        }
    }
}
