package com.example.ilovezappos.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    //Creates the fragment UI and returns the view to Main Activity
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_asks,container,false);
        getActivity().setTitle("Asks");

        bindViews(view);
        loadUiElements();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
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

    //Loads all the UI Elements like recyclerView and timestamp.
    private void loadUiElements() {
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BitstampAsksApi bitstampAsksApi = rf.create(BitstampAsksApi.class);
        Call<Asks> asksCall = bitstampAsksApi.getAsks();
        asksCall.enqueue(new Callback<Asks>() {
            @Override
            public void onResponse(Call<Asks> call, Response<Asks> response) {
                ArrayList<AsksItem> asksData;
                asksData = generateAsksData(response);
                prepareRecyclerView(asksData,getContext());

                java.util.Date time=new java.util.Date((Long.parseLong(response.body().getTimestamp())*1000));
                timestamp.setText(time.toString());
            }
            @Override
            public void onFailure(Call<Asks> call, Throwable t) {
                Toast.makeText(getContext(),"ERROR:"+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //Takes data and prepares the recyclerView.
    private void prepareRecyclerView(ArrayList<AsksItem> asksData, Context context) {
        layoutManager = new LinearLayoutManager(context);
        adapter = new AsksAdapter(asksData,context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_animation);
        recyclerView.setLayoutAnimation(animationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    //Generated recyclerview data from the responses received from the API. This data is later used by prepareRecyclerView() method.
    private ArrayList<AsksItem> generateAsksData(Response<Asks> response) {
        ArrayList<AsksItem> asksDatalocal = new ArrayList<>();
        for(List<String> ask : response.body().getAsks())
        {
            Float temp = (Float.parseFloat(ask.get(0))*Float.parseFloat(ask.get(1)));
            String tempS = temp.toString();
            asksDatalocal.add(new AsksItem(ask.get(0),ask.get(1),tempS));
        }
        return asksDatalocal;
    }

    //Binds all views in the UI
    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        timestamp = view.findViewById(R.id.timestamp);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
    }
}
