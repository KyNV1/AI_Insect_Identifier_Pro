package com.kynv1.aiinsectidentifierpro.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.kynv1.aiinsectidentifierpro.common.noRippleClick
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.DeleteRed
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToAssistance: () -> Unit,
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Centered top title (like action bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp_56),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.collection_title),
                    color = Color.Black,
                    fontSize = Dimens.sp_18,
                    fontWeight = FontWeight.Bold
                )
            }

            if (historyList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Image(
                        painter = painterResource(id = R.drawable.img_basic_red_ladybug),
                        contentDescription = null,
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(Dimens.dp_16)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(id = R.string.collection_empty_title),
                        color = Color.Black,
                        fontSize = Dimens.sp_20,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.collection_empty_desc),
                        color = Color.Gray,
                        fontSize = Dimens.sp_14,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = Dimens.dp_32)
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    Button(
                        onClick = onNavigateToScan,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ActiveGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(Dimens.dp_28),
                        contentPadding = PaddingValues(horizontal = 36.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.collection_btn_add),
                                fontWeight = FontWeight.Bold,
                                fontSize = Dimens.sp_14
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = Dimens.dp_16),
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

        // Cute bee mascot in a floating action button (FAB) at bottom-right corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimens.dp_16, bottom = 40.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp))
                .background(Color.White)
                .clickable { onNavigateToAssistance() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_assistance_bee),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ask AI",
                    color = ActiveGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
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

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.dp_56)
            .background(LightMilkBackground),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = Dimens.dp_8)
                .size(Dimens.dp_48)
                .noRippleClick { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back_left),
                contentDescription = stringResource(R.string.detail_btn_back),
                tint = Color.Black
            )
        }
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = Dimens.sp_18,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}