package com.example.borsh.ui;

import com.example.borsh.rssPOJO.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface ApiRss {
   String BASE_URL_NASA_IMAGE_OF_THE_DAY =
       "https://api.rss2json.com/v1/";

   String API_KEY = "ta9svyflz4ujjwnekx9tdih3pfuz5avhhoixslwr";
   String RSS_URL =
       "https%3A%2F%2Fwww.nasa.gov%2Frss%2Fdyn%2Flg_image_of_the_day.rss";

   @GET("api.json?api_key=" + API_KEY + "&rss_url=" + RSS_URL)
   Call<Response> getRssFeed();

   Retrofit retrofit = new Retrofit
       .Builder()
       .baseUrl(BASE_URL_NASA_IMAGE_OF_THE_DAY)
       .addConverterFactory(GsonConverterFactory.create())
       .build();
}
