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
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao, private val apiService: CivicsApiService) : ViewModel() {

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _isSaved = MutableLiveData<Boolean>(false)
    val isSaved: LiveData<Boolean>
        get() = _isSaved

    private val _apiStatus = MutableLiveData<CivicsApiStatus>()
    val apiStatus: LiveData<CivicsApiStatus>
        get() = _apiStatus

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    fun checkElectionIsSaved(election: Election) {
        viewModelScope.launch {
            _election.value = election
            val savedElection = dataSource.getElectionById(election.id)
            _isSaved.value = savedElection != null
        }
    }

    fun getVoterInformation(election: Election) {
        _apiStatus.value = CivicsApiStatus.LOADING
        viewModelScope.launch {
            val address = if (election.division.state.isEmpty()) election.division.country else "${election.division.country} - ${election.division.state}"
            val response = apiService.getVoterInfoAsync(address, election.id)
            try {
                val result = response.await()
                Log.d("network debug - voter information", result.toString())
                _apiStatus.value = CivicsApiStatus.DONE
                _voterInfo.value = result
            } catch (e: Exception) {
                Log.e("network error", e.localizedMessage)
                _apiStatus.value = CivicsApiStatus.ERROR
            }
        }
    }

    fun saveElection() {
        viewModelScope.launch {
            _isSaved.value?.let { isSaved ->
                val election = election.value
                election?.let {
                    if (isSaved) {
                        dataSource.deleteElectionById(election.id)
                    } else {
                        dataSource.insertElection(election)
                    }
                    _isSaved.value = !isSaved
                }
            }
        }
    }

    fun intentUrl(url: String) {
        _url.value = url
    }

    fun intentUrlCompleted() {
        _url.value = null
    }


}