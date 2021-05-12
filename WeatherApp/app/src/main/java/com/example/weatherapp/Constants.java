package com.example.weatherapp;

public interface Constants {
    // OpenWeatherMap
    String API_KEY = "e50f9454d9d1d78c1737c391839d2163";
    String OPEN_WEATHER_MAP_BASEURL = "https://api.openweathermap.org/data/2.5/";

    // Shared preferences file
    String SHARED_PREFERENCES = "shared_preferences";
    int MODE_PRIVATE = 0;

    // Shared preferences keys
    String UNITS_SPINNER_KEY = "units_spinner_key_1337";
    String LANG_SPINNER_KEY = "lang_spinner_key_1337";
    String LATITUDE_KEY = "latitude_key_1337";
    String LONGITUDE_KEY = "longitude_key_1337";
    String UNITS_KEY = "units_1337";
    String LANG_KEY = "lang_1337";
    String WEATHER_MESSAGE_KEY = "weather_message_1337";
    String TEMPERATURE_KEY = "temperature_1337";
    String MIN_TEMPERATURE_THRESHOLD_KEY = "min_temperature_threshold_1337";
    String MAX_TEMPERATURE_THRESHOLD_KEY = "max_temperature_threshold_1337";
    String ALERT_KEY = "alert_key_1337";

    // Defaults
    String UNITS_DEFAULT = "metric";
    String LANG_DEFAULT = "en";
    int UNITS_DEFAULT_INDEX = 1;
    int LANG_DEFAULT_INDEX = 10;
    double LATITUDE_DEFAULT = 0.0;
    double LONGITUDE_DEFAULT = 0.0;
    boolean DEFAULT_ALERT_VALUE = false;
    int TEMPERATURE_DEFAULT = 0;
    double MIN_TEMPERATURE_THRESHOLD_DEFAULT = -1000;
    double MAX_TEMPERATURE_THRESHOLD_DEFAULT = 1000;

    // Handler message
    String UI_UPDATE = "ui_update";

    // Notifications
    int NOTIFICATION_ID = 1;
    int PENDING_INTENT_REQUEST_CODE = 1;
    String CHANNEL_ID = "420";
    String CHANNEL_NAME = "weatherapp_notifications";
    String CHANNEL_DESCRIPTION = "weather_alerts";

    // Location permissions
    int PERMISSIONS_REQUEST_LOCATION = 42;
    String PERMISSIONS_REQUEST_TITLE = "Location access request";
    String PERMISSIONS_REQUEST_MESSAGE = "Location access needed to retrieve weather forecast";
    String PERMISSIONS_REQUEST_POSITIVE_BUTTON = "Ok";
    String PERMISSIONS_REQUEST_NEGATIVE_BUTTON = "Exit";

    // WorkManager
    int UPDATE_TIME = 15;
    String WORK_TAG = "weatherapp_worker_tag";
    String WORKER_NAME = "weatherapp_update_work";

    // User messages
    String WEATHER_MESSAGE_NOT_AVAILABLE = "Weather not available right now...";
    String TIME_FORMAT = "HH:mm:ss z";
    String FEELS_LIKE_TEXT = "Feels like: ";
    String HUMIDITY_TEXT = "Humidity: ";
    String WIND_SPEED_TEXT = "Wind speed: ";
    String MIN_TEMPERATURE_THRESHOLD_TEXT = "Minimum temperature alert";
    String MAX_TEMPERATURE_THRESHOLD_TEXT = "Maximum temperature alert";
    String TEMPERATURE_IS_TEXT = "Temperature is ";
    String TEMPERATURE_BELOW_TEXT = ", below the target ";
    String TEMPERATURE_OVER_TEXT = ", over the target ";
}
