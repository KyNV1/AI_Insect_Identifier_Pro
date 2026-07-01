package com.kynv1.aiinsectidentifierpro.ui.screens.scan

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkForestGreen, DarkBackground)
                )
            )
            .padding(Dimens.PaddingLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = Dimens.PaddingLarge)
            ) {
                Text(
                    text = stringResource(id = R.string.scan_title),
                    color = ActiveGreen,
                    fontSize = Dimens.TextSizeTitleScan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = stringResource(id = R.string.scan_subtitle),
                    color = Color.Gray,
                    fontSize = Dimens.TextSizeSubNormal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        top = Dimens.PaddingSmall,
                        start = Dimens.PaddingLarge,
                        end = Dimens.PaddingLarge
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ImagePreviewHeight)
                    .padding(vertical = Dimens.PaddingExtraLarge),
                shape = RoundedCornerShape(Dimens.PaddingExtraLarge),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
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
                                .clip(RoundedCornerShape(Dimens.PaddingExtraLarge)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(Dimens.PaddingExtraLarge)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = ActiveGreen,
                                modifier = Modifier.size(Dimens.LargeIconSize)
                            )
                            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
                            Text(
                                text = stringResource(id = R.string.scan_empty_title),
                                color = Color.LightGray,
                                fontSize = Dimens.TextSizeMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                            Text(
                                text = stringResource(id = R.string.scan_empty_desc),
                                color = Color.Gray,
                                fontSize = Dimens.TextSizeSmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.PaddingExtraLarge),
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
                            containerColor = ButtonGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(Dimens.PaddingLarge),
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight)
                            .padding(end = Dimens.PaddingMedium)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                        Text(
                            text = stringResource(id = R.string.scan_btn_take_photo),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkButtonGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(Dimens.PaddingLarge),
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight)
                            .padding(start = Dimens.PaddingMedium),
                        border = BorderStroke(1.dp, ActiveGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                        Text(
                            text = stringResource(id = R.string.scan_btn_gallery),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                Button(
                    onClick = { viewModel.identifyInsect(context) },
                    enabled = selectedImageUri != null && uiState !is ScanUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActiveGreen,
                        contentColor = Color.Black,
                        disabledContainerColor = DisabledButtonGreen,
                        disabledContentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(Dimens.PaddingLarge),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.ButtonHeight)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                    Text(
                        text = stringResource(id = R.string.scan_btn_identify),
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.TextSizeMedium
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
                    shape = RoundedCornerShape(Dimens.PaddingExtraLarge),
                    border = BorderStroke(1.dp, ActiveGreen),
                    modifier = Modifier.padding(Dimens.PaddingDoubleExtraLarge)
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.PaddingDoubleExtraLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = ActiveGreen,
                            modifier = Modifier.size(Dimens.LoadingIndicatorSize)
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))
                        Text(
                            text = stringResource(id = R.string.scan_loading_title),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = Dimens.TextSizeMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                        Text(
                            text = stringResource(id = R.string.scan_loading_desc),
                            color = Color.Gray,
                            fontSize = Dimens.TextSizeSmall,
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
