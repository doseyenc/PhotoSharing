package com.doseyenc.photosharing.ui.feed

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.doseyenc.photosharing.R
import com.doseyenc.photosharing.databinding.FragmentFeedBinding
import com.doseyenc.photosharing.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth


class FeedFragment : Fragment(R.layout.fragment_feed) {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()



        return view
    }


    override fun onResume() {
        super.onResume()
        binding.materialToolbar.setOnMenuItemClickListener { menuItem ->
            val itemId: Int = menuItem.getItemId()
            if (itemId == R.id.action_logout) {
                logout()
                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }
        }
        binding.fabUploadImage.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_uploadFragment)
        }
    }


    private fun logout() {
        auth.signOut()
        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}