package com.example.todofcrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.todofcrud.auth.viewmodel.AuthViewModel
import com.example.todofcrud.navigation.AppNavGraph
import com.example.todofcrud.ui.theme.TodoFCRUDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val authViewModel: AuthViewModel by viewModels()

        setContent {
            TodoFCRUDTheme {
                AppNavGraph(authViewModel = authViewModel)
            }
        }
    }
}