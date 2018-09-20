package garbagecollectors.com.unipool.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
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

import garbagecollectors.com.unipool.R;
import garbagecollectors.com.unipool.application.Globals;
import garbagecollectors.com.unipool.application.UtilityMethods;
import garbagecollectors.com.unipool.models.GenLocation;
import garbagecollectors.com.unipool.models.TripEntry;

import static garbagecollectors.com.unipool.activities.BaseActivity.finalCurrentUser;

public class NewEntryDialog extends DialogFragment implements GoogleApiClient.OnConnectionFailedListener,
                                                              GoogleApiClient.ConnectionCallbacks
{
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static GenLocation source, destination;
    public static String time, sourceSet, destinationSet, message;

    public static EditText findSource, findDestination;
    EditText setTime, setDate, messageText;

    public static String date;

    TimePickerDialog timePickerDialog;

    static GoogleApiClient mGoogleApiClient;

    public static View currentLocationView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_new_entry, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Create", (dialog, id) -> {
                    //Do nothing here because we override this button later in onResume to change the close behaviour.
                    //However, we still need this because on older versions of Android unless we
                    //pass a handler the button doesn't get instantiated
                })
                .setNegativeButton("Cancel", (dialog, id) -> NewEntryDialog.this.getDialog().cancel());

        source = null;
        destination = null;
        message = null;

        time = "";

        findSource = view.findViewById(R.id.findSourceEditText);
        findDestination = view.findViewById(R.id.findDestinationEditText);
        setTime = view.findViewById(R.id.setTimeEditText);
        setDate = view.findViewById(R.id.setDateEditText);
        messageText = view.findViewById(R.id.newEntryMessageText);

        //get current location buttons
        Button getCurrentLocationButton0 = view.findViewById(R.id.get_current_location_button0);
        getCurrentLocationButton0.setOnClickListener(this::onRequestCurrentLocation);
        Button getCurrentLocationButton1 = view.findViewById(R.id.get_current_location_button1);
        getCurrentLocationButton1.setOnClickListener(this::onRequestCurrentLocation);

        //predictSourceAndDestination();

        findSource.setOnClickListener(v -> findSource());
        findDestination.setOnClickListener(v -> findDestination());

        setTime.setOnClickListener(timeView -> openTimePickerDialog(false));

        setDate.setOnClickListener(v ->
        {
            DatePickerFragment.setSetDate(setDate);
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "Date Picker");
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        return builder.create();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                try
                {
                    int code = checkInvalidEntry();
                    if(code != 0)
                    {
                        showExceptionMessages(code);
                    }
                    else
                    {
                        finalSave();
                        d.dismiss();
                    }
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }

    private void predictSourceAndDestination()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
        {
        	findDestination.setText(R.string.uni_name);
        	destination = new GenLocation(Globals.uniInfo.get("name"), Globals.uniInfo.get("address"),
			     Double.parseDouble(Globals.uniInfo.get("latitude")), Double.parseDouble(Globals.uniInfo.get("longitude")));
        }
        else
        {
        	findSource.setText(R.string.uni_name);
	        source = new GenLocation(Globals.uniInfo.get("name"), Globals.uniInfo.get("address"),
			        Double.parseDouble(Globals.uniInfo.get("latitude")), Double.parseDouble(Globals.uniInfo.get("longitude")));
        }

    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop()
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
                getActivity(),
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

    public void findSource()
    {
        showPlaceAutocomplete(1);
    }

    public void findDestination()
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
                            .build(getActivity());
            getActivity().startActivityForResult(intent, i);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
        {
            Toast.makeText(getActivity(), "Google Play Service Error!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestCurrentLocation(View view)
    {
        NewEntryDialog.currentLocationView = view;

        if(NewEntryDialog.currentLocationView.getTag().equals("gct_source"))
            NewEntryDialog.findSource.setText(R.string.currentLoc);

        else if(NewEntryDialog.currentLocationView.getTag().equals("gct_destination"))
            NewEntryDialog.findDestination.setText(R.string.currentLoc);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(getActivity(), "The app doesn't have permission to access your location", Toast.LENGTH_LONG).show();
            }
            else
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        NewEntryDialog.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else NewEntryDialog.getCurrentPlace(getActivity());

    }

    public static void getCurrentPlace(Context context)
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
                Toast.makeText(context, "Turn on 'Location' option on your phone to use this feature...",
                        Toast.LENGTH_LONG).show();
                findSource.setText("Source");
                findDestination.setText("Destination");
            }
        });

    }

    public void finalSave()
    {
        String entryId = Globals.entryDatabaseReference.push().getKey();
        String name = UtilityMethods.sanitizeName(finalCurrentUser.getName());
        System.currentTimeMillis();
        message = messageText.getText().toString();

        if(message.isEmpty())
            message = null;

        TripEntry tripEntry = new TripEntry(name, entryId, finalCurrentUser.getUserId(),
                time, date, source, destination, message, "", finalCurrentUser.getEmail(), false);

        finalCurrentUser.getUserTripEntries().put(tripEntry.getEntry_id(), tripEntry);

        Globals.entryDatabaseReference.child(entryId).setValue(tripEntry);

        Globals.userDatabaseReference.child("userTripEntries").setValue(finalCurrentUser.getUserTripEntries());

        Toast.makeText(getActivity(), "Trip entry created!", Toast.LENGTH_SHORT).show();

        dismiss();
    }

    void showExceptionMessages(int code)
    {
        switch (code)
        {
            case 1:
                Toast.makeText(getActivity(), "The pickup point and drop location can't be the same, silly!",
                        Toast.LENGTH_SHORT).show();
                break;

            case 2:
                Toast.makeText(getActivity(), "This app's developer is still working on time travel...",
                        Toast.LENGTH_LONG).show();
                break;

            case 3:
                Toast.makeText(getActivity(), "Fill in all the details!", Toast.LENGTH_SHORT).show();
                break;

            case 4:
                Toast.makeText(getActivity(), "Planning way too ahead of time, eh?", Toast.LENGTH_LONG).show();
                break;

            case 5:
                Toast.makeText(getActivity(), "You already have 4 entries, give others a chance too...",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    private int checkInvalidEntry() throws ParseException
    {
        int flag = 0;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy.HH:mm");
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

        else if (inputTime.before(currentTime) || setDate.getText().toString().equalsIgnoreCase("Invalid Date"))
            flag = 2;

        else if(diffInDays > 21)
            flag = 4;

        else if(finalCurrentUser.getUserTripEntries().size() > 4)
            flag = 0;

        return flag;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(getActivity(), "Connection problem!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Toast.makeText(getActivity(), "Connection problem!", Toast.LENGTH_SHORT).show();
    }
}
