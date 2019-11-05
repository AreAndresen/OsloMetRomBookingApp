package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




public class RegistrerRom extends AppCompatActivity {

    //--------KNAPPER--------
    private Button btnLeggTil, btnTilbake;

    //--------TEKST--------
    private EditText EnavnResturant;
    private EditText EtlfResturant;
    private EditText EtypeResturant;

    //--------DB HANDLER--------
    //DBhandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer_rom);

        //--------KNAPPER--------
        btnLeggTil = (Button) findViewById(R.id.btnLeggTil);
        btnTilbake = (Button) findViewById(R.id.btnTilbake);

        //--------INPUTS--------
        EnavnResturant = (EditText)findViewById(R.id.navnResturant);
        EtlfResturant = (EditText)findViewById(R.id.tlfResturant);
        EtypeResturant = (EditText)findViewById(R.id.typeResturant);

        //--------DB HANDLER--------
        //db = new DBhandler(this);


        //--------LISTENERS--------
        //KLIKK PÅ LEGG TIL
        btnLeggTil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FULLFØRER OPPRETTELSE AV NY RESTURANT
                fullforLeggTilResturant();
            }
        });

        //KLIKK PÅ TILBAKE
        btnTilbake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VIEW OPPDATERES FORTLØPENDE - FORHINDRER STACK
                Intent intent_tilbake = new Intent (RegistrerRom.this, MainActivity.class);
                startActivity(intent_tilbake);
                finish();
            }
        });
        //--------SLUTT LISTENERS--------

    }//-------CREATE SLUTTER---------



    //--------METODE FOR Å LEGGE TIL OPPRETTET RESTURANT--------
    private void fullforLeggTilResturant() {
        String navn = EnavnResturant.getText().toString();
        String tlf = EtlfResturant.getText().toString();
        String type = EtypeResturant.getText().toString();


        //INPUTVALIDERING
        if(!navn.equals("") && !tlf.equals("") && !type.equals("") && tlf.matches(
                "[0-9\\+\\-\\ ]{2,15}+") && navn.matches("[a-zA-ZæøåÆØÅ\\'\\-\\ \\.]{2,40}+")
                && type.matches("[a-zA-ZæøåÆØÅ0-9\\'\\-\\ \\.]{2,30}+")){


            //GENERERER OG LEGGER TIL NY RESTURANT I DB - TAR INN VERDIER TIL NY RESTURANT
            leggtil(navn, tlf, type);

        } else {
            //INFOMELDING UT - FEIL INPUT
            toastMessage("Alle felter må fylles ut og navn og telefonnummer må være på gyldig format");
        }
    }


    //--------METODE FOR OPPRETTE RESTURANT--------
    public void leggtil(String navn, String tlf, String type) {
        //OPPRETTER NYTT RESTURANT-OBJEKT
        // Resturant nyResturant = new Resturant(navn, tlf, type);

        //LEGGER TIL NY RESTURANT I DB
        //db.leggTilResturant(nyResturant);

        //NULLSTILLER INPUT
        EnavnResturant.setText("");
        EtlfResturant.setText("");
        EtypeResturant.setText("");

        //INFOMELDING UT
        toastMessage("Resturant lagt til!");
        //MELDING TIL LOGG
        Log.d("Legg inn: ", "Resturant lagt til");

        //VIEW OPPDATERES FORTLØPENDE - FORHINDRER STACK
        Intent intent_tilbake = new Intent (RegistrerRom.this, MainActivity.class);
        startActivity(intent_tilbake);
        finish();
    }


    //-------TILBAKE KNAPP - FORHINDRER STACK---------
    @Override
    public void onBackPressed() {
        Intent intent_tilbake = new Intent (RegistrerRom.this, MainActivity.class);
        startActivity(intent_tilbake);
        finish();
    }


    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
