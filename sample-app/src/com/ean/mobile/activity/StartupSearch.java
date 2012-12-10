package com.ean.mobile.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import com.ean.mobile.HotelWrangler;
import com.ean.mobile.R;
import com.ean.mobile.request.DestLookup;
import com.ean.mobile.request.ListRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StartupSearch extends Activity {

    private ProgressDialog searchingDialog;

    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.startupsearch);

        final EditText searchBox = (EditText) findViewById(R.id.searchBox);
        Button arrivalDate = (Button) findViewById(R.id.arrival_date_picker);
        Button departureDate = (Button) findViewById(R.id.departure_date_picker);
        Spinner adultsSpinner = (Spinner) findViewById(R.id.adults_spinner);
        Spinner childrenSpinner = (Spinner) findViewById(R.id.children_spinner);

//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        searchBox.setText(l.toString());
        searchBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    searchingDialog = ProgressDialog.show(StartupSearch.this, "", "Searching", true);
                    performSearch(searchBox.getText().toString().trim());
                    return true;
                } else {;
                    new Thread() {
                        public void run() {
                            try {
                               Log.d("EANDebug", DestLookup.getDestInfos(searchBox.getText().toString()).toString());
                            } catch (Exception e) {
                                Log.d("EANDebug", e.getMessage());
                                // we'll deal with this later.
                            }
                        }
                    }.start();

                }
                return false;
            }
        });

        // add a click listener to the button
        arrivalDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(R.id.arrival_date_picker);
            }
        });

        departureDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(R.id.departure_date_picker);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        updateDateDisplay(arrivalDate, c);
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        updateDateDisplay(departureDate, c);

        adultsSpinner.setAdapter(ArrayAdapter.createFromResource(
            this, R.array.number_of_people_array, android.R.layout.simple_spinner_item));
        ((ArrayAdapter<CharSequence>)adultsSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        adultsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                HotelWrangler wrangler = (HotelWrangler) getApplicationContext();
                wrangler.setNumberOfAdults(Integer.parseInt(adapterView.getItemAtPosition(pos).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing because you can't
            }
        });
        
        childrenSpinner.setAdapter(ArrayAdapter.createFromResource(
            this, R.array.number_of_people_array, android.R.layout.simple_spinner_item));
        ((ArrayAdapter<CharSequence>)childrenSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                HotelWrangler wrangler = (HotelWrangler) getApplicationContext();
                wrangler.setNumberOfChildren(Integer.parseInt(adapterView.getItemAtPosition(pos).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing because you can't
            }
        });

    }

    //TODO: onResume that sets up the adults/children.

    @Override
    protected Dialog onCreateDialog(final int id) {
        if (id != R.id.arrival_date_picker && id != R.id.departure_date_picker) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        return new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar c = Calendar.getInstance();
                            c.set(year, monthOfYear, dayOfMonth);
                            updateDateDisplay((Button) findViewById(id), c);
                        }
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

    }

    private void performSearch(final String searchQuery) {
        Thread requestThread = new Thread() {
            public void run() {
                try {
                    Looper.prepare();
                    HotelWrangler wrangler = (HotelWrangler) getApplicationContext();
                    wrangler.setSearchQuery(searchQuery);
                    wrangler.setArrivalDate(new Date(((Button) findViewById(R.id.arrival_date_picker)).getText().toString()));
                    wrangler.setDepartureDate(new Date(((Button) findViewById(R.id.departure_date_picker)).getText().toString()));
                    //number of adults/children set with the onitemselectedlisteners.
                    ListRequest.searchForHotels(searchQuery, wrangler);
                    Log.d("EANDebug", wrangler.getCacheKey());
                    runOnUiThread(
                        new Runnable() {
                            public void run() {
                                Intent intent = new Intent(StartupSearch.this, HotelList.class);
                                startActivity(intent);
                            }
                        }
                    );
                } catch (Exception e) {
                    Log.d("EANDebug", " An exception occurred: " + e.getMessage());
                } finally {
                     runOnUiThread(
                        new Runnable() {
                            public void run() {
                                StartupSearch.this.searchingDialog.dismiss();
                            }
                        }
                     );
                }
            }
        };
        requestThread.start();
    }

    private void updateDateDisplay(Button dd, Calendar c) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dd.setText(dateFormat.format(c.getTime()));
        HotelWrangler wrangler = (HotelWrangler) getApplicationContext();
        switch(dd.getId()) {
            case R.id.arrival_date_picker:
                wrangler.setArrivalDate(c.getTime());
                break;
            case R.id.departure_date_picker:
                wrangler.setDepartureDate(c.getTime());
                break;
            default:
                break;
        }
    }
}

