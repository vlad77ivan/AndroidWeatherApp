package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapService {
    @GET("weather")
    Call<OpenWeatherMap> getOpenWeather(@Query("lat") double latitude,
                                        @Query("lon") double longitude,
                                        @Query("appid") String key,
                                        @Query("units") String units,
                                        @Query("lang") String lang);

}
