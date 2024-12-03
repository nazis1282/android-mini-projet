package com.example.mini_projet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> favoriteRestaurants;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_favorites, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));  // Use getContext() to get the correct context
        favoriteRestaurants = new ArrayList<>();
        adapter = new RestaurantAdapter(getContext(),favoriteRestaurants);
        recyclerView.setAdapter(adapter);

        // Load favorite restaurants (you'll need to add Firebase logic here)
        fetchFavorites();
        return rootView;
    }
    private void fetchFavorites() {
        String userId = auth.getCurrentUser().getUid();

        // Get the user's favorites
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoriteIds = (List<String>) documentSnapshot.get("favorites");
                        if (favoriteIds != null && !favoriteIds.isEmpty()) {
                            fetchRestaurantDetails(favoriteIds);
                        } else {
                            Toast.makeText(requireContext(), "No favorites added yet!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "User document not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FavoriteActivity", "Error fetching favorites: ", e);
                    Toast.makeText(requireContext(), "Error fetching favorites!", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchRestaurantDetails(List<String> favoriteIds) {
        // Fetch restaurant details based on IDs
        db.collection("restaurants")
                .whereIn(FieldPath.documentId(), favoriteIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    favoriteRestaurants.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String name = document.getString("name");
                        String address = document.getString("address");
                        Double rating = document.getDouble("rating");

                        Restaurant restaurant = new Restaurant(name, address, rating);
                        favoriteRestaurants.add(restaurant);
                    }

                    // Notify the adapter of changes
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("FavoriteActivity", "Error fetching restaurant details: ", e);
                    Toast.makeText(requireContext(), "Error fetching restaurant details!", Toast.LENGTH_SHORT).show();
                });
    }
}
