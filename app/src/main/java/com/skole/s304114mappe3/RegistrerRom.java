package com.skole.s304114mappe3;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;


public class RegistrerRom extends AppCompatActivity {

    //--------KNAPPER--------
    private Button btnLeggTil, btnTilbake;

    //--------TEKST--------
    private EditText EnavnResturant;
    private EditText EtlfResturant;
    private EditText EtypeResturant;

    //--------DB HANDLER--------
    //DBhandler db;

    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer_rom);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.minmeny);
        //toolbar.setNavigationIcon(R.drawable.ic_action_name); //android: //src="@drawable/logo"
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorText2));
        setActionBar(toolbar);


        logo = findViewById(R.id.logo2);

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
                Intent intent = new Intent (RegistrerRom.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.registrerRom:
                Intent intent_statistikk = new Intent (RegistrerRom.this, RegistrerRom.class);
                startActivity(intent_statistikk);
                break;
            case R.id.reserverRom:
                Intent intent_preferanser = new Intent (RegistrerRom.this, ReserverRom.class);
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
        Intent intent_tilbake = new Intent (RegistrerRom.this, MainActivity.class);
        startActivity(intent_tilbake);
        finish();
    }


    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
