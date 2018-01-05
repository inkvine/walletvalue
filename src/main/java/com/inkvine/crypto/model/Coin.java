package com.inkvine.crypto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Coin {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("rank")
    private String rank;
    @JsonProperty("price_usd")
    private Double priceUsd;
    @JsonProperty("price_btc")
    private Double priceBtc;
    @JsonProperty("24h_volume_usd")
    private Double _24hVolumeUsd;
    @JsonProperty("market_cap_usd")
    private Double marketCapUsd;
    @JsonProperty("available_supply")
    private Double availableSupply;
    @JsonProperty("total_supply")
    private Double totalSupply;
    @JsonProperty("percent_change_1h")
    private Double percentChange1h;
    @JsonProperty("percent_change_24h")
    private Double percentChange24h;
    @JsonProperty("percent_change_7d")
    private Double percentChange7d;
    @JsonProperty("last_updated")
    private Double lastUpdated;
    @JsonProperty("max_supply")
    private Double maxSupply;
    @JsonProperty("price_eur")
    private Double priceEur;
    @JsonProperty("24h_volume_eur")
    private Double _24hVolumeEur;
    @JsonProperty("market_cap_eur")
    private Double marketCapEur;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

}
