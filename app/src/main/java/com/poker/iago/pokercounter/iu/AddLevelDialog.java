package com.poker.iago.pokercounter.iu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.poker.iago.pokercounter.R;
import com.poker.iago.pokercounter.model.BlindsDistribution;
import com.poker.iago.pokercounter.model.BlindsLevel;
import com.poker.iago.pokercounter.model.PokerCounter;

public class AddLevelDialog extends DialogFragment {


    private EditText sbEditText;
    private EditText bbEditText;
    private EditText minutesEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.add_level))
                .setPositiveButton(R.string.finish_lbl,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do nothing here because we override this button later to change the close behaviour.
                                //However, we still need this because on older versions of Android unless we
                                //pass a handler the button doesn't get instantiated
                            }
                        }
                )
                .setNegativeButton(R.string.cancel_lbl,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        LayoutInflater i = getActivity().getLayoutInflater();

        View v = i.inflate(R.layout.add_level_dialog, null);
        b.setView(v);

        sbEditText = (EditText) v.findViewById(R.id.sb_edit_text);
        bbEditText = (EditText) v.findViewById(R.id.bb_edit_text);
        minutesEditText = (EditText) v.findViewById(R.id.minutes_edit_text);

        return b.create();
    }

    //Here we handle the onClick event for the finish button
    @Override
    public void onStart() {
        //super.onStart() is where dialog.show() is actually called on the
        // underlying dialog, so we have to do it after this point
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer sb = null;
                    Integer bb = null;
                    Integer minutes = null;
                    try {
                        sb = Integer.parseInt(sbEditText.getText().toString());
                    } catch (NumberFormatException ex) {
                        sbEditText.setError(getString(R.string.input_empty));
                    }
                    try {
                        bb = Integer.parseInt(bbEditText.getText().toString());
                    } catch (NumberFormatException ex) {
                        bbEditText.setError(getString(R.string.input_empty));
                    }
                    try {
                        minutes = Integer.parseInt(minutesEditText.getText().toString());
                    } catch (NumberFormatException ex) {
                        minutesEditText.setError(getString(R.string.input_empty));
                    }
                    //If there are no errors we add the level and finish
                    if ((sb != null) && (bb != null) && (minutes != null)) {
                        addLevel(sb, bb, minutes);
                        dismiss();
                    }
                }
            });
        }
    }

    private void addLevel(int sb, int bb, int minutes) {
        BlindsDistribution distribution = PokerCounter.getInstance(getContext()).getDistribution();
        distribution.addLevel(new BlindsLevel(sb, bb, minutes));
    }

}
