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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.ChipBackgroundGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.ChipBorderGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.ChipTextGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.WarningOrange
import com.kynv1.aiinsectidentifierpro.utils.Constants

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
        // Custom TopBar Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                            .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_8),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SpecimenImageCard(imageUri = insect.imageUri)
                        TaxonomyDetailsCard(info = info)
                        DescriptionCard(description = info.description)
                        Spacer(modifier = Modifier.height(Dimens.dp_24))
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecimenImageCard(
    imageUri: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.dp_24),
        border = BorderStroke(Dimens.dp_1, LightCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.ImageSizeExtraLarge)
                .clip(RoundedCornerShape(Dimens.dp_24)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun TaxonomyDetailsCard(
    info: InsectInfo,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(Dimens.dp_1, LightCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.dp_20)
        ) {
            Text(
                text = stringResource(id = R.string.detail_taxonomy_header),
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
                    text = stringResource(id = R.string.detail_latin_name),
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

            HorizontalDivider(
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

            HorizontalDivider(
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

            HorizontalDivider(
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
}

@Composable
private fun DescriptionCard(
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(Dimens.dp_1, LightCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
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
                text = description,
                color = Color.DarkGray,
                fontSize = Dimens.sp_14,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun CharacteristicsCard(
    characteristics: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(Dimens.dp_1, LightCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
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
                characteristics.forEach { char ->
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = char,
                                fontSize = Dimens.sp_12,
                                color = ChipTextGreen,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = ChipBackgroundGreen
                        ),
                        border = BorderStroke(Dimens.dp_1, ChipBorderGreen)
                    )
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
