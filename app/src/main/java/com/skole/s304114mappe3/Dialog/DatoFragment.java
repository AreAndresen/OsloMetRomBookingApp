package com.skole.s304114mappe3.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;


public class DatoFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //--------HENTER DAGENS DATO--------
        Calendar c = Calendar.getInstance();
        int aar = c.get(Calendar.YEAR);
        int mnd = c.get(Calendar.MONTH);
        int dag = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), aar, mnd, dag);
    }
}