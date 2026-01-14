package com.example.todofcrud.auth.data

import android.content.Context
import android.util.Log
import com.example.todofcrud.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {

    // 1. Get the instance of FirebaseAuth.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 2. Configure Google Sign-In options.
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    // 3. Create the GoogleSignInClient using the options.
    fun getGoogleSignInClient(): GoogleSignInClient {
        return GoogleSignIn.getClient(context, gso)
    }

    // 4. Check if a user is currently logged in.
    fun hasUser(): Boolean {
        return auth.currentUser != null
    }

    fun getUserId(): String {
        return auth.currentUser?.uid.orEmpty()
    }

    // Get the current user object
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun register(email: String, pass: String): Result<FirebaseUser> {
        return try {
            // createUserWithEmailAndPassword sends the request to Firebase.
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration error", e)
            Result.failure(e)
        }
    }

    suspend fun login(email: String, pass: String): Result<FirebaseUser> {
        return try {
            // signInWithEmailAndPassword checks credentials against Firebase.
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(credential: AuthCredential): Result<FirebaseUser> {
        return try {
            // signInWithCredential exchanges the Google token for a Firebase user session.
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Google sign in failed"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google Auth error", e)
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        // Sign out from Google Client to clear local session
        // (Allows user to select a different Google account next time)
        val googleClient = GoogleSignIn.getClient(context, gso)
        googleClient.signOut()
    }
}