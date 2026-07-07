package com.kynv1.aiinsectidentifierpro.ui.screens.assistance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightGreyBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground

@Composable
fun AssistanceScreen(
    viewModel: AssistanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val imeBottomPadding = with(density) { imeBottomPx.toDp() }
    val isKeyboardVisible = imeBottomPx > 0
    val keyboardBottomPadding = if (isKeyboardVisible) {
        imeBottomPadding
    } else {
        0.dp
    }

    val quickQuestions = listOf(
        stringResource(id = R.string.assistance_q1),
        stringResource(id = R.string.assistance_q2),
        stringResource(id = R.string.assistance_q3),
    )

    // Scroll only when a new message is added. Keyboard insets animate frame-by-frame,
    // so using them as a key here makes the chat list visibly jump.
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            withFrameNanos { }
            listState.scrollToItem(uiState.messages.lastIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightMilkBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar using Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp_56)
                    .background(LightMilkBackground),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = Dimens.dp_8)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = stringResource(id = R.string.assistance_title),
                    fontSize = Dimens.sp_18,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Chat Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.dp_16),
                verticalArrangement = Arrangement.spacedBy(Dimens.dp_12),
                contentPadding = PaddingValues(
                    top = Dimens.dp_8,
                    bottom = Dimens.dp_100 + keyboardBottomPadding
                )
            ) {
                // If no messages, show start guide & quick bubbles
                if (uiState.messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.dp_16)
                        ) {
                            Text(
                                text = stringResource(id = R.string.assistance_start_chatting),
                                fontSize = Dimens.sp_18,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = Dimens.dp_16)
                            )

                            quickQuestions.forEach { question ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Dimens.dp_6)
                                        .background(Color.White, RoundedCornerShape(Dimens.dp_20))
                                        .border(
                                            width = Dimens.dp_1,
                                            color = LightGreyBorder,
                                            shape = RoundedCornerShape(Dimens.dp_20)
                                        )
                                        .clickable {
                                            viewModel.sendMessage(question)
                                        }
                                        .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_12)
                                ) {
                                    Text(
                                        text = question,
                                        fontSize = Dimens.sp_14,
                                        color = Color.DarkGray,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(uiState.messages, key = { it.id }) { message ->
                        ChatBubble(message = message)
                    }
                    if (uiState.isSending) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .then(if (isKeyboardVisible) Modifier else Modifier.navigationBarsPadding())
                .background(LightMilkBackground)
                .padding(bottom = keyboardBottomPadding),
            contentAlignment = Alignment.BottomCenter
        ) {
            AssistanceInputBar(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimens.dp_16,
                        top = if (isKeyboardVisible) Dimens.dp_4 else Dimens.dp_12,
                        end = Dimens.dp_16,
                        bottom = if (isKeyboardVisible) 0.dp else Dimens.dp_12
                    )
            )
        }
    }
}

@Composable
fun AssistanceInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(Dimens.dp_48)
                .background(Color.White, RoundedCornerShape(Dimens.dp_24))
                .border(Dimens.dp_1, LightGreyBorder, RoundedCornerShape(Dimens.dp_24))
                .padding(horizontal = Dimens.dp_16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "AI Hint",
                tint = ActiveGreen,
                modifier = Modifier.size(Dimens.dp_20)
            )
            Spacer(modifier = Modifier.width(Dimens.dp_10))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.assistance_message_placeholder),
                        color = Color.Gray,
                        fontSize = Dimens.sp_14
                    )
                }
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.width(Dimens.dp_12))

        Box(
            modifier = Modifier
                .size(Dimens.dp_48)
                .background(ActiveGreen, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onSend),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(Dimens.dp_20)
            )
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val arrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    val bubbleBgColor = if (message.isUser) ActiveGreen else Color.White
    val bubbleTextColor = if (message.isUser) Color.White else Color.Black
    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(
            topStart = Dimens.dp_16,
            topEnd = Dimens.dp_16,
            bottomStart = Dimens.dp_16,
            bottomEnd = 0.dp
        )
    } else {
        RoundedCornerShape(
            topStart = Dimens.dp_16,
            topEnd = Dimens.dp_16,
            bottomStart = 0.dp,
            bottomEnd = Dimens.dp_16
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = Dimens.dp_280)
                .background(bubbleBgColor, bubbleShape)
                .then(
                    if (!message.isUser) Modifier.border(
                        Dimens.dp_1,
                        LightCardBorder,
                        bubbleShape
                    ) else Modifier
                )
                .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_12)
        ) {
            Text(
                text = message.text,
                color = bubbleTextColor,
                fontSize = Dimens.sp_14,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color.White,
                    RoundedCornerShape(
                        topStart = Dimens.dp_16,
                        topEnd = Dimens.dp_16,
                        bottomStart = 0.dp,
                        bottomEnd = Dimens.dp_16
                    )
                )
                .border(
                    Dimens.dp_1,
                    LightCardBorder,
                    RoundedCornerShape(
                        topStart = Dimens.dp_16,
                        topEnd = Dimens.dp_16,
                        bottomStart = 0.dp,
                        bottomEnd = Dimens.dp_16
                    )
                )
                .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_12)
        ) {
            Text(
                text = stringResource(id = R.string.assistance_thinking),
                color = Color.Gray,
                fontSize = Dimens.sp_14,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
