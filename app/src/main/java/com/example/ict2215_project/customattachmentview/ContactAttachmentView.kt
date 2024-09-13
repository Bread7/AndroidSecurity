package com.example.ict2215_project.customattachmentview

import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState

@Composable
fun ContactAttachmentView(attachmentState: AttachmentState, modifier: Modifier = Modifier) {

    val attachment = attachmentState.message.attachments.first { it.type == "contact" }
    val name = attachment.extraData["fullName"] as? String ?: "Unknown"
    val phone = attachment.extraData["phone"] as? String ?: "No number"
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone Icon",
                        modifier = Modifier.width(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = phone
                    )
                }
                LaunchAddContactActivity(name = name, phone = phone)
            }

        }
    }
}
@Composable
fun LaunchAddContactActivity(name: String, phone: String) {
    val addContactLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Nth to do
    }

    Button(onClick = {
        // Intent to launch contacts app
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, name)
            putExtra(ContactsContract.Intents.Insert.PHONE, phone)
        }
        addContactLauncher.launch(intent)
    }, modifier = Modifier.height(IntrinsicSize.Min).padding(top = 10.dp)) {
        Row {
            Icon(Icons.Rounded.Add,"Add Contact", modifier = Modifier.width(18.dp))
            Text("Add Contact" ,textAlign = TextAlign.Center)
        }
    }
}