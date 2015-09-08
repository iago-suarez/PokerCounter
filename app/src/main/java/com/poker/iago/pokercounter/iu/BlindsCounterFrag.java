package com.poker.iago.pokercounter.iu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.poker.iago.pokercounter.R;
import com.poker.iago.pokercounter.model.BlindsLevel;
import com.poker.iago.pokercounter.model.PokerCounter;

/**
 * Created by iago on 21/09/14.
 */
public class BlindsCounterFrag extends Fragment {
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
        return fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.blinds_counter_fragment, container, false);

        pokerCounter = PokerCounter.getInstance(getActivity(), rootView);

        //Si el contador está corriendo

        //En caso de que la vista del fragment haya sido recargado reponemos los onjetos que hemos
        // guardado en el bundle
        if (savedInstanceState != null) {
            //TODO Reponer los objetos
        }

        // TODO Si queremos emplear una distribución especial debemos
        // pasarsela aqui al constructor de pokerCounter
        if (pokerCounter.getState() == PokerCounter.State.RUNNING)
            System.out.println("PokerCounter is RUNNING");
        else System.out.println("PokerCounter is STOPED");


        blindsTable = (TableLayout) rootView
                .findViewById(R.id.blinds_table);

        generateTableRow(inflater);

		/* Asignamos lo el estado al boton de Start/Pause */
        startPauseButt = (Button) rootView.findViewById(R.id.startPauseButt);

        if (pokerCounter.getState() == PokerCounter.State.RUNNING) {
            //Contador corriendo
            startPauseButt.setText(getString(R.string.pause_txt));
        }else if(pokerCounter.getState() == PokerCounter.State.PAUSED){
            //Contador pausado
            startPauseButt.setText(getString(R.string.continue_txt));
        }else{
            //Contador parado
            startPauseButt.setText(getString(R.string.start_txt));
        }

        startPauseButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Si el contador está corriendo lo paramos sino lo arrancamos
                if (pokerCounter.getState() == PokerCounter.State.RUNNING) {
                    pokerCounter.pauseCounter();
                    startPauseButt.setText(getString(R.string.continue_txt));
                } else {
                    pokerCounter.startCounter();
                    startPauseButt.setText(getString(R.string.pause_txt));
                }
            }
        });

		/* Asignamos lo necesario al boton de Next Level */
        nextLevelButt = (Button) rootView
                .findViewById(R.id.nextLevelButt);

        nextLevelButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pokerCounter.nextLevel();
            }

        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainNawDraver) activity).updateTitle(
                getArguments().getInt(ARG_SECTION_NUMBER));
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
        for (BlindsLevel bl : pokerCounter.getDistribution()
                .getBlindsLevels())
            addTableRow(bl, blindsTable, inflater);
    }
}
