package com.kynv1.aiinsectidentifierpro.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.WarningOrange
import com.kynv1.aiinsectidentifierpro.ui.theme.DangerRedBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DangerRedBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightMilkBackground)
    ) {
        // Custom TopBar Box to prevent status bar padding calculation issues
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(Dimens.dp_56)
                .background(LightMilkBackground),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = Dimens.dp_8)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.detail_btn_back),
                    tint = Color.Black
                )
            }
            Text(
                text = stringResource(id = R.string.detail_title),
                fontSize = Dimens.sp_18,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightMilkBackground)
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
                            .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_8)
                    ) {
                        // 1. Specimen Display Card (Hero Card)
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(Dimens.dp_1, LightCardBorder),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.dp_8)
                        ) {
                            AsyncImage(
                                model = insect.imageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimens.ImageSizeExtraLarge)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }



                        // 2. Info & Taxonomy Details Card (Specs card)
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(Dimens.dp_1, LightCardBorder),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.dp_8)
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimens.dp_20)
                            ) {
                                Text(
                                    text = "CHI TIẾT PHÂN LOẠI",
                                    color = Color.Gray,
                                    fontSize = Dimens.sp_12,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = Dimens.dp_16)
                                )

                                // Row 1: Latin Name
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Dimens.dp_8),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tên Latin",
                                        color = Color.Gray,
                                        fontSize = Dimens.sp_14,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = info.scientificName,
                                        color = Color.Black,
                                        fontSize = Dimens.sp_14,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                androidx.compose.material3.HorizontalDivider(
                                    color = LightCardBorder,
                                    thickness = 1.dp
                                )

                                // Row 2: Độ tin cậy (Confidence)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Dimens.dp_8),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.detail_confidence),
                                        color = Color.Gray,
                                        fontSize = Dimens.sp_14,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(ActiveGreen, CircleShape)
                                        )
                                        Text(
                                            text = "${info.confidence}%",
                                            color = Color.Black,
                                            fontSize = Dimens.sp_14,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                androidx.compose.material3.HorizontalDivider(
                                    color = LightCardBorder,
                                    thickness = 1.dp
                                )

                                // Row 3: Mức độ nguy hại (Danger level)
                                val (dangerColor, dangerText) = when (info.dangerLevel.lowercase()) {
                                    Constants.DANGER_LEVEL_HIGH -> Color.Red to stringResource(id = R.string.detail_danger_high)
                                    Constants.DANGER_LEVEL_MEDIUM -> WarningOrange to stringResource(id = R.string.detail_danger_medium)
                                    else -> ActiveGreen to stringResource(id = R.string.detail_danger_low)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Dimens.dp_8),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.detail_danger_level),
                                        color = Color.Gray,
                                        fontSize = Dimens.sp_14,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = dangerText,
                                        color = dangerColor,
                                        fontSize = Dimens.sp_14,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                androidx.compose.material3.HorizontalDivider(
                                    color = LightCardBorder,
                                    thickness = 1.dp
                                )

                                // Row 4: Môi trường sống (Habitat)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Dimens.dp_8)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.detail_habitat_header),
                                        color = Color.Gray,
                                        fontSize = Dimens.sp_14,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = info.habitat,
                                        color = Color.Black,
                                        fontSize = Dimens.sp_14
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimens.dp_12))

                        // 3. Description Card
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(Dimens.dp_1, LightCardBorder),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.dp_8)
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimens.dp_20)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.detail_description_header),
                                    color = Color.Black,
                                    fontSize = Dimens.sp_18,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = Dimens.dp_8)
                                )
                                Text(
                                    text = info.description,
                                    color = Color.DarkGray,
                                    fontSize = Dimens.sp_14,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        // 4. Characteristics Tags (Chips)
                        if (info.characteristics.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(Dimens.dp_12))
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(Dimens.dp_1, LightCardBorder),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dimens.dp_8)
                            ) {
                                Column(
                                    modifier = Modifier.padding(Dimens.dp_20)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.detail_characteristics_header),
                                        color = Color.Black,
                                        fontSize = Dimens.sp_18,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = Dimens.dp_12)
                                    )
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
                                                        color = Color(0xFF3C6A00),
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                },
                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = Color(0xFFE8F5E9)
                                                ),
                                                border = BorderStroke(Dimens.dp_1, Color(0xFFC2C9B5))
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Safety Warning Card (Danger Warning)
                        if (info.dangerDescription.isNotBlank()) {
                            Spacer(modifier = Modifier.height(Dimens.dp_12))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = DangerRedBackground
                                ),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(Dimens.dp_1, DangerRedBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dimens.dp_8)
                            ) {
                                Row(
                                    modifier = Modifier.padding(Dimens.dp_20),
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
                                            color = Color.Red,
                                            fontSize = Dimens.sp_14,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = info.dangerDescription,
                                            color = Color.DarkGray,
                                            fontSize = Dimens.sp_12,
                                            modifier = Modifier.padding(top = Dimens.dp_4)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Dimens.dp_24))
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
