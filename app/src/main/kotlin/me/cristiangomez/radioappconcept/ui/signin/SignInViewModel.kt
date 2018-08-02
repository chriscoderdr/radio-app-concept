package me.cristiangomez.radioappconcept.ui.signin

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import me.cristiangomez.radioappconcept.R


class SignInViewModel : ViewModel() {
    private val facebookCallbackManager = CallbackManager.Factory.create()!!
    private val facebookLoginManager = LoginManager.getInstance()!!
    var onSuccessCallback: (() -> Unit)? = null
    var onErrorCallback: ((error: SignInError) -> Unit)? = null
    var onCancelCallback: (() -> Unit)? = null

    init {
        LoginManager.getInstance().registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val credential = FacebookAuthProvider.getCredential(result!!.accessToken.token)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnSuccessListener {
                            onSuccessCallback?.invoke()
                        }.addOnFailureListener {
                            onErrorCallback?.invoke(SignInError.ERROR)
                        }
            }

            override fun onCancel() {
                onCancelCallback?.invoke()
            }

            override fun onError(error: FacebookException?) {
                Crashlytics.logException(error)
                onErrorCallback?.invoke(SignInError.ERROR)
            }
        })
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (error: SignInError) -> Unit) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener {
                    when (it) {
                        is FirebaseNetworkException -> onError(SignInError.NETWORK_EXCEPTION)
                        is FirebaseAuthInvalidCredentialsException -> onError(SignInError.INVALID_CREDENTIALS)
                        is FirebaseAuthInvalidUserException -> onError(SignInError.INVALID_CREDENTIALS)
                        else -> {
                            Crashlytics.logException(it)
                            onError(SignInError.ERROR)
                        }
                    }
                }
    }

    fun signInWithFacebook(fragment: Fragment, onSuccess: () -> Unit,
                           onError: (error: SignInError) -> Unit, onCancel: () -> Unit) {
        this.onSuccessCallback = onSuccess
        this.onErrorCallback = onError
        this.onCancelCallback = onCancel
        facebookLoginManager.logInWithReadPermissions(fragment, listOf("public_profile", "email"))
    }

    fun onFacebookResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun signInWithGoogle(fragment: Fragment, onSuccess: () -> Unit,
                         onError: (error: SignInError) -> Unit, onCancel: () -> Unit) {
        onErrorCallback = onError
        onSuccessCallback = onSuccess
        onCancelCallback = onCancel
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.activity!!.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val intent = GoogleSignIn.getClient(fragment.requireContext().applicationContext, googleSignInOptions).signInIntent
        fragment.startActivityForResult(intent, ACTIVITY_RESULT_GOOGLE_SIGN_IN)
    }

    fun onGoogleResult(requestCode: Int, data: Intent?) {
        if (requestCode == SignInViewModel.ACTIVITY_RESULT_GOOGLE_SIGN_IN) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnSuccessListener {
                            onSuccessCallback?.invoke()
                        }
                        .addOnFailureListener {
                            Crashlytics.logException(it)
                            onErrorCallback?.invoke(SignInError.ERROR)
                        }
            } catch (apiException: ApiException) {
                if (apiException.statusCode == 7) {
                    onErrorCallback?.invoke(SignInError.NETWORK_EXCEPTION)
                } else {
                    onErrorCallback?.invoke(SignInError.ERROR)
                    Crashlytics.logException(apiException)
                }
            }
        }
    }

    companion object {
        const val ACTIVITY_RESULT_GOOGLE_SIGN_IN = 1212
    }

    enum class SignInError {
        ERROR, NETWORK_EXCEPTION, INVALID_CREDENTIALS
    }
}
