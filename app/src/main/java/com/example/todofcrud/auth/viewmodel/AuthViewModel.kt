package com.example.todofcrud.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todofcrud.auth.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed class to represent the various states of authentication.
// This helps the UI decide what to show (Loading spinner, Login screen, or Home screen).
sealed class AuthState {
    object Unauthenticated : AuthState() // User is not logged in
    object Loading : AuthState()         // Auth operation in progress
    object Authenticated : AuthState()   // User is successfully logged in
    data class Error(val message: String) : AuthState() // An error occurred
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize the repository.
    // We pass the application context because Google Sign-In needs it.
    private val repository = AuthRepository(application)
    
    // MutableStateFlow to hold the current authentication state.
    // We start as 'Loading' to check if a user is already logged in on launch.
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Check if user is currently logged in (persisted session).
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        if (repository.hasUser()) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // login by mail and pass
    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Email and Password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.login(email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    // register by mail and pass
    fun register(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Email and Password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.register(email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    // Get the GoogleSignInClient to launch the sign-in intent from UI.
    fun getGoogleSignInClient(): GoogleSignInClient {
        return repository.getGoogleSignInClient()
    }

    // Complete Google Sign-In with the credential received from the UI.
    fun signInWithGoogle(credential: AuthCredential) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signInWithGoogle(credential)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Google Sign-In failed")
            }
        }
    }

    // logout
    fun logout() {
        repository.logout()
        _authState.value = AuthState.Unauthenticated
    }
}
