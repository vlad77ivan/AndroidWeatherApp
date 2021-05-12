package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        setupSpinners();
        setupAlert();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EditText minTempEdit = findViewById(R.id.edit_min_temp);
        EditText maxTempEdit = findViewById(R.id.edit_max_temp);
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(
                Constants.SHARED_PREFERENCES, Constants.MODE_PRIVATE).edit();

        if (!minTempEdit.getText().toString().isEmpty()) {
            double threshold = Double.parseDouble(minTempEdit.getText().toString());
            editor.putLong(Constants.MIN_TEMPERATURE_THRESHOLD_KEY, Double.doubleToRawLongBits(threshold));
            editor.commit();
            editor.apply();
        }

        if (!maxTempEdit.getText().toString().isEmpty()) {
            double threshold = Double.parseDouble(maxTempEdit.getText().toString());
            editor.putLong(Constants.MAX_TEMPERATURE_THRESHOLD_KEY, Double.doubleToRawLongBits(threshold));
            editor.commit();
            editor.apply();
        }
    }


    private void setupSpinners() {
        Context context = getApplicationContext();
        Spinner unitsSpinner = findViewById(R.id.units_options);
        Spinner langSpinner = findViewById(R.id.language_options);

        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE);
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE).edit();
        unitsSpinner.setSelection(sharedPref.getInt(Constants.UNITS_SPINNER_KEY, Constants.UNITS_DEFAULT_INDEX));
        langSpinner.setSelection(sharedPref.getInt(Constants.LANG_SPINNER_KEY, Constants.LANG_DEFAULT_INDEX));

        unitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                editor.putInt(Constants.UNITS_SPINNER_KEY, position);
                String metric = getResources().getStringArray(R.array.units_array)[position]
                        .split(" ")[0];
                editor.putString(Constants.UNITS_KEY, metric);
                editor.commit();
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                editor.putInt(Constants.LANG_SPINNER_KEY, position);
                String lang = getResources().getStringArray(R.array.language_array)[position]
                        .split(" ")[0];
                editor.putString(Constants.LANG_KEY, lang);
                editor.commit();
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void setupAlert() {
        Context context = getApplicationContext();
        ToggleButton alertToggle = findViewById(R.id.alert_toggle_button);
        EditText minTempEdit = findViewById(R.id.edit_min_temp);
        EditText maxTempEdit = findViewById(R.id.edit_max_temp);

        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE);
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE).edit();

        boolean alertState = sharedPref.getBoolean(Constants.ALERT_KEY, Constants.DEFAULT_ALERT_VALUE);
        double minThreshold = Double.longBitsToDouble(sharedPref.getLong(Constants.MIN_TEMPERATURE_THRESHOLD_KEY,
                Double.doubleToLongBits(Constants.MIN_TEMPERATURE_THRESHOLD_DEFAULT)));
        double maxThreshold = Double.longBitsToDouble(sharedPref.getLong(Constants.MAX_TEMPERATURE_THRESHOLD_KEY,
                Double.doubleToLongBits(Constants.MAX_TEMPERATURE_THRESHOLD_DEFAULT)));

        alertToggle.setChecked(alertState);
        minTempEdit.setText(Double.toString(minThreshold));
        maxTempEdit.setText(Double.toString(maxThreshold));

        alertToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean(Constants.ALERT_KEY, isChecked);
            editor.commit();
            editor.apply();
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}