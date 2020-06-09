package kv.kvchat.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kv.kvchat.data.model.User
import kv.kvchat.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    companion object {
        fun newInstance() = FriendsFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var friendsAdapter: FriendsAdapter

    private lateinit var friends: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        friendsAdapter = FriendsAdapter(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFriendsBinding.inflate(inflater, container, false)

        binding.rvFriends.apply {
            adapter = friendsAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.getFriends().observe(this, Observer { list ->
            friends = list
            friendsAdapter.updateData(list, binding.etSearch.text.toString())
        })
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                friendsAdapter.updateData(friends, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        return binding.root
    }
}
