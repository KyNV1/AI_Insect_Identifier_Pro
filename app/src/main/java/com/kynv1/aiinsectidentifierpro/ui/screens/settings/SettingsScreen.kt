package com.kynv1.aiinsectidentifierpro.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.screens.home.PremiumBanner
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.StarGold

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRateDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightMilkBackground)
        ) {
            SettingsTopBar(onBack = onBack)
            SettingsContent(
                onNavigateToPaywall = onNavigateToPaywall,
                onRateUsClick = { showRateDialog = true },
                onShareClick = {
                    val appName = context.getString(R.string.app_name)
                    val shareText = "Download $appName app: https://play.google.com/store/apps/details?id=${context.packageName}"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.settings_item_share)))
                },
                onFeedbackClick = {
                    Toast.makeText(context, "Contact Support clicked", Toast.LENGTH_SHORT).show()
                },
                onAgreementClick = {
                    openUrl(context, context.getString(R.string.url_terms))
                },
                onPrivacyClick = {
                    openUrl(context, context.getString(R.string.url_privacy))
                }
            )
        }

        if (showRateDialog) {
            RateAppDialog(
                onDismiss = { showRateDialog = false },
                onRateNow = { rating ->
                    showRateDialog = false
                    if (rating >= 4) openPlayStore(context.packageName) { url ->
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } else {
                        Toast.makeText(context, context.getString(R.string.rate_dialog_toast_feedback), Toast.LENGTH_SHORT).show()
                    }
                }
            )
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

@Composable
private fun SettingsContent(
    onNavigateToPaywall: () -> Unit,
    onRateUsClick: () -> Unit,
    onShareClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onAgreementClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Dimens.dp_24)
    ) {
        PremiumBanner(
            onGetPremiumClick = onNavigateToPaywall,
            modifier = Modifier.padding(top = Dimens.dp_8)
        )
        Spacer(modifier = Modifier.height(Dimens.dp_24))
        SettingsSection(title = stringResource(R.string.settings_section_spread_word)) {
            SettingsRowItem(R.drawable.ic_star, stringResource(R.string.settings_item_rate), onRateUsClick)
            HorizontalDivider(color = LightCardBorder, thickness = Dimens.dp_1)
            SettingsRowItem(R.drawable.ic_share, stringResource(R.string.settings_item_share), onShareClick)
            HorizontalDivider(color = LightCardBorder, thickness = Dimens.dp_1)
            SettingsRowItem(R.drawable.ic_comment, stringResource(R.string.settings_item_feedback), onFeedbackClick)
        }
        Spacer(modifier = Modifier.height(Dimens.dp_16))
        SettingsSection(title = stringResource(R.string.settings_section_legal)) {
            SettingsRowItem(R.drawable.ic_description, stringResource(R.string.settings_item_agreement), onAgreementClick)
            HorizontalDivider(color = LightCardBorder, thickness = Dimens.dp_1)
            SettingsRowItem(R.drawable.ic_security, stringResource(R.string.settings_item_privacy), onPrivacyClick)
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    SettingsSectionHeader(title = title)
    Card(
        shape = RoundedCornerShape(Dimens.dp_16),
        border = BorderStroke(Dimens.dp_1, LightCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_8)
    ) {
        Column { content() }
    }
}

private fun openPlayStore(packageName: String, fallback: (String) -> Unit) {
    val url = "https://play.google.com/store/apps/details?id=$packageName"
    fallback(url)
}

private fun openUrl(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

// ─── Rate App Dialog ──────────────────────────────────────────────────────────

@Composable
fun RateAppDialog(
    onDismiss: () -> Unit,
    onRateNow: (rating: Int) -> Unit
) {
    var selectedStars by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .noRippleClick { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(Dimens.dp_16),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.dp_8),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.dp_20, vertical = Dimens.dp_16)
                    .noRippleClick { }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_20),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rate_dialog_title),
                        fontSize = Dimens.sp_20,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    StarRatingRow(
                        selectedStars = selectedStars,
                        onStarClick = { selectedStars = it }
                    )
                    Text(
                        text = stringResource(R.string.rate_dialog_body),
                        fontSize = Dimens.sp_13,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = Dimens.sp_20
                    )
                    DialogActions(
                        isConfirmEnabled = selectedStars > 0,
                        onDismiss = onDismiss,
                        onConfirm = { onRateNow(selectedStars) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StarRatingRow(selectedStars: Int, onStarClick: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { star ->
            StarItem(
                star = star,
                isSelected = star <= selectedStars,
                onClick = { onStarClick(star) }
            )
        }
    }
}

@Composable
private fun StarItem(star: Int, isSelected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.25f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "star_scale_$star"
    )
    Icon(
        imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
        contentDescription = "$star star",
        tint = StarGold,
        modifier = Modifier
            .size(Dimens.dp_44)
            .scale(scale)
            .noRippleClick { onClick() }
    )
}

@Composable
private fun DialogActions(
    isConfirmEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        DialogButton(
            text = stringResource(R.string.rate_dialog_btn_dismiss).uppercase(),
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold,
            onClick = onDismiss
        )
        Spacer(modifier = Modifier.width(Dimens.dp_4))
        DialogButton(
            text = stringResource(R.string.rate_dialog_btn_confirm).uppercase(),
            color = if (isConfirmEnabled) NatureGreen else Color.LightGray,
            fontWeight = FontWeight.Bold,
            enabled = isConfirmEnabled,
            onClick = onConfirm
        )
    }
}

@Composable
private fun DialogButton(
    text: String,
    color: Color,
    fontWeight: FontWeight,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .noRippleClick(enabled = enabled) { onClick() }
            .padding(horizontal = Dimens.dp_12, vertical = Dimens.dp_10),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = Dimens.sp_13,
            fontWeight = fontWeight,
            letterSpacing = 0.5.sp
        )
    }
}

// ─── Reusable sub-components ──────────────────────────────────────────────────

@Composable
fun SettingsSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = Dimens.sp_14,
        fontWeight = FontWeight.Medium,
        modifier = modifier.padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_4)
    )
}

@Composable
fun SettingsRowItem(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClick { onClick() }
            .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = ActiveGreen,
            modifier = Modifier.size(Dimens.dp_24)
        )
        Spacer(modifier = Modifier.width(Dimens.dp_16))
        Text(
            text = title,
            color = Color.Black,
            fontSize = Dimens.sp_14,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_back_right),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(Dimens.dp_20)
        )
    }
}

private fun Modifier.noRippleClick(enabled: Boolean = true, onClick: () -> Unit) = this.clickable(
    interactionSource = MutableInteractionSource(),
    indication = null,
    enabled = enabled,
    onClick = onClick
)

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    SettingsScreen(onBack = {}, onNavigateToPaywall = {})
}
