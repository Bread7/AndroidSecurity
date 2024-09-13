package com.example.ict2215_project.utils

import androidx.compose.runtime.Composable
import com.example.ict2215_project.customattachmentview.ContactAttachmentView
import com.example.ict2215_project.customattachmentview.LocationAttachmentView
import io.getstream.chat.android.compose.ui.attachments.AttachmentFactory
import io.getstream.chat.android.core.ExperimentalStreamChatApi


@ExperimentalStreamChatApi
val customAttachmentFactories: List<AttachmentFactory> = listOf(
    AttachmentFactory(
        canHandle = { attachments -> attachments.any { it.type == "location" } },
        content = @Composable { modifier, attachmentState -> LocationAttachmentView(attachmentState, modifier) },

    ),
    AttachmentFactory(
        canHandle = { attachments -> attachments.any { it.type == "contact" } },
        content = @Composable { modifier, attachmentState -> ContactAttachmentView(attachmentState, modifier) },
    ),

)
