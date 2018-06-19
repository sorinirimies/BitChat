package ro.cluj.sorin.bitchat.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.greenspand.kotlin_ext.animateGone
import com.greenspand.kotlin_ext.animateVisible
import com.greenspand.kotlin_ext.editPrefs
import com.greenspand.kotlin_ext.setString
import com.greenspand.kotlin_ext.snack
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_profile.btnFacebookLogin
import kotlinx.android.synthetic.main.fragment_user_profile.btnGoogleLogin
import kotlinx.android.synthetic.main.fragment_user_profile.btnUserLogout
import kotlinx.android.synthetic.main.fragment_user_profile.contUserLogin
import kotlinx.android.synthetic.main.fragment_user_profile.imgUserIcon
import kotlinx.android.synthetic.main.fragment_user_profile.tvEmail
import kotlinx.android.synthetic.main.fragment_user_profile.tvName
import kotlinx.android.synthetic.main.fragment_user_profile.tvPhoneNumber
import kotlinx.android.synthetic.main.fragment_user_profile.tvSignInLabel
import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import org.kodein.di.generic.instance
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.model.User
import ro.cluj.sorin.bitchat.ui.BaseFragment
import timber.log.Timber
import java.util.Arrays

private const val RC_SIGN_IN = 78

class UserProfileFragment : BaseFragment(), UserProfileView,
  GoogleApiClient.OnConnectionFailedListener,
  FacebookCallback<LoginResult> {
  private val presenter: UserProfilePresenter by instance()
  private val callbackManager by lazy { CallbackManager.Factory.create() }
  private val userRef by lazy { db.collection("user") }

  private val gApiClient by lazy {
    activity?.let {
      GoogleApiClient.Builder(it)
          .enableAutoManage(it /* FragmentActivity */, this /* OnConnectionFailedListener */)
          .addApi(Auth.GOOGLE_SIGN_IN_API, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken(getString(R.string.default_web_client_id))
              .requestProfile()
              .requestEmail()
              .build())
          .build()
    }
  }
  private val channelGoogleSignIn by lazy { BroadcastChannel<GoogleSignInAccount>(1) }
  override fun getLayoutId() = R.layout.fragment_user_profile

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.attachView(this)

    launch {
      channelGoogleSignIn.openSubscription().consumeEach {
        firebaseAuthWithGoogle(it)
      }
    }

    /* Google login click listener*/
    btnGoogleLogin.setOnClickListener {
      val signInIntent = Auth.GoogleSignInApi.getSignInIntent(gApiClient)
      startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /* Facebook login setup*/
    LoginManager.getInstance().registerCallback(callbackManager, this@UserProfileFragment)
    btnFacebookLogin.setOnClickListener {
      LoginManager.getInstance()
          .logInWithReadPermissions(this@UserProfileFragment,
              Arrays.asList("email", "public_profile", "user_friends"))
    }

    /* Logout*/
    btnUserLogout.setOnClickListener { presenter.logoutUser() }
  }

  private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
    val user = firebaseAuth.currentUser
    if (user != null) {
      presenter.userIsLoggedIn(user)
      presenter.createOrUpdateBitChatUser(user)
    } else {
      presenter.userIsLoggedOut()
    }
  }

  override fun onCancel() {
    presenter.showUserLoginFailed("Facebook login cancelled")
  }

  override fun onSuccess(result: LoginResult) {
    firebaseAuthWithFacebook(result.accessToken)
  }

  override fun onError(error: FacebookException?) {
    Timber.e(error?.message)
    presenter.showUserLoginFailed(error?.localizedMessage)
  }

  override fun onStart() {
    super.onStart()
    firebaseAuth.addAuthStateListener(authStateListener)
  }

  override fun onStop() {
    super.onStop()
    firebaseAuth.removeAuthStateListener(authStateListener)
  }

  override fun onDestroy() {
    super.onDestroy()
    LoginManager.getInstance().unregisterCallback(callbackManager)
    channelGoogleSignIn.close()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detachView()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    // Facebook Login callback
    if (callbackManager.onActivityResult(requestCode, resultCode, data)) return
    //Google login callback
    when (requestCode) {
      RC_SIGN_IN -> Auth.GoogleSignInApi.getSignInResultFromIntent(data).handleLoginResult()
    }
  }

  private fun GoogleSignInResult.handleLoginResult() {
    if (isSuccess) {
      launch {
        signInAccount?.let { channelGoogleSignIn.send(it) }
      }
    } else {
      presenter.showUserLoginFailed("Google sign in failed.")
    }
  }

  private fun firebaseAuthWithFacebook(token: AccessToken) {
    val credential = FacebookAuthProvider.getCredential(token.token)
    firebaseSignIn(credential)
    sharedPrefs.editPrefs {
      setString("FACEBOOK_TOKEN" to token.token)
    }
  }

  private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
    val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
    firebaseSignIn(credential)
    sharedPrefs.editPrefs {
      setString("GOOGLE_TOKEN" to acct.idToken as String)
    }
  }

  private fun firebaseSignIn(credential: AuthCredential) {
    if (activity == null) return
    activity?.let {
      firebaseAuth.signInWithCredential(credential)
          .addOnCompleteListener(it) { task ->
            Timber.i("signInWithCredential:onComplete: ${task.isSuccessful}")
            if (!task.isSuccessful) {
              Timber.e(task.exception)
              presenter.showUserLoginFailed("Authentication failed.")
            }
          }
    }
  }

  override fun onConnectionFailed(result: ConnectionResult) {
    Timber.e(result.errorMessage)
    presenter.showUserLoginFailed(result.errorMessage)
  }

  override fun showUserLoggedInFailed(msg: String?) {
    msg?.let { snack(contUserLogin, it) }
  }

  override fun showUserIsLoggedIn(user: FirebaseUser) {
    user.apply {
      Picasso.with(context).load(photoUrl)
          .placeholder(R.drawable.vector_profile)
          .error(R.drawable.vector_profile)
          .into(imgUserIcon)
      tvName.text = displayName
      tvEmail.text = email
      tvPhoneNumber.text = phoneNumber
    }
    listOf(imgUserIcon, btnUserLogout, tvEmail, tvName, tvPhoneNumber).forEach { it.animateVisible() }
    listOf(btnFacebookLogin, btnGoogleLogin, tvSignInLabel).forEach { it.animateGone() }
  }

  override fun showUserIsLoggedOut() {
    listOf(imgUserIcon, btnUserLogout, tvEmail, tvName, tvPhoneNumber).forEach { it.animateGone() }
    listOf(btnFacebookLogin, btnGoogleLogin, tvSignInLabel).forEach { it.animateVisible() }
  }

  override fun createOrUpdateBitChatUser(bitChatUser: User) {
    userRef.document(bitChatUser.id).set(bitChatUser)
  }

  override fun logoutUser() {
    FirebaseAuth.getInstance().signOut()
    presenter.userIsLoggedOut()
  }
}
