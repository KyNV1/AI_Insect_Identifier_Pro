package com.kynv1.aiinsectidentifierpro.ui.screens.assistance

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightGreyBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.TextCharcoal
import com.kynv1.aiinsectidentifierpro.ui.theme.TextMediumGrey

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

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            withFrameNanos { }
            listState.scrollToItem(uiState.messages.lastIndex)
        }
    }

    val context = LocalContext.current
    LaunchedEffect(uiState.errorResId) {
        uiState.errorResId?.let { resId ->
            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
            viewModel.clearError()
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
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = Dimens.dp_8)
                        .size(Dimens.dp_48)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back_left),
                        contentDescription = stringResource(id = R.string.detail_btn_back),
                        tint = TextCharcoal
                    )
                }

                Text(
                    text = stringResource(id = R.string.assistance_title),
                    fontSize = Dimens.sp_18,
                    fontWeight = FontWeight.Bold,
                    color = TextCharcoal
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
                                .padding(vertical = Dimens.dp_16),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BeeMascot()
                            Spacer(modifier = Modifier.height(Dimens.dp_16))
                            Text(
                                text = stringResource(id = R.string.assistance_hi),
                                fontSize = Dimens.sp_20,
                                fontWeight = FontWeight.Bold,
                                color = TextCharcoal,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(Dimens.dp_4))
                            Text(
                                text = stringResource(id = R.string.assistance_intro),
                                fontSize = Dimens.sp_16,
                                fontWeight = FontWeight.SemiBold,
                                color = ActiveGreen,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(Dimens.dp_4))
                            Text(
                                text = stringResource(id = R.string.assistance_help_today),
                                fontSize = Dimens.sp_14,
                                color = TextMediumGrey,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.dp_24))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimens.dp_1)
                                    .background(LightGreyBorder.copy(alpha = 0.5f))
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.dp_24))
                            
                            Text(
                                text = stringResource(id = R.string.assistance_popular_questions),
                                fontSize = Dimens.sp_14,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMediumGrey,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.dp_12))

                            val emojis = listOf("💡", "🌿", "🦋")
                            quickQuestions.forEachIndexed { idx, question ->
                                val emoji = emojis.getOrElse(idx) { "❓" }
                                val itemInteractionSource = remember { MutableInteractionSource() }
                                val itemPressed by itemInteractionSource.collectIsPressedAsState()
                                val itemScale by animateFloatAsState(
                                    targetValue = if (itemPressed) 0.98f else 1f,
                                    label = "press_scale"
                                )
                                val itemElevation by animateDpAsState(
                                    targetValue = if (itemPressed) Dimens.dp_4 else Dimens.dp_1,
                                    label = "press_elevation"
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Dimens.dp_6)
                                        .graphicsLayer(scaleX = itemScale, scaleY = itemScale)
                                        .shadow(
                                            elevation = itemElevation,
                                            shape = RoundedCornerShape(Dimens.dp_20),
                                            clip = false
                                        )
                                        .background(Color.White, RoundedCornerShape(Dimens.dp_20))
                                        .border(
                                            width = Dimens.dp_1,
                                            color = LightGreyBorder,
                                            shape = RoundedCornerShape(Dimens.dp_20)
                                        )
                                        .clickable(
                                            interactionSource = itemInteractionSource,
                                            indication = LocalIndication.current,
                                            onClick = { viewModel.sendMessage(question) }
                                        )
                                        .padding(
                                            start = Dimens.dp_16,
                                            end = Dimens.dp_8,
                                            top = Dimens.dp_8,
                                            bottom = Dimens.dp_8
                                        )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = emoji, fontSize = Dimens.sp_16)
                                        Spacer(modifier = Modifier.width(Dimens.dp_12))
                                        Text(
                                            text = question,
                                            fontSize = Dimens.sp_14,
                                            color = TextCharcoal,
                                            lineHeight = Dimens.sp_18,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(Dimens.dp_8))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = ActiveGreen,
                                            modifier = Modifier.size(Dimens.dp_22)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(Dimens.dp_24))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimens.dp_1)
                                    .background(LightGreyBorder.copy(alpha = 0.5f))
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.dp_16))
                            
                            Text(
                                text = stringResource(id = R.string.assistance_ask_own_question),
                                fontSize = Dimens.sp_14,
                                color = TextCharcoal,
                                fontWeight = FontWeight.SemiBold
                            )
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
                .background(LightMilkBackground)
                .then(if (isKeyboardVisible) Modifier else Modifier.navigationBarsPadding())
                .padding(bottom = keyboardBottomPadding),
            contentAlignment = Alignment.BottomCenter
        ) {
            AssistanceInputBar(
                inputText = inputText,
                isSending = uiState.isSending,
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

/**
 * The animated mascot. Its infinite transitions live here rather than at screen level so they
 * stop once a conversation starts and the mascot leaves composition — at screen level they kept
 * requesting frames forever, even with the bee no longer on screen.
 */
@Composable
private fun BeeMascot(modifier: Modifier = Modifier) {
    val beeTransition = rememberInfiniteTransition(label = "bee")
    val beeOffsetY by beeTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bee_offset"
    )
    val beeRotation by beeTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bee_rotation"
    )
    // Start and end match: the wobble itself comes entirely from the keyframes below.
    val beeScale by beeTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3500
                1.0f at 0
                1.0f at 3000
                1.1f at 3100
                0.92f at 3200
                1.1f at 3300
                1.0f at 3500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bee_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(Dimens.dp_100)
            .graphicsLayer(
                translationY = beeOffsetY,
                rotationZ = beeRotation,
                scaleX = beeScale,
                scaleY = beeScale
            )
            .background(Color.White, CircleShape)
            .border(BorderStroke(Dimens.dp_1, LightGreyBorder), CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_assistance_bee),
            contentDescription = null,
            modifier = Modifier.size(Dimens.dp_72)
        )
    }
}

