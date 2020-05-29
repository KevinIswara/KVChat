package kv.kvchat.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.R
import kv.kvchat.data.model.User
import kv.kvchat.databinding.FriendItemBinding
import kv.kvchat.ui.chat.ChatActivity

class FriendsAdapter(val context: Context): RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {
    private var items: MutableList<User> = mutableListOf()

    fun updateData(items: List<User>?) {
        this.items.clear()
        items?.also {
            this.items.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val binding: FriendItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.friend_item, parent, false)
        return FriendsViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) = with(holder) {
        bindView(items[position])

        holder.itemBinding.root.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("username", items[position].username)
            context.startActivity(intent)
        }
    }

    inner class FriendsViewHolder(val itemBinding: FriendItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindView(item: User) = with(itemView) {
            val options = RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_green_50dp)
                .error(R.drawable.ic_account_circle_green_50dp)
                .fallback(R.drawable.ic_account_circle_green_50dp)

            Glide.with(this).load(item.imageUrl)
                .apply(options)
                .into(itemBinding.ivFriend)

            itemBinding.tvFriendName.text = item.name
        }
    }
}