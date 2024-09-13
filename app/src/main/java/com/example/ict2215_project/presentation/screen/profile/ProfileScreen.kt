package com.example.ict2215_project.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ict2215_project.presentation.screen.loading.LoadingScreen
import io.getstream.chat.android.compose.ui.components.avatar.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit = {},
    navigateToChannelList: () -> Unit = {}
) {
    val state by viewModel.profileState.collectAsState()
    when (state) {
        is ProfileState.Loading -> LoadingScreen(modifier = modifier)
        is ProfileState.Success -> {
            val user = (state as ProfileState.Success).user
            var name by remember { mutableStateOf(user.name) }

            Scaffold(topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = navigateToChannelList) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                             viewModel.updateProfile(name)
                        }) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Save",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            }) { paddingValues ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    UserAvatar(
                        user = user,
                        showOnlineIndicator = false,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp),
                        shape = CircleShape
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Name",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            viewModel.logout()
                            navigateToLogin()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CircleShape
                    ) {
                        Text(text = "Logout", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        else -> Unit // Handle other states if necessary
    }
}