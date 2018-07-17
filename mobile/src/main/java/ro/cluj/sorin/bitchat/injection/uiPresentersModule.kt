package ro.cluj.sorin.bitchat.injection

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import ro.cluj.sorin.bitchat.ui.MainPresenter
import ro.cluj.sorin.bitchat.ui.chat.ChatPresenter
import ro.cluj.sorin.bitchat.ui.groups.GroupsPresenter
import ro.cluj.sorin.bitchat.ui.map.GroupsMapPresenter
import ro.cluj.sorin.bitchat.ui.user.UserProfilePresenter

/**
 * Created by sorin on 12.05.18.
 */
val uiPresentersModule = Kodein.Module {
  bind<GroupsMapPresenter>() with singleton { GroupsMapPresenter() }
  bind<UserProfilePresenter>() with singleton { UserProfilePresenter() }
  bind<ChatPresenter>() with singleton { ChatPresenter(instance()) }
  bind<GroupsPresenter>() with singleton { GroupsPresenter(instance()) }
  bind<MainPresenter>() with singleton { MainPresenter() }
}