package com.example.mini_projet;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

public class RestaurantDetailsActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        // Initialize the MapView
        mapView = findViewById(R.id.osm_mapview);

        // Set up the MapView
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Choose the tile source (default is MAPNIK)
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Enable the current location feature if needed
        // mapView.setUseDataConnection(true);

        // Get the restaurant data

        String name = getIntent().getStringExtra("restaurant_name");
        String address = getIntent().getStringExtra("restaurant_address");
        double latitude = getIntent().getDoubleExtra("restaurant_latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("restaurant_longitude", 0.0);
        double rating = getIntent().getDoubleExtra("restaurant_rating", 0.0);

        // Display restaurant details
        TextView nameTextView = findViewById(R.id.tvRestaurantName);
        TextView addressTextView = findViewById(R.id.tvRestaurantAddress);
        TextView ratingTextView = findViewById(R.id.tvRestaurantRating);
        nameTextView.setText(name);
        addressTextView.setText(address);
        ratingTextView.setText("Rating: " + rating);

        // Add a marker for the restaurant location
        GeoPoint restaurantLocation = new GeoPoint(latitude, longitude);
        Marker marker = new Marker(mapView);
        marker.setPosition(restaurantLocation);
        marker.setTitle(name);
        mapView.getOverlays().add(marker);

        // Set the zoom and center the map on the restaurant location
        IMapController mapController = mapView.getController();
        mapController.setZoom(15);
        mapController.setCenter(restaurantLocation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the current map state to avoid data loss on rotation
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore map state when the activity is resumed
        mapView.onResume();
    }

    public void addtofavorites(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("addtofavorites", "User not logged in");
            return;
        }
        String userId = currentUser.getUid();

// Assuming 'restaurantId' is the ID of the restaurant being added to favorites
        String restaurantId =  getIntent().getStringExtra("restaurant_id");;

// Add the restaurant to the user's favorites array
        db.collection("users").document(userId)
                .update("favorites", FieldValue.arrayUnion(restaurantId)) // Add to favorites array
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Favorite added successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding favorite", e);
                });

    }
}
