package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.skole.s304114mappe3.ListView.SeAlleReservasjoner;
import com.skole.s304114mappe3.klasser.Reservasjon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ReserverRom extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{


    //--------KNAPPER--------
    private Button btnAvbryt, btnReserver;
    private ImageView logoItoolBar;

    //--------TEKST--------
    private TextView visDato,visRomNr;

    //--------SPINNERE--------
    private Spinner spinStart, spinSlutt;

    //--------VERDIER--------
    private String dato;
    private String valgtRomNr;
    private String tidFra, tidTil;
    private boolean RiktigTid, RiktigDato, ReservasjonFinnes;

    //--------ARRAY--------
    ArrayList<Reservasjon> reservasjoner = new ArrayList<Reservasjon>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserver_rom);

        //--------TOOLBAR--------
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        setActionBar(toolbar);


        //--------KNAPPER--------
        btnAvbryt = (Button) findViewById(R.id.btnAvbryt);
        btnReserver = (Button) findViewById(R.id.btnReserver);


        //--------LOGO - FUNGERER SOM KNAPP--------
        logoItoolBar = findViewById(R.id.logo2);


        //--------TEKST--------
        visDato = (TextView) findViewById(R.id.visDato);
        visRomNr = (TextView) findViewById(R.id.visRomNr);


        //--------HENTER ROMNR FRA VALGT ROM I KART FRA MINNE--------
        valgtRomNr = getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("ROMNR", "");

        visRomNr.setText(valgtRomNr);;


        //--------SPINNERE--------
        spinStart = (Spinner) findViewById(R.id.spinStart);
        spinSlutt = (Spinner) findViewById(R.id.spinSlutt);


        //--------POPULERER SPINNERE--------
        populerSpinStart();
        populerSpinSlutt();


        //--------ASYNC SOM HENTER ALLE RESERVASJONER - BRUKES TIL Å TESTE OM DE FINNES FRA FØR--------
        kjorJsonAlleReservasjoner();


        //--------LISTENERS--------
        //KLIKK LOGO I TOOLBAR
        logoItoolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentS = new Intent (ReserverRom.this, MainActivity.class);
                startActivity(intentS);
                finish();
            }
        });

        //KLIKK PÅ VELG DATO
        visDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OPPRETTER DATOFRAGMENTET FOR SETTING AV DATO
                DialogFragment datoValg = new DatoFragment();
                datoValg.show(getSupportFragmentManager(), "dato valg");
            }
        });

        //KLIKK PÅ RESERVER
        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLeggTilReservasjon();

            }
        });

        //KLIKK PÅ TILBAKE
        btnAvbryt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ReserverRom.this, Kart.class);
                startActivity(intent);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------



    //--------HENTER ALLE RESERVASJONER--------
    public void kjorJsonAlleReservasjoner(){

        getJsonAlleReservasjoner task = new getJsonAlleReservasjoner();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentReservasjoner.php"});
    }


    //--------HENTER ALLE RESERVASJONER SOM JSONOBJEKTER FRA WEBSERVICE--------
    private class getJsonAlleReservasjoner extends AsyncTask<String, Void, ArrayList<Reservasjon>> {
        JSONObject jsonObject;
        ArrayList<Reservasjon> jsonArray = new ArrayList<>();

        @Override
        protected ArrayList<Reservasjon> doInBackground(String... urls) {

            String s = "";
            String output = "";

            for (String url : urls) {
                try{
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection)urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if(conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed: HTTP errorcode: "+ conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    System.out.println("Output from Server .... \n");
                    while((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try{
                        JSONArray mat = new JSONArray(output);

                        for (int i = 0; i < mat.length(); i++) {

                            JSONObject jsonobject = mat.getJSONObject(i);

                            //HENTER ALLE RESERVASJONER OG OVERFØRER VERDIENE
                            int id = jsonobject.getInt("id");
                            String dato = jsonobject.getString("dato");
                            String tidFra = jsonobject.getString("tidFra");
                            String tidTil = jsonobject.getString("tidTil");
                            String romNr = jsonobject.getString("romNr");

                            //OPPRETTER NY RESERVASJON
                            Reservasjon nyReservasjon = new Reservasjon(id, dato, tidFra, tidTil, romNr);

                            //OVERFØRER ALLE RESERVASJONER TIL JSONARRAY-ARRAYET
                            jsonArray.add(nyReservasjon);
                        }
                        return jsonArray;
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                    }
                    return jsonArray;
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Reservasjon> jsonArray) {
            //OVERFØRER ARRAYET TIL ET TILGJENGELIG RESERVASJONER-ARRAY
            reservasjoner = jsonArray;
        }
    }


    //--------POPULERER SPINNER FOR START TIDSPUNKT--------
    private void populerSpinStart() {

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.klokkeslett,  R.layout.farge_spinner);
        adapter.setDropDownViewResource(R.layout.spinner_design);

        spinStart.setAdapter(adapter);

        spinStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                tidFra = (String) spinStart.getItemAtPosition(i);

                //SETTER DEFAULT VERDI I TILFELLE BRUKER UTFØRER ET FYSISK TRYKK PÅ LISTEN
                if(tidTil == null) {
                    tidTil = "07:15";
                }

                //FJERNER : FRA STRENGENE FOR LETTERE Å PARSE TIL INT
                String sTidFra = tidFra.replaceAll(":", "");
                String sTidTil = tidTil.replaceAll(":", "");

                //PARSER STRENGENE TIL INT
                int tidFraInt = Integer.parseInt(sTidFra);
                int tidTilInt = Integer.parseInt(sTidTil);

                //KONTROLLERER DIFFERANSEN MELLOM TIDENE
                int sum = tidTilInt - tidFraInt;

                //BOOLEAN BRUKES FOR VERIFISERING SENERE
                RiktigTid = true;

                //HVIS TID FRA ER STØRRE ENN TID TIL - GENERER TOAST MED FEILMELDING
                if(tidFraInt >= tidTilInt) {
                    Toast.makeText(adapterView.getContext(), "Starttid må være før sluttid.", Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }

                //HVIS DIFFERANSEN MELLOM TIDENE ER STØRRE ENN 4 TIMER - GENERER TOAST MED FEILMELDING
                if(sum > 400) {
                    Toast.makeText(adapterView.getContext(), "Reservasjon kan ikke oversige 4 timer ", Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    //--------POPULERER SPINNER FOR START TIDSPUNKT--------
    private void populerSpinSlutt() {

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.klokkesletttil, R.layout.farge_spinner);
        adapter2.setDropDownViewResource(R.layout.spinner_design);

        spinSlutt.setAdapter(adapter2);

        spinSlutt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {

                //GIR VALGTRESTURANT VERDIEN TIL VALGT OBJEKT FRA SPINNER
                tidTil = (String) spinSlutt.getItemAtPosition(i);

                //FJERNER : FRA STRENGENE FOR LETTERE Å PARSE TIL INT
                String sTidFra = tidFra.replaceAll(":", "");
                String sTidTil = tidTil.replaceAll(":", "");

                //PARSER STRENGENE TIL INT
                int tidFraInt = Integer.parseInt(sTidFra);
                int tidTilInt = Integer.parseInt(sTidTil);

                //KONTROLLERER DIFFERANSEN MELLOM TIDENE
                int sum = tidTilInt - tidFraInt;

                //BOOLEAN BRUKES FOR VERIFISERING SENERE
                RiktigTid = true;

                //HVIS TID FRA ER STØRRE ENN TID TIL - GENERER TOAST MED FEILMELDING
                if(tidFraInt >= tidTilInt) {
                    Toast.makeText(adapterView.getContext(), "Starttid må være før sluttid.", Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }

                //HVIS DIFFERANSEN MELLOM TIDENE ER STØRRE ENN 4 TIMER - GENERER TOAST MED FEILMELDING
                if(sum > 400) {
                    Toast.makeText(adapterView.getContext(), "Reservasjon kan ikke oversige 4 timer ", Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    //--------INNEBYGD METODE FOR SETTING AV DATO--------
    @Override
    public void onDateSet(DatePicker view, int aar, int mnd, int dag) {

        //MÅ LEGGE INN DENNE ETTERSOM MÅNEDSTALLET VISER 9 FOR OKTOBER OSV.
        mnd++;

        //GENERERER STRING PÅ 22.10.2019 FORMAT
        dato = dag+"."+mnd+"."+aar;

        //METODE SOM KONTROLLERER OM DATOEN ER TILBAKE I TID
        kontrollerDatoer(dato);
    }


    //--------KONTROLLERER OM VALGT DATO ER TILBAKE I TID - UNNGÅR AT DETTE ER MULIG--------
    public void kontrollerDatoer(String dato) {

        Date dato2 = null;
        Date datoIdag2 = null;

        try {
            //HENTER DAGENS DATO
            Calendar c = Calendar.getInstance();
            int aarD = c.get(Calendar.YEAR);
            int mndD = c.get(Calendar.MONTH);
            int dagD = c.get(Calendar.DAY_OF_MONTH);

            //STRENG AV DAGENS DATO
            mndD++;
            String datoIdag = dagD + "." + mndD + "." + aarD;

            //FORMATERER DATOENE FOR SAMMENLIGNING
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            dato2 = sdf.parse(dato);
            datoIdag2 = sdf.parse(datoIdag);

            //KONTROLLERER AT VALGT DATO ER ETTER ELLER SAMME DATO SOM DAGENS DATO
            if (dato2.after(datoIdag2) || dato2.compareTo(datoIdag2) == 0) {
                visDato.setText(dato);
                RiktigDato = true;
            }
            else{
                //HVIS IKKE - GENEREES FEILMELDINGER OG BOOLEAN = FALSE (FORHINDRER AT RESERVERING KAN UTFØRES)
                RiktigDato = false;
                visDato.setText("Ugyldig dato");
                Toast.makeText(ReserverRom.this, "Det er ikke mulig å bestille rom tilbake i tid.", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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


    //--------UTFORMING AV URL OG KJØRING AV DENNE - RESERVER ROM--------
    public void webLeggTilReservasjon() {

        LastSide task = new LastSide();

        //HENTER VERDIENE TIL URL
        String hentDato = dato;
        String hentTidFra = tidFra;
        String hentTidTil = tidTil;
        String hentRomNr = valgtRomNr;

        ReservasjonFinnes = false;

        //KONTROLLERER OM RESERVASJONEN FINNES FRA FØR (IKKE HELT PERFEKT, DESSVERRE)
        for(Reservasjon r : reservasjoner) {
            if(r.getDato().equals(dato) && r.getRomNr().equals(valgtRomNr)) {
                if(r.getTidFra().equals(tidFra) ||  r.getTidTil().equals(tidTil)) {
                    ReservasjonFinnes = true;
                }
            }
        }

        //HVIS RESERVASJONEN FINNES - FEILMELDING
        if (ReservasjonFinnes) {
            Toast.makeText(ReserverRom.this, "Reservasjonen finnes.", Toast.LENGTH_SHORT).show();
        }

        //KONTROLLERER AT ALLE FELTER SOM ER OBLIGATORISKE ER BENYTTET OG RIKTIG FYLT UT
        if(!visDato.getText().toString().equals("") && RiktigTid && RiktigDato && !ReservasjonFinnes) {

            //UTFORMING AV URL TIL WEBSERVICE
            String url = "http://student.cs.hioa.no/~s304114/LeggTilReservasjon.php/?dato="+hentDato+"&tidFra="+hentTidFra+"&tidTil="+hentTidTil+"&romNr="+hentRomNr;
            //FJERNER MELLOMROM I STRENGEN
            String urlUtenMellomrom = url.replaceAll(" ", "");

            task.execute(new String[]{urlUtenMellomrom});

            //BEKREFTENDE MELDING UT
            toastMessage("Reservasjon registrert!");
            //MELDING TIL LOGG
            Log.d("Legg inn: ", "Reservasjon lagt til");

            //TILBAKE TIL KARET
            Intent intent_tilbake = new Intent (ReserverRom.this, Kart.class);
            startActivity(intent_tilbake);
            finish();
        }

        //HVIS IKKE ALT ER UTFYLT RIKTIG - FEILMEDLING OG RESERVERING BLIR IKKE UTFØRT
        if(visDato.getText().toString().equals("") || !RiktigTid || !RiktigDato) {
            Toast.makeText(ReserverRom.this, "Kontroller at alt er fylt ut riktig.", Toast.LENGTH_SHORT).show();
        }
    }


    //-------LAGER TOOLBAR---------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.minmeny, menu);
        return true;
    }


    //-------ULIKE VALG I TOOLBAREN---------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.velgSted:
                Intent intent = new Intent (ReserverRom.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.SeAlleReservasjoner:
                Intent intent_preferanser = new Intent (ReserverRom.this, SeAlleReservasjoner.class);
                startActivity(intent_preferanser);
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
        Intent intent = new Intent (ReserverRom.this, Kart.class);
        startActivity(intent);
        finish();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}