package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    //--------KNAPPER-------
    Button resturanterKnapp, vennerKnapp, bestillingKnapp, innstillingerKnapp;

    private ImageView logo;


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
        resturanterKnapp = findViewById(R.id.seRomKnapp);
        vennerKnapp = findViewById(R.id.registrerRomKnapp);
        //bestillingKnapp = findViewById(R.id.reserverRomKnapp);
        //innstillingerKnapp = findViewById(R.id.innstillingerKnapp);


        //--------LISTENERS--------
        //KLIKK RESTURANTER
        resturanterKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //koordinater til Pilestredet
                String sLat = "59.919958";
                String sLen = "10.735353";

                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLAT", sLat).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLEN", sLen).apply();

                Intent intent_startspill = new Intent (MainActivity.this, MainActivityNy.class);
                startActivity(intent_startspill);
                finish();
            }
        });

        //KLIKK VENNER
        vennerKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //koordinater til kjeller
                String sLat = "59.976427";
                String sLen = "11.044555";

                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLAT", sLat).apply();
                getSharedPreferences("APP_INFO",MODE_PRIVATE).edit().putString("STEDLEN", sLen).apply();

                Intent intent_statistikk = new Intent (MainActivity.this, MainActivityNy.class);
                startActivity(intent_statistikk);
                finish();
            }
        });

        /*KLIKK BESTILLINGER
        bestillingKnapp.setOnClickListener(new View.OnClickListener() {
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
                Intent intent_preferanser = new Intent (MainActivity.this, MainActivityNy.class);
                startActivity(intent_preferanser);
            }
        });*/
        //--------SLUTT LISTENERS--------

        //LOGO
        logo = findViewById(R.id.logo2);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_startspill = new Intent (MainActivity.this, MainActivity.class);
                startActivity(intent_startspill);
                finish();
            }
        });

    }//-------CREATE SLUTTER---------
}