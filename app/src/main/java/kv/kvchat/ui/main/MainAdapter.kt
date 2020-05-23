package kv.kvchat.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainAdapter(context: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val FRIENDS = 0
    private val CHAT = 1
    private val PROFILE = 2

    private val TABS = intArrayOf(FRIENDS, CHAT, PROFILE)

    private val mContext: Context = context.applicationContext

    override fun getItem(position: Int): Fragment {
        return when (TABS[position]) {
            FRIENDS -> FriendsFragment.newInstance()
            CHAT -> FriendsFragment.newInstance()
            PROFILE -> FriendsFragment.newInstance()
            else -> FriendsFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return TABS.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (TABS[position]) {
            FRIENDS -> return "Friends"
            CHAT -> return "Chat"
            PROFILE -> return "Profile"
        }
        return null
    }
}