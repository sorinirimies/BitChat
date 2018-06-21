package ro.cluj.sorin.bitchat.injection

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import ro.cluj.sorin.bitchat.ui.MainPresenter
import ro.cluj.sorin.bitchat.ui.chat.ChatPresenter
import ro.cluj.sorin.bitchat.ui.groups.GroupsPresenter
import ro.cluj.sorin.bitchat.ui.map.GroupsMapPresenter
import ro.cluj.sorin.bitchat.ui.user.UserProfilePresenter

/**
 * Created by sorin on 12.05.18.
 */
val uiPresentersModule = Kodein.Module {
  bind<GroupsMapPresenter>() with provider { GroupsMapPresenter() }
  bind<UserProfilePresenter>() with provider { UserProfilePresenter() }
  bind<ChatPresenter>() with provider { ChatPresenter() }
  bind<GroupsPresenter>() with provider { GroupsPresenter(instance()) }
  bind<MainPresenter>() with provider { MainPresenter() }
}