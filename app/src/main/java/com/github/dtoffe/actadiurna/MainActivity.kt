package com.github.dtoffe.actadiurna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.github.dtoffe.actadiurna.ui.MainScreen
import com.github.dtoffe.actadiurna.ui.TodoViewModel
import com.github.dtoffe.actadiurna.ui.theme.ActaDiurnaTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActaDiurnaTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
