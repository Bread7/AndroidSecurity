package com.example.ict2215_project.presentation.screen.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit = { },
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val signUpState by viewModel.signUpState.collectAsState()
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
            modifier = Modifier
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
            text = "Sign Up",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            shape = CircleShape,
            label = { Text("Username", style = MaterialTheme.typography.titleSmall) },
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
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
            })
        )
        Spacer(modifier = Modifier.height(16.dp))
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            val signUpOptions = listOf("Sign up as a Student", "Sign up as a Teacher")
            signUpOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = signUpOptions.size
                    ),
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex,
                    modifier = Modifier.fillMaxHeight(),
                    icon = { Text("") },
                    colors = SegmentedButtonColors(
                        Color.White,
                        MmtPink,
                        Color.White,
                        MmtPink,
                        Color.White,
                        Color.White,
                        Color.White,
                        Color.White,
                        Color.White,
                        Color.White,
                        Color.White,
                        Color.White
                    )
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Column(
            modifier = Modifier.width(230.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.signUp(
                            username = username,
                            email = email,
                            password = password,
                            role = if (selectedIndex == 0) "user" else "admin"
                        )
                    },
                    colors = ButtonColors(
                        MmtDarkGrey, Color.White, MmtSelectedDarkGrey, Color.White
                    ),
                    enabled = username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                ) {
                    Text(
                        text = "Sign Up",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = navigateToLogin, colors = ButtonColors(
                        MmtDarkGrey, Color.White, MmtSelectedDarkGrey, Color.White
                    )
                ) {
                    Text(
                        text = "Log In",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
        when (signUpState) {
            is SignUpState.Loading -> // Show loading state
            {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Signing up...", color = MaterialTheme.colorScheme.onPrimary)
            }

            is SignUpState.Success -> // Handle success
            {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sign up successful!", color = MaterialTheme.colorScheme.onPrimary)
            }

            is SignUpState.Error -> // Display error message
            {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sign up failed: ${(signUpState as SignUpState.Error).error.message}")
            }

            else -> Unit
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUpScreen(modifier = Modifier, navigateToLogin = { })
}
