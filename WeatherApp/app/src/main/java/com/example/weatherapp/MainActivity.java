package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Context context;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseComponents();

        if (checkLocationPermission()) {
            DataUtils.startWorker(context);

            SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                    Constants.MODE_PRIVATE);
            String message = sharedPref.getString(Constants.WEATHER_MESSAGE_KEY,
                    Constants.WEATHER_MESSAGE_NOT_AVAILABLE);
            textView.setText(message);
        }
    }

    private void initialiseComponents() {
        Button getWeatherButton = findViewById(R.id.get_weather_button);

        context = getApplicationContext();
        textView = findViewById(R.id.weather_data_text_view);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message obj) {
                SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                        Constants.MODE_PRIVATE);
                String message = sharedPref.getString(Constants.WEATHER_MESSAGE_KEY,
                        Constants.WEATHER_MESSAGE_NOT_AVAILABLE);
                textView.setText(message);
            }
        };

        getWeatherButton.setOnClickListener(v -> {
            Runnable runnable = () -> {
                DataUtils.updateWeatherAndUi(context, handler);
            };
            Thread thread = new Thread(runnable);
            thread.start();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                DataUtils.updateWeatherAndUi(context, handler);
            }
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle(Constants.PERMISSIONS_REQUEST_TITLE)
                        .setMessage(Constants.PERMISSIONS_REQUEST_MESSAGE)
                        .setPositiveButton(Constants.PERMISSIONS_REQUEST_POSITIVE_BUTTON,
                                (dialogInterface, i) -> ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        Constants.PERMISSIONS_REQUEST_LOCATION))
                        .setNegativeButton(Constants.PERMISSIONS_REQUEST_NEGATIVE_BUTTON,
                                (dialog, which) -> finishAndRemoveTask())
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DataUtils.startWorker(context);
                } else {
                    finishAndRemoveTask();
                }
            }
        }
    }
}