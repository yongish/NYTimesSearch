package com.codepath.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytimessearch.DatePickerFragment;
import com.codepath.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etDate = (EditText) findViewById(R.id.etDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel(c);
    }

    public void updateLabel(Calendar c) {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(c.getTime()));
    }

    public void onSubmit(View v) {
        Intent data = new Intent();
        data.putExtra("begin", etDate.getText().toString());
        data.putExtra("sort", ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString());
        data.putExtra("arts", ((CheckBox) findViewById(R.id.cbArts)).isChecked());
        data.putExtra("fashion", ((CheckBox) findViewById(R.id.cbFashion)).isChecked());
        data.putExtra("sports", ((CheckBox) findViewById(R.id.cbSports)).isChecked());
        setResult(RESULT_OK, data);
        finish();
    }
}
