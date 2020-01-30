package com.example.ilovezappos.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    ArrayList<Entry> data = new ArrayList<Entry>();
    LineChart lineChart;
    TextView currPrice,priceAlert;
    Button setalert, cancelalert;
    EditText alertprice;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        getActivity().setTitle("Transactions");
        lineChart = view.findViewById(R.id.graph);
        currPrice = view.findViewById(R.id.currPrice);
        priceAlert = view.findViewById(R.id.priceAlert);
        setalert = view.findViewById(R.id.setalert);
        cancelalert = view.findViewById(R.id.cancelAlert);
        File f = new File(getContext().getFilesDir(),"price.txt");
        if(f.exists())
        {
            System.out.println(f.exists());
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
            System.out.println(f.exists());
            priceAlert.setText("Alert not set");
        }
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        lineChart = view.findViewById(R.id.graph);
        currPrice = view.findViewById(R.id.currPrice);
        priceAlert = view.findViewById(R.id.priceAlert);
        setalert = view.findViewById(R.id.setalert);
        cancelalert = view.findViewById(R.id.cancelAlert);

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
                    lineChart.animateX(1500, Easing.Linear);
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
                    currPrice.setText(response.body().getLast()+" USD");
                }
            }

            @Override
            public void onFailure(Call<Price> call, Throwable t) {

            }
        });

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

                        System.out.println("LAYOUT ID: "+alertprice.getText().toString());
                        String price = alertprice.getText().toString();
                        priceAlert.setText(price+" USD");
                        FileOutputStream fOut;
                        try {
                            fOut = getContext().openFileOutput("price.txt",getContext().MODE_PRIVATE);
                            System.out.println("price is"+price);
                            fOut.write(price.getBytes());
                            fOut.close();
                            final WorkManager mWorkManager = WorkManager.getInstance();
                            final PeriodicWorkRequest mRequest = new PeriodicWorkRequest.Builder(PriceWorker.class,1, TimeUnit.HOURS)
                                    .build();
                            mWorkManager.cancelAllWork();
                            mWorkManager.enqueue(mRequest);

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
        cancelalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WorkManager mWorkManager = WorkManager.getInstance();
                mWorkManager.cancelAllWork();
                priceAlert.setText("Alert not set.");
                File f = new File(getContext().getFilesDir(),"price.txt");
                f.delete();

            }
        });

        return view;

    }
}
