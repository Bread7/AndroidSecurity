package com.example.ict2215_project.customattachmentview



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import java.lang.Double.parseDouble

@Composable
fun LocationAttachmentView(attachmentState: AttachmentState,modifier: Modifier = Modifier,) {

    val LocationAttachment = attachmentState.message.attachments.first { it.type == "location" }
    val latitude = LocationAttachment.extraData["latitude"].toString()
    val longitude = LocationAttachment.extraData["longitude"].toString()

    val currentPosition = LatLng(parseDouble(latitude),parseDouble(longitude))
    val cameraPositionState = CameraPositionState(CameraPosition.fromLatLngZoom(currentPosition,10f))
    Box(
        modifier = Modifier
            .padding(6.dp)
            .clip(ChatTheme.shapes.attachment)
            .defaultMinSize(minHeight = 300.dp, minWidth = 300.dp)
            .background(Color.White)
    ) {

        //Column(modifier= Modifier.fillMaxHeight()) {
            GoogleMap(modifier= Modifier.matchParentSize(), cameraPositionState= cameraPositionState){
                    Marker(
                        state = MarkerState(
                            position = currentPosition,
                        ),
                        title="Here",
                    )
            }
            //Text(latitude)
            //Text(longitude)
       // }
    }
}