// Neither depends on any parameter, so both are hoisted out of AssistanceInputBar —
// otherwise BasicTextField would allocate fresh instances on every keystroke.
private val AssistanceInputTextStyle = TextStyle(color = TextCharcoal, fontSize = Dimens.sp_14)
private val AssistanceInputKeyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)

@Composable
fun AssistanceInputBar(
    inputText: String,
    isSending: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canSend = inputText.isNotBlank() && !isSending

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
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.assistance_message_placeholder),
                        color = TextMediumGrey,
                        fontSize = Dimens.sp_14
                    )
                }
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = AssistanceInputTextStyle,
                    // Lets the user send straight from the keyboard instead of reaching
                    // for the button after every message. Gated by canSend so it can't
                    // fire (and silently clear the field) while a reply is in flight.
                    keyboardOptions = AssistanceInputKeyboardOptions,
                    keyboardActions = remember(canSend, onSend) {
                        KeyboardActions(onSend = { if (canSend) onSend() })
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(Dimens.dp_12))

        Box(
            modifier = Modifier
                .size(Dimens.dp_48)
                // Dimmed when there is nothing to send, so a dead tap is never a silent one.
                .background(
                    if (canSend) ActiveGreen else ActiveGreen.copy(alpha = 0.3f),
                    CircleShape
                )
                .clip(CircleShape)
                .clickable(enabled = canSend, onClick = onSend),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(id = R.string.assistance_send_desc),
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
    val bubbleTextColor = if (message.isUser) Color.White else TextCharcoal
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
                text = parseMarkdownToAnnotatedString(message.text),
                color = bubbleTextColor,
                fontSize = Dimens.sp_14,
                lineHeight = Dimens.sp_20
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
                color = TextMediumGrey,
                fontSize = Dimens.sp_14,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

private fun parseMarkdownToAnnotatedString(rawText: String): AnnotatedString {
    val cleanedText = rawText
        .lines()
        .filter { line -> line.trim() != "---" && line.trim() != "***" }
        .joinToString("\n")
        .replace(Regex("^###\\s+", RegexOption.MULTILINE), "")
        .replace(Regex("^##\\s+", RegexOption.MULTILINE), "")
        .replace(Regex("^#\\s+", RegexOption.MULTILINE), "")

    return buildAnnotatedString {
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        var lastIndex = 0

        for (match in boldRegex.findAll(cleanedText)) {
            val start = match.range.first
            val end = match.range.last + 1
            val innerText = match.groupValues[1]

            if (start > lastIndex) {
                append(cleanedText.substring(lastIndex, start))
            }

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(innerText)
            }

            lastIndex = end
        }

        if (lastIndex < cleanedText.length) {
            append(cleanedText.substring(lastIndex))
        }
    }
}
