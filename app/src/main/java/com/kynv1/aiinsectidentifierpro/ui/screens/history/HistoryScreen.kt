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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.DeleteRed
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.TextCharcoal
import com.kynv1.aiinsectidentifierpro.ui.theme.TextMediumGrey
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

    // Non-null while the confirm dialog is open; also carries which row is being deleted.
    var pendingDelete by remember { mutableStateOf<InsectEntity?>(null) }

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
            modifier = Modifier.fillMaxSize()
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
                    color = TextCharcoal,
                    fontSize = Dimens.sp_18,
                    fontWeight = FontWeight.Bold
                )
            }

            val insects = historyList
            when {
                // Still loading: render nothing rather than the empty state, which would
                // otherwise flash for users who do have a collection.
                insects == null -> Spacer(modifier = Modifier.weight(1f))

                insects.isEmpty() -> EmptyCollection(
                    onNavigateToScan = onNavigateToScan,
                    modifier = Modifier.weight(1f)
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.dp_16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.dp_12),
                    contentPadding = PaddingValues(bottom = Dimens.dp_24)
                ) {
                    items(insects, key = { it.id }) { insect ->
                        HistoryItem(
                            insect = insect,
                            formattedTime = formatTimestamp(insect.timestamp),
                            onClick = { onNavigateToDetail(insect.id) },
                            onDelete = { pendingDelete = insect }
                        )
                    }
                }
            }
        }

        // Cute bee mascot in a floating action button (FAB) at bottom-right corner
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimens.dp_16, bottom = Dimens.dp_40)
                .shadow(elevation = Dimens.dp_6, shape = RoundedCornerShape(Dimens.dp_24))
                .background(Color.White)
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(id = R.string.collection_ask_ai_desc),
                    onClick = onNavigateToAssistance
                )
                .heightIn(min = Dimens.dp_48)
                .padding(horizontal = Dimens.dp_14, vertical = Dimens.dp_8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_assistance_bee),
                contentDescription = null,
                modifier = Modifier.size(Dimens.dp_28)
            )
            Spacer(modifier = Modifier.width(Dimens.dp_6))
            Text(
                text = stringResource(id = R.string.collection_ask_ai),
                color = ActiveGreen,
                fontSize = Dimens.sp_13,
                fontWeight = FontWeight.Bold
            )
        }

    }

    // Colours are passed explicitly throughout: MaterialTheme.colorScheme is still the
    // purple Android Studio template, so anything relying on defaults renders purple.
    pendingDelete?.let { insect ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = Color.White,
            titleContentColor = TextCharcoal,
            textContentColor = TextMediumGrey,
            title = {
                Text(
                    text = stringResource(id = R.string.collection_delete_title),
                    fontSize = Dimens.sp_18,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.collection_delete_message,
                        insect.commonName
                    ),
                    fontSize = Dimens.sp_14
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteInsect(insect.id)
                        pendingDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = DeleteRed)
                ) {
                    Text(
                        text = stringResource(id = R.string.collection_delete_confirm),
                        fontSize = Dimens.sp_14,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingDelete = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextMediumGrey)
                ) {
                    Text(
                        text = stringResource(id = R.string.collection_delete_cancel),
                        fontSize = Dimens.sp_14
                    )
                }
            }
        )
    }
}

@Composable
private fun EmptyCollection(
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        // Centred rather than pushed down by a fixed spacer, so the block stays
        // balanced on any screen height.
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_basic_red_ladybug),
            contentDescription = null,
            modifier = Modifier
                .size(Dimens.EmptyStateImageSize)
                .clip(RoundedCornerShape(Dimens.dp_16)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(Dimens.dp_24))
        Text(
            text = stringResource(id = R.string.collection_empty_title),
            color = TextCharcoal,
            fontSize = Dimens.sp_20,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.dp_8))
        Text(
            text = stringResource(id = R.string.collection_empty_desc),
            color = TextMediumGrey,
            fontSize = Dimens.sp_14,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.dp_32)
        )
        Spacer(modifier = Modifier.height(Dimens.dp_28))

        Button(
            onClick = onNavigateToScan,
            colors = ButtonDefaults.buttonColors(
                containerColor = ActiveGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(Dimens.dp_28),
            contentPadding = PaddingValues(horizontal = Dimens.dp_36),
            modifier = Modifier.height(Dimens.dp_56)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.dp_20)
                )
                Spacer(modifier = Modifier.width(Dimens.dp_8))
                Text(
                    text = stringResource(id = R.string.collection_btn_add),
                    fontWeight = FontWeight.Bold,
                    fontSize = Dimens.sp_14
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
                    color = TextCharcoal,
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
                    color = TextMediumGrey,
                    fontSize = Dimens.sp_12,
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
