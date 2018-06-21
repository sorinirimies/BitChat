package ro.cluj.sorin.bitchat

/**
 * Created by Sorin Albu-Irimies on 5/18/2018.
 */
interface MvpBase {
  interface View

  interface Presenter<V : View> {
    fun attachView(view: V)
    fun detachView()
  }
}
