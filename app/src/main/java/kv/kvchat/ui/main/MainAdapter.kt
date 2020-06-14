package kv.kvchat.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val CHATS = 0
    private val FRIENDS = 1
    private val PROFILE = 2

    private val TABS = intArrayOf(FRIENDS, CHATS, PROFILE)

    private val mContext: Context = context.applicationContext

    override fun getItem(position: Int): Fragment {
        return when (TABS[position]) {
            FRIENDS -> FriendsFragment.newInstance()
            CHATS -> ChatsFragment.newInstance()
            PROFILE -> ProfileFragment.newInstance()
            else -> FriendsFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return TABS.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (TABS[position]) {
            FRIENDS -> return "Friends"
            CHATS -> return "Chats"
            PROFILE -> return "Profile"
        }
        return null
    }
}