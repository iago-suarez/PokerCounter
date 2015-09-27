package com.poker.iago.pokercounter.iu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.poker.iago.pokercounter.R;

public class NoMoreLevelsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.oops_lbl)
                .setMessage(R.string.no_more_levels_msg)
                .setPositiveButton(R.string.add_level, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AddLevelDialog().show(getFragmentManager(), "NoMoreLevels");
                    }
                })
                .setNegativeButton(R.string.return_lbl, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
