package com.convenience.store.core.ui.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PasswordTextField(
    state: TextFieldState,
    label: String,
    contentDescription: String
) {
    var showPassword by remember { mutableStateOf(false) }

    BasicSecureTextField(
        state = state,
        textObfuscationMode =
            if (showPassword) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.RevealLastTyped
            },
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(6.dp)),
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp, end = 48.dp)
                ) {
                    if (state.text.isEmpty()) {
                        Text(
                            text = label,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
                Icon(
                    imageVector = if (showPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .requiredSize(48.dp)
                        .padding(12.dp)
                        .clickable { showPassword = !showPassword }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PasswordTextFieldPreview() {
    PasswordTextField(
        rememberTextFieldState(),
        "Password",
        "Toggle password visibility"
    )
}