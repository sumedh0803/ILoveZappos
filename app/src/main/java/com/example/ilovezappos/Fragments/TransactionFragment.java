package com.example.ilovezappos.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.ilovezappos.API.BitstampTickerApi;
import com.example.ilovezappos.API.BitstampTransactionApi;
import com.example.ilovezappos.R;
import com.example.ilovezappos.Utils.Price;
import com.example.ilovezappos.Utils.PriceWorker;
import com.example.ilovezappos.Utils.Transaction;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionFragment extends Fragment {
    private ArrayList<Entry> data = new ArrayList<>();
    private LineChart lineChart;
    private TextView currPrice,priceAlert;
    private Button setalert, cancelalert;
    private EditText alertprice;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout progress;
    @Nullable
    @Override
    //Creates the fragment UI and returns the view to Main Activity
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        getActivity().setTitle("Transactions");
        bindViews(view);
        loadUiElements();

        //When "set alert" button is clicked, a custom alert dialog is shown, which takes user input for the BTC rate, stores it in a file,
        //and starts the PeriodicWorkRequest
        setalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View dialogbox = layoutInflater.inflate(R.layout.dialog_custom, null);
                alertprice = dialogbox.findViewById(R.id.alertprice);
                builder.setView(dialogbox);
                builder.setTitle("Set Alert");
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"Alert was set",Toast.LENGTH_LONG).show();
                        String price = alertprice.getText().toString();
                        priceAlert.setText(price+" USD");
                        FileOutputStream fOut;
                        try {
                            fOut = getContext().openFileOutput("price.txt",getContext().MODE_PRIVATE);
                            System.out.println("price is"+price);
                            fOut.write(price.getBytes());
                            fOut.close();
                            startWorker();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();

            }
        });
        //When "cancel" button is clicked, file containing price data is deleted and work is cancelled. Thus no alerts are sent to the user
        cancelalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WorkManager mWorkManager = WorkManager.getInstance();
                mWorkManager.cancelAllWork();
                priceAlert.setText("Alert not set.");
                File f = new File(getContext().getFilesDir(),"price.txt");
                f.delete();
                Toast.makeText(getActivity(),"Alert Cancelled",Toast.LENGTH_LONG).show();

            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        //For aesthetic purposes, the SwipeRefreshLayout will run for 2500ms and then get dismissed.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUiElements();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);

            }
        });
        return view;

    }

    //Sets the alert by starting a PeriodicWorkRequest. Price alerts are sent every 1 hour.
    private void startWorker()
    {
        final WorkManager mWorkManager = WorkManager.getInstance();
        final PeriodicWorkRequest mRequest = new PeriodicWorkRequest.Builder(PriceWorker.class,1, TimeUnit.HOURS)
                .build();
        //Before enqueueing our request we need to cancel all previous requests, else multiple requests will
        //be enqueued and multiple notifications will be sent to the user at irregular intervals.
        mWorkManager.cancelAllWork();
        mWorkManager.enqueue(mRequest);
    }

    //Loads all the UI Elements like Graph, Price alert and current price.
    private void loadUiElements() {
        lineChart.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        boolean alertCreated = checkAlertCreated();
        if(alertCreated)
        {
            FileInputStream fileInputStream = null;
            String priceFile = "";
            try {
                fileInputStream = getContext().openFileInput("price.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                priceFile = bufferedReader.readLine();
                bufferedReader.close();
                inputStreamReader.close();
                priceAlert.setText(priceFile+" USD");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            priceAlert.setText("Alert not set");
        }
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

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
                    data = generateGraphData(response);
                    showGraph(data,lineChart);
                }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getContext(),"Error:"+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        lineChart.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);

        BitstampTickerApi bitstampTickerApi = rf.create(BitstampTickerApi.class);
        Call<Price> tickerCall = bitstampTickerApi.getPrice();
        tickerCall.enqueue(new Callback<Price>() {
            @Override
            public void onResponse(Call<Price> call, Response<Price> response) {
                if(!response.isSuccessful())
                {
                    Toast.makeText(getContext(),"Error:"+response.code(),Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    currPrice.setText(response.body().getLast()+" USD");
                }
            }

            @Override
            public void onFailure(Call<Price> call, Throwable t) {

            }
        });
    }

    //Checks if price alert is set or not. if "price.txt" is present in the internal memory, the alert is set.
    //Once the alert is removed, the file gets deleted and thus we can ensure that no alert is set.
    private boolean checkAlertCreated() {
        File f = new File(getContext().getFilesDir(),"price.txt");
        return f.exists();
    }

    //Takes data and chart view and sets different properties of the chart.
    private void showGraph(ArrayList<Entry> data, LineChart chart)
    {
        LineDataSet lineDataSet = new LineDataSet(data,"BTC/USD");
        lineDataSet.setLineWidth(2);

        //price is rising
        lineDataSet.setColors(Color.parseColor("#2979ff"));
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.parseColor("#b3cfff"));
        lineDataSet.setCircleColor(Color.parseColor("#2979ff"));


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.animateX(1500, Easing.Linear);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        chart.invalidate();
    }

    //Generated graph data from the responses received from the API. This data is later used by showGraph() method.
    private ArrayList<Entry> generateGraphData(Response<List<Transaction>> response)
    {
        ArrayList<Entry> datalocal = new ArrayList<Entry>();
        List<Transaction> transactions = response.body();
        ArrayList<Long> temp = new ArrayList<>();
        for(Transaction transaction : transactions)
        {

            datalocal.add(new Entry(Long.parseLong(transaction.getDate()),Float.parseFloat(transaction.getPrice())));

        }
        Collections.sort(datalocal,Transaction.timeComparator);
        int i = 1;
        while(i != datalocal.size()-1)
        {
            if(datalocal.get(i-1).getX() == datalocal.get(i).getX())
            {
                //timestamp is same
                if(datalocal.get(i-1).getY() >= datalocal.get(i).getY())
                {
                    datalocal.remove(i);
                }
                else
                {
                    datalocal.remove(i-1);
                }
            }
            else
            {
                i++;
            }
        }
        return datalocal;
    }

    //Binds all views in the UI
    private void bindViews(View view)
    {
        lineChart = view.findViewById(R.id.graph);
        currPrice = view.findViewById(R.id.currPrice);
        priceAlert = view.findViewById(R.id.priceAlert);
        setalert = view.findViewById(R.id.setalert);
        cancelalert = view.findViewById(R.id.cancelAlert);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        progress = view.findViewById(R.id.progress);
    }
}
