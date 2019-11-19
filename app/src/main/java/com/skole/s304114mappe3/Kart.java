package com.skole.s304114mappe3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.DialogFragment;
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
import android.widget.ImageView;
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
import com.skole.s304114mappe3.Dialog.SeReservasjonerDialog;
import com.skole.s304114mappe3.Dialog.SlettRomDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Kart extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        SeReservasjonerDialog.DialogClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnInfoWindowLongClickListener,
        SlettRomDialog.DialogClickListener {


    //--------DIALOG ETTER LONGCLICK PÅ MARKØR/MESSAGE VED SLETTING AV ROM--------
    //KLIKK PÅ JA
    @Override
    public void jaClickRom() {
        //SLETTER ROMMET
        webSlettRom();

        //SLETTER TILHØRENDE RESERVASJONER TIL ROM
        webSlettTilhorendeReservasjoner();

        //TILBAKE TIL KART
        Intent intent = new Intent (Kart.this, Kart.class);
        startActivity(intent);
        finish();
        return;
    }
    //KLIKK PÅ NEI
    @Override
    public void neiClickRom() {
        return;
    }


    //--------DIALOG ETTER CLICK PÅ MARKØR - VALG FOR Å RESERVERE ROM--------
    //KLIKK PÅ RESERVER
    @Override
    public void reserverClick() {
        Intent intent = new Intent (Kart.this, ReserverRom.class);
        startActivity(intent);
        finish();
    }
    //KLIKK PÅ AVBRYT
    @Override
    public void avbrytClick() {
        return;
    }


    //--------GOOGLEMAP--------
    public static final String TAG = Kart.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;

    //--------ARRAYS--------
    ArrayList<Rom> markorerNy = new ArrayList<Rom>();
    ArrayList<Reservasjon> reservasjoner = new ArrayList<Reservasjon>();

    //--------VERDIER--------
    String datoIdag, valgtRomNr;

    //--------KNAPP - LOGO--------
    private ImageView logoItoolBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kart);

        //--------TOOLBAR--------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        setActionBar(toolbar);


        //-------HENTER KARTFRAGMENT---------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //-------GOOGLE API---------
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        //-------HENTER LOKASJON TIL GPS---------
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                //ENDRET OPPDATERINGER FOR KART ZOOM
                .setInterval(60 * 10000)
                .setFastestInterval(60 * 10000);


        //--------LOGO - FUNGERER SOM KNAPP--------
        logoItoolBar = findViewById(R.id.logo2);


        //--------LISTENERS--------
        //KLIKK LOGO I TOOLBAR
        logoItoolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Kart.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------



    //--------ZOOMER TIL STEDET VALGT I MAIN--------
    public void zoomTilSted() {

        //HENTER KOORDINATENE FRA MINNET
        Double lat = Double.parseDouble(getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("STEDLAT", ""));
        Double len = Double.parseDouble(getSharedPreferences("APP_INFO",MODE_PRIVATE).getString("STEDLEN", ""));

        LatLng latLng = new LatLng(lat, len);

        //BESTEMMER HØYDE OG HASTIGHET PÅ ZOOMET
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17), 1500, null);
    }


    //--------ASYNC NR 1 (ETTER ASYNC MAP) - HENTER ALLE ROM--------
    public void kjorJsonAlleRom(){

        getJsonAlleRom task = new getJsonAlleRom();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentRom.php"});
    }


    //--------ASYNC NR 2 - HENTER ALLE RESERVASJONER--------
    public void kjorJsonAlleReservasjoner(){

        getJsonAlleReservasjoner task = new getJsonAlleReservasjoner();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentReservasjoner.php"});
    }


    //--------HENTER ALLE ROM SOM JSONOBJEKTER FRA WEBSERVICE--------
    private class getJsonAlleRom extends AsyncTask<String, Void, ArrayList<Rom>> {
        JSONObject jsonObject;
        ArrayList<Rom> jsonArray = new ArrayList<>();

        @Override
        protected ArrayList<Rom> doInBackground(String... urls) {

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

                            //HENTER ALLE ROM OG OVERFØRER VERDIENE
                            String romNr = jsonobject.getString("romNr");
                            String bygg = jsonobject.getString("bygg");
                            String antSitteplasser = jsonobject.getString("antSitteplasser");
                            String lat = jsonobject.getString("lat");
                            String len = jsonobject.getString("len");

                            //PARSER KOORDINATENE TIL DOUBLE
                            Double latD = Double.parseDouble(lat);
                            Double lenD = Double.parseDouble(len);

                            //PRODUSERER KOORDINATENE TIL LATLNG
                            LatLng koordinater = new LatLng(latD, lenD);

                            //OPPRETTER NYTT ROM
                            Rom nyttRom = new Rom(romNr, bygg, antSitteplasser, koordinater);

                            //OVERFØRER ALLE ROM TIL JSONARRAY-ARRAYET
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
                    e.printStackTrace();
                }
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Rom> jsonArray) {
            markorerNy = jsonArray;

            //STARTER ASYNC NR 2 HER FOR HENTING ALLE RESERVASJONER
            kjorJsonAlleReservasjoner();

            //LØKKE GJENNOM JSONARRAY-ARRAYET
            for(int i = 0; i<markorerNy.size(); i++) {

                //MELDING PÅ MARKØRER
                String info = "Trykk for å reservere eller se dagens reservasjoner.";

                //PLASSERER ALLE ROM SOM MARKØRER PÅ KARTET - MED TITTEL(ROMNR) OG INFO
                mMap.addMarker(new MarkerOptions().position(markorerNy.get(i).getLatLen()).title(markorerNy.get(i).getRomNr()).snippet(info));
            }
        }
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



    //--------CLICK PÅ MARKØR - SE RESERVASJONER/RESERVER--------
    @Override
    public boolean onMarkerClick(Marker marker) {

        //OVERFØRER TITELEN TIL MARKØREN TIL MINNET
        valgtRomNr = marker.getTitle();

        //LAGRER ID I MINNET - BENYTTES I RESERVASJON OG SEBESTILLINGSINFODIALOG FRAGMENT
        getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("ROMNR", valgtRomNr).apply();

        return false;
    }


    //--------LONGCLICK PÅ INFOVINDUET TIL MARKØREN FOR ROM - SLETTING AV ROMMET--------
    @Override
    public void onInfoWindowLongClick(Marker marker) {
        valgtRomNr = marker.getTitle();

        //STARTER OPP EN DIALOGBOKS FOR EVT SLETTING AV ROM
        DialogFragment dialog = new SlettRomDialog();
        dialog.show(getFragmentManager(), "Slett");
    }


    //--------KLIKK PÅ KART - REGISTRERING AV ROM PÅ POSISJONEN--------
    @Override
    public void onMapClick(LatLng latLng) {

        String lat = latLng.latitude+"";
        String len = latLng.longitude+"";

        //KLIKK PÅ KART LEDER TIL REGISTRER ROM ACTIVITY MED KOORDINATER FOR TRYKKET PLASSERING
        Intent intent = new Intent (Kart.this, RegistrerRom.class);
        intent.putExtra("LAT",lat);
        intent.putExtra("LEN",len);
        startActivity(intent);
        finish();
    }


    //--------GPS - HÅNDTERER ENDRING AV PLASSERING--------
    public void handleNewLocation(Location location) {

        Log.d(TAG, location.toString());

        //VILLE BENYTTET DISSE KOORDINATENE OPPRINNELIG FOR GPS OG ENDRING
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        //ETTERSOM EMULATOR ER I USA MANIPULERES POSISJON TIL Å VÆRE VED PILESTREDET P32
        LatLng latLng = new LatLng(59.919958, 10.735353); //currentLatitude, currentLongitude 59.919958, 10.735353

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Jeg er her!");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //endrer farge på min markør
        mMap.addMarker(options);

        //MÅ LEGGE TILBAKE DENNE FOR Å FÅ ZOOM TIL POSISJONEN MIN
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 2000, null);

        //ZOOMER TIL VALGT STED FRA MAIN
        zoomTilSted();

    }


    //--------HÅNDTERER PLASSERINGER OG RETTIGHETER--------
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

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            handleNewLocation(location);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");

    }


    //--------HÅNDTERER FEIL VED PLASSERINGER OG KART--------
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
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

    //--------HÅNDTERER ENDRING AV PLASSERING--------
    @Override
    public void onLocationChanged(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        handleNewLocation(location);

    }


    //--------AKTIVERES NÅR ASYNC FOR KART ER FULLFØRT OG KLART TIL BRUK--------
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //FORSKJELLIGE ONCLICK-LISTERNERS TIL KARTET
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        //MÅ HA DENNE I TILFELLE BRUKER IKKE HAR SATT PÅ GPS - FOR Å SØKE TIL VALGT STED (DA UTEN LOCATION CHANGE)
        zoomTilSted();

        //NÅR ASYNC FOR KART ER FERDIG UTFØRES ASYNC FOR Å HENTE ALLE ROM - MAKØRER
        kjorJsonAlleRom();
    }


    //--------VED KLIKK PÅ INFOVINDUET TIL MARKØREN--------
    @Override
    public void onInfoWindowClick(Marker marker) {

        //UTFORMER RESERVASJONSMELDING
        if(marker.getTitle().equals("Jeg er her!")){
            //marker.hideInfoWindow();
        }
        else{
            String romNr = marker.getTitle();

            String reservasjonerTekst = "";

            //HENTER DAGENS DATO I RIKTIG FORMAT TIL SAMMENLIGNING
            Calendar c = Calendar.getInstance();
            int aar = c.get(Calendar.YEAR);
            int mnd = c.get(Calendar.MONTH);
            int dag = c.get(Calendar.DAY_OF_MONTH);

            mnd++;
            datoIdag = dag+"."+mnd+"."+aar;


            for(Reservasjon r : reservasjoner) {
                //HENTER ROMNR
                if(r.getRomNr().equals(romNr)) {

                    //HENTER DATO FRA RESERVASJONEN
                    String dato1 = r.getDato();

                    //--------FORMATERER DATOENE FOR SAMMENLIGNING--------
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date dato2 = null;
                    Date dato4 = null;

                    try {
                        dato2 = sdf.parse(dato1);
                        dato4 = sdf.parse(datoIdag);

                        //--------SAMMENLIGNINGER AV FORMATERTE DATOER--------
                        //HVIS DATO ER I DAG - HENTER EVT RESERVASJONER
                        if((dato2.compareTo(dato4) == 0)) {

                            reservasjonerTekst += "Kl: " + r.getTidFra() + "-" + r.getTidTil() + ".\n";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }

            //HVIS INGEN MELDING ER UTFORMET ETTR LØKKEN
            if(reservasjonerTekst.equals("")){
                reservasjonerTekst = "Ingen aktive reservasjoner i dag.";
            }

            //METODE SOM AKTIVERER DIALOG FOR VISNING AV RESERVASJONER OG KNAPPER (RESERVASJON)
            visReservasjonssinfo(reservasjonerTekst);

        }
    }

    //--------OPPRETTER SEBESTILLINGSINFODIALOG--------
    private void visReservasjonssinfo(String reservasjoner)  {

        //OPPRETTER NYTT DIALOGFRAGMENT
        SeReservasjonerDialog rFragment = new SeReservasjonerDialog();

        //OVERFØRER BESTILLINGSINFO TIL FRAGMENTET MED METODE FRA FRAGMENTET
        rFragment.hentReservasjoner(reservasjoner);

        //VISER DIALOGVINDUET
        rFragment.show(getFragmentManager(), "Reservasjoner");
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


    //--------UTFORMING AV URL OG KJØRING AV DENNE - SLETT ROM--------
    public void webSlettRom() {

        LastSide task = new LastSide();

        String url = "http://student.cs.hioa.no/~s304114/SlettRom.php/?romNr="+valgtRomNr;

        task.execute(new String[]{url});
    }


    //--------UTFORMING AV URL OG KJØRING AV DENNE - SLETT RESERVASJONER--------
    public void webSlettTilhorendeReservasjoner() {

        LastSide task = new LastSide();

        String url = "http://student.cs.hioa.no/~s304114/SlettTilhorendeReservasjoner.php/?romNr="+valgtRomNr;

        task.execute(new String[]{url});
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
                Intent intentS = new Intent (Kart.this, MainActivity.class);
                startActivity(intentS);
                finish();
                break;
            case R.id.SeAlleReservasjoner:
                Intent intentSe = new Intent (Kart.this, SeAlleReservasjoner.class);
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
        Intent intent = new Intent (Kart.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}