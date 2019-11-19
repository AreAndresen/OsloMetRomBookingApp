package com.skole.s304114mappe3.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.skole.s304114mappe3.R;


public class SeReservasjonerDialog extends DialogFragment {

    private DialogClickListener callback;

    TextView reservasjonerTekst;
    private String reservasjoner;

    public interface DialogClickListener{
        void reserverClick();
        void avbrytClick();
    }

    //--------METODE FOR Å OVERFØRE INFO TIL DIALOGFRAGMENTET--------
    public void hentReservasjoner(String reservasjoner) {
        this.reservasjoner = reservasjoner;
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


    //EGENDEFINERT ALERT BOKS MED RESERVASJONER
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.activity_se_reservasjoner_dialog);


        //--------TEKST--------
        reservasjonerTekst = dialog.findViewById(R.id.reservasjonerTekst);

        Button btnTllReservering = dialog.findViewById(R.id.btnTilReservering);
        Button btnTllbake = dialog.findViewById(R.id.btnTilbake);

        reservasjonerTekst.setText(reservasjoner);

        //--------LISTENERS--------
        //KLIKK PÅ RESERVER
        btnTllReservering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.reserverClick();
                dismiss();
            }
        });

        //KLIKK PÅ TILBAKE
        btnTllbake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.avbrytClick();
                dismiss();
            }
        });
        dialog.show();
        return dialog;
    }
}