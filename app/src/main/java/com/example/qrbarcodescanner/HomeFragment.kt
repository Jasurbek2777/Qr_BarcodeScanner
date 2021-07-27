package com.example.qrbarcodescanner

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.example.qrbarcodescanner.databinding.FragmentHomeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var param1: String? = null
    private var param2: String? = null
    lateinit var scannerView: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var handler = Handler(Looper.getMainLooper())
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        scannerView = CodeScanner(requireContext(), binding.scannerView)
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    scannerView.startPreview()
                    scannerView.camera = CodeScanner.CAMERA_BACK
                    scannerView.formats = CodeScanner.ALL_FORMATS
                    scannerView.autoFocusMode = AutoFocusMode.SAFE
                    scannerView.scanMode = ScanMode.SINGLE
                    scannerView.isAutoFocusEnabled = true
                    scannerView.isFlashEnabled = false
                    scannerView.decodeCallback = DecodeCallback {
                        handler.postDelayed({
                            var bundle = Bundle()
                            bundle.putString("param1", it.text)
                            findNavController().navigate(R.id.webViewFragment, bundle)
                        }, 1000)

                    }
                    scannerView.errorCallback = ErrorCallback {
                        handler.postDelayed({
                            Toast.makeText(
                                requireContext(),
                                "Error ${it.message}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }, 1000)

                    }
                    binding.scannerView.setOnClickListener {
                        scannerView.startPreview()
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        requireContext(),
                        "If you want to use this app please grant the permission",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", "com.example.qrbarcodescanner", null)
                        )
                    )
                }
            }).check()
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}