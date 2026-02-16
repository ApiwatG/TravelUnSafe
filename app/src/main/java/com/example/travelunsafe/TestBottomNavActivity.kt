package com.example.travelunsafe

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.travelunsafe.components.CustomBottomNavBar

class TestBottomNavActivity : AppCompatActivity() {

    private lateinit var bottomNavBar: CustomBottomNavBar
    private lateinit var tvCurrentPage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bottom_nav)

        // Find views
        tvCurrentPage = findViewById(R.id.tv_current_page)
        bottomNavBar = findViewById(R.id.custom_bottom_nav)

        // Setup navigation
        setupBottomNavigation()
        
        // Setup back press handler
        setupBackPressHandler()
        
        // Set initial page
        updatePageText("Home")
    }
    
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // If not on home, go to home first
                if (bottomNavBar.getCurrentSelectedTab() != R.id.nav_home) {
                    bottomNavBar.selectTab(R.id.nav_home)
                    updatePageText("Home")
                } else {
                    // If already on home, exit the app
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupBottomNavigation() {
        // Home button
        bottomNavBar.setOnHomeClickListener {
            updatePageText("Home")
            showToast("Home clicked")
        }

        // Messages button
        bottomNavBar.setOnMessagesClickListener {
            updatePageText("Messages")
            showToast("Messages clicked")
        }

        // Favorites button
        bottomNavBar.setOnFavoritesClickListener {
            updatePageText("Favorites")
            showToast("Favorites clicked")
        }

        // Profile button
        bottomNavBar.setOnProfileClickListener {
            updatePageText("Profile")
            showToast("Profile clicked")
        }

        // Guide button (from popup)
        bottomNavBar.setOnGuideClickListener {
            updatePageText("Guide Creation")
            showToast("Guide clicked - Navigate to Guide creation screen")
        }

        // Travel Plan button (from popup)
        bottomNavBar.setOnTravelPlanClickListener {
            updatePageText("Travel Plan Creation")
            showToast("Travel Plan clicked - Navigate to Travel Plan screen")
        }
    }

    private fun updatePageText(pageName: String) {
        tvCurrentPage.text = "Current Page: $pageName"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
