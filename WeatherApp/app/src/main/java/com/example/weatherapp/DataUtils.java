package com.example.weatherapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Handler;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface DataUtils {

    static void updateLastLocation(Context context, double latitude, double longitude) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE).edit();

        editor.putLong(Constants.LATITUDE_KEY, Double.doubleToRawLongBits(latitude));
        editor.putLong(Constants.LONGITUDE_KEY, Double.doubleToRawLongBits(longitude));

        editor.commit();
        editor.apply();
    }

    static void updateWeather(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE);

        // Retrieve saved location and weather format preferences
        double latitude = Double.longBitsToDouble(sharedPref.getLong(Constants.LATITUDE_KEY,
                Double.doubleToLongBits(Constants.LATITUDE_DEFAULT)));
        double longitude = Double.longBitsToDouble(sharedPref.getLong(Constants.LONGITUDE_KEY,
                Double.doubleToLongBits(Constants.LONGITUDE_DEFAULT)));
        String units = sharedPref.getString(Constants.UNITS_KEY, Constants.UNITS_DEFAULT);
        String lang = sharedPref.getString(Constants.LANG_KEY, Constants.LANG_DEFAULT);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.OPEN_WEATHER_MAP_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherMapService openWeatherMapService = retrofit.create(OpenWeatherMapService.class);
        Call<OpenWeatherMap> call = openWeatherMapService.getOpenWeather(latitude, longitude,
                Constants.API_KEY, units, lang);

        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                assert response.body() != null;
                String locationName = response.body().getName();
                Double coordLat = response.body().getCoord().getLat();
                Double coordLon = response.body().getCoord().getLon();
                String weatherDescription = response.body().getWeather().get(0).getDescription();
                Double temperature = response.body().getMain().getTemp();
                Double feelsLike = response.body().getMain().getFeelsLike();
                Integer humidity = response.body().getMain().getHumidity();
                Double windSpeed = response.body().getWind().getSpeed();

                String weatherMessage = formatWeatherMessage(locationName, coordLat, coordLon,
                        weatherDescription, temperature, feelsLike, humidity, windSpeed);

                SharedPreferences.Editor editor = context.getSharedPreferences(
                        Constants.SHARED_PREFERENCES,
                        Constants.MODE_PRIVATE).edit();

                // Update saved weather to latest data
                editor.putString(Constants.WEATHER_MESSAGE_KEY, weatherMessage);
                editor.putLong(Constants.TEMPERATURE_KEY, Double.doubleToRawLongBits(temperature));
                editor.commit();
                editor.apply();
            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
            }
        });
    }

    static void updateWeatherAndUi(Context context, Handler handler) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE);

        // Retrieve saved location and weather format preferences
        double latitude = Double.longBitsToDouble(sharedPref.getLong(Constants.LATITUDE_KEY,
                Double.doubleToLongBits(Constants.LATITUDE_DEFAULT)));
        double longitude = Double.longBitsToDouble(sharedPref.getLong(Constants.LONGITUDE_KEY,
                Double.doubleToLongBits(Constants.LONGITUDE_DEFAULT)));
        String units = sharedPref.getString(Constants.UNITS_KEY, Constants.UNITS_DEFAULT);
        String lang = sharedPref.getString(Constants.LANG_KEY, Constants.LANG_DEFAULT);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.OPEN_WEATHER_MAP_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherMapService openWeatherMapService = retrofit.create(OpenWeatherMapService.class);
        Call<OpenWeatherMap> call = openWeatherMapService.getOpenWeather(latitude, longitude,
                Constants.API_KEY, units, lang);

        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                String locationName = response.body().getName();
                Double coordLat = response.body().getCoord().getLat();
                Double coordLon = response.body().getCoord().getLon();
                String weatherDescription = response.body().getWeather().get(0).getDescription();
                Double temperature = response.body().getMain().getTemp();
                Double feelsLike = response.body().getMain().getFeelsLike();
                Integer humidity = response.body().getMain().getHumidity();
                Double windSpeed = response.body().getWind().getSpeed();

                String weatherMessage = formatWeatherMessage(locationName, coordLat, coordLon,
                        weatherDescription, temperature, feelsLike, humidity, windSpeed);

                SharedPreferences.Editor editor = context.getSharedPreferences(
                        Constants.SHARED_PREFERENCES,
                        Constants.MODE_PRIVATE).edit();

                // Update saved weather to latest data
                editor.putString(Constants.WEATHER_MESSAGE_KEY, weatherMessage);
                editor.putLong(Constants.TEMPERATURE_KEY, Double.doubleToRawLongBits(temperature));
                editor.commit();
                editor.apply();

                // Trigger UI update
                Message message = handler.obtainMessage();
                message.obj = Constants.UI_UPDATE;
                message.sendToTarget();
            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
            }
        });
    }

    static String formatWeatherMessage(String locationName, Double coordLat, Double coordLon,
                                       String weatherDescription, Double temperature,
                                       Double feelsLike, Integer humidity, Double windspeed) {

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT);
        String currentTime = sdf.format(new Date());

        return locationName + " -> " + coordLat + " : " + coordLon + "\n"
                + currentTime + "\n"
                + temperature + " - " + weatherDescription + "\n"
                + Constants.FEELS_LIKE_TEXT + feelsLike + "\n"
                + Constants.HUMIDITY_TEXT + humidity + "\n"
                + Constants.WIND_SPEED_TEXT + windspeed;
    }

    static void verifyAlert(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Constants.MODE_PRIVATE);

        if (sharedPref.getBoolean(Constants.ALERT_KEY, Constants.DEFAULT_ALERT_VALUE)
                && sharedPref.contains(Constants.TEMPERATURE_KEY)) {

            double temperature = Double.longBitsToDouble(sharedPref.getLong(Constants.TEMPERATURE_KEY,
                    Double.doubleToLongBits(Constants.TEMPERATURE_DEFAULT)));
            double minThreshold = Double.longBitsToDouble(sharedPref.getLong(Constants.MIN_TEMPERATURE_THRESHOLD_KEY,
                    Double.doubleToLongBits(Constants.MIN_TEMPERATURE_THRESHOLD_DEFAULT)));
            double maxThreshold = Double.longBitsToDouble(sharedPref.getLong(Constants.MAX_TEMPERATURE_THRESHOLD_KEY,
                    Double.doubleToLongBits(Constants.MAX_TEMPERATURE_THRESHOLD_DEFAULT)));

            if (temperature <= minThreshold) {
                createAlertNotification(context, Constants.MIN_TEMPERATURE_THRESHOLD_TEXT,
                        Constants.TEMPERATURE_IS_TEXT + temperature + Constants.TEMPERATURE_BELOW_TEXT
                                + minThreshold);
            }

            if (temperature >= maxThreshold) {
                createAlertNotification(context, Constants.MAX_TEMPERATURE_THRESHOLD_TEXT,
                        Constants.TEMPERATURE_IS_TEXT + temperature + Constants.TEMPERATURE_OVER_TEXT
                                + maxThreshold);
            }
        }
    }

    static void createAlertNotification(Context context, String title, String text) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Constants.PENDING_INTENT_REQUEST_CODE,
                intent, 0);
        Notification notification = new Notification.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setChannelId(Constants.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .build();


        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME,
                importance);
        channel.setDescription(Constants.CHANNEL_DESCRIPTION);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

    static void startWorker(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest periodicSyncDataWork =
                new PeriodicWorkRequest.Builder(UpdateWorker.class, Constants.UPDATE_TIME, TimeUnit.MINUTES)
                        .addTag(Constants.WORK_TAG)
                        .setConstraints(constraints)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .build();

        workManager.enqueueUniquePeriodicWork(
                Constants.WORKER_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicSyncDataWork);
    }
}
