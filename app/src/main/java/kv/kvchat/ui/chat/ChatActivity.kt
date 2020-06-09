package kv.kvchat.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.ChatApplication
import kv.kvchat.R
import kv.kvchat.data.model.User
import kv.kvchat.databinding.ActivityChatBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ChatActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: ChatViewModelFactory by instance()

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var friend: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        viewModel = ViewModelProviders.of(this, factory).get(ChatViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.toolbar.contentInsetStartWithNavigation = 0
        binding.toolbar.toolbar.setNavigationOnClickListener { finish() }

        setAdapter()
        getChatList()
        setObserver()
    }

    private fun setToolbar() {
        binding.toolbar.tvTitle.text = friend.name

        val options = RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle_white_30dp)
            .error(R.drawable.ic_account_circle_white_30dp)
            .fallback(R.drawable.ic_account_circle_white_30dp)

        Glide.with(this).load(friend.imageUrl)
            .apply(options)
            .into(binding.toolbar.ivToolbar)
    }

    private fun setAdapter() {
        chatAdapter = ChatAdapter(context = this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.rvChat.apply {
            adapter = chatAdapter
            this.layoutManager = layoutManager
            setHasFixedSize(true)
        }
    }

    // fetch friend data and chat data
    private fun getChatList() {
        val friendUsername = intent.getStringExtra("username")
        viewModel.getFriendDataFromFirebase(friendUsername ?: "")
        viewModel.getChatData(
            ChatApplication.getUser().username ?: "",
            friendUsername ?: ""
        )
        chatAdapter.updateCurUserUsername(ChatApplication.getUser().username)
    }

    private fun setObserver() {
        // observe friend data to change toolbar (display friend's profile and name)
        viewModel.getFriends().observe(this, Observer { friendData ->
            friend = friendData
            setToolbar()
            chatAdapter.updateFriendImageUrl(friendData.imageUrl)
        })

        // observe chats to update recycle view
        viewModel.getChatList().observe(this, Observer { listChat ->
            listChat?.let {
                if (it.isNotEmpty()) {
                    chatAdapter.updateData(listChat)
                    binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        })
    }
}
