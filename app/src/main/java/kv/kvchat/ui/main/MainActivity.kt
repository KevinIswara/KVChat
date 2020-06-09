package kv.kvchat.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import kv.kvchat.ChatApplication
import kv.kvchat.R
import kv.kvchat.databinding.ActivityMainBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: MainViewModelFactory by instance()

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        binding.viewmodel = viewModel
        binding.handler = this
        binding.manager = supportFragmentManager

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.lifecycleOwner = this
        setToolbar()
    }

    private fun setToolbar() {
        binding.toolbar.tvTitle.text = ChatApplication.getUser().name

        val options = RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle_white_30dp)
            .error(R.drawable.ic_account_circle_white_30dp)
            .fallback(R.drawable.ic_account_circle_white_30dp)

        Glide.with(this).load(ChatApplication.getUser().imageUrl)
            .apply(options)
            .into(binding.toolbar.ivToolbar)
    }
}

@BindingAdapter("bind:handler")
fun bindViewPagerAdapter(view: ViewPager, activity: MainActivity) {
    val adapter = MainAdapter(view.context, activity.supportFragmentManager)
    view.adapter = adapter
}

@BindingAdapter("bind:pager")
fun bindViewPagerTabs(view: TabLayout, pagerView: ViewPager) {
    view.setupWithViewPager(pagerView, true)
}
