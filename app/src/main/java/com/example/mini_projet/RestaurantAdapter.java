package com.example.mini_projet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private final List<Restaurant> restaurantList;
    private final Context context;

    // Constructor
    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.nameTextView.setText(restaurant.getName());
        holder.addressTextView.setText(restaurant.getAddress());
        holder.ratingTextView.setText("Rating: " + restaurant.getRating());
        // Set OnClickListener on the whole item (or a specific view)
        holder.itemView.setOnClickListener(v -> {
            // Start a new activity and pass the restaurant data
            Intent intent = new Intent(context, RestaurantDetailsActivity.class);
            intent.putExtra("restaurant_id",restaurant.getId());
            intent.putExtra("restaurant_name", restaurant.getName());
            intent.putExtra("restaurant_address", restaurant.getAddress());
            intent.putExtra("restaurant_latitude", restaurant.getLatitude());
            intent.putExtra("restaurant_longitude", restaurant.getLongitude());
            intent.putExtra("restaurant_rating", restaurant.getRating());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // ViewHolder Class
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView ratingTextView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvRestaurantName);
            addressTextView = itemView.findViewById(R.id.tvRestaurantAddress);
            ratingTextView = itemView.findViewById(R.id.tvRestaurantRating);
        }
    }
}

