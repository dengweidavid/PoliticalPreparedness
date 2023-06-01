package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionsBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment: Fragment() {

    private val viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao, CivicsApi.retrofitService)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentElectionsBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val upcomingElectionAdapter = ElectionListAdapter(ElectionListener { navigateToVoterInfoFragment(it) })
        val savedElectionAdapter = ElectionListAdapter(ElectionListener { navigateToVoterInfoFragment(it) })

        binding.recyclerUpcomingElections.adapter = upcomingElectionAdapter
        binding.recyclerSavedElections.adapter = savedElectionAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            upcomingElectionAdapter.submitList(it)
        }

        viewModel.savedElections.observe(viewLifecycleOwner) {
            savedElectionAdapter.submitList(it)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUpcomingElectionsFromCivics()
        viewModel.getSavedElectionsFromDatabase()
    }

    private fun navigateToVoterInfoFragment(election: Election) {
        this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
    }

}