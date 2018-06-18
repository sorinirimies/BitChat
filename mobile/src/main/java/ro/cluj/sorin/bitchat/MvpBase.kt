package ro.cluj.sorin.bitchat

interface MvpBase {
    interface View

    interface Presenter<V : View> {
        fun attachView(view: V)
        fun detachView()
    }
}
