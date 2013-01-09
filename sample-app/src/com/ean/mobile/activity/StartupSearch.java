package com.ean.mobile.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import com.ean.mobile.R;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.SampleApp;
import com.ean.mobile.SampleConstants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.request.ListRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class StartupSearch extends Activity {


    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);


    private ProgressDialog searchingDialog;

    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.startupsearch);

        final EditText searchBox = (EditText) findViewById(R.id.searchBox);

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    searchingDialog = ProgressDialog.show(StartupSearch.this, "", "Searching", true);
                    performSearch(searchBox.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        // get the current date
        LocalDate today = LocalDate.now();
        SampleApp.arrivalDate = today;
        SampleApp.departureDate = today.plusDays(1);


        Button arrivalDate = (Button) findViewById(R.id.arrival_date_picker);
        Button departureDate = (Button) findViewById(R.id.departure_date_picker);
        // set the date on the button
        arrivalDate.setText(DATE_TIME_FORMATTER.print(SampleApp.arrivalDate));
        departureDate.setText(DATE_TIME_FORMATTER.print(SampleApp.departureDate));

        ArrayAdapter<CharSequence> adultsSpinnerAdapter
                = ArrayAdapter.createFromResource(this, R.array.number_of_people_array, android.R.layout.simple_spinner_item);
        adultsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        Spinner adultsSpinner = (Spinner) findViewById(R.id.adults_spinner);
        adultsSpinner.setAdapter(adultsSpinnerAdapter);
        adultsSpinner.setOnItemSelectedListener(new PeopleSpinnerListener(true));

        ArrayAdapter<CharSequence> childrenSpinnerAdapter
                = ArrayAdapter.createFromResource(this, R.array.number_of_people_array, android.R.layout.simple_spinner_item);
        childrenSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner childrenSpinner = (Spinner) findViewById(R.id.children_spinner);
        childrenSpinner.setAdapter(childrenSpinnerAdapter);
        childrenSpinner.setOnItemSelectedListener(new PeopleSpinnerListener(false));

    }

    public void showDatePickerDialog(View view) {
        new DatePickerFragment(view.getId(), (Button) view).show(getFragmentManager(), "StartupSearchDatePicker");
    }

    private void preSearch() {
        SampleApp.searchQuery = null;
        SampleApp.numberOfAdults = 0;
        SampleApp.numberOfChildren = 0;
        SampleApp.arrivalDate = null;
        SampleApp.departureDate = null;
        SampleApp.foundHotels = null;

        SampleApp.selectedHotel = null;

        SampleApp.selectedRoom = null;

        SampleApp.EXTENDED_INFOS.clear();

        SampleApp.HOTEL_ROOMS.clear();
    }


    private void performSearch(final String searchQuery) {
        preSearch();
        SampleApp.searchQuery = searchQuery;
        SampleApp.arrivalDate = DATE_TIME_FORMATTER.parseLocalDate(((Button) findViewById(R.id.arrival_date_picker)).getText().toString());
        SampleApp.departureDate = DATE_TIME_FORMATTER.parseLocalDate(((Button) findViewById(R.id.departure_date_picker)).getText().toString());
        new PerformSearchTask().execute((Void) null);
    }

    private class PeopleSpinnerListener implements AdapterView.OnItemSelectedListener {

        private final boolean adults;

        /**
         * This is the main constructor.
         * @param adults Whether or not the spinner is the adults spinner. If false, assumes the children spinner.
         */
        public PeopleSpinnerListener(boolean adults) {
            this.adults = adults;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            final int count = Integer.parseInt(adapterView.getItemAtPosition(pos).toString());
            if (adults) {
                SampleApp.numberOfAdults = count;
            } else {
                SampleApp.numberOfChildren = count;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //do nothing because you can't
        }
    }

    private class PerformSearchTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SampleApp.foundHotels = ListRequest.searchForHotels(SampleApp.searchQuery, SampleApp.occupancy(), SampleApp.arrivalDate, SampleApp.departureDate, SampleApp.LOCALE.toString(), SampleApp.CURRENCY.toString());
            } catch (IOException e) {
                Log.d(SampleConstants.DEBUG, "An IOException occurred while searching for hotels.", e);
            } catch (EanWsError ewe) {
                //TODO: This should be handled better. If this exception occurs, it's likely an input error and
                // should be recoverable.
                Log.d(SampleConstants.DEBUG, "An APILevel Exception occurred.", ewe);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aLong) {
            Intent intent = new Intent(StartupSearch.this, HotelList.class);
            startActivity(intent);
            try {
                StartupSearch.this.searchingDialog.dismiss();
            } catch (IllegalArgumentException iae) {
                // just ignore it... it's because the window rotated at an inopportune time.
            }
        }

        @Override
        protected void onCancelled() {
            StartupSearch.this.searchingDialog.dismiss();
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private final int pickerId;

        private final Button pickerButton;

        public DatePickerFragment(final int pickerId, final Button pickerButton) {
            super();
            this.pickerId = pickerId;
            this.pickerButton = pickerButton;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final LocalDate date;
            if (pickerId == R.id.arrival_date_picker) {
                date = SampleApp.arrivalDate;
            } else {
                date = SampleApp.departureDate;
            }

            return new DatePickerDialog(getActivity(), this, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            LocalDate chosenDate = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            pickerButton.setText(DATE_TIME_FORMATTER.print(chosenDate));
            switch(pickerId) {
                case R.id.arrival_date_picker:
                    SampleApp.arrivalDate = chosenDate;
                    break;
                case R.id.departure_date_picker:
                    SampleApp.departureDate = chosenDate;
                    break;
                default:
                    break;
            }
        }
    }
}

