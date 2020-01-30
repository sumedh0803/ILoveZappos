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

public class AsksAdapter extends RecyclerView.Adapter<AsksAdapter.AsksViewHolder> {
    ArrayList<AsksItem> asksList;
    Context context;

    public AsksAdapter(ArrayList<AsksItem> asksList, Context context) {
        this.asksList = asksList;
        this.context = context;
    }

    @NonNull
    @Override
    public AsksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asks_item,parent,false);
        AsksViewHolder bvh = new AsksViewHolder(v);
        return bvh;
    }
    private void setAnimation(View viewToAnimate) {

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);


    }

    @Override
    public void onBindViewHolder(@NonNull AsksAdapter.AsksViewHolder holder, int position) {
        AsksItem item = asksList.get(position);
        holder.value.setText(item.getValue());
        holder.amt.setText(item.getAmt());
        holder.asks.setText(item.getAsks());
        setAnimation(holder.item_parent);


    }

    @Override
    public int getItemCount() {
        return asksList.size();
    }

    public static class AsksViewHolder extends RecyclerView.ViewHolder
    {
        public TextView asks,value,amt;
        ConstraintLayout item_parent;

        public AsksViewHolder(@NonNull View itemView) {
            super(itemView);
            asks = itemView.findViewById(R.id.asks);
            value = itemView.findViewById(R.id.value);
            amt = itemView.findViewById(R.id.amt);
            item_parent = itemView.findViewById(R.id.item_parent);

        }
    }
}
