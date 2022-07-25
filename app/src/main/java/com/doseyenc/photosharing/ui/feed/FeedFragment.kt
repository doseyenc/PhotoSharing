package com.doseyenc.photosharing.ui.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doseyenc.photosharing.R
import com.doseyenc.photosharing.adapter.PostAdapter
import com.doseyenc.photosharing.databinding.FragmentFeedBinding
import com.doseyenc.photosharing.model.PostModel
import com.doseyenc.photosharing.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class FeedFragment : Fragment(R.layout.fragment_feed) {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!


    private lateinit var postAdapter: PostAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    var postList: ArrayList<PostModel> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root


        auth = FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getDataFromFirebase()
        postAdapter.notifyDataSetChanged()
    }




    private fun setupRecyclerView() {
        binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter()
        binding.rvFeed.adapter = postAdapter

    }

    override fun onResume() {
        super.onResume()
        binding.materialToolbar.setOnMenuItemClickListener { menuItem ->
            val itemId: Int = menuItem.itemId
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
        ContextCompat.startActivity(requireContext(), Intent(requireContext(), LoginActivity::class.java), null)
        activity?.finish()
    }
    private fun getDataFromFirebase() {
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error getting documents: ${error.message}")
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            val documents = snapshot.documents
                            postList.clear()
                            for (document in documents) {
                                val post = PostModel(
                                    document.get("date").toString(),
                                    document.get("imageUrl").toString(),
                                    document.get("userComment").toString(),
                                    document.get("userEmail").toString()
                                )
                                postList.add(post)
                                val size = postList.size

                            }
                            Log.e("postList", postList.size.toString())


                        }
                    }
                }
            }
        postAdapter.updateList(postList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}