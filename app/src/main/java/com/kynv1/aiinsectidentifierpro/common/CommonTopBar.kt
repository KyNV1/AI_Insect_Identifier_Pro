package com.kynv1.aiinsectidentifierpro.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground

@Composable
fun CommonTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
                painter = painterResource(id = R.drawable.ic_back_left),
                contentDescription = stringResource(id = R.string.detail_btn_back),
                tint = Color.Black
            )
        }
        Text(
            text = title,
            fontSize = Dimens.sp_18,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
