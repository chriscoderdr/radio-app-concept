package me.cristiangomez.radioappconcept.ui.signup

import androidx.lifecycle.ViewModel;
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpViewModel : ViewModel() {
    fun signUp(displayName: String, email: String, password: String,
               onSuccess: () -> Unit, onError: (error: SignUpError) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    it.user.updateProfile(UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build())
                    onSuccess()
                }
                .addOnFailureListener {
                    when (it) {
                        is FirebaseAuthUserCollisionException -> onError(SignUpError.UserCollision)
                        is FirebaseNetworkException -> onError(SignUpError.NetworkError)
                        else -> {
                            Crashlytics.logException(it)
                            onError(SignUpError.Error)
                        }
                    }
                }
                .addOnCanceledListener {
                    onError(SignUpError.Error)
                }
    }

    enum class SignUpError {
        Error, UserCollision, NetworkError
    }
}
