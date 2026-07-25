package com.kynv1.aiinsectidentifierpro.ui.screens.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.TextCharcoal
import com.kynv1.aiinsectidentifierpro.ui.theme.TextDarkGrey
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureDarkGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureLightGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.NeonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreenText
import com.kynv1.aiinsectidentifierpro.ui.theme.ButtonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkButtonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.DisabledButtonGreen
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedImageUri = viewModel.selectedImageUri

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempPhotoUri?.let { viewModel.onImageSelected(it) }
            }
        }
    )

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.onImageSelected(it) }
        }
    )

    fun createTempImageUri(): Uri {
        val tempFile = File.createTempFile(
            "insect_scan_",
            ".jpg",
            context.externalCacheDir ?: context.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, tempFile)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "viewfinder")
    val viewfinderScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "viewfinder_scale"
    )

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

    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success) {
            val insectId = (uiState as ScanUiState.Success).insectId
            onNavigateToDetail(insectId)
            viewModel.resetState()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = Dimens.dp_16)
            ) {
                Text(
                    text = stringResource(id = R.string.scan_title),
                    color = DarkForestGreenText,
                    fontSize = Dimens.sp_24,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = stringResource(id = R.string.scan_subtitle),
                    color = TextCharcoal,
                    fontSize = Dimens.sp_13,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        top = Dimens.dp_4,
                        start = Dimens.dp_16,
                        end = Dimens.dp_16
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ImagePreviewHeight)
                    .padding(vertical = Dimens.dp_24)
                    .clip(RoundedCornerShape(Dimens.dp_24))
                    .clickable {
                        if (selectedImageUri == null) {
                            val uri = createTempImageUri()
                            tempPhotoUri = uri
                            takePictureLauncher.launch(uri)
                        }
                    },
                shape = RoundedCornerShape(Dimens.dp_24),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.55f)),
                border = BorderStroke(Dimens.dp_1, Color.White.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(Dimens.dp_24)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(Dimens.dp_24)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(160.dp)
                                    .graphicsLayer(scaleX = viewfinderScale, scaleY = viewfinderScale)
                                    .drawBehind {
                                        val strokeWidth = 3.dp.toPx()
                                        val l = 20.dp.toPx()
                                        val color = NatureGreen
                                        
                                        // Top-Left
                                        drawLine(color, Offset(0f, 0f), Offset(l, 0f), strokeWidth)
                                        drawLine(color, Offset(0f, 0f), Offset(0f, l), strokeWidth)
                                        
                                        // Top-Right
                                        drawLine(color, Offset(size.width, 0f), Offset(size.width - l, 0f), strokeWidth)
                                        drawLine(color, Offset(size.width, 0f), Offset(size.width, l), strokeWidth)
                                        
                                        // Bottom-Left
                                        drawLine(color, Offset(0f, size.height), Offset(l, size.height), strokeWidth)
                                        drawLine(color, Offset(0f, size.height), Offset(0f, size.height - l), strokeWidth)
                                        
                                        // Bottom-Right
                                        drawLine(color, Offset(size.width, size.height), Offset(size.width - l, size.height), strokeWidth)
                                        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - l), strokeWidth)
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = DarkForestGreenText,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(Dimens.dp_20))
                            Text(
                                text = "Scan an insect",
                                color = DarkForestGreenText,
                                fontSize = Dimens.sp_16,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Dimens.dp_8))
                            Text(
                                text = "Take a photo or choose one from Gallery",
                                color = TextDarkGrey,
                                fontSize = Dimens.sp_12,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.dp_24),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val uri = createTempImageUri()
                            tempPhotoUri = uri
                            takePictureLauncher.launch(uri)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(Dimens.dp_16),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.dp_56)
                            .padding(end = Dimens.dp_8)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(NatureDarkGreen, NatureLightGreen)
                                ),
                                shape = RoundedCornerShape(Dimens.dp_16)
                            )
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(Dimens.dp_8))
                        Text(
                            text = stringResource(id = R.string.scan_btn_take_photo),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.6f),
                            contentColor = ActiveGreen
                        ),
                        shape = RoundedCornerShape(Dimens.dp_16),
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.dp_56)
                            .padding(start = Dimens.dp_8),
                        border = BorderStroke(Dimens.dp_2, ActiveGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(Dimens.dp_8))
                        Text(
                            text = stringResource(id = R.string.scan_btn_gallery),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.dp_16))

                Button(
                    onClick = { viewModel.identifyInsect(context) },
                    enabled = selectedImageUri != null && uiState !is ScanUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NatureGreen,
                        contentColor = Color.White,
                        disabledContainerColor = Color.White.copy(alpha = 0.3f),
                        disabledContentColor = DarkForestGreenText.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(Dimens.dp_16),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.dp_56)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(Dimens.dp_8))
                    Text(
                        text = if (selectedImageUri != null) {
                            stringResource(id = R.string.scan_btn_identify)
                        } else {
                            "Select a photo first"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.sp_16
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = uiState is ScanUiState.Loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(Dimens.dp_24),
                    border = BorderStroke(Dimens.dp_1, ActiveGreen),
                    modifier = Modifier.padding(Dimens.dp_32)
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.dp_32),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = ActiveGreen,
                            modifier = Modifier.size(Dimens.dp_48)
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_24))
                        Text(
                            text = stringResource(id = R.string.scan_loading_title),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = Dimens.sp_16,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_8))
                        Text(
                            text = stringResource(id = R.string.scan_loading_desc),
                            color = Color.Gray,
                            fontSize = Dimens.sp_12,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (uiState is ScanUiState.Error) {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Red
                    )
                },
                title = { Text(text = stringResource(id = R.string.scan_error_title)) },
                text = {
                    val errorState = uiState as ScanUiState.Error
                    val errorMessage = if (errorState.dynamicArg != null) {
                        stringResource(id = errorState.resId, errorState.dynamicArg)
                    } else {
                        stringResource(id = errorState.resId)
                    }
                    Text(text = errorMessage)
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetState() }) {
                        Text(
                            text = stringResource(id = R.string.scan_btn_close),
                            color = ActiveGreen
                        )
                    }
                },
                containerColor = CardBackground,
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }
}
