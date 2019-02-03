package ro.cluj.sorin.bitchat.injection

import org.koin.dsl.module
import ro.cluj.sorin.bitchat.ui.MainPresenter
import ro.cluj.sorin.bitchat.ui.chat.ChatPresenter
import ro.cluj.sorin.bitchat.ui.groups.GroupsPresenter
import ro.cluj.sorin.bitchat.ui.map.GroupsMapPresenter
import ro.cluj.sorin.bitchat.ui.user.UserProfilePresenter

/**
 * Created by sorin on 12.05.18.
 */
val uiPresentersModule = module {
    single { GroupsMapPresenter() }
    single { UserProfilePresenter() }
    single { ChatPresenter(get()) }
    single { GroupsPresenter(get()) }
    single { MainPresenter() }
}