package com.skole.s304114mappe3.Dialog;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.skole.s304114mappe3.ListView.SeAlleReservasjoner;
import com.skole.s304114mappe3.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SeReservasjonsInfoFragment extends AppCompatActivity implements SlettReservasjonDialog.DialogClickListener{

    //--------DIALOG KNAPPER TIL AVBESTILLDIALOG--------
    //KLIKK PÅ JA
    @Override
    public void jaClick() {
        fullforAvbestilling();
    }

    //KLIKK PÅ NEI
    @Override
    public void neiClick() {
        return;
    }


    //--------VERDIER--------
    int ID;
    String dato, tidFra, tidTil, romNr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////--------HENTER ID TIL RESERVASJONEN SOM SKAL VISES FRA MINNE--------
        ID = getSharedPreferences("APP_INFO",MODE_PRIVATE).getInt("VISNINGSID", 0);
        dato = getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("VISNINGSDATO", "");
        tidFra = getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("VISNINGSTIDFRA", "");
        tidTil = getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("VISNINGSTIDTIL", "");
        romNr = getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("VISNINGSROMNR", "");


        //--------KJØRER SERESERVASJONINFO FRAGMENTET--------
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SeReservasjonsInfo()).commit();

    }
    //-------CREATE SLUTTER---------


    //-----------------------METODER TIL HOVEDKLASSE-----------------------------
    //-------HENTER RESERVASJONSINFO FRA HOVEDKLASSEN - BENYTTES I FRAGMENTET---------
    public String getDato() {
        return dato;
    }
    public String getTidFra() {
        return tidFra;
    }
    public String getTidTil() {
        return tidTil;
    }
    public String getRomNr() {
        return romNr;
    }



    //-------FULLFØRER AVBESTILLING - BENYTTES INNAD I jaClick()---------
    public void fullforAvbestilling() {

        //SLETTER RESERVASJONEN GJENNOM WEBSERVICE
        webSlettReservasjon();

        //MELDING TIL LOGG
        Log.d("Legg inn: ", "Rom lagt til");

        //FORTLØPENDE TILBAKE TIL OVERSIKTEN OVER ALLE RESERVAJONER - NÅ UTEN SLETTET RESERVASJON
        Intent intent = new Intent (SeReservasjonsInfoFragment.this, SeAlleReservasjoner.class);
        startActivity(intent);
        finish();
    }
    //-----------------------METODER TIL HOVEDKLASSE-----------------------------



    //--------HÅNDTERER KJØRING AV WEBSERVICE AV URLs--------
    private class LastSide extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String s = "";
            String hele = "";
            for (String url : urls) {
                try{
                    URL minurl= new URL(urls[0]);
                    HttpURLConnection con = (HttpURLConnection) minurl.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while((s = in.readLine()) != null) {
                        hele = hele + s;
                    }
                    in.close();
                    con.disconnect();
                    return hele;
                }
                catch(Exception e) {
                    return"Noe gikk feil";
                }
            }
            return hele;
        }

        @Override
        protected void onPostExecute(String ss) {
        }
    }


    //--------UTFORMING AV URL OG KJØRING AV DENNE - SLETT RESERVASJON--------
    public void webSlettReservasjon() {

        LastSide task = new LastSide();
        String url = "http://student.cs.hioa.no/~s304114/SlettReservasjon.php/?id="+ID;

        //FJERNER MELLOMROM I STRENGEN
        String urlUtenMellomrom = url.replaceAll(" ", "");

        task.execute(new String[]{urlUtenMellomrom});
    }


    //--------FRAGMENT STARTER--------
    public static class SeReservasjonsInfo extends Fragment {

        //--------TEKST--------
        TextView resDato,resTidFra, resTidTil, resRomNr;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_se_reservasjons_info_fragment, container, false);


            //BENYTTER METODENE FRA HOVEDKLASSEN TIL Å HENTE INFO TIL RESERVASJONEN
            final String dato = ((SeReservasjonsInfoFragment)getActivity()).getDato();
            final String tidFra = ((SeReservasjonsInfoFragment)getActivity()).getTidFra();
            final String tidTil = ((SeReservasjonsInfoFragment)getActivity()).getTidTil();
            final String romNr = ((SeReservasjonsInfoFragment)getActivity()).getRomNr();

            //--------TEKST--------
            resDato = v.findViewById(R.id.resDato);
            resTidFra = v.findViewById(R.id.resTidFra);
            resTidTil = v.findViewById(R.id.resTidTil);
            resRomNr = v.findViewById(R.id.resRomNr);

            //--------SETTER TEKST FRA HENTET INFO--------
            resDato.setText(dato);
            resTidFra.setText(tidFra);
            resTidTil.setText(tidTil);
            resRomNr.setText(romNr);


            //--------KNAPPER--------
            Button btnOk = v.findViewById(R.id.btnOk);
            Button btnAvbestill = v.findViewById(R.id.btnAvbestill);


            //--------LISTENERS--------
            //KLIKK PÅ OK
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BENYTTER SAMME METODE SOM TILBAKE KNAPP - VISNING AV RESERVASJONER OPPDATERES DERMED FORTLØPENDE
                    ((SeReservasjonsInfoFragment)getActivity()).onBackPressed();
                }
            });

            //KLIKK PÅ AVBESTILL
            btnAvbestill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //VISER DIALOG VED AVBESTILLING - SPØR OM BRUKER ER SIKKER PÅ SLETTING/AVBESTILLING
                    visAvbestillDialog();
                }
            });
            return v;
        }


        //-------VISER DIALOG VED TRYKK PÅ KNAPP: AVBESTILL-----
        private void visAvbestillDialog() {
            DialogFragment dialog = new SlettReservasjonDialog();
            dialog.show(getFragmentManager(), "Avslutt");
        }

    } //SLUTT FRAGMENT


    //-------TILBAKEKNAPP - OPPDATERER INTENT FOR Å OPPDATERE EVENTUELL SLETTING/AVBESTILLING---------
    @Override
    public void onBackPressed() {
        Intent intent_tilbake = new Intent (SeReservasjonsInfoFragment.this, SeAlleReservasjoner.class);
        startActivity(intent_tilbake);
        finish();
    }
}