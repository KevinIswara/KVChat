package kv.kvchat.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kv.kvchat.R
import kv.kvchat.databinding.ActivityMainBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.bumptech.glide.Priority


class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory : MainViewModelFactory by instance()

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        binding.viewmodel = viewModel

        setToolbar()
    }

    fun setToolbar() {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.tvTitle.text = viewModel.name

        val options = RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle_green_24dp)
            .error(R.drawable.ic_account_circle_green_24dp)
            .fallback(R.drawable.ic_account_circle_green_24dp)

        Glide.with(this).load("https://i.picsum.photos/id/1/300/300.jpg")
            .setdefaultrequestoption(options)
            .into(binding.toolbar.ivToolbar)
    }
}
