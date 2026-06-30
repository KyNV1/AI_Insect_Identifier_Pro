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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.kynv1.aiinsectidentifierpro.R
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

    // Xử lý camera
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempPhotoUri?.let { viewModel.onImageSelected(it) }
            }
        }
    )

    // Xử lý gallery
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.onImageSelected(it) }
        }
    )

    // Hàm tạo Uri tạm cho ảnh chụp từ camera
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

    // Lắng nghe sự kiện quét thành công để chuyển sang trang chi tiết
    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success) {
            val insectId = (uiState as ScanUiState.Success).insectId
            onNavigateToDetail(insectId)
            viewModel.resetState()
        }
    }

    // Giao diện chính sử dụng tone màu xanh lá thiên nhiên (Nature Green) phối hợp Dark theme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1E16), // Dark Forest Green
                        Color(0xFF0A0F0D)  // Deep Charcoal Black
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "INSECT IDENTIFIER",
                    color = Color(0xFF4CAF50), // Nature Green
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Chụp hoặc chọn ảnh côn trùng để nhận diện bằng AI",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
                )
            }

            // Image Preview Card hoặc Box placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241C)),
                border = BorderStroke(1.dp, Color(0xFF2E4C3E))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Insect Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Icon",
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Chưa chọn hình ảnh nào",
                                color = Color.LightGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bấm chụp ảnh hoặc chọn ảnh từ máy để bắt đầu.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hàng nút chọn ảnh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Nút chụp ảnh
                    Button(
                        onClick = {
                            val uri = createTempImageUri()
                            tempPhotoUri = uri
                            takePictureLauncher.launch(uri)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(end = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chụp Ảnh", fontWeight = FontWeight.SemiBold)
                    }

                    // Nút chọn từ thư viện
                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(start = 8.dp),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50))
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thư Viện", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút kích hoạt nhận diện AI
                Button(
                    onClick = { viewModel.identifyInsect(context) },
                    enabled = selectedImageUri != null && uiState !is ScanUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFF1E3528),
                        disabledContentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NHẬN DIỆN NGAY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Overlay Loading hoặc Error
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14241C)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "AI đang phân tích ảnh...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mô hình Gemini 1.5 Flash đang nhận dạng côn trùng, vui lòng đợi trong giây lát.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Dialog báo lỗi
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
                title = { Text("Lỗi nhận diện") },
                text = { Text((uiState as ScanUiState.Error).message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetState() }) {
                        Text("Đóng", color = Color(0xFF4CAF50))
                    }
                },
                containerColor = Color(0xFF14241C),
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }
}
