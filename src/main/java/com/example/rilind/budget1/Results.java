package com.example.rilind.budget1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Results extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner month_f;
    Spinner month_t;
    Spinner year_t;
    Spinner year_f;
    Spinner day_t;
    Spinner day_f;

    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_results, container, false);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v= getView();
        // Array of months for the month spinners
        String months[] = {"Month", "Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Agusti", "September", "Oktober", "November", "December"};

        month_f = (Spinner) v.findViewById(R.id.month_f);
        month_t = (Spinner) v.findViewById(R.id.month_t);
        year_t = (Spinner) v.findViewById(R.id.year_t);
        year_f = (Spinner) v.findViewById(R.id.year_f);
        day_t = (Spinner) v.findViewById(R.id.day_t);
        day_f = (Spinner) v.findViewById(R.id.day_f);

        setSpinner(months, month_f);
        setSpinner(months, month_t);
        Button sync = (Button) v.findViewById(R.id.button6);
        sync.setOnClickListener(this);

        int y;
        //get current year
        y = Calendar.getInstance().get(Calendar.YEAR);

        String[] years = new String[31];
        years[0] = "Year";
        for (int i = 1; i < years.length; i++) {
            years[i] = String.valueOf((y + 1) - i);
        }

        setSpinner(years, year_f);
        setSpinner(years, year_t);

        days(32, day_f);
        days(32, day_t);
        //listener for when the month spinners are pressed
        month_f.setOnItemSelectedListener(this);
        month_t.setOnItemSelectedListener(this);



    }

    //changes the amount of days in the days spinners
    public void days(int z, Spinner spinner) {
        String[] days = new String[z];
        days[0] = "Day";
        for (int i = 1; i < days.length; i++) {
            days[i] = String.valueOf(i);
        }
        setSpinner(days, spinner);
    }

    //prints out the results for a time interval when the results button is pressed
    public void results() throws ParseException, IOException {
        //you can only show results if you first give the year interval
        if (year_t.getSelectedItemPosition() != 0 && year_f.getSelectedItemPosition() != 0) {
            //get date and save it as in the format of "YYYY-MM-DD"
            String from = year_f.getSelectedItem().toString() + "-" + month_f.getSelectedItemPosition() + "-" + day_f.getSelectedItemPosition();
            String to = year_t.getSelectedItem().toString() + "-" + month_t.getSelectedItemPosition() + "-" + day_t.getSelectedItemPosition();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date_f = sdf.parse(from);
            Date date_t = sdf.parse(to);
            //open database file
            SQLiteDatabase myDB = getActivity().openOrCreateDatabase("Budget", getActivity().MODE_PRIVATE, null);
            //retrieve only the items in the given time interval
            Cursor c = myDB.rawQuery("SELECT * FROM "+MainActivity.username+" where date between \"" + sdf.format(date_f) + "\" and \"" + sdf.format(date_t) + "\" ", null);

            double moms_ut = 0;
            double kopt_med_moms = 0;
            double moms_in = 0;
            double solt_med_moms = 0;
            c.moveToFirst();
            //loop through the given items and calculate the results
            while (!c.isAfterLast()) {
                //if the item is a expense then: total_VAT_expense += item * item_VAT
                if (c.getString(6).endsWith("UT")) {
                    moms_ut += c.getDouble(3)- c.getDouble(3)/(1+c.getDouble(2));
                    kopt_med_moms += c.getDouble(3);
                } else {
                    if (c.getString(7).equalsIgnoreCase("no"))
                        moms_in +=  c.getDouble(3)- c.getDouble(3)/(1+c.getDouble(2));
                        //if the item is a sold item and is used then: total_revenue_VAT += item * item_vat * 80%
                    else
                        moms_in += c.getDouble(3)- c.getDouble(3)/(1+(c.getDouble(2)*0.8));
                    solt_med_moms += c.getDouble(3) ;
                }
                c.moveToNext();
            }
            double moms_total=-(moms_in - moms_ut);
            //write out the results on the textfields
            TextView one = (TextView) v.findViewById(R.id.one);
            TextView two = (TextView) v.findViewById(R.id.two);
            TextView three = (TextView) v.findViewById(R.id.three);
            TextView four = (TextView) v.findViewById(R.id.four);
            String sf = String.format("%.2f", solt_med_moms);
            one.setText(sf);
            sf = String.format("%.2f", -kopt_med_moms);
            two.setText(sf);
            sf = String.format("%.2f", moms_total);
            three.setText(sf);
            sf = String.format("%.2f", solt_med_moms - kopt_med_moms + moms_total);
            four.setText(sf);

        } else {
            //error message
        }
    }

    //writes out the text on the spinners
    public void setSpinner(String[] array, Spinner spinner) {
        Spinner s = spinner;
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        s.setAdapter(spinnerArrayAdapter);
    }

    //changes the day spinner, so when you choose a month you get the amount a days corresponding to the chosen month
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner month = null;
        Spinner day = null;
        Spinner year = null;
        //checks which month spinner was pressed
        if (adapterView.getId() == R.id.month_f) {
            day = day_f;
            month = month_f;
            year = year_f;
        }
        if (adapterView.getId() == R.id.month_t) {
            day = day_t;
            month = month_t;
            year = year_t;
        }
        //gives the right amount of days for the chosen month
        int z = month.getSelectedItemPosition();
        if (z == 0 || z == 1 || z == 3 || z == 5 || z == 7 || z == 8 || z == 12 || z == 10) {
            days(32, day);
        }
        if (z == 4 || z == 6 || z == 9 || z == 11) {
            days(31, day);
        }
        //gives february 29 days if it's a leap year, else it gives 28 days
        if (z == (2)) {
            double y = 1;
            if (year.getSelectedItemPosition() != 0)
                y = Integer.parseInt(year.getSelectedItem().toString());
            if ((y % 400 == 0 || y % 100 != 0) && (y % 4 == 0))
                days(30, day);
            else
                days(29, day);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        try {
            results();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
