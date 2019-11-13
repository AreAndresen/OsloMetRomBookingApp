package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skole.s304114mappe3.klasser.Rom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


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
    private TextView visDato;

    //--------SPINNERE--------
    private Spinner spinStart, spinSlutt, spinnerRomNr;

    //--------VERDIER--------
    private String dato, tid;

    private String tidFra, tidTil;

    ArrayList<Rom> alleRom = new ArrayList<Rom>();
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

        kjorJson();


        logo = findViewById(R.id.logo2);

        //--------KNAPPER--------
        btnAvbryt = (Button) findViewById(R.id.btnAvbryt);
        //btnLeggTilVenn = (Button) findViewById(R.id.btnLeggTilVenn);
        btnReserver = (Button) findViewById(R.id.btnReserver);


        //--------TEKST--------
        visDato = (TextView) findViewById(R.id.visDato);
        //visTid = (TextView) findViewById(R.id.visTid);
        //vennerListView = (ListView) findViewById(R.id.list);


        //--------SPINNERE--------
        spinStart = (Spinner) findViewById(R.id.spinStart);
        spinSlutt = (Spinner) findViewById(R.id.spinSlutt);
        spinnerRomNr = (Spinner) findViewById(R.id.spinnerRomNr);

        populerSpinStart();
        populerSpinSlutt();
        lagRomSpinner();

        //--------DB HANDLER--------
        //db = new DBhandler(this);


        //--------OPPRETTER OG POPULERER SPINNERE--------
        //lagResturantSpinner();
        //lagVennerSpinner();


        //--------LISTENERS--------
        /*KLIKK PÅ LEGG TIL
        btnLeggTilVenn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LEGGER TIL VALGT VENN I valgteVenner ARRAYET
                leggTilValgtVenn();
            }
        });*/

        //KLIKK PÅ VELG DATO
        visDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OPPRETTER DATOFRAGMENTET FOR SETTING AV DATO
                DialogFragment datoValg = new DatoFragment();
                datoValg.show(getSupportFragmentManager(), "dato valg");
            }
        });


        //KLIKK PÅ UTFØR BESTILLING
        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //KONTROLLERER AT ALLE FELTER SOM ER OBLIGATORISKE ER BENYTTET
                if (!visDato.getText().toString().equals("")) {

                    //OPPRETTER SEBESTILLINGSINFODIALOG OG VISER VALGT INFO
                    readWebpage();

                    //INFOMELDING UT
                    toastMessage("Reservasjon registrert!");
                    //MELDING TIL LOGG
                    Log.d("Legg inn: ", "Rom lagt til");


                    Intent intent_tilbake = new Intent (ReserverRom.this, MainActivityNy.class);
                    startActivity(intent_tilbake);
                    finish();

                }
                else{
                    //INFOMELDING UT - FEIL INPUT
                    Toast.makeText(ReserverRom.this, "Dato for reservasjon må være fylt ut.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //KLIKK PÅ TILBAKE
        btnAvbryt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ReserverRom.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------




    }

    public void kjorJson(){
        //JSON GREIER
        //textView = (TextView) findViewById(R.id.jasontekst);
        getJSON task = new getJSON();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentRom.php"});
    }



    //METODER FOR Å HENTE JSONOBJEKTENE FRA URL  - Sette inn ArrayList HER?
    private class getJSON extends AsyncTask<String, Void, ArrayList<Rom>> {
        JSONObject jsonObject;
        ArrayList<Rom> jsonArray = new ArrayList<>();

        //kjører i bakgrunnen - heavy work
        @Override
        protected ArrayList<Rom> doInBackground(String... urls) {
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

                            String romNr = jsonobject.getString("romNr");
                            String bygg = jsonobject.getString("bygg");
                            String antSitteplasser = jsonobject.getString("antSitteplasser");
                            String lat = jsonobject.getString("lat");
                            String len = jsonobject.getString("len");
                            //retur = retur +beskrivelse+": "+lat+ " "+len+"\n";

                            Double latD = Double.parseDouble(lat);
                            Double lenD = Double.parseDouble(len);

                            LatLng koordinater = new LatLng(latD, lenD);

                            Rom nyttRom = new Rom(romNr, bygg, antSitteplasser, koordinater);

                            jsonArray.add(nyttRom);
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
        protected void onPostExecute(ArrayList<Rom> jsonArray) {
            alleRom = jsonArray;

            lagRomSpinner();
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

                Toast.makeText(adapterView.getContext(), tidFra, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /*@Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }*/

    //--------POPULERER VENNERLISTVIEWET - MULIGHET FOR LESTTING DIREKTE--------
    private void populerSpinSlutt() {

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.klokkeslett, R.layout.farge_spinner);
        adapter2.setDropDownViewResource(R.layout.spinner_design);

        spinSlutt.setAdapter(adapter2);

        spinSlutt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {

                //GIR VALGTRESTURANT VERDIEN TIL VALGT OBJEKT FRA SPINNER
                tidTil = (String) spinSlutt.getItemAtPosition(i);

                Toast.makeText(adapterView.getContext(), tidTil, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }



    //--------GENERERER SPINNER MED ALLE RESTURATENE SOM ER LAGT TIL I DB--------
    private void lagRomSpinner() {

        //LEGGER ALLE RESTURANTER I RESTURANT-ARRAY - HENTET FRA DB
        ArrayList<Rom> alleRomNy = alleRom;

        //GENERERER ARRAYADAPTER TIL SPINNER
        final ArrayAdapter<Rom> adapterRes = new ArrayAdapter<Rom>(this, R.layout.spinner_design, alleRomNy);
        adapterRes.setDropDownViewResource(R.layout.spinner_design);

        spinnerRomNr.setAdapter(adapterRes);

        //VED VALG/KLIKK AV RESTURANT I SPINNEREN
        spinnerRomNr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //GIR VALGTRESTURANT VERDIEN TIL VALGT OBJEKT FRA SPINNER
                valgtRom = (Rom) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }



    //--------GENERERER SPINNER MED ALLE RESTURATENE SOM ER LAGT TIL I DB--------
    /*private void populerSpinSlutt2() {

        //LEGGER ALLE RESTURANTER I RESTURANT-ARRAY - HENTET FRA DB
        ArrayList<Resturant> resturanter = db.finnAlleResturanter();

        //GENERERER ARRAYADAPTER TIL SPINNER
        final ArrayAdapter<Resturant> adapterRes = new ArrayAdapter<Resturant>(this, android.R.layout.simple_list_item_1, resturanter);
        adapterRes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerResturanter.setAdapter(adapterRes);

        //VED VALG/KLIKK AV RESTURANT I SPINNEREN
        spinnerResturanter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //GIR VALGTRESTURANT VERDIEN TIL VALGT OBJEKT FRA SPINNER
                valgtResturant = (Resturant) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }*/



    /*--------OPPRETTER SEBESTILLINGSINFODIALOG--------
    private void visBestillingsinfo()  {

        //OPPRETTER NYTT DIALOGFRAGMENT
        SeBestillingsInfoDialog bFragment = new SeBestillingsInfoDialog();

        //OVERFØRER BESTILLINGSINFO TIL FRAGMENTET MED METODE FRA FRAGMENTET
        bFragment.hentInfo(dato, tid, valgtResturant, valgteVenner, db);

        //VISER DIALOGVINDUET
        bFragment.show(getFragmentManager(), "Bestillingsinfo");
    }*/


    //--------INNEBYGD METODE FOR SETTING AV DATO--------
    @Override
    public void onDateSet(DatePicker view, int aar, int mnd, int dag) {

        //MÅ LEGGE INN DENNE ETTERSOM MÅNEDSTALLET VISER 9 FOR OKTOBER OSV.
        mnd++;

        //GENERERER STRING PÅ 22/10/2019 FORMAT
        dato = dag+"."+mnd+"."+aar;
        visDato.setText(dato);
    }





    /*--------GENERERER SPINNER MED ALLE RESTURATENE SOM ER LAGT TIL I DB--------
    private void lagResturantSpinner() {

        //LEGGER ALLE RESTURANTER I RESTURANT-ARRAY - HENTET FRA DB
        ArrayList<Resturant> resturanter = db.finnAlleResturanter();

        //GENERERER ARRAYADAPTER TIL SPINNER
        final ArrayAdapter<Resturant> adapterRes = new ArrayAdapter<Resturant>(this, android.R.layout.simple_list_item_1, resturanter);
        adapterRes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerResturanter.setAdapter(adapterRes);

        //VED VALG/KLIKK AV RESTURANT I SPINNEREN
        spinnerResturanter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //GIR VALGTRESTURANT VERDIEN TIL VALGT OBJEKT FRA SPINNER
                valgtResturant = (Resturant) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    //--------GENERERER SPINNER MED ALLE VENNER SOM ER LAGT TIL I DB--------
    private void lagVennerSpinner() {

        //LEGGER ALLE VENNER I VENNER-ARRAY - HENTET FRA DB
        ArrayList<Venn> venner = db.finnAlleVenner();

        //GENERERER ARRAYADAPTER TIL SPINNER
        final ArrayAdapter<Venn> adapterVenner = new ArrayAdapter<Venn>(this, android.R.layout.simple_list_item_1, venner);
        adapterVenner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerVenner.setAdapter(adapterVenner);

        //VED VALG/KLIKK AV RESTURANT I SPINNEREN
        spinnerVenner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //GIR VALGTRESTURANT VERDIEN TIL VALGT OBJEKT FRA SPINNER
                valgtVenn = (Venn) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    //-------SJEKK OM INDEKS/VENN ALLEREDE ER LAGT TIL I VALGTEVENNER-ARRAY---------
    private boolean sjekkVenn(Venn venn) {
        boolean sjekk = false;

        //GÅR GJENNOM ARRAY OG KONTROLLERER ETTER VENNER
        for (Venn v : valgteVenner) {
            if (v == venn) {
                //FINNES I ARRAYET
                sjekk = true;
                break;
            }
        }
        return sjekk;
    }


    //-------BENYTTER KONTROLLEN OG LEGGER TIL VALGT VENN I VALGTEVENNER-ARRAY---------
    public void leggTilValgtVenn() {
        //KONTROLL AV VENN
        if(!sjekkVenn(valgtVenn)) {

            //HVIS KONTROLL GODKJENT - LEGGER TIL VENN
            valgteVenner.add(valgtVenn);

            //MELDING UT OM AT VENN ER LAGT TIL
            Toast.makeText(this, valgtVenn.getNavn() + " lagt til i bestilling.", Toast.LENGTH_LONG).show();


            //POPULERER FORTLØPENDE LISTVIEWET SOM VISER VALGTE VENNER SOM ER LAGT TIL
            populerListView();
        }
        else {
            Toast.makeText(this, valgtVenn.getNavn()+" er allerede lagt til i bestilling.", Toast.LENGTH_LONG).show();
        }
    }


    //-------GENERERER NØKLER FOR MELDINGER TIL MINNET OG FULLFØRER BESTILLINGEN---------
    private void fullforBestilling() {
        //GENERERER NØKKEL SOM BRUKES SOM BESTILLINGSID - HENTER FRA MINNET OG PLUSSER FOR HVER GANG
        int indeksen = 1 + getSharedPreferences("APP_INFO", MODE_PRIVATE).getInt("LOPENUMMERBESTILLING", 0);

        //LAGRER NØKKEL MED NØKKEL
        getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putInt("LOPENUMMERBESTILLING", indeksen).apply();


        //LEGGER TIL BESTILLING I DB
        leggTilBestilling(indeksen);

        //LEGGER TIL DELTAKELSER FOR HVER VENN SOM ER MED I BESTILLINGEN
        leggTilDeltakelser(indeksen);


        //OPPRETTER NY MELDING TIL PÅMINNELSER
        String meldingUt = "Husk bestilling i dag hos "+valgtResturant.getNavn() + ". Dato: " + dato + ". " + tid;


        //OPPRETTER NY LØPENDE NØKKEL
        String NOKKEL = indeksen + "";
        //LAGRER NØKKELEN I MINNET
        getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putInt(NOKKEL, indeksen).apply();


        //OPPRETTER NY LØPENDE NØKKEL TIL MELDING (BENYTTER INDEKSEN)
        String nokkel_MELDING = "melding" + indeksen;
        //LAGRER MELDING MED LØPENDE NØKKEL I MINNET
        getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString(nokkel_MELDING, meldingUt).apply();


        //VIEW OPPDATERES FORTLØPENDE - FORHINDRER STACK
        Intent intent_preferanser = new Intent (RegistrerBestilling.this, SeBestillinger.class);
        startActivity(intent_preferanser);
        finish();
    }


    //-------LEGGER TIL DELTAKELSER I DB---------
    private void leggTilDeltakelser(int indeksen) {
        //GENERERER EN DELTAKELSE FOR HVER VENN SOM ER MED I BESTILLINGEN
        for(Venn i : valgteVenner) {
            //OPPRETTER NY DELTAKELSE MED DET GENERERTE LØPENUMMERET FRA MINNET
            Deltakelse deltakelse = new Deltakelse(indeksen, i.getID(), i.getNavn());
            //LEGGER TIL DELTAKELSEN I DB
            db.leggTilDeltakelse(deltakelse);
        }
    }


    //-------LEGGER TIL BESTILLING I DB---------
    private void leggTilBestilling(int indeksen) {
        //OPPRETTER NY BESTILLING
        Bestilling bestilling = new Bestilling(dato, tid, valgtResturant.getNavn(), valgtResturant.get_ID());

        //LEGGER TIL BESTILLINGEN I DB MED GENERERT LØPENUMMER FRA MINNET
        db.leggTilBestilling(bestilling, indeksen);
    }
    */


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
        String hentDato = dato;
        String hentTidFra = tidFra;;//beskrivelse.getText().toString();
        String hentTidTil = tidTil;;//beskrivelse.getText().toString();
        String hentRomNr = valgtRom.getRomNr();


        //String noSpaceStr = str.replaceAll("\\s", ""); // using built in method
        //System.out.println(noSpaceStr);


        //må fikse  denne strengen så den er uten mellomrom og nordiske tegn og kan brukes i url
        String url = "http://student.cs.hioa.no/~s304114/LeggTilReservasjon.php/?dato="+hentDato+"&tidFra="+hentTidFra+"&tidTil="+hentTidTil+"&romNr="+hentRomNr;
        //FJERNER MELLOMROM I STRENGEN
        String urlUtenMellomrom = url.replaceAll(" ", "");


        task.execute(new String[]{urlUtenMellomrom});
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
                Intent intent = new Intent (ReserverRom.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.registrerRom:
                Intent intent_statistikk = new Intent (ReserverRom.this, RegistrerRom.class);
                startActivity(intent_statistikk);
                break;
            case R.id.reserverRom:
                Intent intent_preferanser = new Intent (ReserverRom.this, ReserverRom.class);
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


    //-------TILBAKE KNAPP - FORHINDRER STACK---------
    @Override
    public void onBackPressed() {
        Intent intent = new Intent (ReserverRom.this, MainActivityNy.class);
        startActivity(intent);
        finish();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}