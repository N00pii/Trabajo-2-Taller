package com.example.asistenteiachat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente IA") },
                actions = {
                    Button(onClick = onNavigateToHistory) {
                        Text("Ver historial")
                    }
                }
            )
        },
        bottomBar = {
            InputBar(onSendMessage = onSendMessage)
        }
    ) { paddingValues ->
        ConversationArea(
            messages = messages,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBar(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val submitMessage = {
        if (text.isNotBlank()) {
            onSendMessage(text)
            text = ""
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("ESCRIBIR AQUÍ") },
            modifier = Modifier.weight(1f),
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { submitMessage() })
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = submitMessage,
            // El 'enabled' es un parámetro estándar y no experimental en IconButton
            enabled = text.isNotBlank(),
            modifier = Modifier
                .size(48.dp) // Le damos un tamaño similar al FAB
                .background(
                    if (text.isNotBlank()) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = androidx.compose.foundation.shape.CircleShape // Forma circular
                )
        ) {
            // Usaremos el ícono de envío estándar, ya que el 'AutoMirrored' puede ser parte del conflicto
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar mensaje",
                tint = MaterialTheme.colorScheme.onPrimary // Color del icono
            )
        }
    }
}

@Composable
fun ConversationArea(messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
        contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        items(messages.reversed()) { message ->
            MessageBubble(message = message)
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isUser) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
    val textColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .widthIn(max = 300.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}