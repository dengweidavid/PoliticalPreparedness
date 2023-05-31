package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElection(election: Election)

    @Query("SELECT * FROM election_table")
    suspend fun getAllElections(): List<Election>

    @Query("SELECT * from election_table where id = :id")
    suspend fun getElectionById(id: Int): Election?

    @Query("DELETE FROM election_table where id = :id")
    suspend fun deleteElectionById(id: Int)

    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections()
}