package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsApi

class VoterInfoFragment : Fragment() {

    private val viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao,
            CivicsApi.retrofitService)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentVoterInfoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())
        viewModel.checkElectionIsSaved(args.argElection)
        viewModel.getVoterInformation(args.argElection)

        viewModel.url.observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                startUrlIntent(it)
                viewModel.intentUrlCompleted()
            }
        })

        binding.buttonSave.setOnClickListener { viewModel.saveOrDeleteElection() }

        return binding.root
    }

    private fun startUrlIntent(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

}