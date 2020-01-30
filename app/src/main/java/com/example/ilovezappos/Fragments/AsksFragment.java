package com.example.ilovezappos.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilovezappos.API.BitstampAsksApi;
import com.example.ilovezappos.API.BitstampBidsApi;
import com.example.ilovezappos.R;
import com.example.ilovezappos.Utils.Asks;
import com.example.ilovezappos.Utils.AsksAdapter;
import com.example.ilovezappos.Utils.Bids;
import com.example.ilovezappos.Utils.BidsAdapter;
import com.example.ilovezappos.Utils.AsksItem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AsksFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView timestamp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_asks,container,false);
        getActivity().setTitle("Asks");
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BitstampAsksApi bitstampAsksApi = rf.create(BitstampAsksApi.class);
        Call<Asks> asksCall = bitstampAsksApi.getAsks();
        asksCall.enqueue(new Callback<Asks>() {
            @Override
            public void onResponse(Call<Asks> call, Response<Asks> response) {

                ArrayList<AsksItem> asksData = new ArrayList<>();
                for(List<String> ask : response.body().getAsks())
                {
                    Float temp = (Float.parseFloat(ask.get(0))*Float.parseFloat(ask.get(1)));
                    String tempS = temp.toString();
                    asksData.add(new AsksItem(ask.get(0),ask.get(1),tempS));
                    //System.out.println(bid.get(0)+":"+bid.get(1));
                }
                recyclerView = view.findViewById(R.id.recycler);
                timestamp = view.findViewById(R.id.timestamp);
                layoutManager = new LinearLayoutManager(getContext());
                adapter = new AsksAdapter(asksData);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                //System.out.println(response.body().getTimestamp());
                String timest = response.body().getTimestamp();
                java.util.Date time=new java.util.Date((Long.parseLong(timest)*1000));
                timestamp.setText(time.toString());


            }

            @Override
            public void onFailure(Call<Asks> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
        return view;
    }
}
