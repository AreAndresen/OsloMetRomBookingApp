package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.skole.s304114mappe3.ListView.SeAlleReservasjoner;
import com.skole.s304114mappe3.klasser.Rom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegistrerRom extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    //--------KNAPPER--------
    private Button btnRegistrer, btnAvbryt;

    //--------TEKST--------
    private EditText romNr;
    private TextView latKoordinat, lenKoordinat;

    //--------SPINNERE--------
    private Spinner spinBygg;
    NumberPicker sitteplasser;

    //--------DB HANDLER--------
    //DBhandler db;

    Rom nyttRom;

    //brukes av tallpicker
    int antSitteplasser;
    String bygg;

    private ImageView logo;

    private String lat,len;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer_rom);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        //toolbar.setNavigationIcon(R.drawable.ic_action_name); //android: //src="@drawable/logo"
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorText2));
        setActionBar(toolbar);


        logo = findViewById(R.id.logo2);


        //--------KNAPPER--------
        btnRegistrer = (Button) findViewById(R.id.btnRegistrer);
        btnAvbryt = (Button) findViewById(R.id.btnAvbryt);

        //--------INPUTS--------
        romNr = (EditText)findViewById(R.id.romNr);
        //beskrivelse = (EditText)findViewById(R.id.beskrivelse);


        latKoordinat = (TextView) findViewById(R.id.latKoordinat);
        lenKoordinat = (TextView) findViewById(R.id.lenKoordinat);


        //HENTER FRA MAINNY KOORDINATER INTENT
        lat = getIntent().getStringExtra("LAT");
        len = getIntent().getStringExtra("LEN");


        latKoordinat.setText(lat);
        lenKoordinat.setText(len);


        //--------SPINNERE--------
        spinBygg = (Spinner) findViewById(R.id.spinBygg);

        //--------TALL PICKER--------
        sitteplasser = findViewById(R.id.sitteplasser);

        sitteplasser.setMinValue(1);
        sitteplasser.setMaxValue(10);
        sitteplasser.setOnValueChangedListener(this);



        //--------DB HANDLER--------
        //db = new DBhandler(this);


        //--------LISTENERS--------
        //KLIKK PÅ LEGG TIL
        btnRegistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullforRegistrering();
            }
        });

        //KLIKK PÅ TILBAKE
        btnAvbryt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VIEW OPPDATERES FORTLØPENDE - FORHINDRER STACK
                Intent intent_tilbake = new Intent (RegistrerRom.this, Kart.class);
                startActivity(intent_tilbake);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------


        populerSpinBygg();

    }//-------CREATE SLUTTER---------


    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        //tvShowNumbers.setText("Old Value = " + i + " New Value = " + i1);
        antSitteplasser = i1;
        //INFOMELDING UT
        toastMessage(antSitteplasser+"");

    }

    //FIKS INPUTVALIDERING HER
    //--------METODE FOR Å LEGGE TIL OPPRETTET RESTURANT--------
    private void fullforRegistrering() {

        String hentRomNr = romNr.getText().toString();

        //INPUTVALIDERING
        if(!hentRomNr.equals("") && hentRomNr.matches("[a-zA-Z0-9\\-\\ \\.]{2,20}+")) {

            readWebpage();

            //INFOMELDING UT
            toastMessage("Rom lagt til!");
            //MELDING TIL LOGG
            Log.d("Legg inn: ", "Rom lagt til");


            Intent intent_tilbake = new Intent (RegistrerRom.this, Kart.class);
            startActivity(intent_tilbake);
            finish();

       }
       else {
            ////INFOMELDING UT - FEIL INPUT
            toastMessage("Romnummer må være på riktig format (Tips: PH360 eller FI10.117");
        }
    }


    //--------POPULERER VENNERLISTVIEWET - MULIGHET FOR LESTTING DIREKTE--------
    private void populerSpinBygg() {

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bygg,  R.layout.farge_spinner);
        adapter.setDropDownViewResource(R.layout.spinner_design);

        spinBygg.setAdapter(adapter);

        spinBygg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //bygg = adapterView.getItemAtPosition(i).toString();


                bygg = (String) spinBygg.getItemAtPosition(i);

                Toast.makeText(adapterView.getContext(), bygg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    //--------METODE FOR OPPRETTE RESTURANT--------
    public void leggtil(String romNummer, String bygg, String antallSitteplasser, String lat, String len) {
        //OPPRETTER NYTT RESTURANT-OBJEKT
        Double latD = Double.parseDouble(lat);
        Double lenD = Double.parseDouble(len);
        LatLng koordinater = new LatLng(latD, lenD);

        nyttRom = new Rom(romNummer,bygg, antallSitteplasser, koordinater);

        //LEGGER TIL NY RESTURANT I DB
        //db.leggTilResturant(nyResturant);

        //NULLSTILLER INPUT
        romNr.setText("");
       // beskrivelse.setText("");
        latKoordinat.setText("");
        lenKoordinat.setText("");

        //INFOMELDING UT
        toastMessage("Rom lagt til!");
        //MELDING TIL LOGG
        Log.d("Legg inn: ", "Rom lagt til");

        //VIEW OPPDATERES FORTLØPENDE - FORHINDRER STACK
        Intent intent_tilbake = new Intent (RegistrerRom.this, MainActivity.class);
        startActivity(intent_tilbake);
        finish();
    }


    //En metode for å lage To o l b a rfra minmeny.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.minmeny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.seRom:
                Intent intent = new Intent (RegistrerRom.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.registrerRom:
                Intent intent_statistikk = new Intent (RegistrerRom.this, RegistrerRom.class);
                startActivity(intent_statistikk);
                break;
            case R.id.SeAlleReservasjoner:
                Intent intent_preferanser = new Intent (RegistrerRom.this, SeAlleReservasjoner.class);
                startActivity(intent_preferanser);
                finish();
                break;
            default:
                // If wegothere, theuser'saction wasnot recognized
                // Invokethesuperclassto handle it.
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    //forsøk på å kjøre websiden
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
            //textView.setText(ss);
        }
    }

    public void readWebpage() {

        LastSide task = new LastSide();

        //lager stringer til url url
        String hentRomNr = romNr.getText().toString();
        String hentBygg = bygg;;//beskrivelse.getText().toString();
        String hentAntSitteplasser = antSitteplasser+"";
        String hentLat = latKoordinat.getText().toString();
        String hentLen = lenKoordinat.getText().toString();


        //må fikse  denne strengen så den er uten mellomrom og nordiske tegn og kan brukes i url
        String url = "http://student.cs.hioa.no/~s304114/LeggTilRom.php/?romNr="+hentRomNr+"&bygg="+hentBygg+"&antSitteplasser="+hentAntSitteplasser+"&lat="+hentLat+"&len="+hentLen;
        //FJERNER MELLOMROM I STRENGEN
        String urlUtenMellomrom = url.replaceAll(" ", "");


        task.execute(new String[]{urlUtenMellomrom});
    }





    //-------TILBAKE KNAPP - FORHINDRER STACK---------
    @Override
    public void onBackPressed() {
        Intent intent_tilbake = new Intent (RegistrerRom.this, Kart.class);
        startActivity(intent_tilbake);
        finish();
    }


    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
