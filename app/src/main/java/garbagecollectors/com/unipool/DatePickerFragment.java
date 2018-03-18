package garbagecollectors.com.unipool;

/**
 * Created by SIMRAN on 22-11-2017.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import garbagecollectors.com.unipool.activities.NewEntryActivity;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    String stringOfDate;

    public DatePickerFragment()
    {}

    int year_now,month_now,day_now;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        setDateNow(year,month,day);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    private void setDateNow(int year, int month, int day)
    {
        this.day_now=day;
        this.month_now=month;
        this.year_now=year;
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        //Do something with the date chosen by the user
        EditText setDate = getActivity().findViewById(R.id.setDateEditText);
        if((year==year_now && month==month_now && day>=day_now)|| (year==year_now && month>month_now) ||
                (year>year_now))
        {
            stringOfDate = day + "/" + (++month) + "/" + year;

            setDate.setText(stringOfDate);
            NewEntryActivity.date=stringOfDate;
        }
        else
            setDate.setText("Invalid Date");

    }
}
