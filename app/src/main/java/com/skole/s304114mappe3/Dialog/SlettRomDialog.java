package com.skole.s304114mappe3.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class SlettRomDialog extends DialogFragment {
    private DialogClickListener callback;

    public interface DialogClickListener{
        void jaClickRom();
        void neiClickRom();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            callback = (DialogClickListener)getActivity();
        }
        catch(ClassCastException e) {
            throw new ClassCastException("Feil ved kalling av interface!");
        }
    }

    //--------DIALOGBOKS FOR SLETTING AV ROM--------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle("Slett rom").setMessage("Er du sikker på at du vil slette valgt rom?").
                setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton){
                        callback.jaClickRom();
                    }
                })
                .setNegativeButton("Nei",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton){
                        callback.neiClickRom();
                    }
                })
                .create();
    }
}