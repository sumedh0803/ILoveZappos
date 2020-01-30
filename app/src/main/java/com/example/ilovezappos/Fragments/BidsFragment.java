package com.example.ilovezappos.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilovezappos.API.BitstampBidsApi;
import com.example.ilovezappos.R;
import com.example.ilovezappos.Utils.Bids;
import com.example.ilovezappos.Utils.BidsAdapter;
import com.example.ilovezappos.Utils.BidsItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BidsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private  RecyclerView.LayoutManager layoutManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bids,container,false);
        getActivity().setTitle("Bids");
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BitstampBidsApi bitstampBidsApi = rf.create(BitstampBidsApi.class);
        Call<Bids> bidsCall = bitstampBidsApi.getBids();
        bidsCall.enqueue(new Callback<Bids>() {
            @Override
            public void onResponse(Call<Bids> call, Response<Bids> response) {

                ArrayList<BidsItem> bidsData = new ArrayList<>();
                for(List<String> bid : response.body().getBids())
                {
                    Float temp = (Float.parseFloat(bid.get(0))*Float.parseFloat(bid.get(1)));
                    String tempS = temp.toString();
                    bidsData.add(new BidsItem(bid.get(0),bid.get(1),tempS));
                    //System.out.println(bid.get(0)+":"+bid.get(1));
                }
                recyclerView = view.findViewById(R.id.recycler);
                layoutManager = new LinearLayoutManager(getContext());
                adapter = new BidsAdapter(bidsData);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onFailure(Call<Bids> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
        return view;
    }
}
