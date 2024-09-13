package com.example.ict2215_project.presentation.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ict2215_project.R
import com.example.ict2215_project.ui.theme.MmtDarkGrey
import com.example.ict2215_project.ui.theme.MmtPink
import com.example.ict2215_project.ui.theme.MmtSelectedDarkGrey
import com.example.ict2215_project.ui.theme.MomoTalkTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToConversations: () -> Unit = { },
    navigateToSignUp: () -> Unit = { }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MmtPink)
            .padding(40.dp, 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = modifier
                .padding(8.dp, 2.dp)
                .size(150.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.logo_unscaled),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Logo",
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "MomoTalk",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Login",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            shape = CircleShape,
            label = { Text("Email", style = MaterialTheme.typography.titleSmall) },
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            shape = CircleShape,
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password", style = MaterialTheme.typography.titleSmall) },
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                viewModel.login(email, password)
            })
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.width(230.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.login(email, password)
                    }, colors = ButtonColors(
                        MmtDarkGrey, Color.White, MmtSelectedDarkGrey, Color.White
                    ), enabled = true //email.isNotEmpty() && password.isNotEmpty()
                ) {
                    Text(
                        text = "Log in",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = navigateToSignUp, colors = ButtonColors(
                        MmtDarkGrey, Color.White, MmtSelectedDarkGrey, Color.White
                    )
                ) {
                    Text(
                        text = "Sign Up",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
        when (loginState) {
            is LoginState.Loading -> {
                // Show a loading indicator
                Spacer(modifier = Modifier.height(16.dp))
                Text("Logging in...", color = MaterialTheme.colorScheme.onPrimary)
            }

            is LoginState.Success -> {
                LaunchedEffect(Unit) {
                    navigateToConversations()
                }
            }

            is LoginState.Error -> {
                Text("Login failed: ${(loginState as LoginState.Error).error.message}")
            }

            else -> Unit // Do nothing
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MomoTalkTheme {
        LoginScreen(modifier = Modifier, navigateToConversations = { })
    }
}
