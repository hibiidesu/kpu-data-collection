package com.example.kpudata

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File

class ViewDataActivity : AppCompatActivity() {
    private lateinit var tvNik: TextView
    private lateinit var tvNama: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var ivViewGambar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_data)

        // Initialize Views
        tvNik = findViewById(R.id.tvNik)
        tvNama = findViewById(R.id.tvNama)
        tvNoHp = findViewById(R.id.tvNoHp)
        tvGender = findViewById(R.id.tvGender)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvAlamat = findViewById(R.id.tvAlamat)
        ivViewGambar = findViewById(R.id.ivViewGambar)

        loadVoterData()
    }

    private fun loadVoterData() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(this@ViewDataActivity)
            val repository = VoterRepository(database.voterDao())
            val voter = repository.getVoter()

            if (voter != null) {
                // Populate the UI with Database values
                tvNik.text = voter.nik
                tvNama.text = voter.nama
                tvNoHp.text = voter.noHp
                tvGender.text = voter.gender
                tvTanggal.text = voter.tanggal
                tvAlamat.text = voter.alamat

                // Load Image if the path exists
                if (voter.imagePath.isNotEmpty()) {
                    val imgFile = File(voter.imagePath)
                    if (imgFile.exists()) {
                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        ivViewGambar.setImageBitmap(bitmap)
                    }
                }
            } else {
                Toast.makeText(this@ViewDataActivity, "Belum ada data pemilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
