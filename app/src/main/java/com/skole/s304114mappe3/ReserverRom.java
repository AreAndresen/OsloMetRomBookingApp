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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.skole.s304114mappe3.ListView.SeAlleReservasjoner;
import com.skole.s304114mappe3.klasser.Reservasjon;
import com.skole.s304114mappe3.klasser.Rom;

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

//public class ReserverRom extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
  //      TimePickerDialog.OnTimeSetListener, SeBestillingsInfoDialog.DialogClickListener {


    /*--------DIALOG KNAPPER TIL SEBESTILLINGSINFOTDIALOG--------
    @Override
    public void bestillClick() {
        fullforBestilling();
    }

    @Override
    public void avbrytClick() {
        Toast.makeText(getApplicationContext(),"Avbrutt bestilling",Toast.LENGTH_LONG).show();
        return;
    }*/
    private ImageView logo;

    //--------KNAPPER--------
    private Button btnAvbryt, btnLeggTilVenn, btnReserver;

    //--------TEKST--------
    private TextView visDato,visRomNr;

    //--------SPINNERE--------
    private Spinner spinStart, spinSlutt, spinnerRomNr;

    //--------VERDIER--------
    private String dato;
    private String datoIdag;

    private String valgtRomNr;

    private String tidFra, tidTil;
    private boolean RiktigTid, RiktigDato, ReservasjonFinnes;

    ArrayList<Rom> alleRom = new ArrayList<Rom>();

    ArrayList<Reservasjon> reservasjoner = new ArrayList<Reservasjon>();
    Rom valgtRom;

    //--------OBJEKTER--------
    //private Venn valgtVenn, valgtVennSlett;
    //private Resturant valgtResturant;

    //--------ARRAYS--------
    //private ArrayList<Venn> valgteVenner = new ArrayList<Venn>();

    //--------LISTVIEW--------
    ListView vennerListView;

    //--------DB HANDLER--------
    //DBhandler db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserver_rom);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        //toolbar.setNavigationIcon(R.drawable.ic_action_name); //android: //src="@drawable/logo"
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorText2));
        setActionBar(toolbar);


        //kjorJson();


        logo = findViewById(R.id.logo2);

        //--------KNAPPER--------
        btnAvbryt = (Button) findViewById(R.id.btnAvbryt);
        //btnLeggTilVenn = (Button) findViewById(R.id.btnLeggTilVenn);
        btnReserver = (Button) findViewById(R.id.btnReserver);


        //--------TEKST--------
        visDato = (TextView) findViewById(R.id.visDato);
        visRomNr = (TextView) findViewById(R.id.visRomNr);

        //--------SETTER OUTPUT--------

        ////--------HENTER ID TIL BESTILLINGEN SOM SKAL VISES FRA MINNE - DEFINERT I SEBESTILLINGER OG I NOTIFIKASJON/SERVICE--------
        valgtRomNr = getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("ROMNR", ""); //putString("ROMNR", valgtRomNr).apply();

        visRomNr.setText(valgtRomNr);;
        //visTid = (TextView) findViewById(R.id.visTid);
        //vennerListView = (ListView) findViewById(R.id.list);


        //--------SPINNERE--------
        spinStart = (Spinner) findViewById(R.id.spinStart);
        spinSlutt = (Spinner) findViewById(R.id.spinSlutt);
        //spinnerRomNr = (Spinner) findViewById(R.id.spinnerRomNr);

        populerSpinStart();
        populerSpinSlutt();

        kjorJsonAlleReservasjoner();
        //lagRomSpinner();

        //--------HENTER DAGENS DATO I RIKTIG FORMAT TIL SAMMENLIGNING AV DET SOM LIGGER I DB--------

        //KLIKK PÅ VELG DATO
        visDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //OPPRETTER DATOFRAGMENTET FOR SETTING AV DATO
                DialogFragment datoValg = new DatoFragment();
                datoValg.show(getSupportFragmentManager(), "dato valg");

                //kontrollerDatoer();
            }
        });



        //KLIKK PÅ UTFØR BESTILLING
        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                readWebpage();

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




    }

    //ASYNC NR 2
    public void kjorJsonAlleReservasjoner(){
        //JSON GREIER
        //textView = (TextView) findViewById(R.id.jasontekst);
        getJsonAlleReservasjoner task = new getJsonAlleReservasjoner();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentReservasjoner.php"});
    }



    //METODER FOR Å HENTE JSONOBJEKTENE FRA URL  - Sette inn ArrayList HER?
    private class getJsonAlleReservasjoner extends AsyncTask<String, Void, ArrayList<Reservasjon>> {
        JSONObject jsonObject;
        ArrayList<Reservasjon> jsonArray = new ArrayList<>();

        //kjører i bakgrunnen - heavy work
        @Override
        protected ArrayList<Reservasjon> doInBackground(String... urls) {
            //String retur = "";

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
                            //henter alle json objectene
                            JSONObject jsonobject = mat.getJSONObject(i);

                            int id = jsonobject.getInt("id");
                            String dato = jsonobject.getString("dato");
                            String tidFra = jsonobject.getString("tidFra");
                            String tidTil = jsonobject.getString("tidTil");
                            String romNr = jsonobject.getString("romNr");


                            Reservasjon nyReservasjon = new Reservasjon(id, dato, tidFra, tidTil, romNr);

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
                    //return "Noe gikk feil";
                    e.printStackTrace();
                }
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Reservasjon> jsonArray) {
            reservasjoner = jsonArray;

        }
    }



    //--------POPULERER VENNERLISTVIEWET - MULIGHET FOR LESTTING DIREKTE--------
    private void populerSpinStart() {

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.klokkeslett,  R.layout.farge_spinner);
        adapter.setDropDownViewResource(R.layout.spinner_design);

        spinStart.setAdapter(adapter);

        spinStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();

                tidFra = (String) spinStart.getItemAtPosition(i);


                String sTidFra = tidFra.replaceAll(":", "");

                if(tidTil == null) {
                    tidTil = "07:15";
                }

                String sTidTil = tidTil.replaceAll(":", "");

                int tidFraInt = Integer.parseInt(sTidFra);
                int tidTilInt = Integer.parseInt(sTidTil);

                int sum = tidTilInt - tidFraInt;

                RiktigTid = true;

                if(tidFraInt >= tidTilInt) {
                    Toast.makeText(adapterView.getContext(), "Starttid må være før sluttid."+sum, Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }
                if(sum > 200) {
                    Toast.makeText(adapterView.getContext(), "Reservasjon kan ikke oversige 2 timer "+sum, Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }
                Toast.makeText(adapterView.getContext(), tidFra, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    //--------POPULERER VENNERLISTVIEWET - MULIGHET FOR LESTTING DIREKTE--------
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


                String sTidFra = tidFra.replaceAll(":", "");
                String sTidTil = tidTil.replaceAll(":", "");

                int tidFraInt = Integer.parseInt(sTidFra);
                int tidTilInt = Integer.parseInt(sTidTil);

                int sum = tidTilInt - tidFraInt;

                RiktigTid = true;

                if(tidFraInt >= tidTilInt) {
                    Toast.makeText(adapterView.getContext(), "Starttid må være før sluttid."+sum, Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }
                if(sum > 200) {
                    Toast.makeText(adapterView.getContext(), "Reservasjon kan ikke oversige 2 timer "+sum, Toast.LENGTH_SHORT).show();
                    RiktigTid = false;
                }


                Toast.makeText(adapterView.getContext(), tidFraInt+" OG "+tidTilInt+" . "+tidTil+" = "+sum, Toast.LENGTH_SHORT).show();
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

        //GENERERER STRING PÅ 22/10/2019 FORMAT
        dato = dag+"."+mnd+"."+aar;


        kontrollerDatoer(dato);
    }


    public void kontrollerDatoer(String dato) {

        //boolean riktigDato = false;

        //--------FORMATERER DATOENE FOR SAMMENLIGNING--------
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dato2 = null;
        Date dato4 = null;

        //------------------FIKS DENNE INSTANSIERINGEN AV DATOER FEIL VED DATOMETODEN MÅ INSTANSIERES FØRST ------------------------------------->>>>
        //if (dato2 == null && dato4 == null) {
            try {
                Calendar c = Calendar.getInstance();
                int aarD = c.get(Calendar.YEAR);
                int mndD = c.get(Calendar.MONTH);
                int dagD = c.get(Calendar.DAY_OF_MONTH);

                mndD++;
                String datoIdag = dagD + "." + mndD + "." + aarD;

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                dato2 = sdf.parse(dato); //"01/01/2017"
                dato4 = sdf.parse(datoIdag); //"01/04/2017"

                if (dato2.after(dato4) || dato2.compareTo(dato4) == 0) {
                    visDato.setText(dato);
                    RiktigDato = true; //
                }
                else{
                    RiktigDato = false;
                    Toast.makeText(ReserverRom.this, "Det er ikke mulig å bestille rom tilbake i tid.", Toast.LENGTH_SHORT).show();
                    visDato.setText("Ugyldig dato");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        //}
        Toast.makeText(ReserverRom.this, dato2+" "+dato4, Toast.LENGTH_SHORT).show();

        //return riktigDato;
    }



    //REGISTRERER RESERVASJON
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
        String hentDato = dato;
        String hentTidFra = tidFra;;//beskrivelse.getText().toString();
        String hentTidTil = tidTil;;//beskrivelse.getText().toString();
        String hentRomNr = valgtRomNr;

        ReservasjonFinnes = false;


        for(Reservasjon r : reservasjoner) {
            if(r.getDato().equals(dato) && r.getRomNr().equals(valgtRomNr)) {

                if(r.getTidFra().equals(tidFra) ||  r.getTidTil().equals(tidTil)) {
                    ReservasjonFinnes = true;
                }
            }
        }


        //KONTROLLERER AT ALLE FELTER SOM ER OBLIGATORISKE ER BENYTTET
        if (ReservasjonFinnes) { // && kontrollerDatoer()

            Toast.makeText(ReserverRom.this, "Reservasjonen finnes.", Toast.LENGTH_SHORT).show();

        }

        if(!visDato.getText().toString().equals("") && RiktigTid && RiktigDato && !ReservasjonFinnes) {
            //må fikse  denne strengen så den er uten mellomrom og nordiske tegn og kan brukes i url
            String url = "http://student.cs.hioa.no/~s304114/LeggTilReservasjon.php/?dato="+hentDato+"&tidFra="+hentTidFra+"&tidTil="+hentTidTil+"&romNr="+hentRomNr;
            //FJERNER MELLOMROM I STRENGEN
            String urlUtenMellomrom = url.replaceAll(" ", "");


            task.execute(new String[]{urlUtenMellomrom});

            //INFOMELDING UT
            toastMessage("Reservasjon registrert!");
            //MELDING TIL LOGG
            Log.d("Legg inn: ", "Rom lagt til");


            Intent intent_tilbake = new Intent (ReserverRom.this, Kart.class);
            startActivity(intent_tilbake);
            finish();

        }
        if(visDato.getText().toString().equals("") || !RiktigTid || !RiktigDato) {
            Toast.makeText(ReserverRom.this, "Kontroller at alt er fylt ut riktig.", Toast.LENGTH_SHORT).show();
        }
    }




    //-------lAGER TOOLBAR---------
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