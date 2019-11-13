package com.skole.s304114mappe3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skole.s304114mappe3.Dialog.RegistrerRomDialog;
import com.skole.s304114mappe3.Dialog.reserverRomDialog;
import com.skole.s304114mappe3.klasser.Reservasjon;
import com.skole.s304114mappe3.klasser.Rom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivityNy extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, reserverRomDialog.DialogClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    @Override
    public void bestillClick() {

    }

    @Override
    public void avbrytClick() {
        Toast.makeText(getApplicationContext(), "Avbrutt bestilling", Toast.LENGTH_LONG).show();
        return;
    }


    public static final String TAG = Kart.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    //spør om posisjon
    private LocationRequest mLocationRequest;



    private GoogleMap mMap;
    //ArrayList<LatLng> markorer = new ArrayList<LatLng>();
    ArrayList<Rom> markorerNy = new ArrayList<Rom>();

    ArrayList<Reservasjon> reservasjoner = new ArrayList<Reservasjon>();

    String valgtRomNr;

    // LatLng PH360 = new LatLng(59.919566, 10.734934);
    //LatLng PH373Koord = new LatLng(59.919458, 10.735091);
    //Rom PH373 = new Rom("PH373","Lite gruppe med 3 sitteplasser.",PH373Koord);


    //LatLng PH360Koord = new LatLng(59.919566, 10.734934);
    //Rom PH360 = new Rom("PH360",PH360Koord);
    //LatLng PH351 = new LatLng(59.919466, 10.734803);
    //LatLng N020117 = new LatLng(59.920152, 10.735870);

    //--------VERDIER--------
    String datoIdag;

    private ImageView logo;

    private Button reserverRom;

    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kart);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        //toolbar.setNavigationIcon(R.drawable.ic_action_name); //android: //src="@drawable/logo"
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorText2));
        setActionBar(toolbar);


        //GPS
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //HUSK GETMAPASYNC ER EN ASYNC OGSÅ, MÅ SEPARERE DENNE MED JSON HENTING ASYNC
        mapFragment.getMapAsync(this);



        //markorer.add(PH360);
        //markorerNy.add(PH373);
        //markorer.add(PH351);
        //markorer.add(N020117);

        //NYTT GPS
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //henter lokajon
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        //LOGO
        logo = findViewById(R.id.logo2);
        reserverRom = findViewById(R.id.reserverRom);


        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_startspill = new Intent(MainActivityNy.this, MainActivity.class);
                startActivity(intent_startspill);
                finish();
            }
        });

        reserverRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //visBestillingsinfo();

                //INTENT TIL SEBESTILLINGSINFOFRAGMENT
                Intent intent_preferanser = new Intent (MainActivityNy.this, ReserverRom.class);
                startActivity(intent_preferanser);
                finish();

            }
        });

        kjorJsonAlleRom();
        kjorJsonAlleReservasjoner();


    }//utenfor create


    /*--------OPPRETTER SEBESTILLINGSINFODIALOG--------
    private void visBestillingsinfo()  {

        //OPPRETTER NYTT DIALOGFRAGMENT
        reserverRomDialog rFragment = new reserverRomDialog();

        //OVERFØRER BESTILLINGSINFO TIL FRAGMENTET MED METODE FRA FRAGMENTET
        rFragment.hentInfo(valgtRomNr);

        //VISER DIALOGVINDUET
        rFragment.show(getSupportFragmentManager(), "Bestillingsinfo");
    }*/

    public void kjorJsonAlleRom(){
        //JSON GREIER
        //textView = (TextView) findViewById(R.id.jasontekst);
        getJsonAlleRom task = new getJsonAlleRom();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentRom.php"});
    }

    public void kjorJsonAlleReservasjoner(){
        //JSON GREIER
        //textView = (TextView) findViewById(R.id.jasontekst);
        getJsonAlleReservasjoner task = new getJsonAlleReservasjoner();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentReservasjoner.php"});
    }



    //OVERFØRER TITELEN TIL MARKØREN TIL MINNET FOR BRUK VED RESERVASJON
    @Override
    public boolean onMarkerClick(Marker marker) {
        valgtRomNr = marker.getTitle();

        //LAGRER ID I MINNET - BENYTTES TIL I SEBESTILLINGSINFODIALOGFRAGMENT OG I MINSERVICE/NOTIFIKASJON FOR VISNING
        getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("ROMNR", valgtRomNr).apply();

        /*final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(marker.getSnippet())
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();*/

        return false;
    }




    //METODER FOR Å HENTE JSONOBJEKTENE FRA URL  - Sette inn ArrayList HER?
    private class getJsonAlleRom extends AsyncTask<String, Void, ArrayList<Rom>> {
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
            markorerNy = jsonArray;

            for(int i = 0; i<markorerNy.size(); i++) {                                     //LEGGER INN ROMNR SOM DET SOM KOMMER VED TRYKK PÅ MARKØR



                /*--------HENTER DAGENS DATO I RIKTIG FORMAT TIL SAMMENLIGNING AV DET SOM LIGGER I DB--------
                Calendar c = Calendar.getInstance();
                int aar = c.get(Calendar.YEAR);
                int mnd = c.get(Calendar.MONTH);
                int dag = c.get(Calendar.DAY_OF_MONTH);

                mnd++;
                datoIdag = dag+"/"+mnd+"/"+aar;

                //HENTER DATO FRA BESTILLINGEN
                //String dato1 = reservasjon.getDato();


                //--------FORMATERER DATOENE FOR SAMMENLIGNING--------
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date dato2 = null;
                Date dato4 = null;

                try {
                    dato2 = sdf.parse(dato1);
                    dato4 = sdf.parse(datoIdag);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //--------SAMMENLIGNINGER AV FORMATERTE DATOER--------
                //HVIS DATO ER I DAG
                if((dato2.compareTo(dato4) == 0)) {}*/

                String info = "Se reservasjoner for i dag.";

                /*else {
                    String info = "Ingen reservasjoner i dag";
                }*/

                mMap.addMarker(new MarkerOptions().position(markorerNy.get(i).getLatLen()).title(markorerNy.get(i).getRomNr()).snippet(info));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(markorerNy.get(i).getLatLen()));
            }
        }
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

                            String dato = jsonobject.getString("dato");
                            String tidFra = jsonobject.getString("tidFra");
                            String tidTil = jsonobject.getString("tidTil");
                            String romNr = jsonobject.getString("romNr");


                            Reservasjon nyReservasjon = new Reservasjon(dato, tidFra, tidTil, romNr);

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



    //HERFRA OG NEDOVER NYTT GPS
    public void handleNewLocation(Location location) {

        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        //Manipulerer utgangspunktet for posisjonen
        LatLng latLng = new LatLng(59.919958, 10.735353); //currentLatitude, currentLongitude 59.919958, 10.735353

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Jeg er her!");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //endrer farge på min markør
        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 2000, null);
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(markorerNy.get(i).getLatLen()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        //Location location = locationManager.getLastKnownLocation(bestProvider);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            //handleNewLocation(location);

        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        handleNewLocation(location);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);

        mMap.setOnMarkerClickListener(this);
        //løkke gjennom koordinat arrayet og setter alle markørene på kartet



        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //mMap = googleMap;

    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTitle().equals("Jeg er her!")){
            //marker.hideInfoWindow();
        }
        else{
            String romNr = marker.getTitle();

            String reservasjonerTekst = "";

            for(Reservasjon r : reservasjoner) {
                if(romNr == r.getRomNr()) {

                    //--------HENTER DAGENS DATO I RIKTIG FORMAT TIL SAMMENLIGNING AV DET SOM LIGGER I DB--------
                    Calendar c = Calendar.getInstance();
                    int aar = c.get(Calendar.YEAR);
                    int mnd = c.get(Calendar.MONTH);
                    int dag = c.get(Calendar.DAY_OF_MONTH);

                    mnd++;
                    datoIdag = dag+"."+mnd+"."+aar;

                    //HENTER DATO FRA BESTILLINGEN
                    String dato1 = r.getDato();


                    //--------FORMATERER DATOENE FOR SAMMENLIGNING--------
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date dato2 = null;
                    Date dato4 = null;

                    try {
                        dato2 = sdf.parse(dato1);
                        dato4 = sdf.parse(datoIdag);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    //--------SAMMENLIGNINGER AV FORMATERTE DATOER--------
                    //HVIS DATO ER I DAG
                    if((dato2.compareTo(dato4) == 0)) {

                        reservasjonerTekst += "Kl: " + r.getTidFra() + "-" + r.getTidTil() + ".\n";
                    }
                }
            }

            if(reservasjonerTekst == ""){
                reservasjonerTekst = "Ingen aktive reservasjoner i dag.";
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reservasjoner i dag")
                    .setMessage(reservasjonerTekst)
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.dismiss();
                        }
                    });
                    /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });*/
            final AlertDialog alert = builder.create();
            alert.show();
        }
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
                Intent intent_startspill = new Intent (MainActivityNy.this, MainActivity.class);
                startActivity(intent_startspill);
                finish();
                break;
            case R.id.registrerRom:
                Intent intent_statistikk = new Intent (MainActivityNy.this, RegistrerRom.class);
                startActivity(intent_statistikk);
                finish();
                break;
            case R.id.reserverRom:
                Intent intent_preferanser = new Intent (MainActivityNy.this, ReserverRom.class);
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
        finish();
    }
}



