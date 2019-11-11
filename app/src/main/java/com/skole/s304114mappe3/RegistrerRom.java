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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegistrerRom extends AppCompatActivity {

    //--------KNAPPER--------
    private Button btnRegistrer, btnAvbryt;

    //--------TEKST--------
    private EditText beskrivelse, latKoordinat, lenKoordinat, romNr;

    //--------DB HANDLER--------
    //DBhandler db;

    private ImageView logo;


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
        beskrivelse = (EditText)findViewById(R.id.beskrivelse);
        latKoordinat = (EditText)findViewById(R.id.latKoordinat);
        lenKoordinat = (EditText)findViewById(R.id.lenKoordinat);

        //--------DB HANDLER--------
        //db = new DBhandler(this);


        //--------LISTENERS--------
        //KLIKK PÅ LEGG TIL
        btnRegistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FULLFØRER OPPRETTELSE AV NY RESTURANT
                fullforRegistrering();
            }
        });

        //KLIKK PÅ TILBAKE
        btnAvbryt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VIEW OPPDATERES FORTLØPENDE - FORHINDRER STACK
                Intent intent_tilbake = new Intent (RegistrerRom.this, MainActivity.class);
                startActivity(intent_tilbake);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------



    //--------METODE FOR Å LEGGE TIL OPPRETTET RESTURANT--------
    private void fullforRegistrering() {
        String hentRomNr = romNr.getText().toString();
        String hentBeskrivelse = beskrivelse.getText().toString();
        String hentLat = latKoordinat.getText().toString();
        String hentLen = lenKoordinat.getText().toString();


        //INPUTVALIDERING
        if(!hentRomNr.equals("") && !hentBeskrivelse.equals("") && !hentLat.equals("") && !hentLen.equals("") && hentLat.matches(
                "[0-9\\+\\-\\ ]{2,15}+") && hentBeskrivelse.matches("[a-zA-ZæøåÆØÅ\\'\\-\\ \\.]{2,40}+")
                && hentLen.matches("[a-zA-ZæøåÆØÅ0-9\\'\\-\\ \\.]{2,30}+") && hentRomNr.matches("[a-zA-ZæøåÆØÅ0-9\\'\\-\\ \\.]{2,30}+")){


            //GENERERER OG LEGGER TIL NY RESTURANT I DB - TAR INN VERDIER TIL NY RESTURANT
            leggtil(hentBeskrivelse, hentLat, hentLen);

        } else {
            //INFOMELDING UT - FEIL INPUT
            toastMessage("Alle felter må fylles ut og navn og telefonnummer må være på gyldig format");
        }
    }


    //--------METODE FOR OPPRETTE RESTURANT--------
    public void leggtil(String navn, String tlf, String type) {
        //OPPRETTER NYTT RESTURANT-OBJEKT
        // Resturant nyResturant = new Resturant(navn, tlf, type);

        //LEGGER TIL NY RESTURANT I DB
        //db.leggTilResturant(nyResturant);

        //NULLSTILLER INPUT
        beskrivelse.setText("");
        latKoordinat.setText("");
        lenKoordinat.setText("");

        //INFOMELDING UT
        toastMessage("Resturant lagt til!");
        //MELDING TIL LOGG
        Log.d("Legg inn: ", "Resturant lagt til");

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
                Intent intent = new Intent (RegistrerRom.this, MainActivityNy.class);
                startActivity(intent);
                finish();
                break;
            case R.id.registrerRom:
                Intent intent_statistikk = new Intent (RegistrerRom.this, RegistrerRom.class);
                startActivity(intent_statistikk);
                break;
            case R.id.reserverRom:
                Intent intent_preferanser = new Intent (RegistrerRom.this, ReserverRom.class);
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
        protected String doInBackground(String... urls) {String s = "";
        String hele = "";for (String url : urls) {
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
    public void readWebpage(View view) {
        LastSide task = new LastSide();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/LeggTilRom.php"});
    }





    //-------TILBAKE KNAPP - FORHINDRER STACK---------
    @Override
    public void onBackPressed() {
        Intent intent_tilbake = new Intent (RegistrerRom.this, MainActivityNy.class);
        startActivity(intent_tilbake);
        finish();
    }


    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
