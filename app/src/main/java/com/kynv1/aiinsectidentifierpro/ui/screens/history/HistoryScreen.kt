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
import com.kynv1.aiinsectidentifierpro.ui.theme.DeleteRed
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
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
            .background(LightMilkBackground)
            .padding(Dimens.dp_16)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.dp_16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = ActiveGreen,
                    modifier = Modifier.size(Dimens.dp_28)
                )
                Spacer(modifier = Modifier.width(Dimens.dp_12))
                Text(
                    text = stringResource(id = R.string.history_title),
                    color = Color.Black,
                    fontSize = Dimens.sp_22,
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
                        modifier = Modifier.padding(Dimens.dp_32)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(Dimens.dp_80)
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_16))
                        Text(
                            text = stringResource(id = R.string.history_empty_title),
                            color = Color.Black.copy(alpha = 0.7f),
                            fontSize = Dimens.sp_18,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp_8))
                        Text(
                            text = stringResource(id = R.string.history_empty_desc),
                            color = Color.Gray,
                            fontSize = Dimens.sp_13,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.dp_12),
                    contentPadding = PaddingValues(bottom = Dimens.dp_24)
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
            .clip(RoundedCornerShape(Dimens.dp_16))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.dp_16),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(Dimens.dp_1, LightCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.dp_12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = insect.imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(Dimens.dp_72)
                    .clip(RoundedCornerShape(Dimens.dp_12)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(Dimens.dp_16))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = insect.commonName,
                    color = Color.Black,
                    fontSize = Dimens.sp_16,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = insect.scientificName,
                    color = ActiveGreen,
                    fontSize = Dimens.sp_13,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = Dimens.dp_2)
                )
                Text(
                    text = stringResource(
                        id = R.string.history_item_confidence_format,
                        formattedTime,
                        insect.confidence
                    ),
                    color = Color.Gray,
                    fontSize = Dimens.sp_11,
                    modifier = Modifier.padding(top = Dimens.dp_6)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.history_btn_delete_desc),
                    tint = DeleteRed
                )
            }
        }
    }
}
