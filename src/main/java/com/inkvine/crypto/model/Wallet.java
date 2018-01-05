package com.inkvine.crypto.model;

import lombok.Data;

import java.util.Map;

@Data
public class Wallet {

    Map<String, Integer> coinsAmount;

}
