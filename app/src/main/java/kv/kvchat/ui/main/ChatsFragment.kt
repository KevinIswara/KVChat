package kv.kvchat.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kv.kvchat.ChatApplication
import kv.kvchat.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {

    companion object {
        fun newInstance() = ChatsFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatAdapter = ChatAdapter(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChatsBinding.inflate(inflater, container, false)


        binding.rvChats.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        viewModel.getChatFriendsFromFirebase(ChatApplication.getUser().username ?: "")

        viewModel.getChatFriends().observe(this, Observer { list ->
            chatAdapter.updateData(list)
        })

        return binding.root
    }
}
