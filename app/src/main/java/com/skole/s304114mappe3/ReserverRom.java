package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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


public class ReserverRom extends AppCompatActivity {

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
    private Button btnTilbake, btnLeggTilVenn, btnSeReservasjonsinfo;

    //--------TEKST--------
    private TextView visDato, visTid;

    //--------SPINNERE--------
    private Spinner spinStart, spinSlutt;

    //--------VERDIER--------
    private String dato, tid;

    private String tidStart, tidSlutt;

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


        logo = findViewById(R.id.logo2);

        //--------KNAPPER--------
        btnTilbake = (Button) findViewById(R.id.btnTilbake);
        //btnLeggTilVenn = (Button) findViewById(R.id.btnLeggTilVenn);
        btnSeReservasjonsinfo = (Button) findViewById(R.id.btnSeReservasjonsinfo);


        //--------TEKST--------
        visDato = (TextView) findViewById(R.id.visDato);
        //visTid = (TextView) findViewById(R.id.visTid);
        //vennerListView = (ListView) findViewById(R.id.list);


        //--------SPINNERE--------
        spinStart = (Spinner) findViewById(R.id.spinStart);
        spinSlutt = (Spinner) findViewById(R.id.spinSlutt);

        populerSpinStart();
        populerSpinSlutt();

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

        /*KLIKK PÅ VELG TID
        visTid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OPPRETTER TIDFRAGMENTET FOR SETTING AV TID
                DialogFragment tidValg = new TidFragment();
                tidValg.show(getSupportFragmentManager(), "tid valg");
            }
        });*/

        /*KLIKK PÅ UTFØR BESTILLING
        btnSeBestillingsinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //KONTROLLERER AT ALLE FELTER SOM ER OBLIGATORISKE ER BENYTTET
                if (!visDato.getText().toString().equals("") && !visTid.getText().toString().equals("")) {

                    //OPPRETTER SEBESTILLINGSINFODIALOG OG VISER VALGT INFO
                    visBestillingsinfo();
                }
                else{
                    //INFOMELDING UT - FEIL INPUT
                    Toast.makeText(ReserverRom.this, "Tid og dato for bestilling må være fylt ut.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //KLIKK PÅ TILBAKE
        btnTilbake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ReserverRom.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------




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
                Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
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
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.klokkeslett, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinSlutt.setAdapter(adapter2);

        spinSlutt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                //GIR VALGTRESTURANT VERDIEN TIL VALGT OBJEKT FRA SPINNER
                tidSlutt = parent.getItemAtPosition(i).toString();

                tidSlutt = (String) parent.getSelectedItem();
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


    /*--------INNEBYGD METODE FOR SETTING AV DATO--------
    @Override
    public void onDateSet(DatePicker view, int aar, int mnd, int dag) {

        //MÅ LEGGE INN DENNE ETTERSOM MÅNEDSTALLET VISER 9 FOR OKTOBER OSV.
        mnd++;

        //GENERERER STRING PÅ 22/10/2019 FORMAT
        dato = dag+"/"+mnd+"/"+aar;
        visDato.setText(dato);
    }


    //--------INNEBYGD METODE FOR SETTING AV TIDSPUNKT--------
    @Override
    public void onTimeSet(TimePicker view, int time, int min) {

        tid = "Kl: " + time + ":";
        //SØRGER FOR AT DET STÅR f.eks 10:05 ISTEDEN FOR 10:5
        if(min < 10) {
            tid += "0";
        }

        //GENERERER STRING PÅ KL: 16:35 FORMAT
        tid += min;

        visTid.setText(tid);
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
                Intent intent_startspill = new Intent (ReserverRom.this, Kart.class);
                startActivity(intent_startspill);
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
        Intent intent = new Intent (ReserverRom.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}