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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.skole.s304114mappe3.Dialog.SeReservasjonsInfoFragment;
import com.skole.s304114mappe3.Kart;
import com.skole.s304114mappe3.MainActivity;
import com.skole.s304114mappe3.R;
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
    private ImageView logoItoolBar;

    //--------LISTVIEW--------
    private ListView reservasjonerListView;

    //--------OBJEKT--------
    private Reservasjon valgtReservasjon;

    ArrayList<Reservasjon> reservasjoner = new ArrayList<Reservasjon>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_se_alle_reservasjoner);

        //--------TOOLBAR--------
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        setActionBar(toolbar);


        //--------KNAPPER--------
        btnTilbake = (Button) findViewById(R.id.btnTilbake);


        //--------LOGO - FUNGERER SOM KNAPP--------
        logoItoolBar = findViewById(R.id.logo2);


        //--------LISTVIEW--------
        reservasjonerListView = (ListView) findViewById(R.id.list);


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

        //KLIKK LOGO I TOOLBAR
        logoItoolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentS = new Intent (SeAlleReservasjoner.this, MainActivity.class);
                startActivity(intentS);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------


        //--------HENTER ALLE RESERVASJONER FRA WEBSERVICE--------
        kjorJsonAlleReservasjoner();

    }//-------CREATE SLUTTER---------



    //--------POPULERER RESERVASJONER-LISTVIEWET--------
    private void populerListView() {

        //HENTER ALLE RESERVASJONER HENTET FRA WEBSERVICE LEGGER OVER I ARRAY
        final ArrayList<Reservasjon> HentReservasjoner = reservasjoner;

        //GENERERER ARRAYADAPTER TIL LISTVIEWET
        ArrayAdapter<Reservasjon> adapter = new ArrayAdapter<Reservasjon>(this, android.R.layout.simple_list_item_1, HentReservasjoner);
        reservasjonerListView.setAdapter(adapter);


        //VED KLIKK PÅ RESERVASJON I LISTVIEWET
        reservasjonerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //GIR VALGTRESERVASJON VERDIEN TIL VALGT OBJEKT FRA LISTVIEWET
                valgtReservasjon = (Reservasjon) reservasjonerListView.getItemAtPosition(i);

                //HENTER OG PARSER VERDIER FRA RESERVASJONEN
                int ID = (int) valgtReservasjon.getId();
                String dato = (String) valgtReservasjon.getDato();
                String tidFra = (String) valgtReservasjon.getTidFra();
                String tidTil = (String) valgtReservasjon.getTidTil();
                String romNr = (String) valgtReservasjon.getRomNr();


                //LAGRER VERDIER I MINNET - BENYTTES TIL I SERESERVASJONINFODIALOGFRAGMENT FOR VISNING
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putInt("VISNINGSID", ID).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSDATO", dato).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSTIDFRA", tidFra).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSTIDTIL", tidTil).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("VISNINGSROMNR", romNr).apply();


                toastMessage(ID+""+valgtReservasjon.getRomNr());

                //GÅR TIL SERESERVASJONSINFO-FRAGMENTET ETTER TRYKK PÅ OBJEKT FRA LISTEN
                Intent intentet = new Intent(SeAlleReservasjoner.this, SeReservasjonsInfoFragment.class);
                startActivity(intentet);
                finish();
            }
        });
    }


    //--------HENTER ALLE RESERVASJONER--------
    public void kjorJsonAlleReservasjoner(){

        getJSONHentReservasjoner task = new getJSONHentReservasjoner();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentReservasjoner.php"});
    }


    //--------HENTER ALLE RESERVASJONER SOM JSONOBJEKTER FRA WEBSERVICE--------
    private class getJSONHentReservasjoner extends AsyncTask<String, Void, ArrayList<Reservasjon>> {
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

            //--------POPULERER LISTVIWET ETTER ASYN ER UTFØRT--------
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