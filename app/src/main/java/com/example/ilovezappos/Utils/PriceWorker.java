package com.example.ilovezappos.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ilovezappos.API.BitstampTickerApi;
import com.example.ilovezappos.MainActivity;
import com.example.ilovezappos.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PriceWorker extends Worker {
    Context context;
    public PriceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        FileInputStream fileInputStream = null;
        String priceFile = "";
        try {
            fileInputStream = context.openFileInput("price.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            priceFile = bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BitstampTickerApi bitstampTickerApi = rf.create(BitstampTickerApi.class);
        Call<Price> tickerCall = bitstampTickerApi.getPrice();

        final String finalPriceFile = priceFile;
        System.out.println(priceFile);

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
                    String priceAPI = response.body().getLast();
                    System.out.println(priceAPI);
                    if(Float.parseFloat(priceAPI) < Float.parseFloat(finalPriceFile))
                    {
                        showNotification("Rates have fallen!","Rates have fallen below "+finalPriceFile+". Click here to check them out. ");
                    }
                }
            }

            @Override
            public void onFailure(Call<Price> call, Throwable t) {

            }
        });
        return Result.success();
    }
    private void showNotification(String title, String desc) {

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "task_channel";
        String channelName = "task_name";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText(desc)
                .setContentIntent(pendingNotificationIntent)
                .setSmallIcon(R.drawable.ic_icon)
                .setAutoCancel(true);


        manager.notify(1, builder.build());

    }
}
