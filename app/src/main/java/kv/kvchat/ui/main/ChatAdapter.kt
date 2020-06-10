package kv.kvchat.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.R
import kv.kvchat.data.model.User
import kv.kvchat.databinding.FriendItemBinding
import kv.kvchat.ui.chat.ChatActivity

class ChatAdapter(val context: Context) : RecyclerView.Adapter<ChatAdapter.FriendsViewHolder>() {
    private var items: MutableList<User?> = mutableListOf()
    var lastMessage: HashMap<User, String?> = HashMap()

    fun updateData(items: HashMap<User, String?>) {
        this.items.clear()
        this.lastMessage = items
        val friendList = ArrayList(items.keys)
        friendList.also {
            for (item in friendList) {
                this.items.add(item)
            }
        }
        if (this.items.size == 0) {
            this.items.add(null)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val binding: FriendItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.friend_item,
            parent,
            false
        )
        return FriendsViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int): Unit = with(holder) {
        bindView(items[position])

        holder.itemBinding.root.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("username", items[position]?.username)
            context.startActivity(intent)
        }
    }

    inner class FriendsViewHolder(val itemBinding: FriendItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindView(item: User?) = with(itemView) {
            if (item != null) {
                itemBinding.clError.visibility = View.GONE
                itemBinding.rlItem.visibility = View.VISIBLE

                val options = RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_account_circle_green_50dp)
                    .error(R.drawable.ic_account_circle_green_50dp)
                    .fallback(R.drawable.ic_account_circle_green_50dp)

                Glide.with(this).load(item.imageUrl)
                    .apply(options)
                    .into(itemBinding.ivFriend)

                itemBinding.tvFriendName.text = item.name
                lastMessage[item]?.let {
                    itemBinding.tvLastMessage.visibility = View.VISIBLE
                    if (it.length >= 40) {
                        itemBinding.tvLastMessage.text = "${it.subSequence(0, 39)} ..."
                    } else itemBinding.tvLastMessage.text = it
                }
            } else {
                itemBinding.clError.visibility = View.VISIBLE
                itemBinding.rlItem.visibility = View.GONE
                Glide.with(this).load(R.drawable.ic_sentiment_dissatisfied_black_100dp)
                    .into(itemBinding.ivError)
            }
        }
    }
}