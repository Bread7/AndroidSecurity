package com.example.ict2215_project.presentation.screen.channelList.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ict2215_project.ui.theme.MmtTextMain
import com.example.ict2215_project.ui.theme.robotoFamily

@Composable
fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No channels available",
            color = MmtTextMain,
            fontFamily = robotoFamily,
        )
    }
}