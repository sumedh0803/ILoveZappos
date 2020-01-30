package com.example.ilovezappos.API;

import com.example.ilovezappos.Utils.Bids;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BitstampBidsApi {
    @GET("api/v2/order_book/btcusd")
    Call<Bids> getBids();
}
