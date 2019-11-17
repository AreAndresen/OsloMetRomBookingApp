package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;
import com.skole.s304114mappe3.ListView.SeAlleReservasjoner;


public class MainActivity extends AppCompatActivity {

    //--------KNAPPER-------
    Button pilestredetKnapp, kjellerKnapp;

    private ImageView logoItoolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--------TOOLBAR--------
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        setActionBar(toolbar);


        //--------KNAPPER--------
        pilestredetKnapp = findViewById(R.id.pilestredetKnapp);
        kjellerKnapp = findViewById(R.id.kjellerKnapp);

        //--------LOGO - FUNGERER SOM KNAPP--------
        logoItoolBar = findViewById(R.id.logo2);


        //--------LISTENERS--------
        //KLIKK PILESTREDET
        pilestredetKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //KOORDINATER TIL Pilestredet
                String sLat = "59.919958";
                String sLen = "10.735353";

                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLAT", sLat).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLEN", sLen).apply();

                Intent intentP = new Intent (MainActivity.this, Kart.class);
                startActivity(intentP);
                finish();
            }
        });

        //KLIKK KJELLER
        kjellerKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //KOORDINATER TIL KJELLER
                String sLat = "59.976427";
                String sLen = "11.044555";

                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLAT", sLat).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLEN", sLen).apply();

                Intent intentK = new Intent (MainActivity.this, Kart.class);
                startActivity(intentK);
                finish();
            }
        });

        //KLIKK LOGO I TOOLBAR
        logoItoolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentS = new Intent (MainActivity.this, MainActivity.class);
                startActivity(intentS);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------



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
                Intent intentS = new Intent (MainActivity.this, MainActivity.class);
                startActivity(intentS);
                finish();
                break;
            case R.id.SeAlleReservasjoner:
                Intent intentSe = new Intent (MainActivity.this, SeAlleReservasjoner.class);
                startActivity(intentSe);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    //-------TILBAKE KNAPP - LUKKER APP---------
    @Override
    public void onBackPressed() {
        finish();
    }
}