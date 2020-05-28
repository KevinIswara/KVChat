package kv.kvchat.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kv.kvchat.databinding.FriendsFragmentBinding

class FriendsFragment : Fragment() {

    companion object {
        fun newInstance() = FriendsFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var friendsAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        friendsAdapter = FriendsAdapter(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  {
        val binding = FriendsFragmentBinding.inflate(inflater, container, false)

        binding.rvFriends.apply {
            adapter = friendsAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.friends.observe(this, Observer { list ->
            friendsAdapter.updateData(list)
        })
        return binding.root
    }
}
