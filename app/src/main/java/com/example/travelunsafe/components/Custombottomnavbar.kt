package com.example.travelunsafe.components



import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.travelunsafe.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomBottomNavBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var bottomNav: BottomNavigationView
    private var fabAdd: CardView
    private var popupWindow: PopupWindow? = null

    // Listeners
    private var onHomeClickListener: (() -> Unit)? = null
    private var onMessagesClickListener: (() -> Unit)? = null
    private var onFavoritesClickListener: (() -> Unit)? = null
    private var onProfileClickListener: (() -> Unit)? = null
    private var onGuideClickListener: (() -> Unit)? = null
    private var onTravelPlanClickListener: (() -> Unit)? = null

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.layout_custom_bottom_nav, this, true)

        // Find views
        bottomNav = findViewById(R.id.bottom_navigation)
        fabAdd = findViewById(R.id.fab_add)

        // Setup navigation
        setupBottomNavigation()
        setupFabClick()
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    onHomeClickListener?.invoke()
                    true
                }
                R.id.nav_messages -> {
                    onMessagesClickListener?.invoke()
                    true
                }
                R.id.nav_favorites -> {
                    onFavoritesClickListener?.invoke()
                    true
                }
                R.id.nav_profile -> {
                    onProfileClickListener?.invoke()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFabClick() {
        fabAdd.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(anchor: View) {
        // Dismiss existing popup if any
        popupWindow?.dismiss()

        // Inflate the popup layout
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_add_menu, null)

        // Create popup window
        val popupWidth = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        val popupHeight = context.resources.getDimensionPixelSize(R.dimen.popup_height)

        popupWindow = PopupWindow(
            popupView,
            popupWidth,
            popupHeight,
            true
        )

        // Set background and elevation
        popupWindow?.elevation = 12f
        popupWindow?.setBackgroundDrawable(
            ContextCompat.getDrawable(context, R.drawable.popup_background)
        )
        popupWindow?.animationStyle = R.style.PopupAnimation

        // Get buttons from popup
        val btnGuide = popupView.findViewById<CardView>(R.id.btn_guide)
        val btnTravelPlan = popupView.findViewById<CardView>(R.id.btn_travel_plan)

        // Set click listeners
        btnGuide.setOnClickListener {
            onGuideClickListener?.invoke()
            popupWindow?.dismiss()
        }

        btnTravelPlan.setOnClickListener {
            onTravelPlanClickListener?.invoke()
            popupWindow?.dismiss()
        }

        // Calculate position to show above the FAB
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        val xOffset = location[0] - popupWidth / 2 + anchor.width / 2
        val yOffset = location[1] - popupHeight - 20

        // Show popup
        popupWindow?.showAtLocation(anchor, Gravity.NO_GRAVITY, xOffset, yOffset)
    }

    // Public methods to set listeners
    fun setOnHomeClickListener(listener: () -> Unit) {
        onHomeClickListener = listener
    }

    fun setOnMessagesClickListener(listener: () -> Unit) {
        onMessagesClickListener = listener
    }

    fun setOnFavoritesClickListener(listener: () -> Unit) {
        onFavoritesClickListener = listener
    }

    fun setOnProfileClickListener(listener: () -> Unit) {
        onProfileClickListener = listener
    }

    fun setOnGuideClickListener(listener: () -> Unit) {
        onGuideClickListener = listener
    }

    fun setOnTravelPlanClickListener(listener: () -> Unit) {
        onTravelPlanClickListener = listener
    }

    // Method to select specific tab programmatically
    fun selectTab(tabId: Int) {
        bottomNav.selectedItemId = tabId
    }

    // Method to get current selected tab
    fun getCurrentSelectedTab(): Int {
        return bottomNav.selectedItemId
    }

    // Dismiss popup if showing
    fun dismissPopup() {
        popupWindow?.dismiss()
    }
}