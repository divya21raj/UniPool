package garbagecollectors.com.snucabpool.activities;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import garbagecollectors.com.snucabpool.DatePickerFragment;
import garbagecollectors.com.snucabpool.GenLocation;
import garbagecollectors.com.snucabpool.R;
import garbagecollectors.com.snucabpool.TripEntry;

public class NewEntryActivity extends BaseActivity
{

    GenLocation source, destination;
    String time, sourceSet, destinationSet;
    String AM_PM ;
    Button buttonstartSetDialog,buttonChangeDate, buttonFinalSave;
    TextView text_source,text_destination,text_time;

    public static String date;

    private HashMap<Long, Float> map = new HashMap<>();          //HashMap contains entry_id(Long value) and lambda(Float value)

    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        source = null;
        destination = null;

        time = "";

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        text_source = (TextView)findViewById(R.id.searched_source);//Check
        text_source.setText("Select Pickup Point");
        text_destination = (TextView)findViewById(R.id.searched_destination);
        text_destination.setText("Select Drop Location");
        text_time = (TextView)findViewById(R.id.searched_time);

        buttonChangeDate = (Button)findViewById(R.id.SetTime);

        buttonChangeDate.setOnClickListener(v -> openTimePickerDialog(false));

        buttonstartSetDialog = (Button)findViewById(R.id.btnChangeDate);
        buttonstartSetDialog.setOnClickListener(v ->
        {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(),"Date Picker");
        });

        buttonFinalSave = (Button)findViewById(R.id.finalSave);
        buttonFinalSave.setOnClickListener(v ->
        {
            try
            {
                finalSave(v);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        });
    }

    private void openTimePickerDialog(boolean is24r)
    {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(
                NewEntryActivity.this,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Choose Time");
        timePickerDialog.show();

    }

    OnTimeSetListener onTimeSetListener
            = new OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            int HourOfDay=calSet.get(Calendar.HOUR_OF_DAY);
            String minOfDay= String.valueOf(calSet.get(Calendar.MINUTE));
            if(minOfDay.length()==1){
                minOfDay="0"+minOfDay;
            }

            if(hourOfDay < 12)
                AM_PM = "AM";
            else
                AM_PM = "PM";

            time=HourOfDay+":"+minOfDay+" "+AM_PM;
            text_time.setText(time);
        }
    };

    public void findSource(View view)
    {
        try
        {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, 1);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
        {
            Toast.makeText(this, "Google Play Service Error!", Toast.LENGTH_SHORT).show();
        }

    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            Place place = PlaceAutocomplete.getPlace(this, data);
            Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
            LatLng latLng;
            switch (requestCode)
            {

                case 1:
                    latLng = place.getLatLng();
                    source = new GenLocation(place.getName().toString(), place.getAddress().toString(), latLng.latitude, latLng.longitude);//check

                    sourceSet = (place.getName() + ",\n" +
                            place.getAddress() + "\n" + place.getPhoneNumber());//check
                    text_source.setText(sourceSet);
                    break;
                case 2:
                    latLng = place.getLatLng();
                    destination = new GenLocation(place.getName().toString(), place.getAddress().toString(), latLng.latitude, latLng.longitude);//check

                    destinationSet = (place.getName() + ",\n" +
                            place.getAddress() + "\n" + place.getPhoneNumber());//check
                    text_destination.setText(destinationSet);
                    break;
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
        {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            Log.e("Tag", status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED)
        {
            // The user canceled the operation.
        }
    }

    public void findDestination(View view)
    {
        try
        {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, 2);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
        {
            Toast.makeText(this, "Google Play Service Error!", Toast.LENGTH_SHORT).show();
        }
    }

    public void finalSave(View view) throws ParseException
    {
        switch (checkInvalidEntry())
        {
            case 0:
                String entryId = entryDatabaseReference.push().getKey();
                String name= currentUser.getDisplayName();

                //initialise lambda map for this tripEntry here!!!
                TripEntry tripEntry = new TripEntry(name,entryId, currentUser.getUid(), time, date, source, destination, null);

                finalCurrentUser.getUserTripEntries().add(tripEntry);

                entryDatabaseReference.child(entryId).setValue(tripEntry);

                userDatabaseReference.child(finalCurrentUser.getUserId()).child("userTripEntries").setValue(finalCurrentUser.getUserTripEntries());

                Toast.makeText(this, "TripEntry created!", Toast.LENGTH_SHORT).show();

                finish();
                startActivity(new Intent(this, HomeActivity.class));
                break;

            case 1:
                Toast.makeText(this, "The pickup point and drop location can't be the same, silly!", Toast.LENGTH_SHORT).show();
                break;

            case 2:
                Toast.makeText(this, "The devs are still working on time travel...", Toast.LENGTH_SHORT).show();
                break;

            case 3:
                Toast.makeText(this, "Fill in all the details!", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private int checkInvalidEntry() throws ParseException
    {
        int flag = 0;

        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy.HH:mm");
        Date currentTime = new Date();
        Date inputTime = null;

        if(date!=null)
            inputTime = parser.parse(date + "." + time);

        if((time.isEmpty()||source == null||destination == null))
            flag = 3;

        else if((source.getLatitude().compareTo(destination.getLatitude()) == 0) && (source.getLongitude().compareTo(destination.getLongitude()) == 0))
            flag = 1;

        else if(date == null)
            flag = 2;

        else if(inputTime.before(currentTime))
            flag = 2;

        return flag;
    }

    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_new_entry;
    }

    @Override
    protected int getNavigationMenuItemId()
    {
        return R.id.navigation_newEntry;
    }

}
