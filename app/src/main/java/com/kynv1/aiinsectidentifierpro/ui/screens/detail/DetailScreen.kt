package com.kynv1.aiinsectidentifierpro.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.utils.Constants
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.MediumForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.WarningOrange
import com.kynv1.aiinsectidentifierpro.ui.theme.DangerRedBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DangerRedBorder

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
                title = {
                    Text(
                        text = stringResource(id = R.string.detail_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.sp_20
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.detail_btn_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkForestGreen,
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
                        colors = listOf(DarkForestGreen, DarkBackground)
                    )
                )
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ActiveGreen)
                    }
                }

                is DetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.dp_24),
                        contentAlignment = Alignment.Center
                    ) {
                        val errorMessage = if (state.dynamicArg != null) {
                            stringResource(id = state.resId, state.dynamicArg)
                        } else {
                            stringResource(id = state.resId)
                        }
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = Dimens.sp_16,
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.ImageSizeExtraLarge)
                        ) {
                            AsyncImage(
                                model = insect.imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                DarkBackground.copy(alpha = 0.8f),
                                                DarkBackground
                                            ),
                                            startY = 300f
                                        )
                                    )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Dimens.dp_16,
                                    vertical = Dimens.dp_8
                                )
                        ) {
                            Text(
                                text = info.commonName,
                                color = Color.White,
                                fontSize = Dimens.sp_28,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = info.scientificName,
                                color = ActiveGreen,
                                fontSize = Dimens.sp_18,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(top = Dimens.dp_4)
                            )

                            Spacer(modifier = Modifier.height(Dimens.dp_16))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Dimens.dp_12)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                                    shape = RoundedCornerShape(Dimens.dp_16),
                                    border = BorderStroke(Dimens.dp_1, CardBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Dimens.dp_16),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.detail_confidence),
                                            color = Color.Gray,
                                            fontSize = Dimens.sp_12
                                        )
                                        Spacer(modifier = Modifier.height(Dimens.dp_4))
                                        Text(
                                            text = "${info.confidence}%",
                                            color = ActiveGreen,
                                            fontSize = Dimens.sp_20,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                val (dangerColor, dangerText) = when (info.dangerLevel.lowercase()) {
                                    Constants.DANGER_LEVEL_HIGH -> Color.Red to stringResource(id = R.string.detail_danger_high)
                                    Constants.DANGER_LEVEL_MEDIUM -> WarningOrange to stringResource(id = R.string.detail_danger_medium)
                                    else -> ActiveGreen to stringResource(id = R.string.detail_danger_low)
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                                    shape = RoundedCornerShape(Dimens.dp_16),
                                    border = BorderStroke(Dimens.dp_1, CardBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Dimens.dp_16),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.detail_danger_level),
                                            color = Color.Gray,
                                            fontSize = Dimens.sp_12
                                        )
                                        Spacer(modifier = Modifier.height(Dimens.dp_4))
                                        Text(
                                            text = dangerText,
                                            color = dangerColor,
                                            fontSize = Dimens.sp_20,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(Dimens.dp_24))

                            Text(
                                text = stringResource(id = R.string.detail_description_header),
                                color = Color.White,
                                fontSize = Dimens.sp_18,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = info.description,
                                color = Color.LightGray,
                                fontSize = Dimens.sp_14,
                                lineHeight = Dimens.sp_20,
                                modifier = Modifier.padding(top = Dimens.dp_8)
                            )

                            Spacer(modifier = Modifier.height(Dimens.dp_24))

                            if (info.characteristics.isNotEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.detail_characteristics_header),
                                    color = Color.White,
                                    fontSize = Dimens.sp_18,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(Dimens.dp_8))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.dp_8),
                                    verticalArrangement = Arrangement.spacedBy(Dimens.dp_8)
                                ) {
                                    info.characteristics.forEach { char ->
                                        SuggestionChip(
                                            onClick = { },
                                            label = {
                                                Text(
                                                    text = char,
                                                    fontSize = Dimens.sp_12,
                                                    color = Color.LightGray
                                                )
                                            },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MediumForestGreen
                                            ),
                                            border = BorderStroke(Dimens.dp_1, CardBorder)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(Dimens.dp_24))
                            }

                            Text(
                                text = stringResource(id = R.string.detail_habitat_header),
                                color = Color.White,
                                fontSize = Dimens.sp_18,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = info.habitat,
                                color = Color.LightGray,
                                fontSize = Dimens.sp_14,
                                modifier = Modifier.padding(top = Dimens.dp_8)
                            )

                            Spacer(modifier = Modifier.height(Dimens.dp_24))

                            if (info.dangerDescription.isNotBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = DangerRedBackground
                                    ),
                                    shape = RoundedCornerShape(Dimens.dp_12),
                                    border = BorderStroke(Dimens.dp_1, DangerRedBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = Dimens.dp_24)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(Dimens.dp_16),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color.Red,
                                            modifier = Modifier.size(Dimens.dp_24)
                                        )
                                        Spacer(modifier = Modifier.width(Dimens.dp_16))
                                        Column {
                                            Text(
                                                text = stringResource(id = R.string.detail_safety_warning_header),
                                                color = Color.White,
                                                fontSize = Dimens.sp_14,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = info.dangerDescription,
                                                color = Color.LightGray,
                                                fontSize = Dimens.sp_12,
                                                modifier = Modifier.padding(top = Dimens.dp_4)
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
