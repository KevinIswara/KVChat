package kv.kvchat.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.R
import kv.kvchat.data.model.User
import kv.kvchat.databinding.ActivityChatBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ChatActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory : ChatViewModelFactory by instance()

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        viewModel = ViewModelProviders.of(this, factory).get(ChatViewModel::class.java)
        binding.viewmodel = viewModel

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.toolbar.contentInsetStartWithNavigation = 0
        binding.toolbar.toolbar.setNavigationOnClickListener { finish() }

        val username = intent.getStringExtra("username")
        viewModel.getFriendData(username)
        viewModel.userData.observe(this, Observer { userData ->
            user = userData
            setToolbar()
        })
    }

    fun setToolbar() {
        binding.toolbar.tvTitle.text = user.name

        val options = RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle_white_30dp)
            .error(R.drawable.ic_account_circle_white_30dp)
            .fallback(R.drawable.ic_account_circle_white_30dp)

        Glide.with(this).load(user.imageUrl)
            .apply(options)
            .into(binding.toolbar.ivToolbar)
    }
}
