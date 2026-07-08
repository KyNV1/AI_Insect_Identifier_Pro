package com.kynv1.aiinsectidentifierpro.ui.screens.settings

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.screens.home.PremiumBanner
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightCardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightMilkBackground)
    ) {
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_left),
                    contentDescription = stringResource(id = R.string.detail_btn_back),
                    tint = Color.Black
                )
            }
            Text(
                text = stringResource(id = R.string.settings_title),
                fontSize = Dimens.sp_18,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = Dimens.dp_24)
        ) {
            // 1. Premium Upgrade Banner
            PremiumBanner(
                onGetPremiumClick = onNavigateToPaywall,
                modifier = Modifier.padding(top = Dimens.dp_8)
            )

            Spacer(modifier = Modifier.height(Dimens.dp_24))

            // 2. Spread the Word Section
            SettingsSectionHeader(title = stringResource(id = R.string.settings_section_spread_word))
            Card(
                shape = RoundedCornerShape(Dimens.dp_16),
                border = BorderStroke(Dimens.dp_1, LightCardBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_8)
            ) {
                Column {
                    SettingsRowItem(
                        icon = R.drawable.ic_star,
                        title = stringResource(id = R.string.settings_item_rate),
                        onClick = {
                            Toast.makeText(context, "Rate Us clicked", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = LightCardBorder, thickness = Dimens.dp_1)
                    SettingsRowItem(
                        icon = R.drawable.ic_share,
                        title = stringResource(id = R.string.settings_item_share),
                        onClick = {
                            Toast.makeText(context, "Share App clicked", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = LightCardBorder, thickness = Dimens.dp_1)
                    SettingsRowItem(
                        icon = R.drawable.ic_comment,
                        title = stringResource(id = R.string.settings_item_feedback),
                        onClick = {
                            Toast.makeText(context, "Contact Support clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.dp_16))

            // 3. Legal & Policy Section
            SettingsSectionHeader(title = stringResource(id = R.string.settings_section_legal))
            Card(
                shape = RoundedCornerShape(Dimens.dp_16),
                border = BorderStroke(Dimens.dp_1, LightCardBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_8)
            ) {
                Column {
                    SettingsRowItem(
                        icon = R.drawable.ic_description,
                        title = stringResource(id = R.string.settings_item_agreement),
                        onClick = {
                            Toast.makeText(context, "User Agreement clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                    HorizontalDivider(color = LightCardBorder, thickness = Dimens.dp_1)
                    SettingsRowItem(
                        icon = R.drawable.ic_security,
                        title = stringResource(id = R.string.settings_item_privacy),
                        onClick = {
                            Toast.makeText(context, "Privacy Policy clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
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

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    SettingsScreen(
        onBack = {},
        onNavigateToPaywall = {}
    )
}
