package com.kynv1.aiinsectidentifierpro.ui.screens.sound

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.NeonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreenText
import com.kynv1.aiinsectidentifierpro.ui.theme.AccentLime
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.MediumForestGreen
import kotlinx.coroutines.delay

sealed interface SoundScanState {
    object Listening : SoundScanState
    object Analyzing : SoundScanState
    data class Success(val insectName: String, val confidence: Int, val id: Long) : SoundScanState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundScanScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var scanState by remember { mutableStateOf<SoundScanState>(SoundScanState.Listening) }
    var secondsLeft by remember { mutableIntStateOf(5) }

    val bgTransition = rememberInfiniteTransition(label = "bg_zoom")
    val bgScale by bgTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_scale"
    )

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        scanState = SoundScanState.Analyzing
        delay(2500)
        scanState = SoundScanState.Success(
            insectName = "Honey Bee (Apis mellifera)",
            confidence = 94,
            id = 10001L
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.sound_scan_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.sp_20
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkForestGreen,
                    titleContentColor = Color.White
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_scan_insect_butterfly),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = bgScale, scaleY = bgScale)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.12f))
            )

            when (val state = scanState) {
                is SoundScanState.Listening -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(Dimens.dp_16)
                    ) {
                        Text(
                            text = stringResource(id = R.string.sound_scan_subtitle),
                            color = DarkForestGreenText,
                            fontSize = Dimens.sp_22,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_4))
                        Text(
                            text = stringResource(id = R.string.sound_scan_instruction),
                            color = Color(0xFF333333),
                            fontSize = Dimens.sp_14,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_48))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.White.copy(alpha = 0.48f), shape = CircleShape)
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = null,
                                tint = NatureGreen,
                                modifier = Modifier.size(Dimens.dp_64)
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.dp_48))

                        SoundWaveAnimation()

                        Spacer(modifier = Modifier.height(Dimens.dp_48))

                        Text(
                            text = "0:0${secondsLeft}",
                            color = NatureGreen,
                            fontSize = Dimens.sp_28,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                is SoundScanState.Analyzing -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(Dimens.dp_16)
                    ) {
                        CircularProgressIndicator(
                            color = NatureGreen,
                            modifier = Modifier.size(Dimens.dp_64)
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_24))
                        Text(
                            text = stringResource(id = R.string.sound_scan_loading),
                            color = DarkForestGreenText,
                            fontSize = Dimens.sp_18,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is SoundScanState.Success -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(Dimens.dp_16)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(Dimens.dp_64) 
                                .background(NatureGreen.copy(alpha = 0.2f), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = NatureGreen,
                                modifier = Modifier.size(Dimens.dp_40)
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.dp_24))
                        Text(
                            text = stringResource(id = R.string.sound_scan_success),
                            color = NatureGreen,
                            fontSize = Dimens.sp_22,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_16))

                        Card(
                            shape = RoundedCornerShape(Dimens.dp_16),
                            border = BorderStroke(Dimens.dp_1, Color.White.copy(alpha = 0.6f)),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.48f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.dp_16)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.dp_16),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = state.insectName,
                                    color = DarkForestGreenText,
                                    fontSize = Dimens.sp_18,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(Dimens.dp_4))
                                Text(
                                    text = stringResource(
                                        id = R.string.sound_scan_confidence_format,
                                        state.confidence
                                    ),
                                    color = NatureGreen,
                                    fontSize = Dimens.sp_14,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(Dimens.dp_24))

                                Button(
                                    onClick = { onNavigateToDetail(state.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NatureGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(Dimens.dp_50),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.sound_scan_btn_view_details),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = Dimens.sp_16
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimens.dp_16))

                        TextButton(onClick = {
                            secondsLeft = 5
                            scanState = SoundScanState.Listening
                        }) {
                            Text(
                                text = "Scan Again",
                                color = Color(0xFF757575),
                                fontSize = Dimens.sp_14
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SoundWaveAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val count = 12
    val waveScales = List(count) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 400 + (index * 60),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "wave_scale_$index"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.dp_64),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        waveScales.forEach { scale ->
            Box(
                modifier = Modifier
                    .padding(horizontal = Dimens.dp_3)
                    .width(Dimens.dp_6)
                    .fillMaxHeight(scale.value)
                    .clip(RoundedCornerShape(Dimens.dp_50))
                    .background(AccentLime)
            )
        }
    }
}
