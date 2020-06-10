package kv.kvchat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kv.kvchat.R
import kv.kvchat.data.firebase.FirebaseSource
import kv.kvchat.databinding.ActivityLoginBinding
import kv.kvchat.ui.main.MainActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivityLoginBinding

    private var loginSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this

        viewModel.getUserDataResponse().observe(this, Observer { response ->
            if (loginSuccess) {
                if (response.status == FirebaseSource.USER_DATA_SUCCESS) {
                    onSuccess(FirebaseSource.USER_DATA_SUCCESS)
                } else if (response.status == FirebaseSource.USER_DATA_FAILED) {
                    onFailure(response.message ?: "getUserData Failed")
                }
                viewModel.setStatus(0)
            }
        })
    }

    override fun onStarted() {
        binding.progressbar.visibility = View.VISIBLE
    }

    override fun onSuccess(code: Int) {
        if (code == AuthViewModel.LOGIN_SUCCESS) {
            loginSuccess = true
            viewModel.getUserData()
        } else if (code == FirebaseSource.USER_DATA_SUCCESS) {
            binding.progressbar.visibility = View.GONE
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(message)
            .setPositiveButton(this.resources.getString(R.string.ok_caps)) { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onStart() {
        super.onStart()
        viewModel.user?.let {
            onSuccess(AuthViewModel.LOGIN_SUCCESS)
        }
    }
}