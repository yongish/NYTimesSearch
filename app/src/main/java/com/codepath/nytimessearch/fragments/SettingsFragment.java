package com.codepath.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingsFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText etDate;
    Button btnSave;

    public SettingsFragment() {}

    public static SettingsFragment newInstance(String title) {
        SettingsFragment frag = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container);
        etDate = (EditText) view.findViewById(R.id.etDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(SettingsFragment.this, 300);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsListener listener = (SettingsListener) getActivity();
                Settings settings = new Settings();
                settings.setBegin(etDate.getText().toString());
                settings.setSort(((Spinner) getView().findViewById(R.id.spinner)).getSelectedItem().toString());
                settings.setArts(((CheckBox) getView().findViewById(R.id.cbArts)).isChecked());
                settings.setFashion(((CheckBox) getView().findViewById(R.id.cbFashion)).isChecked());
                settings.setSports(((CheckBox) getView().findViewById(R.id.cbSports)).isChecked());
                listener.onFinishEditDialog(settings);
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public interface SettingsListener {
        void onFinishEditDialog(Settings settings);
    }

    /*public void onSubmit(View v) {
        SettingsListener listener = (SettingsListener) getActivity();
        listener.onFinishEditDialog(etDate.getText().toString());
        dismiss();
        Intent data = new Intent();
        data.putExtra("begin", etDate.getText().toString());
        data.putExtra("sort", ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString());
        data.putExtra("arts", ((CheckBox) findViewById(R.id.cbArts)).isChecked());
        data.putExtra("fashion", ((CheckBox) findViewById(R.id.cbFashion)).isChecked());
        data.putExtra("sports", ((CheckBox) findViewById(R.id.cbSports)).isChecked());
        setResult(RESULT_OK, data);
        finish();
    }*/

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
}
