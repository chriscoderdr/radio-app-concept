package me.cristiangomez.radioappconcept.ui.userprofile

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel;
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class UserProfileViewModel : ViewModel() {
    fun logout(activity: AppCompatActivity) {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            if (GoogleSignIn.getLastSignedInAccount(activity) != null) {
                GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .revokeAccess()
            }
            LoginManager.getInstance().logOut()
            firebaseAuth.signOut()
        }
    }
}
