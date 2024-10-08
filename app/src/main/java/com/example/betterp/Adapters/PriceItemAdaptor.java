package com.example.betterp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betterp.Model.PriceItem;
import com.example.betterp.R;

import java.util.ArrayList;

public class PriceItemAdaptor  extends RecyclerView.Adapter<PriceItemAdaptor.ItemViewHolder>{
    private Context context;
    private ArrayList<PriceItem> priceItems;
    public PriceItemAdaptor(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.rv_items,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PriceItem priceItem = priceItems.get(position);
        Glide.with(context)
                .load(priceItem.getRideImageUrl())// Optional placeholder while loading// Optional error image if loading fails
                .into(holder.rideTypeImage);  // Load the image into the ImageView
        holder.rideTypeText.setText(String.valueOf(priceItem.getRideType()));
        holder.ridePrice.setText(priceItem.getRidePrice());
    }





    @Override
    public int getItemCount() {
        return priceItems.size();
    }
    public void setPriceItems(ArrayList<PriceItem> priceItems){
        this.priceItems=priceItems;
        notifyDataSetChanged();
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView rideTypeImage;
        TextView  rideTypeText , ridePrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            rideTypeImage=itemView.findViewById(R.id.ride_type_car_image);
            rideTypeText=itemView.findViewById(R.id.ride_type);
            ridePrice=itemView.findViewById(R.id.ride_type_price);

        }
    }
}
