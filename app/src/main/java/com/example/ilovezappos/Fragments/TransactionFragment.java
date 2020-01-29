package com.example.ilovezappos.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ilovezappos.API.BitstampTickerApi;
import com.example.ilovezappos.API.BitstampTransactionApi;
import com.example.ilovezappos.R;
import com.example.ilovezappos.Utils.Price;
import com.example.ilovezappos.Utils.Transaction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionFragment extends Fragment {
    ArrayList<Entry> data = new ArrayList<Entry>();
    LineChart lineChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        lineChart = view.findViewById(R.id.graph);
        BitstampTransactionApi bitstampTransactionApi = rf.create(BitstampTransactionApi.class);
        Call<List<Transaction>> transactionCall = bitstampTransactionApi.getTransactions();

        transactionCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if(!response.isSuccessful())
                {
                    Toast.makeText(getContext(),"Error:"+response.code(),Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {

                    List<Transaction> transactions = response.body();
                    ArrayList<Long> temp = new ArrayList<>();
                    for(Transaction transaction : transactions)
                    {

                        data.add(new Entry(Long.parseLong(transaction.getDate()),Float.parseFloat(transaction.getPrice())));

                    }
                    Collections.sort(data,Transaction.timeComparator);
                    int i = 1;
                    while(i != data.size()-1)
                    {
                        //System.out.println((long)data.get(i).getX() +","+data.get(i).getY());
                        if(data.get(i-1).getX() == data.get(i).getX())
                        {
                            //timestamp is same
                            if(data.get(i-1).getY() >= data.get(i).getY())
                            {
                                data.remove(i);
                            }
                            else
                            {
                                data.remove(i-1);
                            }
                        }
                        else
                        {
                            i++;
                        }
                    }



                    LineDataSet lineDataSet = new LineDataSet(data,"BTC/USD");
                    lineDataSet.setLineWidth(2);

                    float lastPrice = data.get(data.size()-1).getY();
                    float secondLastPrice = data.get(data.size()-2).getY();
                    System.out.println((long)data.get(data.size()-1).getX()+":"+data.get(data.size()-1).getY());
                    System.out.println((long)data.get(data.size()-2).getX()+":"+data.get(data.size()-2).getY());
                    System.out.println(lastPrice);
                    System.out.println(secondLastPrice);
                    if(lastPrice - secondLastPrice < 0.0f)
                    {
                        //price is rising
                        System.out.println("Price rising");
                        lineDataSet.setColors(Color.parseColor("#1b9405"));
                        lineDataSet.setDrawFilled(true);
                        lineDataSet.setFillColor(Color.parseColor("#9fdf9f"));
                        lineDataSet.setCircleColor(Color.parseColor("#1b9405"));
                    }
                    else
                    {
                        System.out.println("Price falling");
                        System.out.println(secondLastPrice);
                        lineDataSet.setColors(Color.parseColor("#FF0000"));
                        lineDataSet.setDrawFilled(true);
                        lineDataSet.setFillColor(Color.parseColor("#ffb3b3"));
                        lineDataSet.setCircleColor(Color.parseColor("#ff0000"));
                    }
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet);
                    LineData lineData = new LineData(dataSets);
                    lineChart.setData(lineData);
                    lineChart.getXAxis().setLabelRotationAngle(10);
                    //lineChart.getXAxis().setValueFormatter(new DateFormatter());
                    lineChart.animate();
                    //lineChart.getXAxis().setGranularity(5f);
                    lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    lineChart.invalidate();
                }


            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });


        BitstampTickerApi bitstampTickerApi = rf.create(BitstampTickerApi.class);
        Call<Price> tickerCall = bitstampTickerApi.getPrice();
        tickerCall.enqueue(new Callback<Price>() {
            @Override
            public void onResponse(Call<Price> call, Response<Price> response) {
                if(!response.isSuccessful())
                {
                    //Log.i(TAG, "onResponse: error");
                    return;
                }
                else
                {
                    //tv1.setText("1 BTC = "+response.body().getLast()+" USD");
                }
            }

            @Override
            public void onFailure(Call<Price> call, Throwable t) {

            }
        });
        return view;

    }
}
