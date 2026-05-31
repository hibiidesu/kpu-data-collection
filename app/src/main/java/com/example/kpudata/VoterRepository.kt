package com.example.kpudata

class VoterRepository(private val voterDao: VoterDao) {

    suspend fun insert(voter: Voter) {
        voterDao.insert(voter)
    }

    suspend fun getVoter(): Voter? {
        return voterDao.getVoter()
    }
}