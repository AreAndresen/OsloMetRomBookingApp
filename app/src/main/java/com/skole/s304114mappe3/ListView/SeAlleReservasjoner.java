package com.skole.s304114mappe3.ListView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.skole.s304114mappe3.Dialog.SeReservasjonsInfoFragment;
import com.skole.s304114mappe3.Kart;
import com.skole.s304114mappe3.MainActivity;
import com.skole.s304114mappe3.R;
import com.skole.s304114mappe3.RegistrerRom;
import com.skole.s304114mappe3.klasser.Reservasjon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SeAlleReservasjoner extends AppCompatActivity {

    //--------KNAPPER--------
    private Button btnTilbake;

    //--------LISTVIEW--------
    private ListView reservasjonerListView;

    //--------OBJEKT--------
    private Reservasjon valgtReservasjon;

    ArrayList<Reservasjon> reservasjoner = new ArrayList<Reservasjon>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_se_alle_reservasjoner);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        //toolbar.setNavigationIcon(R.drawable.ic_action_name); //android: //src="@drawable/logo"
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorText2));
        setActionBar(toolbar);

        //--------KNAPPER--------
        btnTilbake = (Button) findViewById(R.id.btnTilbake);

        //--------LISTVIEW--------
        reservasjonerListView = (ListView) findViewById(R.id.list);


        //--------POPULERER BESTILLINGER LISTVIEWET--------
        //populerListView();


        //--------LISTENERS--------
        //KLIKK PÅ TILBAKE
        btnTilbake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (SeAlleReservasjoner.this, Kart.class);
                startActivity(intent);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------


        kjorJsonAlleReservasjoner();

    }//-------CREATE SLUTTER---------


    //--------POPULERER BESTILLINGER-LISTVIEWET--------
    private void populerListView() {

        //HENTER ALLE BESTILLINGER FRA DB OG LEGGER OVER I ARRAY
        final ArrayList<Reservasjon> HentReservasjoner = reservasjoner;

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        ArrayAdapter<Reservasjon> adapter = new ArrayAdapter<Reservasjon>(this, android.R.layout.simple_list_item_1, HentReservasjoner);
        reservasjonerListView.setAdapter(adapter);


        //VED KLIKK PÅ BESTILLING I LISTVIEWET
        reservasjonerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //GIR VALGTBESTILLING VERDIEN TIL VALGT OBJEKT FRA LISTVIEWET
                valgtReservasjon = (Reservasjon) reservasjonerListView.getItemAtPosition(i);

                //HENTER OG PARSER ID FRA BESTILLINGEN
                int ID = (int) valgtReservasjon.getId();
                String dato = (String) valgtReservasjon.getDato();
                String tidFra = (String) valgtReservasjon.getTidFra();
                String tidTil = (String) valgtReservasjon.getTidTil();
                String romNr = (String) valgtReservasjon.getRomNr();


                //LAGRER ID I MINNET - BENYTTES TIL I SEBESTILLINGSINFODIALOGFRAGMENT OG I MINSERVICE/NOTIFIKASJON FOR VISNING
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putInt("VISNINGSID", ID).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSDATO", dato).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSTIDFRA", tidFra).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSTIDTIL", tidTil).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSROMNR", romNr).apply();


                toastMessage(ID+""+valgtReservasjon.getRomNr());

                //INTENT TIL SEBESTILLINGSINFOFRAGMENT
                Intent intentet = new Intent(SeAlleReservasjoner.this, SeReservasjonsInfoFragment.class);
                startActivity(intentet);
                finish();
            }
        });
    }


    public void kjorJsonAlleReservasjoner(){
        //JSON GREIER
        //textView = (TextView) findViewById(R.id.jasontekst);
        getJSONHentReservasjoner task = new getJSONHentReservasjoner();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentReservasjoner.php"});
    }



    //METODER FOR Å HENTE JSONOBJEKTENE FRA URL  - Sette inn ArrayList HER?
    private class getJSONHentReservasjoner extends AsyncTask<String, Void, ArrayList<Reservasjon>> {
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

            populerListView();
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
                Intent intent = new Intent (SeAlleReservasjoner.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.SeAlleReservasjoner:
                Intent intent_preferanser = new Intent (SeAlleReservasjoner.this, SeAlleReservasjoner.class);
                startActivity(intent_preferanser);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


    //-------TILBAKE KNAPP - FORHINDRER STACK---------
    @Override
    public void onBackPressed() {
        Intent intent = new Intent (SeAlleReservasjoner.this, Kart.class);
        startActivity(intent);
        finish();
    }
}