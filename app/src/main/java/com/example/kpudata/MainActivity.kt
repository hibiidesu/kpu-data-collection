package com.example.kpudata

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val listView: ListView = findViewById(R.id.menuListView)

        val menuOptions = listOf(
            DashboardMenu(android.R.drawable.ic_dialog_info, getString(R.string.info_button)),
            DashboardMenu(android.R.drawable.ic_menu_edit, getString(R.string.form_button)),
            DashboardMenu(android.R.drawable.ic_menu_view, getString(R.string.show_button)),
            DashboardMenu(android.R.drawable.ic_lock_power_off, getString(R.string.exit_button))
        )

        val adapter = MenuAdapter(this, menuOptions)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    // Navigate to Information Screen
                    startActivity(Intent(this, InfoActivity::class.java))
                }
                1 -> {
                    // Navigate to Data Entry Form
                    lifecycleScope.launch {
                        val database = AppDatabase.getDatabase(this@MainActivity)
                        val repository = VoterRepository(database.voterDao())
                        val existingVoter = repository.getVoter()

                        if (existingVoter != null) {
                            // Data already exists, redirect to View Data
                            startActivity(Intent(this@MainActivity, ViewDataActivity::class.java))
                        } else {
                            // No data found, proceed to Form Entry
                            startActivity(Intent(this@MainActivity, FormEntryActivity::class.java))
                        }
                    }
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