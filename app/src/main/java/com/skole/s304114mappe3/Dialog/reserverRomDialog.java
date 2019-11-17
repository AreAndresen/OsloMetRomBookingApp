package com.skole.s304114mappe3.Dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.skole.s304114mappe3.DatoFragment;
import com.skole.s304114mappe3.R;
import com.skole.s304114mappe3.klasser.Rom;

import java.util.ArrayList;

public class reserverRomDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DialogClickListener callback;

    //--------KNAPPER--------
    private Button btnAvbryt, btnReserver;

    //--------TEKST--------
    private TextView visDato, visRomNr;

    //--------SPINNERE--------
    private Spinner spinStart, spinSlutt, spinnerRomNr;

    //--------VERDIER--------
    private String dato, tid;

    private String tidFra, tidTil;

    ArrayList<Rom> alleRom = new ArrayList<Rom>();
    private Rom valgtRom;
    private String valgtRomNr;




    //--------DIALOG KNAPPER TIL SEBESTILLINGSINFOTDIALOG--------
    public interface DialogClickListener{
        void bestillClick();
        void avbrytClick();
    }


    //--------METODE FOR Å OVERFØRE INFO TIL DIALOGFRAGMENTET--------
    public void hentInfo(String valgtRomNr) {
        this.valgtRomNr = valgtRomNr;

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
        dialog.setContentView(R.layout.activity_reserver_rom_dialog);

        //--------KNAPPER--------
        btnAvbryt = (Button) dialog.findViewById(R.id.btnAvbryt);
        //btnLeggTilVenn = (Button) findViewById(R.id.btnLeggTilVenn);
        btnReserver = (Button) dialog.findViewById(R.id.btnReserver);


        //--------TEKST--------
        visDato = (TextView) dialog.findViewById(R.id.visDato);
        visRomNr = (TextView) dialog.findViewById(R.id.visRomNr);
        //visTid = (TextView) findViewById(R.id.visTid);
        //vennerListView = (ListView) findViewById(R.id.list);


        //--------SPINNERE--------
        spinStart = (Spinner) dialog.findViewById(R.id.spinStart);
        spinSlutt = (Spinner) dialog.findViewById(R.id.spinSlutt);




        //--------SETTER OUTPUT--------
        visRomNr.setText(valgtRomNr);;



        //--------KNAPPER--------
        btnReserver = dialog.findViewById(R.id.btnReserver);
        btnAvbryt = dialog.findViewById(R.id.btnAvbryt);


        //--------LISTENERS--------

        //KLIKK PÅ VELG DATO
        visDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OPPRETTER DATOFRAGMENTET FOR SETTING AV DATO
                DialogFragment datoValg = new DatoFragment();
                datoValg.show(getFragmentManager(), "dato valg");
            }
        });

        //KLIKK PÅ BESTILL
        btnReserver.setOnClickListener(new View.OnClickListener() {
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
        });
        dialog.show();
        return dialog;
    }

    //--------INNEBYGD METODE FOR SETTING AV DATO--------
    @Override
    public void onDateSet(DatePicker view, int aar, int mnd, int dag) {

        //MÅ LEGGE INN DENNE ETTERSOM MÅNEDSTALLET VISER 9 FOR OKTOBER OSV.
        mnd++;

        //GENERERER STRING PÅ 22/10/2019 FORMAT
        dato = dag+"."+mnd+"."+aar;
        visDato.setText(dato);
    }

}
