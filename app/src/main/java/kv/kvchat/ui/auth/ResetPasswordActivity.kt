package kv.kvchat.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kv.kvchat.R
import kv.kvchat.data.firebase.FirebaseSource
import kv.kvchat.databinding.ActivityForgetpasswordBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ResetPasswordActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private val factory: ResetPasswordViewModelFactory by instance()

    private lateinit var viewModel: ResetPasswordViewModel
    private lateinit var binding: ActivityForgetpasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgetpassword)
        viewModel = ViewModelProviders.of(this, factory).get(ResetPasswordViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.getResetPasswordResponse().observe(this, Observer { response ->
            var alertDialog: AlertDialog
            if (response.status != 0) {
                alertDialog = if (response.status == FirebaseSource.RESET_PASSWORD_SUCCESS) {
                    getDialog(
                        response.title ?: "Success!",
                        response.message ?: "Please check your email!"
                    )
                } else {
                    getDialog(
                        response.title ?: "Failed!",
                        response.message ?: "Failed to reset your password! Please try again later"
                    )
                }
                viewModel.setStatus(0)
                alertDialog.show()
            }
        })
    }

    private fun getDialog(title: String, message: String): AlertDialog {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(message)
            .setTitle(title)
            .setPositiveButton(this.resources.getString(R.string.ok_caps)) { dialog, _ ->
                dialog.dismiss()
            }
        return alertDialogBuilder.create()
    }
}