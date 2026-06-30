package com.kynv1.aiinsectidentifierpro.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsState()

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
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
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.PaddingLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = ActiveGreen,
                    modifier = Modifier.size(Dimens.StarSize)
                )
                Spacer(modifier = Modifier.width(Dimens.PaddingNormal))
                Text(
                    text = stringResource(id = R.string.history_title),
                    color = Color.White,
                    fontSize = Dimens.TextSizeTitleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(Dimens.PaddingDoubleExtraLarge)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = CardBorder,
                            modifier = Modifier.size(Dimens.MapHeight)
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
                        Text(
                            text = stringResource(id = R.string.history_empty_title),
                            color = Color.LightGray,
                            fontSize = Dimens.TextSizeLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                        Text(
                            text = stringResource(id = R.string.history_empty_desc),
                            color = Color.Gray,
                            fontSize = Dimens.TextSizeSubNormal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
                    contentPadding = PaddingValues(bottom = Dimens.PaddingExtraLarge)
                ) {
                    items(historyList, key = { it.id }) { insect ->
                        HistoryItem(
                            insect = insect,
                            formattedTime = formatTimestamp(insect.timestamp),
                            onClick = { onNavigateToDetail(insect.id) },
                            onDelete = { viewModel.deleteInsect(insect.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    insect: InsectEntity,
    formattedTime: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingNormal),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = insect.imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(Dimens.ThumbnailSize)
                    .clip(RoundedCornerShape(Dimens.PaddingNormal)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(Dimens.PaddingLarge))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = insect.commonName,
                    color = Color.White,
                    fontSize = Dimens.TextSizeMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = insect.scientificName,
                    color = ActiveGreen,
                    fontSize = Dimens.TextSizeSubNormal,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = Dimens.PaddingMicro)
                )
                Text(
                    text = stringResource(
                        id = R.string.history_item_confidence_format,
                        formattedTime,
                        insect.confidence
                    ),
                    color = Color.Gray,
                    fontSize = Dimens.TextSizeMicro,
                    modifier = Modifier.padding(top = Dimens.PaddingMediumSmall)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.history_btn_delete_desc),
                    tint = Color(0xFFC62828)
                )
            }
        }
    }
}
