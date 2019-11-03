package com.skole.s304114mappe3;



import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //--------KNAPPER-------
    Button resturanterKnapp, vennerKnapp, bestillingKnapp, innstillingerKnapp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //--------KNAPPER--------
        resturanterKnapp = findViewById(R.id.resturanterKnapp);
        vennerKnapp = findViewById(R.id.vennerKnapp);
        bestillingKnapp = findViewById(R.id.bestillingerKnapp);
        innstillingerKnapp = findViewById(R.id.innstillingerKnapp);


        //--------LISTENERS--------
        //KLIKK RESTURANTER
        resturanterKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent_startspill = new Intent (MainActivity.this, Resturanter.class);
                //startActivity(intent_startspill);
                finish();
            }
        });

        //KLIKK VENNER
        vennerKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent_statistikk = new Intent (MainActivity.this, Venner.class);
                //startActivity(intent_statistikk);
            }
        });

        //KLIKK BESTILLINGER
        bestillingKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent_preferanser = new Intent (MainActivity.this, Bestillinger.class);
                //startActivity(intent_preferanser);
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
