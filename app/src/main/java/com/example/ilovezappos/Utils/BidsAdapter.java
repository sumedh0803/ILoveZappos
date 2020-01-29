package com.example.ilovezappos.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilovezappos.R;

import java.util.ArrayList;

public class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.BidsViewHolder> {
    ArrayList<BidsItem> bidsList;
    public BidsAdapter(ArrayList<BidsItem> bidsList) {
        this.bidsList = bidsList;
    }

    @NonNull
    @Override
    public BidsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bids_item,parent,false);
        BidsViewHolder bvh = new BidsViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BidsViewHolder holder, int position) {
        BidsItem item = bidsList.get(position);
        holder.value.setText(item.getValue());
        holder.amt.setText(item.getAmt());
        holder.bids.setText(item.getBids());


    }

    @Override
    public int getItemCount() {
        return bidsList.size();
    }

    public static class BidsViewHolder extends RecyclerView.ViewHolder
    {
        public TextView bids,value,amt;

        public BidsViewHolder(@NonNull View itemView) {
            super(itemView);
            bids = itemView.findViewById(R.id.bids);
            value = itemView.findViewById(R.id.value);
            amt = itemView.findViewById(R.id.amt);

        }
    }
}
