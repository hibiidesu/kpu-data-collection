package com.example.kpudata

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val listView: ListView = findViewById(R.id.menuListView)

        // 1. Initialize our menu data
        val menuOptions = listOf(
            DashboardMenu(android.R.drawable.ic_dialog_info, getString(R.string.info_button)),
            DashboardMenu(android.R.drawable.ic_menu_edit, getString(R.string.form_button)),
            DashboardMenu(android.R.drawable.ic_menu_view, getString(R.string.show_button)),
            DashboardMenu(android.R.drawable.ic_lock_power_off, getString(R.string.exit_button))
        )

        // 2. Set the Adapter
        val adapter = MenuAdapter(this, menuOptions)
        listView.adapter = adapter

        // 3. Handle Navigation Clicks
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    // Navigate to Information Screen
                    startActivity(Intent(this, InfoActivity::class.java))
                }
                1 -> {
                    // Navigate to Data Entry Form
                    startActivity(Intent(this, FormEntryActivity::class.java))
                }
                2 -> {
                    // Navigate to View Data Screen
                    startActivity(Intent(this, ViewDataActivity::class.java))
                }
                3 -> {
                    // Exit the Application safely
                    finishAffinity()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}