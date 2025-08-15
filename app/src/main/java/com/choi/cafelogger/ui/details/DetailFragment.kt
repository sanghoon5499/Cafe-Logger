package com.choi.cafelogger.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import coil.load
import com.choi.cafelogger.R
import com.choi.cafelogger.databinding.FragmentDetailsBinding
import com.choi.cafelogger.model.UploadItem
import androidx.core.net.toUri

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val upload: UploadItem? = arguments?.getParcelable(ARG_UPLOAD)

        if (upload == null) {
            // Nothing to show; you could popBackStack() or show a message.
            // For now, just return.
            return
        }

        // Image
        val uri = upload.imageUri?.toUri()
        binding.imgDetailThumb.load(uri) {
            crossfade(true)
            placeholder(R.drawable.image_1)
            error(R.drawable.image_2)
        }

        // Fields (read-only “outlined” inputs)
        binding.etDetailType.setText(upload.type.orDash())
        binding.etDetailLocation.setText(upload.location.orDash())
        binding.etDetailRoast.setText(upload.roast.orDash())
        binding.etDetailOrigin.setText(upload.origin.orDash())
        binding.etDetailProcess.setText(upload.process.orDash())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_UPLOAD = "upload"

        fun newInstance(upload: UploadItem) = DetailFragment().apply {
            arguments = bundleOf(ARG_UPLOAD to upload)
        }
    }
}

/** Helper to show "-" when null/blank */
private fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this
