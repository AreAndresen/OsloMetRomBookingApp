package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //--------KNAPPER-------
    Button seRomKnapp, registrerRomKnapp, reserverRomKnapp, innstillingerKnapp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //--------KNAPPER--------
        seRomKnapp = findViewById(R.id.seRomKnapp);
        registrerRomKnapp = findViewById(R.id.registrerRomKnapp);
        reserverRomKnapp = findViewById(R.id.reserverRomKnapp);
        innstillingerKnapp = findViewById(R.id.innstillingerKnapp);


        //--------LISTENERS--------
        //KLIKK RESTURANTER
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
}
