package com.choi.cafelogger.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.choi.cafelogger.R
import com.choi.cafelogger.ui.upload.UploadFragment
import com.choi.cafelogger.ui.maps.MapsFragment
import com.choi.cafelogger.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNav.selectedItemId = R.id.nav_home

        // .addToBackStack(null) exists to allow the user to press "back" to exit
        binding.btnOpenMap.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnOpenUpload.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, UploadFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // already on home, no‐op
                    true
                }
                R.id.nav_search -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MapsFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}