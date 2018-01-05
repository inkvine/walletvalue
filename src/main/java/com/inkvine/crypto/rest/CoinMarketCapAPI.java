package com.inkvine.crypto.rest;

import com.inkvine.crypto.model.Coin;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface CoinMarketCapAPI {

    public static final String BASE_URL = "https://api.coinmarketcap.com/v1/";

    @GET("ticker/{id}")
    Call<List<Coin>> getCoinById(@Path("id") String coinId, @Query("convert") String convert);

}
