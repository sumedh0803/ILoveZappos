package com.example.ilovezappos.API;

import com.example.ilovezappos.Utils.Price;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BitstampTickerApi {

    @GET("api/v2/ticker_hour/btcusd/")
    Call<Price> getPrice();
}
