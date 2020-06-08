package kv.kvchat.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kv.kvchat.R
import kv.kvchat.data.auth.User
import kv.kvchat.databinding.FriendItemBinding


class FriendsAdapter : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {
    private var items: MutableList<User?> = mutableListOf()
    private var filter: String = ""

    fun updateData(items: List<User>?, filter: String) {
        this.filter = filter
        this.items.clear()
        items?.also {
            for (item in items) {
                item.name?.let {
                    if (it.toLowerCase().contains(filter.toLowerCase())) {
                        this.items.add(item)
                    }
                }
            }
        }
        if (this.items.size == 0) {
            this.items.add(null)
        }
        notifyDataSetChanged()
    }

    fun getItems(): List<User?>? = this.items

    fun clearItems() {
        val count = itemCount
        this.items.clear()
        notifyItemRangeRemoved(0, count)
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
    }

    inner class FriendsViewHolder(private val itemBinding: FriendItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
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
            } else {
                itemBinding.clError.visibility = View.VISIBLE
                itemBinding.rlItem.visibility = View.GONE
                Glide.with(this).load(R.drawable.ic_sentiment_dissatisfied_black_100dp)
                    .into(itemBinding.ivError)
            }
        }
    }
}