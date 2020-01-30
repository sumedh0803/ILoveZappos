package com.example.ilovezappos.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilovezappos.R;

import java.util.ArrayList;

public class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.BidsViewHolder> {
    ArrayList<BidsItem> bidsList;
    Context context;
    public BidsAdapter(ArrayList<BidsItem> bidsList, Context context) {
        this.bidsList = bidsList;
        this.context = context;
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
        setAnimation(holder.item_parent);


    }
    private void setAnimation(View viewToAnimate) {

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
        viewToAnimate.startAnimation(animation);


    }

    @Override
    public int getItemCount() {
        return bidsList.size();
    }

    public static class BidsViewHolder extends RecyclerView.ViewHolder
    {
        public TextView bids,value,amt;
        ConstraintLayout item_parent;

        public BidsViewHolder(@NonNull View itemView) {
            super(itemView);
            bids = itemView.findViewById(R.id.bids);
            value = itemView.findViewById(R.id.value);
            amt = itemView.findViewById(R.id.amt);
            item_parent = itemView.findViewById(R.id.item_parent);

        }
    }
}
