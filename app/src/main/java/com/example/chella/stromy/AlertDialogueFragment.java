package com.example.chella.stromy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by chella on 29-05-2015.
 */
public class AlertDialogueFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Opps ! Sorry !")
                .setMessage("There was  Error..Plase try again Later")
                .setPositiveButton("Ok",null);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
