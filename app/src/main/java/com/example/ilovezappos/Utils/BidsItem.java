package com.example.ilovezappos.Utils;

public class BidsItem {
    private String bids, value,amt;

    public BidsItem(String bids, String value, String amt) {
        this.bids = bids;
        this.value = value;
        this.amt = amt;
    }

    public String getBids() {
        return bids;
    }

    public String getValue() {
        return value;
    }

    public String getAmt() {
        return amt;
    }
}