/*package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    //--------KNAPPER-------
    Button seRomKnapp, registrerRomKnapp, reserverRomKnapp, innstillingerKnapp;

    ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        //toolbar.setNavigationIcon(R.drawable.ic_action_name); //android: //src="@drawable/logo"
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorText2));
        setActionBar(toolbar);


        logo = findViewById(R.id.logo2);

        //--------KNAPPER--------
        seRomKnapp = findViewById(R.id.seRomKnapp);
        registrerRomKnapp = findViewById(R.id.registrerRomKnapp);
        reserverRomKnapp = findViewById(R.id.reserverRomKnapp);
        innstillingerKnapp = findViewById(R.id.innstillingerKnapp);


        //--------LISTENERS--------
        //KLIKK RESTURANTER
        //--------LISTENERS--------
        //KLIKK RESTURANTER
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_startspill = new Intent (MainActivity.this, MainActivity.class);
                startActivity(intent_startspill);
                finish();
            }
        });


        seRomKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_startspill = new Intent (MainActivity.this, Kart.class);
                startActivity(intent_startspill);
                finish();
            }
        });

        //KLIKK VENNER
        registrerRomKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_statistikk = new Intent (MainActivity.this, RegistrerRom.class);
                startActivity(intent_statistikk);
            }
        });

        //KLIKK BESTILLINGER
        reserverRomKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_preferanser = new Intent (MainActivity.this, ReserverRom.class);
                startActivity(intent_preferanser);
                finish();
            }
        });

        //KLIKK INNSTILLINGER
        innstillingerKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent_preferanser = new Intent (MainActivity.this, NotifikasjonFragment.class);
                //startActivity(intent_preferanser);
            }
        });
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------


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
                Intent intent_startspill = new Intent (MainActivity.this, Kart.class);
                startActivity(intent_startspill);
                finish();
                break;
            case R.id.registrerRom:
                Intent intent_statistikk = new Intent (MainActivity.this, RegistrerRom.class);
                startActivity(intent_statistikk);
                break;
            case R.id.reserverRom:
                Intent intent_preferanser = new Intent (MainActivity.this, ReserverRom.class);
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
}*/
