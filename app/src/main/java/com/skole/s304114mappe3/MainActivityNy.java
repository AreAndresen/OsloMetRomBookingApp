package com.skole.s304114mappe3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.skole.s304114mappe3.Dialog.RegistrerRomDialog;
import com.skole.s304114mappe3.klasser.Rom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityNy extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, RegistrerRomDialog.DialogClickListener {

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
    private LocationRequest mLocationRequest;



    private GoogleMap mMap;
    ArrayList<LatLng> markorer = new ArrayList<LatLng>();
    ArrayList<Rom> markorerNy = new ArrayList<Rom>();

    // LatLng PH360 = new LatLng(59.919566, 10.734934);
    //LatLng PH373Koord = new LatLng(59.919458, 10.735091);
    //Rom PH373 = new Rom("PH373","Lite gruppe med 3 sitteplasser.",PH373Koord);


    //LatLng PH360Koord = new LatLng(59.919566, 10.734934);
    //Rom PH360 = new Rom("PH360",PH360Koord);
    //LatLng PH351 = new LatLng(59.919466, 10.734803);
    //LatLng N020117 = new LatLng(59.920152, 10.735870);


    private ImageView logo;

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
        mapFragment.getMapAsync(this);


        //JSON GREIER
        //textView = (TextView) findViewById(R.id.jasontekst);
        getJSON task = new getJSON();
        task.execute(new String[]{"http://student.cs.hioa.no/~s304114/HentRom.php"});

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

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        //LOGO
        logo = findViewById(R.id.logo2);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_startspill = new Intent(MainActivityNy.this, MainActivity.class);
                startActivity(intent_startspill);
                finish();
            }
        });


    }




    //METODER FOR Å HENTE JSONOBJEKTENE FRA URL
    private class getJSON extends AsyncTask<String, Void,String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
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

                            String romNr = jsonobject.getString("romNr");
                            String beskrivelse = jsonobject.getString("beskrivelse");
                            String lat = jsonobject.getString("lat");
                            String len = jsonobject.getString("len");
                            retur = retur +beskrivelse+": "+lat+ " "+len+"\n";

                            Double latD = Double.parseDouble(lat);
                            Double lenD = Double.parseDouble(len);

                            LatLng koordinater = new LatLng(latD, lenD);

                            Rom nyttRom = new Rom(romNr, beskrivelse, koordinater);

                            //Nye koordinater
                            //LatLng koordinater = new LatLng(latD, lenD);
                            //markorer.add(koordinater);

                            markorerNy.add(nyttRom);
                        }
                        return retur;
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                    }
                    return retur;
                }
                catch(Exception e) {
                    return"Noe gikk feil";
                }
            }
            return retur;
        }



        @Override
        protected void onPostExecute(String ss) {
            //markorerNy.add(PH360);

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
        //løkke gjennom koordinat arrayet og setter alle markørene på kartet
        for(int i = 0; i<markorerNy.size(); i++) {                                     //LEGGER INN ROMNR SOM DET SOM KOMMER VED TRYKK PÅ MARKØR
            mMap.addMarker(new MarkerOptions().position(markorerNy.get(i).getLatLen()).title(markorerNy.get(i).getRomNr()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markorerNy.get(i).getLatLen()));
        }


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //mMap = googleMap;

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
                //Intent intent_startspill = new Intent (MainActivity.this, Kart.class);
                //startActivity(intent_startspill);
                //finish();
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
