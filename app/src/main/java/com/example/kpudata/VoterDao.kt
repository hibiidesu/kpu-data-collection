package com.example.kpudata
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VoterDao {
    @Insert
    suspend fun insert(voter: Voter)

    @Query("SELECT * FROM pemilih LIMIT 1")
    suspend fun getVoter(): Voter?
}