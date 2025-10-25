package com.example.asistenteiachat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyMessages: List<List<Message>>,
    onConversationClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de conversación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        val historySummaries = historyMessages.mapIndexed { index, messages ->
            val firstUserMessage = messages.firstOrNull { it.isUser }?.text
                ?: "Conversación sin mensajes de usuario."

            val greeting = messages.firstOrNull { !it.isUser }?.text?.take(40) ?: "Asistente IA"

            val truncatedUserMessage = firstUserMessage.take(40) + if (firstUserMessage.length > 40) "..." else ""

            ConversationSummary(
                id = index,
                assistantGreeting = greeting,
                userPromptSummary = truncatedUserMessage
            )
        }

        LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            items(historySummaries) { summary ->
                HistoryItem(
                    summary = summary,
                    onClick = { onConversationClick(summary.id) }
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    summary: ConversationSummary,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(summary.id) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = summary.assistantGreeting,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        Text(
            text = summary.userPromptSummary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            maxLines = 1,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "20 May 20:30",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}