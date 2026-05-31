package com.example.kpudata

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FormEntryActivity : AppCompatActivity() {
    private lateinit var etNik: EditText
    private lateinit var etNama: EditText
    private lateinit var etNoHp: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var etTanggal: EditText
    private lateinit var etAlamat: EditText
    private lateinit var btnCekLokasi: Button
    private lateinit var ivGambar: ImageView
    private lateinit var btnSubmit: Button
    private var currentImagePath: String = ""
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            fetchLocation()
        } else {
            Toast.makeText(this, "Izin lokasi dibutuhkan", Toast.LENGTH_SHORT).show()
        }
    }
    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(this, "Izin kamera dibutuhkan", Toast.LENGTH_SHORT).show()
        }
    }
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            ivGambar.setImageBitmap(bitmap)
            saveImageToInternalStorage(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_entry)

        // Initialize Views
        etNik = findViewById(R.id.etNik)
        etNama = findViewById(R.id.etNama)
        etNoHp = findViewById(R.id.etNoHp)
        rgGender = findViewById(R.id.rgGender)
        etTanggal = findViewById(R.id.etTanggal)
        etAlamat = findViewById(R.id.etAlamat)
        btnCekLokasi = findViewById(R.id.btnCekLokasi)
        ivGambar = findViewById(R.id.ivGambar)
        btnSubmit = findViewById(R.id.btnSubmit)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etTanggal.setText(sdf.format(Date()))

        btnCekLokasi.setOnClickListener {
            checkLocationPermission()
        }

        ivGambar.setOnClickListener {
            checkCameraPermission()
        }

        btnSubmit.setOnClickListener {
            submitData()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            requestLocationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        Toast.makeText(this, "Mencari lokasi...", Toast.LENGTH_SHORT).show()
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        etAlamat.setText(addresses[0].getAddressLine(0))
                    } else {
                        etAlamat.setText("${location.latitude}, ${location.longitude}")
                    }
                } catch (e: Exception) {
                    etAlamat.setText("${location.latitude}, ${location.longitude}")
                }
            } else {
                Toast.makeText(
                    this,
                    "Gagal mendapatkan lokasi. Pastikan GPS aktif.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePictureLauncher.launch(null)
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        val filename = "KPU_${System.currentTimeMillis()}.jpg"
        val file = File(applicationContext.filesDir, filename)
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            currentImagePath = file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitData() {
        val nik = etNik.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val noHp = etNoHp.text.toString().trim()
        val tanggal = etTanggal.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()

        val selectedGenderId = rgGender.checkedRadioButtonId
        val gender = if (selectedGenderId == R.id.rbLaki) "Laki-laki" else if (selectedGenderId == R.id.rbPerempuan) "Perempuan" else ""

        if (nik.isEmpty() || nama.isEmpty() || noHp.isEmpty() || gender.isEmpty() || alamat.isEmpty() || currentImagePath.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data dan foto", Toast.LENGTH_SHORT).show()
            return
        }

        val voter = Voter(
            nik = nik,
            nama = nama,
            noHp = noHp,
            gender = gender,
            tanggal = tanggal,
            alamat = alamat,
            imagePath = currentImagePath
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(this@FormEntryActivity)
            val repository = VoterRepository(database.voterDao())
            repository.insert(voter)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@FormEntryActivity, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish() // Returns to MainActivity
            }
        }
    }
}
