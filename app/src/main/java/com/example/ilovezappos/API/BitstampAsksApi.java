package com.example.ilovezappos.API;

import com.example.ilovezappos.Utils.Asks;
import com.example.ilovezappos.Utils.Bids;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BitstampAsksApi {
    @GET("api/v2/order_book/btcusd")
    Call<Asks> getAsks();
}
