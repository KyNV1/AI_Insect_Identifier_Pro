package com.kynv1.aiinsectidentifierpro.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    insectId: Long,
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(insectId) {
        viewModel.loadInsect(insectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi Tiết Nhận Diện", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1E16),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F1E16),
                            Color(0xFF0A0F0D)
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                    }
                }
                is DetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is DetailUiState.Success -> {
                    val insect = state.insect
                    val info = insect.toInsectInfo()
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Ảnh côn trùng lớn
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            AsyncImage(
                                model = insect.imageUri,
                                contentDescription = "Captured Insect",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Gradient che mờ chân ảnh
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0xFF0A0F0D).copy(alpha = 0.8f),
                                                Color(0xFF0A0F0D)
                                            ),
                                            startY = 300f
                                        )
                                    )
                            )
                        }

                        // Phần thông tin
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // Tên loài
                            Text(
                                text = info.commonName,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            // Tên khoa học
                            Text(
                                text = info.scientificName,
                                color = Color(0xFF81C784),
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Hàng thông số Độ tin cậy & Mức độ nguy hiểm
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Card độ tin cậy
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14241C)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFF2E4C3E))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Độ tin cậy", color = Color.Gray, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${info.confidence}%",
                                            color = Color(0xFF4CAF50),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Card mức độ nguy hại
                                val (dangerColor, dangerText) = when (info.dangerLevel.lowercase()) {
                                    "high" -> Color.Red to "Nguy hiểm"
                                    "medium" -> Color(0xFFFF9800) to "Cảnh báo"
                                    else -> Color(0xFF4CAF50) to "An toàn"
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14241C)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFF2E4C3E))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Mức độ nguy hại", color = Color.Gray, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = dangerText,
                                            color = dangerColor,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Mô tả chi tiết
                            Text(
                                text = "Mô tả loài",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = info.description,
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Đặc điểm nhận dạng
                            if (info.characteristics.isNotEmpty()) {
                                Text(
                                    text = "Đặc điểm nhận dạng",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    info.characteristics.forEach { char ->
                                        SuggestionChip(
                                            onClick = { },
                                            label = { Text(char, fontSize = 12.sp, color = Color.LightGray) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = Color(0xFF1B3227)
                                            ),
                                            border = BorderStroke(1.dp, Color(0xFF2E4C3E))
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // Môi trường sống
                            Text(
                                text = "Môi trường sống",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = info.habitat,
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Cảnh báo an toàn
                            if (info.dangerDescription.isNotBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF241414)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF5E2020)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = Color.Red,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Lưu ý an toàn",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = info.dangerDescription,
                                                color = Color.LightGray,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
