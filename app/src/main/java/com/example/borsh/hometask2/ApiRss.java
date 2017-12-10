package com.example.borsh.hometask2;

import com.example.borsh.rss.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRss {
		String BASE_URL_NASA_IMAGE_OF_THE_DAY =
				"https://api.rss2json.com/v1/";

		@GET("api.json?rss_url=https%3A%2F%2Fwww.nasa.gov%2Frss%2Fdyn%2Flg_image_of_the_day.rss")
		Call<Response> getRssFeed();
		//@GET("api.json")
		//Call<Response> getRssFeed(
		//		@Query("rss_url") String rssUrl
		//);

		Retrofit retrofit = new Retrofit
				.Builder()
				.baseUrl(BASE_URL_NASA_IMAGE_OF_THE_DAY)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
}
