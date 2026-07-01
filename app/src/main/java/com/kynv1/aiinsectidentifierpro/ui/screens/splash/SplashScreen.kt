package com.kynv1.aiinsectidentifierpro.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.data.local.OnboardingStore
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen

@Composable
fun SplashScreen(
    onboardingStore: OnboardingStore,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val duration = 2000L
        val steps = 100
        val delayTime = duration / steps
        for (i in 1..steps) {
            kotlinx.coroutines.delay(delayTime)
            progress = i / 100f
        }

        if (onboardingStore.isOnboardingCompleted()) {
            onNavigateToScan()
        } else {
            onNavigateToOnboarding()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                shape = RoundedCornerShape(36.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.size(160.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(180.dp))

            Text(
                text = stringResource(id = R.string.splash_loading_text),
                color = NatureGreen,
                fontSize = Dimens.TextSizeNormal,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingNormal))
            LinearProgressIndicator(
                progress = { progress },
                color = NatureGreen,
                trackColor = NatureGreen.copy(alpha = 0.2f),
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp)
                    .clip(CircleShape)
            )
        }
    }
}
