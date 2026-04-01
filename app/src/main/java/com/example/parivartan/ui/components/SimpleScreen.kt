package com.example.parivartan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SimpleScreen(
    title: String,
    modifier: Modifier = Modifier,
    actions: List<SimpleAction> = emptyList(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        if (actions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            actions.forEach { action ->
                Button(onClick = action.onClick) {
                    Text(action.label)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

data class SimpleAction(
    val label: String,
    val onClick: () -> Unit
)
