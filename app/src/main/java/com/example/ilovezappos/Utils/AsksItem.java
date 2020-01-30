package com.example.ilovezappos.Utils;

public class AsksItem {
    private String asks, value,amt;

    public AsksItem(String asks, String value, String amt) {
        this.asks = asks;
        this.value = value;
        this.amt = amt;
    }

    public String getAsks() {
        return asks;
    }

    public String getValue() {
        return value;
    }

    public String getAmt() {
        return amt;
    }
}
