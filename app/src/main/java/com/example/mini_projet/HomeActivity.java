package com.example.mini_projet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Fragment {

    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurantList;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude;
    private double userLongitude;


    public HomeActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_home, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerViewRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize data list and adapter
        restaurantList = new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(requireContext(), restaurantList);
        recyclerView.setAdapter(restaurantAdapter);

        // Initialize location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Check if location services are enabled
        checkLocationServices();

        // Fetch data from Firestore
        fetchRestaurantsFromFirestore();

        // Check and request location permissions if necessary
        requestLocationPermissions();

        return rootView;
    }

    private void checkLocationServices() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Enable Location")
                    .setMessage("Location services are required for this app. Please enable them.")
                    .setPositiveButton("Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Permission granted, fetch location
            fetchUserLocation();
        }
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        Log.d("Location", "Lat: " + userLatitude + ", Long: " + userLongitude);
                        Toast.makeText(requireContext(), "Location: " + userLatitude + ", " + userLongitude, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("LocationError", "Location is null.");
                        Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("LocationError", "Error fetching location: " + e.getMessage()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                fetchUserLocation();
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Permission denied. Cannot fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }

    private void fetchRestaurantsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(requireContext(), "No restaurants found.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Restaurant restaurant = new Restaurant();

                            restaurant.setName(document.getString("name"));
                            restaurant.setAddress(document.getString("address"));
                            restaurant.setId(document.getString("id"));

                            // Handle nullable fields for latitude, longitude, and rating
                            if (document.contains("latitude") && document.getDouble("latitude") != null) {
                                restaurant.setLatitude(document.getDouble("latitude"));
                            } else {
                                restaurant.setLatitude(0.0);  // or a default value
                            }

                            if (document.contains("longitude") && document.getDouble("longitude") != null) {
                                restaurant.setLongitude(document.getDouble("longitude"));
                            } else {
                                restaurant.setLongitude(0.0);  // or a default value
                            }

                            if (document.contains("rating") && document.getDouble("rating") != null) {
                                restaurant.setRating(document.getDouble("rating"));
                            } else {
                                restaurant.setRating(0.0);  // or a default value
                            }

                            Log.d("Firestore", "Document data: " + document.getData());
                            Log.d("Firestore", "Mapped Restaurant: " +
                                    "Name: " + restaurant.getName() +
                                    ", Latitude: " + restaurant.getLatitude() +
                                    ", Longitude: " + restaurant.getLongitude() +
                                    ", Rating: " + restaurant.getRating());

                            // Calculate distance
                            double distance = calculateDistance(userLatitude, userLongitude, restaurant.getLatitude(), restaurant.getLongitude());
                            Log.d("Distance", "Distance: " + distance);

                            // If within 5 km, add to list
                            if (distance <= 100) {
                                restaurantList.add(restaurant);
                            }
                        }

                        // Notify adapter about data change
                        restaurantAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}




