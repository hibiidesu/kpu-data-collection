package com.example.kpudata
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pemilih")
data class Voter(
    @PrimaryKey val nik: String,
    val nama: String,
    val noHp: String,
    val gender: String,
    val tanggal: String,
    val alamat: String,
    val imagePath: String
)
