package com.example.chexanhfe.view

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.chexanhfe.R
import com.example.chexanhfe.screen.CartFragment
import com.example.chexanhfe.screen.DiscountFragment
import com.example.chexanhfe.screen.HomeFragment
import com.example.chexanhfe.screen.OderFragment
import com.example.chexanhfe.screen.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        // Khởi tạo bottomMenu trước khi sử dụng
        bottomMenu = findViewById(R.id.bottom_navigation_view)
        bottomMenu.background = null
        bottomMenu.menu.getItem(2).isEnabled = false

        bottomMenu.setOnItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.home -> {
                    replaceFragment(HomeFragment(), "Home")
                    true
                }
                R.id.cod -> {
                    replaceFragment(OderFragment(),  "Oder")
                    true
                }
                R.id.placeholder -> {
                    replaceFragment(CartFragment(),  "Cart")
                    true
                }
                R.id.discount -> {
                    replaceFragment(DiscountFragment(),  "Discount")
                    true
                }
                R.id.person -> {
                    replaceFragment(ProfileFragment(),  "Profile")
                    true
                }
                else -> false
            }
        }

        // Thiết lập sự kiện click cho FloatingActionButton
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {

            // Đặt item placeholder thành checked khi click FAB
            bottomMenu.selectedItemId = R.id.placeholder
            replaceFragment(CartFragment(), "Giỏ hàng")
        }

        // Tải fragment mặc định khi activity khởi động
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment(), "Home")
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Sử dụng fragment_container
                .addToBackStack(null) // hỗ trợ back
                .commit()

            this.title = title
        } catch (e: Exception) {
            Log.e("MainActivity", "Error replacing fragment", e)
        }
    }
}