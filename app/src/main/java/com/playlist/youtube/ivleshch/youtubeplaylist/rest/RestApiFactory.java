package com.playlist.youtube.ivleshch.youtubeplaylist.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiFactory {

    public static RetrofitApiInterface getRetrofitApiInterface() {
        OkHttpClient.Builder builderHttp = new OkHttpClient.Builder();
        builderHttp.readTimeout(10, TimeUnit.SECONDS);
        builderHttp.writeTimeout(10, TimeUnit.SECONDS);
        OkHttpClient client = builderHttp.build();

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(RetrofitApiInterface.class);
    }
}



