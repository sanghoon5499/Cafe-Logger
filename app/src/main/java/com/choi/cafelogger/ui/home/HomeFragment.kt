package com.choi.cafelogger.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.choi.cafelogger.R
import com.choi.cafelogger.ui.upload.UploadFragment
import com.choi.cafelogger.ui.maps.MapsFragment
import com.choi.cafelogger.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNav.selectedItemId = R.id.nav_home

        binding.btnOpenMap.setOnClickListener { navigateTo(MapsFragment()) }
        binding.btnOpenUpload.setOnClickListener { navigateTo(UploadFragment()) }

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true // already on home
                R.id.nav_search -> {
                    navigateTo(MapsFragment())
                    true
                }
                R.id.nav_profile -> {
                    navigateTo(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        val adapter = RecentAdapter { item ->
            // Open a details screen where
//            Log.d("cafeloggerDEBUG", "Clicked: ${item.location}")
        }

        binding.recentRecycler.adapter = adapter

        viewModel.allUploads.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.take(6))
        }
    }

    // .addToBackStack(null) exists to allow the user to press "back" to exit
    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}