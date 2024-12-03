package com.example.mini_projet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        if (mAuth.getCurrentUser() == null) {
            // If the user is not logged in, navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity to prevent it from being accessible in the back stack
        } else {
            // If the user is logged in, show the main app content
            setContentView(R.layout.activity_main);

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

            // Default Fragment on Start
            loadFragment(new HomeActivity()); // Set HomeFragment initially

            bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;

                // Switch fragments based on menu selection
                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeActivity();
                } else if (item.getItemId() == R.id.nav_favorites) {
                    selectedFragment = new FavoritesActivity();
                }

                return loadFragment(selectedFragment);
            });
        }
    }

    // Method to load a fragment into the container
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null); // Optional: Add to back stack
            transaction.commit();
            return true;
        }
        return false;
    }
}
