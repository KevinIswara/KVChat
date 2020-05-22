package kv.kvchat.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.R
import kv.kvchat.data.auth.User
import kv.kvchat.databinding.ActivityMainBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory : MainViewModelFactory by instance()

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        binding.viewmodel = viewModel

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel.getUserData().observe(this, Observer { userData ->
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
