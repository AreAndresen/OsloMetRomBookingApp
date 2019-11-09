package com.skole.s304114mappe3;

public class divSQL {

    /*--------STRINGER MED SQL FOR Å GENERERE TABELLER --------
    //--------RESTURANT TABLE--------
    static String TABLE_RESTURANTER = "Resturant";
    static String KEY_ID = "_ID";
    static String KEY_NAME = "Navn";
    static String KEY_PH_NO = "Telefon";
    static String KEY_TYPE = "Type";

    //--------VENNER TABLE--------
    static String TABLE_VENNER = "Venner";
    static String VENN_ID = "ID";
    static String VENN_NAME = "Navnet";
    static String VENN_TLF = "Tlf";

    //--------BESTILLINGER TABLE--------
    static String TABLE_BESTILLINGER = "Bestillinger";
    static String BESTILLING_ID = "IDen";
    static String BESTILLING_DATO = "Dato";
    static String BESTILLING_TID = "Tidspunkt";
    static String BESTILLING_RESTURANTNAVN = "Resturantnavn";
    static String BESTILLING_RESTURANT = "Resturanten_ID";

    //--------DELTAKELSE TABLE--------
    static String TABLE_DELTAKELSE = "Deltakelse";
    static String DELTAKELSE_ID = "Deltakelse_id";
    static String DTK_BESTILLING_ID = "dtk_bestilling_ID";
    static String DTK_PERSON_ID = "dtk_person_id";
    static String DTK_PERSON_NAVN = "dtk_person_navn";

    static int DATABASE_VERSION = 1;
    static String DATABASE_NAME = "Resturanter";


    //--------STRINGER MED SQL FOR Å GENERERE TABELLER --------
    String LAG_RESTURANTER = "CREATE TABLE " + TABLE_RESTURANTER + "(" + KEY_ID +
            " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PH_NO + " TEXT," + KEY_TYPE + " TEXT" + ")";

    String LAG_VENNER = "CREATE TABLE " + TABLE_VENNER + "(" + VENN_ID +
            " INTEGER PRIMARY KEY," + VENN_NAME + " TEXT," + VENN_TLF + " TEXT" + ")";

    String LAG_BESTILLINGER = "CREATE TABLE " + TABLE_BESTILLINGER + "(" + BESTILLING_ID +
            " INTEGER PRIMARY KEY," + BESTILLING_DATO + " TEXT," + BESTILLING_TID + " TEXT," + BESTILLING_RESTURANTNAVN + " TEXT," + BESTILLING_RESTURANT + " " +
            " INTEGER, FOREIGN KEY(Resturanten_ID) REFERENCES TABLE_RESTURANTER (KEY_ID)" + ")";

    String LAG_DELTAKELSE = "CREATE TABLE " + TABLE_DELTAKELSE + "(" + DELTAKELSE_ID +
            " INTEGER PRIMARY KEY," + DTK_BESTILLING_ID + " INTEGER, " + DTK_PERSON_ID + " INTEGER, " + DTK_PERSON_NAVN + " "+
            " TEXT, FOREIGN KEY(dtk_bestilling_ID) REFERENCES TABLE_BESTILLINGER (BESTILLING_ID)," +
            " FOREIGN KEY(dtk_person_id) REFERENCES TABLE_VENNER (VENN_ID)" + ")";


    public DBhandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


     */
}
