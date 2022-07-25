package com.doseyenc.photosharing.ui.uploadImage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.doseyenc.photosharing.R
import com.doseyenc.photosharing.databinding.FragmentUploadBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class UploadFragment : Fragment(R.layout.fragment_upload) {
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    var selectedImage: Uri? = null
    var selectecBitmap: Bitmap? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        val view = binding.root
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        return view

    }

    override fun onResume() {
        super.onResume()
        binding.imageViewSelect.setOnClickListener {
            handlePermission()
        }




        binding.buttonShare.setOnClickListener {
            //depo işlemleri
            val reference = storage.reference
            val uuid = UUID.randomUUID()

            val imageRef = reference.child("images/${uuid}.jpg")
            if (selectedImage != null) {
                imageRef.putFile(selectedImage!!).addOnSuccessListener {
                    val uploadedImageReference = storage.reference.child("images/${uuid}.jpg")
                    uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        val currentUserEmail = auth.currentUser?.email.toString()
                        val userComment = binding.editTextTextComment.text.toString()
                        val date = Timestamp.now()
                        val postHashMap = hashMapOf<String , Any>()
                        postHashMap.put("imageUrl", downloadUrl)
                        postHashMap.put("userEmail", currentUserEmail)
                        postHashMap.put("userComment", userComment)
                        postHashMap.put("date", date)
                        database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("UploadFragment", "Post added to database")
                                findNavController().navigate(R.id.action_uploadFragment_to_feedFragment)
                            }
                        }.addOnFailureListener { exception ->
                            Log.d("UploadFragment", "Error adding post to database", exception)
                        }
                    }
                }
            }

        }
    }

    private fun handlePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin alınmamış
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            //izin zaten varsa
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //izin verilince yapılacaklar
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data
            if (selectedImage != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(
                        requireActivity().contentResolver,
                        selectedImage!!
                    )
                    selectecBitmap = ImageDecoder.decodeBitmap(source)
                    binding.imageViewSelect.setImageBitmap(selectecBitmap)
                } else {
                    selectecBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        selectedImage
                    )
                    binding.imageViewSelect.setImageBitmap(selectecBitmap)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}