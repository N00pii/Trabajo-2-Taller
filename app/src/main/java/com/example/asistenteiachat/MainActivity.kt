package com.example.asistenteiachat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.asistenteiachat.ui.theme.AsistenteIAChatTheme

sealed class Screen(val route: String) {
    data object Chat : Screen("chat")
    data object History : Screen("history")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AsistenteIAChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: ChatViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Chat.route
                    ) {
                        composable(Screen.Chat.route) {
                            val uiState by viewModel.uiState.collectAsState()

                            ChatScreen(
                                messages = uiState.messages,
                                onSendMessage = viewModel::sendMessage,
                                onNavigateToHistory = {
                                    viewModel.saveCurrentConversation()
                                    navController.navigate(Screen.History.route)
                                }
                            )
                        }

                        composable(Screen.History.route) {
                            val historyState by viewModel.history.collectAsState()

                            HistoryScreen(
                                historyMessages = historyState,
                                onConversationClick = { index ->
                                    viewModel.resumeConversation(index)
                                    navController.popBackStack(Screen.Chat.route, inclusive = false)
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}