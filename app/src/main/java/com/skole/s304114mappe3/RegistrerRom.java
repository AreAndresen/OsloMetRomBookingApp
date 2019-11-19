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
import com.skole.s304114mappe3.ListView.SeAlleReservasjoner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegistrerRom extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    //--------KNAPPER--------
    private Button btnRegistrer, btnAvbryt;
    private ImageView logoItoolBar;

    //--------TEKST--------
    private EditText romNr;
    private TextView latKoordinat, lenKoordinat;

    //--------SPINNERE--------
    private Spinner spinBygg;

    //--------TALLPICKER--------
    NumberPicker sitteplasser;

    //--------VERDIER--------
    private int antSitteplasser;
    private String lat,len, bygg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer_rom);

        //--------TOOLBAR--------
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        setActionBar(toolbar);


        //--------LOGO - FUNGERER SOM KNAPP--------
        logoItoolBar = findViewById(R.id.logo2);


        //--------KNAPPER--------
        btnRegistrer = (Button) findViewById(R.id.btnRegistrer);
        btnAvbryt = (Button) findViewById(R.id.btnAvbryt);


        //--------INPUTS--------
        romNr = (EditText)findViewById(R.id.romNr);
        latKoordinat = (TextView) findViewById(R.id.latKoordinat);
        lenKoordinat = (TextView) findViewById(R.id.lenKoordinat);


        //--------HENTER KOORDINATENE HVOR DET BLE TRYKKET PÅ KARTET--------
        lat = getIntent().getStringExtra("LAT");
        len = getIntent().getStringExtra("LEN");


        //--------SETTER INN KOORDINATENE FERDIG UTFYLT--------
        latKoordinat.setText(lat);
        lenKoordinat.setText(len);


        //--------SPINNERE--------
        spinBygg = (Spinner) findViewById(R.id.spinBygg);


        //--------POPULERER SPINNER FOR BYGG--------
        populerSpinBygg();


        //--------TALL PICKER--------
        sitteplasser = findViewById(R.id.sitteplasser);


        //SETTER MIN OG MAKS VERDI PÅ ANTALL PLASSER
        sitteplasser.setMinValue(1);
        sitteplasser.setMaxValue(15);
        sitteplasser.setOnValueChangedListener(this);


        //--------LISTENERS--------
        //KLIKK LOGO I TOOLBAR
        logoItoolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentS = new Intent (RegistrerRom.this, MainActivity.class);
                startActivity(intentS);
                finish();
            }
        });

        //KLIKK PÅ REGISTRER
        btnRegistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //METODE FOR Å KONTROLLRE AT ALT ER VALIDERT FØR REGISTRERING
                fullforRegistrering();
            }
        });

        //KLIKK PÅ AVBRYT
        btnAvbryt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_tilbake = new Intent (RegistrerRom.this, Kart.class);
                startActivity(intent_tilbake);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------



    //-------HENTER UT VERDIEN VED ENDRINGER I TALLVELGER FOR ANTALL ROM---------
    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

        antSitteplasser = i1;
    }


    //--------METODE FOR Å REGISTRER ROM - KONTROLLERER VERDIR--------
    private void fullforRegistrering() {

        String hentRomNr = romNr.getText().toString();

        //INPUTVALIDERING - BARE ROM SOM TRENGER ETTERSOM ALT ANNET ER LÅSTE VERDIER
        if(!hentRomNr.equals("") && hentRomNr.matches("[a-zA-Z0-9\\-\\.]{2,20}+")) {

            //WEBSERVICE SOM UTFØRER REGISTRERINGEN AV ROM
            webLeggTilRom();

            //INFOMELDING UT
            toastMessage("Rom lagt til!");
            //MELDING TIL LOGG
            Log.d("Legg inn: ", "Rom lagt til");

            //TILBAKE TIL KARTET
            Intent intent_tilbake = new Intent (RegistrerRom.this, Kart.class);
            startActivity(intent_tilbake);
            finish();

       }
        //HVIS INPUT IKKE ER GODKJENT - FEILMELDING MED TIPS
       else {
            //INFOMELDING UT - FEIL INPUT
            toastMessage("Romnummer må være på riktig format (Tips: PH360 eller FI10.117)");
        }
    }


    //--------POPULERER SPINNER FOR BYGG--------
    private void populerSpinBygg() {

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bygg,  R.layout.farge_spinner);
        adapter.setDropDownViewResource(R.layout.spinner_design);

        spinBygg.setAdapter(adapter);

        spinBygg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //VALGT BYGG OVERFØRES TIL BYGG VERDI
                bygg = (String) spinBygg.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


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


    //--------UTFORMING AV URL OG KJØRING AV DENNE - LEGG TIL ROM--------
    public void webLeggTilRom() {

        LastSide task = new LastSide();

        //HENTER VERDIENE TIL URL
        String hentRomNr = romNr.getText().toString();
        String hentBygg = bygg;
        String hentAntSitteplasser = antSitteplasser+"";
        String hentLat = latKoordinat.getText().toString();
        String hentLen = lenKoordinat.getText().toString();

        //UTFORMING AV URL TIL WEBSERVICE
        String url = "http://student.cs.hioa.no/~s304114/LeggTilRom.php/?romNr="+hentRomNr+"&bygg="+hentBygg+"&antSitteplasser="+hentAntSitteplasser+"&lat="+hentLat+"&len="+hentLen;
        //FJERNER MELLOMROM I STRENGEN
        String urlUtenMellomrom = url.replaceAll(" ", "");

        task.execute(new String[]{urlUtenMellomrom});
    }


    //-------LAGER TOOLBAR---------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.minmeny, menu);
        return true;
    }


    //-------ULIKE VALG I TOOLBAR---------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.velgSted:
                Intent intent = new Intent (RegistrerRom.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.SeAlleReservasjoner:
                Intent intentSe = new Intent (RegistrerRom.this, SeAlleReservasjoner.class);
                startActivity(intentSe);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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