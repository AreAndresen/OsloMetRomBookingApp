package com.skole.s304114mappe3.Dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skole.s304114mappe3.MainActivity;
import com.skole.s304114mappe3.R;
import com.skole.s304114mappe3.RegistrerRom;

import java.util.ArrayList;


public class RegistrerRomDialog extends DialogFragment {

    private DialogClickListener callback;

    //--------KNAPPER--------
    //--------KNAPPER--------
    private Button btnLeggTil, btnTilbake;

    //--------TEKST--------
    private EditText EnavnResturant;
    private EditText EtlfResturant;
    private EditText EtypeResturant;


    //--------DIALOG KNAPPER TIL SEBESTILLINGSINFOTDIALOG--------
    public interface DialogClickListener{
        void bestillClick();
        void avbrytClick();
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


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.activity_registrer_rom_dialog);

        //--------KNAPPER--------
        btnLeggTil = (Button) dialog.findViewById(R.id.btnAvbryt);
        btnTilbake = (Button) dialog.findViewById(R.id.btnTilbake);

        //--------INPUTS--------
        EnavnResturant = (EditText)dialog.findViewById(R.id.navnResturant);
        EtlfResturant = (EditText)dialog.findViewById(R.id.tlfResturant);
        EtypeResturant = (EditText)dialog.findViewById(R.id.typeResturant);

        //--------DB HANDLER--------
        //db = new DBhandler(this);


        //--------LISTENERS--------
        //KLIKK PÅ LEGG TIL
        btnLeggTil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //FULLFØRER OPPRETTELSE AV NY RESTURANT
                    //fullforLeggTilResturant();
                }
            });

           /* //KLIKK PÅ TILBAKE
            btnTilbake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //VIEW OPPDATERES FORTLØPENDE - FORHINDRER STACK
                    Intent intent_tilbake = new Intent (RegistrerRomDialog.this, MainActivity.class);
                    startActivity(intent_tilbake);
                    finish();
                }
            });
            //--------SLUTT LISTENERS--------


            //--------HENTER OG VISER ALLE DELTAKENDE VENNER--------
            String vennNavn = "";
            for (Venn i : venner) {
                vennNavn += i.getNavn()+"\n";
            }
            bVenner.setText(vennNavn);


            //--------KNAPPER--------
            btnBestill = dialog.findViewById(R.id.btnOk);
            btnAvbryt = dialog.findViewById(R.id.btnAvbryt);


            //--------LISTENERS--------
            //KLIKK PÅ BESTILL
            btnBestill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.bestillClick();
                    dismiss();
                }
            });

            //KLIKK PÅ AVBRYT
            btnAvbryt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.avbrytClick();
                    dismiss();
                }
            });*/
            dialog.show();
            return dialog;
        }
    }
