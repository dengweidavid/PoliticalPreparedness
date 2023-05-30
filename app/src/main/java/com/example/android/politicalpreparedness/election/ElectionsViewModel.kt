package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(private val database: ElectionDao, private val apiService: CivicsApiService): ViewModel() {

    private val _apiStatus = MutableLiveData<CivicsApiStatus>()
    val apiStatus: LiveData<CivicsApiStatus>
        get() = _apiStatus

    private val _upComingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upComingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    fun getUpcomingElectionsFromCivics() {
        _apiStatus.value = CivicsApiStatus.LOADING
        viewModelScope.launch {
            val response = apiService.getElectionListAsync()
            try {
                val result = response.await()
                Log.d("network debug", result.toString())
                _apiStatus.value = CivicsApiStatus.DONE
                _upComingElections.value = result.elections
            } catch (e: Exception) {
                Log.e("network error", e.localizedMessage)
                _apiStatus.value = CivicsApiStatus.ERROR
                _upComingElections.value = ArrayList()
            }
        }
    }

}