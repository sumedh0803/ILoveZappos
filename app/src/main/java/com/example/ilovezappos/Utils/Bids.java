package com.example.ilovezappos.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public class Bids {
    List<List<String>> bids;
    String timestamp;

    public String getTimestamp()
    {
        return timestamp;
    }

    public List<List<String>> getBids() {
        return bids;
    }

}
