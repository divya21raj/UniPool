package garbagecollectors.com.snucabpool;

/**
 * Created by SIMRAN on 22-11-2017.
 */
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.DatePicker;
import android.app.Dialog;
import java.util.Calendar;

import garbagecollectors.com.snucabpool.activities.NewEntryActivity;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    String stringOfDate;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Create a new DatePickerDialog instance and return it
        /*
            DatePickerDialog Public Constructors - Here we uses first one
            public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
            public DatePickerDialog (Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
         */
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Do something with the date chosen by the user
        TextView text_date = (TextView) getActivity().findViewById(R.id.searched_date);
        String stringOfDate = day + "/" + month + "/" + year;
        NewEntryActivity.date=stringOfDate;
        text_date.setText(NewEntryActivity.date);
    }
}
