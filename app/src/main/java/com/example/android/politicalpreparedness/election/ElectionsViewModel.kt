package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val database: ElectionDao, private val apiService: CivicsApiService): ViewModel() {

    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}