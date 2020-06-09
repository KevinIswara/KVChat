package kv.kvchat.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.R
import kv.kvchat.data.model.Chat
import kv.kvchat.databinding.ChatItemLeftBinding
import kv.kvchat.databinding.ChatItemRightBinding

class ChatAdapter(val context: Context) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var items: MutableList<Chat> = mutableListOf()
    private var friendImageUrl: String = ""
    private var curUserUsername: String = ""

    companion object {
        const val MESSAGE_TYPE_LEFT = 0
        const val MESSAGE_TYPE_RIGHT = 1
    }

    fun updateFriendImageUrl(url: String?) {
        friendImageUrl = url ?: "default"
    }

    fun updateCurUserUsername(username: String?) {
        curUserUsername = username ?: ""
    }

    fun updateData(items: List<Chat>?) {
        items?.let {
            if (items.size > this.items.size) {
                for (i in this.items.size until items.size) {
                    this.items.add(items[i])
                }
            } else {
                this.items.clear()
                this.items.addAll(it)
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == MESSAGE_TYPE_LEFT) {
            val binding: ChatItemLeftBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.chat_item_left, parent, false
            )
            ChatLeftViewHolder(binding)
        } else {
            val binding: ChatItemRightBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.chat_item_right, parent, false
            )
            ChatRightViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].sender == curUserUsername) MESSAGE_TYPE_RIGHT else MESSAGE_TYPE_LEFT
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) = with(holder) {
        bindView(items[position])
    }

    abstract inner class ChatViewHolder(itemView: ViewDataBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        abstract fun bindView(item: Chat)
    }

    inner class ChatLeftViewHolder(private val itemBinding: ChatItemLeftBinding) :
        ChatViewHolder(itemBinding) {
        override fun bindView(item: Chat) {
            itemBinding.tvMessage.text = item.message

            val options = RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_green_50dp)
                .error(R.drawable.ic_account_circle_green_50dp)
                .fallback(R.drawable.ic_account_circle_green_50dp)

            Glide.with(context).load(friendImageUrl)
                .apply(options)
                .into(itemBinding.ivProfilePicure)
        }
    }

    inner class ChatRightViewHolder(private val itemBinding: ChatItemRightBinding) :
        ChatViewHolder(itemBinding) {
        override fun bindView(item: Chat) {
            itemBinding.tvMessage.text = item.message
        }
    }
}