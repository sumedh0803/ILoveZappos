package com.example.ilovezappos.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilovezappos.R;

import java.util.ArrayList;

public class AsksAdapter extends RecyclerView.Adapter<AsksAdapter.AsksViewHolder> {
    ArrayList<AsksItem> asksList;
    public AsksAdapter(ArrayList<AsksItem> asksList) {
        this.asksList = asksList;
    }

    @NonNull
    @Override
    public AsksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asks_item,parent,false);
        AsksViewHolder bvh = new AsksViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull AsksAdapter.AsksViewHolder holder, int position) {
        AsksItem item = asksList.get(position);
        holder.value.setText(item.getValue());
        holder.amt.setText(item.getAmt());
        holder.asks.setText(item.getAsks());


    }

    @Override
    public int getItemCount() {
        return asksList.size();
    }

    public static class AsksViewHolder extends RecyclerView.ViewHolder
    {
        public TextView asks,value,amt;

        public AsksViewHolder(@NonNull View itemView) {
            super(itemView);
            asks = itemView.findViewById(R.id.asks);
            value = itemView.findViewById(R.id.value);
            amt = itemView.findViewById(R.id.amt);

        }
    }
}
