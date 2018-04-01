package garbagecollectors.com.unipool.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import garbagecollectors.com.unipool.DatePickerFragment;
import garbagecollectors.com.unipool.GenLocation;
import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.TripEntry;
import garbagecollectors.com.unipool.UtilityMethods;

public class NewEntryActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
                                                              GoogleApiClient.ConnectionCallbacks
{
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    GenLocation source, destination;
    String time, sourceSet, destinationSet;

    EditText findSource, findDestination, setTime, setDate;
    Button buttonFinalSave;

    public static String date;

    TimePickerDialog timePickerDialog;

    GoogleApiClient mGoogleApiClient;

    View currentLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = findViewById(R.id.new_entry_layout);

        navDrawerStateListener();

        navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(menuItem ->
        {
            dealWithSelectedMenuItem(menuItem);
            drawerLayout.closeDrawers();

            return true;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        source = null;
        destination = null;

        time = "";

        findSource = findViewById(R.id.findSourceEditText);
        findDestination = findViewById(R.id.findDestinationEditText);
        setTime = findViewById(R.id.setTimeEditText);
        setDate = findViewById(R.id.setDateEditText);

        setTime.setOnClickListener(view -> openTimePickerDialog(false));

        setDate.setOnClickListener(v ->
        {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "Date Picker");
        });

        buttonFinalSave = findViewById(R.id.finalSave);
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

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
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
            int HourOfDay = calSet.get(Calendar.HOUR_OF_DAY);
            String minOfDay = String.valueOf(calSet.get(Calendar.MINUTE));
            if (minOfDay.length() == 1)
            {
                minOfDay = "0" + minOfDay;
            }

            time = HourOfDay + ":" + minOfDay;

            setTime.setText(time);
        }
    };

    public void findSource(View view)
    {
        showPlaceAutocomplete(1);
    }

    public void findDestination(View view)
    {
        showPlaceAutocomplete(2);
    }

    private void showPlaceAutocomplete(int i)
    {
        try
        {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("IN")
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(autocompleteFilter)
                            .build(this);
            startActivityForResult(intent, i);

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
                    source = new GenLocation(place.getName().toString(), place.getAddress().toString(),
                                                latLng.latitude, latLng.longitude);//check

                    sourceSet = (place.getName() + ",\n" +
                            place.getAddress() + "\n" + place.getPhoneNumber());//check

                    findSource.setText(sourceSet);
                    break;
                case 2:
                    latLng = place.getLatLng();
                    destination = new GenLocation(place.getName().toString(), place.getAddress().toString(),
                                                    latLng.latitude, latLng.longitude);//check

                    destinationSet = (place.getName() + ",\n" +
                            place.getAddress() + "\n" + place.getPhoneNumber());//check

                    findDestination.setText(destinationSet);
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
            //Toast.makeText(getApplicationContext(), "Cancelled...", Toast.LENGTH_SHORT).show();
        }
    }

    public void finalSave(View view) throws ParseException
    {
        switch (checkInvalidEntry())
        {
            case 0:
                String entryId = entryDatabaseReference.push().getKey();
                String name = UtilityMethods.sanitizeName(finalCurrentUser.getName());
                System.currentTimeMillis();

                TripEntry tripEntry = new TripEntry(name, entryId, finalCurrentUser.getUserId(),
                                                        time, date, source, destination, null);

                finalCurrentUser.getUserTripEntries().put(tripEntry.getEntry_id(), tripEntry);

                entryDatabaseReference.child(entryId).setValue(tripEntry);

                userDatabaseReference.child("userTripEntries").setValue(finalCurrentUser.getUserTripEntries());

                Toast.makeText(this, "Trip entry created!", Toast.LENGTH_SHORT).show();

                finish();
                startActivity(new Intent(this, HomeActivity.class));
                break;

            case 1:
                Toast.makeText(this, "The pickup point and drop location can't be the same, silly!",
                        Toast.LENGTH_SHORT).show();
                break;

            case 2:
                Toast.makeText(this, "The developers are still working on time travel...",
                        Toast.LENGTH_LONG).show();
                break;

            case 3:
                Toast.makeText(this, "Fill in all the details!", Toast.LENGTH_SHORT).show();
                break;

            case 4:
                Toast.makeText(this, "Planning way too ahead of time, eh?", Toast.LENGTH_LONG).show();
                break;

            case 5:
                Toast.makeText(this, "You already have 4 entries, give others a chance too...", Toast.LENGTH_LONG).show();
        }
    }

    private int checkInvalidEntry() throws ParseException
    {
        int flag = 0;

        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy.HH:mm");
        Date currentTime = new Date();
        Date inputTime = null;

        int diffInDays = 0;

        if (date != null)
        {
            inputTime = parser.parse(date + "." + time);
            diffInDays = (int) ((inputTime.getTime() - currentTime.getTime()) / (1000 * 60 * 60 * 24));
        }

        if ((time.isEmpty() || source == null || destination == null ||
                findSource.getText().toString().isEmpty()|| findDestination.getText().toString().isEmpty()))
            flag = 3;

        else if ((source.getLatitude().compareTo(destination.getLatitude()) == 0) &&
                    (source.getLongitude().compareTo(destination.getLongitude()) == 0))
            flag = 1;

        else if (date == null)
            flag = 2;

        else if (inputTime.before(currentTime))
            flag = 2;

        else if(diffInDays > 21)
            flag = 4;

        else if(finalCurrentUser.getUserTripEntries().size() > 4)
            flag = 5;

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

    public void onRequestCurrentLocation(View view)
    {
        currentLocationView = view;

        if(currentLocationView.getTag().equals("gct_source"))
            findSource.setText(R.string.currentLoc);

        else if(currentLocationView.getTag().equals("gct_destination"))
            findDestination.setText(R.string.currentLoc);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "The app doesn't have permission to access your location", Toast.LENGTH_LONG).show();
            }
            else
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else getCurrentPlace();

    }

    private void getCurrentPlace()
    {
        final Place[] place = new Place[1];
        final LatLng[] latLng = new LatLng[1];


        @SuppressLint("MissingPermission")
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(likelyPlaces ->
        {
            try
            {

                PlaceLikelihood placeLikelihood = likelyPlaces.get(0);
                place[0] = placeLikelihood.getPlace();
                latLng[0] = place[0].getLatLng();

                if (currentLocationView.getTag().equals("gct_source"))
                {
                    source = new GenLocation(place[0].getName().toString(), place[0].getAddress().toString(),
                            latLng[0].latitude, latLng[0].longitude);
                    sourceSet = (place[0].getName() + ",\n" +
                            place[0].getAddress() + "\n" + place[0].getPhoneNumber());//check
                    findSource.setText(sourceSet);

                } else if (currentLocationView.getTag().equals("gct_destination"))
                {
                    destination = new GenLocation(place[0].getName().toString(), place[0].getAddress().toString(),
                            latLng[0].latitude, latLng[0].longitude);
                    destinationSet = (place[0].getName() + ",\n" +
                            place[0].getAddress() + "\n" + place[0].getPhoneNumber());//check
                    findDestination.setText(destinationSet);
                }

                likelyPlaces.release();
            }
            catch (IllegalStateException ils)
            {
                Toast.makeText(getApplicationContext(), "Turn on 'Location' option on your phone to use this feature...",
                        Toast.LENGTH_LONG).show();
                findSource.setText("Source");
                findDestination.setText("Destination");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        getCurrentPlace();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You have to accept that to get current location",
                                                Toast.LENGTH_SHORT).show();
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }

                }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this, "Connection problem!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Toast.makeText(this, "Connection problem!", Toast.LENGTH_SHORT).show();
    }
}
