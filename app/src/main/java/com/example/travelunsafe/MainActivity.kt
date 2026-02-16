package com.example.travelunsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.travelunsafe.ui.theme.TravelUnSafeTheme
import androidx.activity.OnBackPressedCallback
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.contentcapture.ContentCaptureManager.Companion.isEnabled
import com.example.travelunsafe.components.CustomBottomNavBar

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavBar: CustomBottomNavBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the custom bottom navigation bar
        bottomNavBar = findViewById(R.id.custom_bottom_nav)

        // Setup navigation listeners
        setupBottomNavigation()

        // Setup back press handler
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // If not on home, go to home first
                if (bottomNavBar.getCurrentSelectedTab() != R.id.nav_home) {
                    bottomNavBar.selectTab(R.id.nav_home)
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
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
            // Navigate to home fragment or activity
            // supportFragmentManager.beginTransaction()
            //     .replace(R.id.fragment_container, HomeFragment())
            //     .commit()
        }

        // Messages button
        bottomNavBar.setOnMessagesClickListener {
            Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            // Navigate to messages fragment or activity
            // startActivity(Intent(this, MessagesActivity::class.java))
        }

        // Favorites button
        bottomNavBar.setOnFavoritesClickListener {
            Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show()
            // Navigate to favorites fragment or activity
        }

        // Profile button
        bottomNavBar.setOnProfileClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            // Navigate to profile fragment or activity
        }

        // Guide button (from popup)
        bottomNavBar.setOnGuideClickListener {
            Toast.makeText(this, "Guide clicked", Toast.LENGTH_SHORT).show()
            // Navigate to guide creation activity
            // startActivity(Intent(this, GuideActivity::class.java))
        }

        // Travel Plan button (from popup)
        bottomNavBar.setOnTravelPlanClickListener {
            Toast.makeText(this, "Travel Plan clicked", Toast.LENGTH_SHORT).show()
            // Navigate to travel plan creation activity
            // startActivity(Intent(this, TravelPlanActivity::class.java))
        }
    }
}
