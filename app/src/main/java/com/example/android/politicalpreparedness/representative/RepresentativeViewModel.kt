package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val savedState: SavedStateHandle): ViewModel() {

    companion object {
        const val ADDRESS_KEY = "ADDRESS_KEY"
        const val REPRESENTATIVES_KEY = "REPRESENTATIVES_KEY"
    }

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

    private val _motionLayoutState = MutableLiveData<Int>()
    val motionLayoutState: LiveData<Int>
        get() = _motionLayoutState

    init {
        _address.value = savedState.get<Address>(ADDRESS_KEY)
        _representatives.value = savedState.get<List<Representative>>(REPRESENTATIVES_KEY)
    }

    fun getRepresentativesByAddress() {
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
    }

    fun saveState() {
        savedState.set(ADDRESS_KEY, _address.value)
        savedState.set(REPRESENTATIVES_KEY, _representatives.value)
    }

    fun setAddress(address: Address?) {
        _address.value = address
    }

    fun setMotionLayoutState(state: Int?) {
        _motionLayoutState.value = state
    }

}
