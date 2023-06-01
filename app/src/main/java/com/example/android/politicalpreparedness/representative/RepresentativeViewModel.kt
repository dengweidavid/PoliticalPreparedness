package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    private val apiService = CivicsApi.retrofitService

    private val _apiStatus = MutableLiveData<CivicsApiStatus>()
    val apiStatus: LiveData<CivicsApiStatus>
        get() = _apiStatus

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    init {
        _address.value = Address("", null, "", "", "" )
    }

    fun getRepresentativesByAddress() {
        if (_address.value != null) {
            _apiStatus.value = CivicsApiStatus.LOADING
            viewModelScope.launch {
                try {
                    val (offices, officials) = apiService.getRepresentativesAsync(
                        _address.value!!.toFormattedString()
                    )
                        .await()
                    Log.d("network debug - representatives", officials.toString())
                    _apiStatus.value = CivicsApiStatus.DONE
                    _representatives.value =
                        offices.flatMap { office -> office.getRepresentatives(officials) }
                } catch (e: Exception) {
                    e.localizedMessage?.let { Log.e("network error - representative", it) }
                    _apiStatus.value = CivicsApiStatus.ERROR
                    _representatives.value = emptyList()
                }
            }
        } else {
            _representatives.value = emptyList()
        }
    }

    fun setAddress(address: Address) {
        _address.value = address
    }

}
