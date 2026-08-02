package com.kynv1.aiinsectidentifierpro.ui.screens.sound

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.common.AudioRecorderHelper
import com.kynv1.aiinsectidentifierpro.common.SoundClassifierEngine
import com.kynv1.aiinsectidentifierpro.data.local.InsectDatabase
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.remote.GeminiServiceClient
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import com.kynv1.aiinsectidentifierpro.ui.theme.AccentLime
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreenText
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.TextCharcoal
import com.kynv1.aiinsectidentifierpro.ui.theme.TextMediumGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.File

sealed interface SoundScanState {
    object Listening : SoundScanState
    object Analyzing : SoundScanState
    data class Success(val insectName: String, val confidence: Int, val id: Long) : SoundScanState
}

@Composable
fun SoundScanScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var scanState by remember { mutableStateOf<SoundScanState>(SoundScanState.Listening) }
    var secondsLeft by remember { mutableIntStateOf(5) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    val audioRecorder = remember { AudioRecorderHelper(context) }
    val geminiService = remember { GeminiServiceClient() }
    val insectDao = remember { InsectDatabase.getDatabase(context).insectDao() }
    val repository = remember { InsectRepository(insectDao, geminiService) }
    var scanToken by remember { mutableIntStateOf(0) }

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
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(hasPermission, scanToken) {
        if (scanState is SoundScanState.Listening) {
            secondsLeft = 5
            var recordedFile: File? = null
            if (hasPermission) {
                recordedFile = audioRecorder.startRecording()
            }

            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }

            val audioFile = if (hasPermission) audioRecorder.stopRecording() else recordedFile
            scanState = SoundScanState.Analyzing

            var detectedName: String
            var detectedScientific: String
            var detectedConfidence: Int

            var audioInfo: com.kynv1.aiinsectidentifierpro.data.model.InsectInfo? = null
            try {
                withTimeoutOrNull(15000L) {
                    audioInfo = geminiService.identifyInsectFromAudioFile(audioFile)
                }
            } catch (e: Exception) {
                Timber.e(e, "Gemini audio recognition error")
            }

            var targetId = 10009L

            val info = audioInfo
            if (info != null && !info.commonName.contains("Unrecognized", ignoreCase = true) && !info.commonName.contains("Lỗi", ignoreCase = true) && !info.commonName.contains("Thiếu", ignoreCase = true)) {
                detectedName = info.commonName
                detectedScientific = info.scientificName
                detectedConfidence = info.confidence

                val entity = InsectEntity.fromInsectInfo(
                    info,
                    "android.resource://${context.packageName}/${R.drawable.bg_scan_insect_butterfly}"
                )
                targetId = repository.insertInsect(entity)
            } else {
                detectedName = context.getString(R.string.sound_scan_no_match)
                detectedScientific = ""
                detectedConfidence = 0
            }

            val displayName = if (detectedScientific.isNotBlank() && !detectedScientific.equals("None", ignoreCase = true)) {
                "$detectedName ($detectedScientific)"
            } else {
                detectedName
            }

            scanState = SoundScanState.Success(
                insectName = displayName,
                confidence = detectedConfidence,
                id = targetId
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(Dimens.dp_16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SoundScanTopBar(onBack = onBack)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = scanState) {
                    is SoundScanState.Listening -> {
                        SoundScanListeningContent(secondsLeft = secondsLeft)
                    }

                    is SoundScanState.Analyzing -> {
                        SoundScanAnalyzingContent()
                    }

                    is SoundScanState.Success -> {
                        SoundScanSuccessContent(
                            state = state,
                            onNavigateToDetail = onNavigateToDetail,
                            onScanAgain = {
                                scanToken++
                                scanState = SoundScanState.Listening
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundScanTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.dp_16),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = Dimens.dp_8)
                .size(Dimens.dp_36)
                .background(Color.White.copy(alpha = 0.25f), CircleShape)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current
                ) { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back_left),
                contentDescription = stringResource(id = R.string.detail_btn_back),
                tint = Color.White,
                modifier = Modifier.size(Dimens.dp_18)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.sound_scan_title),
                color = DarkForestGreenText,
                fontSize = Dimens.sp_20,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(Dimens.dp_2))
            Text(
                text = stringResource(id = R.string.sound_scan_subtitle),
                color = TextCharcoal,
                fontSize = Dimens.sp_12,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.dp_16)
            )
        }
    }
}

@Composable
private fun SoundScanListeningContent(secondsLeft: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(Dimens.dp_16)
    ) {
        Text(
            text = stringResource(id = R.string.sound_scan_instruction),
            color = TextCharcoal,
            fontSize = Dimens.sp_14,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.dp_32))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(Dimens.dp_120)
                .background(
                    Color.White.copy(alpha = 0.48f),
                    shape = CircleShape
                )
                .border(
                    BorderStroke(Dimens.dp_1, Color.White.copy(alpha = 0.6f)),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Hearing,
                contentDescription = null,
                tint = NatureGreen,
                modifier = Modifier.size(Dimens.dp_64)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.dp_32))

        SoundWaveAnimation()

        Spacer(modifier = Modifier.height(Dimens.dp_32))

        Text(
            text = "0:0$secondsLeft",
            color = NatureGreen,
            fontSize = Dimens.sp_28,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SoundScanAnalyzingContent() {
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

@Composable
private fun SoundScanSuccessContent(
    state: SoundScanState.Success,
    onNavigateToDetail: (Long) -> Unit,
    onScanAgain: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(Dimens.dp_16)
    ) {
        Card(
            shape = RoundedCornerShape(Dimens.dp_16),
            border = BorderStroke(Dimens.dp_1, Color.White.copy(alpha = 0.6f)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.48f)
            ),
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
                Spacer(modifier = Modifier.height(Dimens.dp_6))
                Text(
                    text = stringResource(
                        id = R.string.sound_scan_confidence_format,
                        state.confidence
                    ),
                    color = TextCharcoal,
                    fontSize = Dimens.sp_14
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.dp_32))

        Button(
            onClick = { onNavigateToDetail(state.id) },
            colors = ButtonDefaults.buttonColors(containerColor = NatureGreen),
            shape = RoundedCornerShape(Dimens.dp_24),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp_50)
        ) {
            Text(
                text = stringResource(id = R.string.sound_scan_btn_view_details),
                fontSize = Dimens.sp_16,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Dimens.dp_12))

        TextButton(onClick = onScanAgain) {
            Text(
                text = stringResource(id = R.string.sound_scan_btn_scan_again),
                color = TextMediumGrey,
                fontSize = Dimens.sp_14
            )
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
