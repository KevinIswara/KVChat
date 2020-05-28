package kv.kvchat.ui.main

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.R
import kv.kvchat.data.firebase.FirebaseSource
import kv.kvchat.databinding.FragmentProfileBinding
import kv.kvchat.ui.auth.LoginActivity


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
        val IMAGE_REQUEST = 1
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var pd: ProgressDialog
    private lateinit var dialog: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        setProfile()
        setLogout()
        pd = ProgressDialog(context)
        context?.let {
            dialog = AlertDialog.Builder(it)
        }
        setUploadImageResponse()
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
        binding.ivEditProfilePicture.setOnClickListener {
            pickImage()
        }
        binding.ivEditName.setOnClickListener {
            editName()
        }
    }

    private fun setLogout() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            val i = Intent(this.context, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }

    private fun pickImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = context?.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let {
                if (isNetworkConnected()) {
                    viewModel.imageUri = it
                    pd.setTitle("Changing Profile Picture")
                    pd.setMessage("Uploading")
                    pd.show()
                    uploadImage()
                } else {
                    dialog.setTitle("Failed")
                        .setMessage("You are not connected to the internet!")
                        .setCancelable(false)
                        .setPositiveButton(
                            "OK"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                    dialog.show()
                }
            }
        }
    }

    private fun setUploadImageResponse() {
        viewModel.getImageUpdateResponse().observe(viewLifecycleOwner, Observer { response ->
            response.status.let {

            }
            if (response.status == FirebaseSource.IMAGE_UPLOAD_SUCCESS) {
                pd.dismiss()
                dialog.setTitle("Success")
                    .setMessage("Profile picture has been changed!")
                    .setCancelable(false)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                dialog.show()
            } else if (response.status == FirebaseSource.IMAGE_UPLOAD_FAILED) {
                pd.dismiss()
                response.message?.let {
                    dialog.setMessage(it)
                }
                dialog.setTitle(response.title)
                    .setCancelable(false)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                dialog.show()
            }
        })
    }

    private fun uploadImage() {
        viewModel.imageUri?.let {
            getFileExtension(it)?.let {
                viewModel.uploadProfilePicture(it)
            }
        }
    }

    private fun editName() {
        binding.etName.setText(binding.tvName.text)
        binding.etName.visibility = View.VISIBLE
        binding.tvName.visibility = View.INVISIBLE
        binding.etName.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.etName.visibility = View.GONE
                    binding.tvName.visibility = View.VISIBLE
                    binding.etName.text.toString().let {
                        if (it.isBlank()) {
                            dialog.setTitle("Failed")
                                .setMessage("Name cannot be blank")
                        } else if (isNetworkConnected()) {
                            viewModel.changeName(it)
                            binding.tvName.text = binding.etName.text.toString()
                            dialog.setTitle("Success")
                                .setMessage("Name has been changed!")
                        } else {
                            dialog.setTitle("Failed")
                                .setMessage("You are not connected to the internet!")
                        }
                        dialog.setCancelable(false)
                            .setPositiveButton(
                                "OK"
                            ) { dialog, _ ->
                                dialog.dismiss()
                            }
                        dialog.show()
                    }
                    return true
                }
                return false
            }

        })
    }

    private fun isNetworkConnected(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}
