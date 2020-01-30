package com.example.ilovezappos.Utils;

import com.github.mikephil.charting.data.Entry;

import java.util.Comparator;

public class Transaction{
    String date,price,type;

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    /*Comparator for sorting the list by TimeStamp*/
    public static Comparator<Entry> timeComparator = new Comparator<Entry>() {

        public int compare(Entry e1, Entry e2) {
            long time1 =(long) e1.getX();
            long time2 =(long) e2.getX();

            //ascending order
            return String.valueOf(time1).compareTo(String.valueOf(time2));

        }};

}
