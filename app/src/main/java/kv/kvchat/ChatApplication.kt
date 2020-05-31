package kv.kvchat

import android.app.Application
import kv.kvchat.data.repository.UserRepository
import kv.kvchat.data.firebase.FirebaseSource
import kv.kvchat.data.model.User
import kv.kvchat.data.repository.ChatRepository
import kv.kvchat.ui.auth.AuthViewModelFactory
import kv.kvchat.ui.chat.ChatViewModelFactory
import kv.kvchat.ui.main.MainViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ChatApplication : Application(), KodeinAware {

    companion object {
        private var currUser = User()

        fun setUser(user: User) {
            currUser = user
        }

        fun getUser(): User {
            return currUser
        }
    }

    override val kodein = Kodein.lazy {
        import(androidXModule(this@ChatApplication))

        bind() from singleton { FirebaseSource() }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { ChatRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { MainViewModelFactory(instance()) }
        bind() from provider { ChatViewModelFactory(instance(), instance()) }
    }
}