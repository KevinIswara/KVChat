package kv.kvchat.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import kv.kvchat.R
import kv.kvchat.databinding.FragmentProfileBinding
import kv.kvchat.ui.auth.LoginActivity

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!! ).get(MainViewModel::class.java)
        setProfile()
        setLogout()
    }

    @SuppressLint("SetTextI18n")
    fun setProfile() {
        viewModel.getUser().observe(viewLifecycleOwner, Observer { userData ->

            val options = RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_100dp)
                .error(R.drawable.ic_account_circle_black_100dp)
                .fallback(R.drawable.ic_account_circle_black_100dp)

            Glide.with(this).load(userData.imageUrl)
                .apply(options)
                .into(binding.ivProfilePicure)

            binding.tvName.text = userData.name
            binding.tvUsename.text = "Username: ${userData.username}"
        })
    }

    fun setLogout() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            val i = Intent(this.context, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }
}
