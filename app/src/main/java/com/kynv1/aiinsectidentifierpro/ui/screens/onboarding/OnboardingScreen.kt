package com.kynv1.aiinsectidentifierpro.ui.screens.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.theme.AccentLime
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.StarGold
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })

    val progress by remember {
        derivedStateOf {
            pagerState.currentPage + pagerState.currentPageOffsetFraction
        }
    }

    val forestAlpha by remember {
        derivedStateOf {
            if (progress <= 1f) {
                1f - progress
            } else {
                0f
            }
        }
    }

    val honeyBeeAlpha by remember {
        derivedStateOf {
            if (progress in 0f..1f) {
                progress
            } else if (progress in 1f..2f) {
                2f - progress
            } else {
                0f
            }
        }
    }

    val redBeetleAlpha by remember {
        derivedStateOf {
            if (progress in 1f..2f) {
                progress - 1f
            } else if (progress in 2f..3f) {
                3f - progress
            } else {
                0f
            }
        }
    }

    val atlasMothAlpha by remember {
        derivedStateOf {
            if (progress >= 2.0f) {
                (progress - 2.0f).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_onboarding_forest),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (progress in 2f..3f) 0f else maxOf(forestAlpha, 0f)
        )

        Image(
            painter = painterResource(id = R.drawable.img_onboarding_honey_bee),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = honeyBeeAlpha
        )

        Image(
            painter = painterResource(id = R.drawable.img_onboarding_red_beetle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = redBeetleAlpha
        )

        Image(
            painter = painterResource(id = R.drawable.img_onboarding_atlas_moth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = atlasMothAlpha
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> OnboardingPage1()
                    1 -> OnboardingPage2()
                    2 -> OnboardingPage3()
                    3 -> OnboardingPage4()
                }
            }

            Row(
                modifier = Modifier.padding(vertical = Dimens.PaddingLarge),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) { index ->
                    val active = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = Dimens.PaddingSmall)
                            .size(if (active) Dimens.IndicatorActiveSize else Dimens.IndicatorInactiveSize)
                            .clip(CircleShape)
                            .background(if (active) NatureGreen else Color.Gray.copy(alpha = 0.5f))
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < 3) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.completeOnboarding()
                        onNavigateToScan()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NatureGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.PaddingDoubleExtraLarge,
                        vertical = Dimens.PaddingExtraLarge
                    )
                    .height(Dimens.ButtonHeight)
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_btn_continue),
                    fontSize = Dimens.TextSizeMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun OnboardingPage1() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.ImageSizeExtraLarge)
                .clip(RoundedCornerShape(Dimens.PaddingExtraLarge))
                .border(Dimens.PaddingMicro, NatureGreen.copy(alpha = 0.6f), RoundedCornerShape(Dimens.PaddingExtraLarge))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_onboarding_green_beetle),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.White.copy(alpha = 0.2f))
            )

            val infiniteTransition = rememberInfiniteTransition(label = "scan")
            val scanPosition by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 260f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scanPosition"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .offset(y = scanPosition.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AccentLime,
                                NatureGreen,
                                AccentLime,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(Dimens.PaddingDoubleExtraLarge))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.MapHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🗺️",
                fontSize = 54.sp,
                modifier = Modifier.blur(1.dp)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

        Text(
            text = stringResource(id = R.string.onboarding_p1_subtitle),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = Dimens.TextSizeLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(id = R.string.onboarding_p1_title),
            color = Color.White,
            fontSize = Dimens.TextSizeTitleHuge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun OnboardingPage2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(Dimens.PaddingDoubleExtraLarge))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(Dimens.CardCornerRadius),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    Dimens.PaddingMicro,
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(Dimens.CardCornerRadius)
                )
        ) {
            Row(
                modifier = Modifier.padding(Dimens.PaddingLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.CardThumbSize)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_onboarding_honey_bee),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.PaddingNormal))
                Column {
                    Text(
                        text = stringResource(id = R.string.onboarding_p2_card_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.TextSizeMedium
                    )
                    Text(
                        text = stringResource(id = R.string.onboarding_p2_card_desc),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = Dimens.TextSizeSmall,
                        maxLines = 2
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = Dimens.PaddingDoubleExtraLarge)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_p2_subtitle),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = Dimens.TextSizeLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            Text(
                text = stringResource(id = R.string.onboarding_p2_title),
                color = Color.White,
                fontSize = Dimens.TextSizeTitleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OnboardingPage3() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(Dimens.PaddingDoubleExtraLarge))

        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            SmoothSineWave(
                modifier = Modifier
                    .width(130.dp)
                    .height(60.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = Dimens.PaddingDoubleExtraLarge)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_p3_subtitle),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = Dimens.TextSizeLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.onboarding_p3_title),
                color = Color.White,
                fontSize = Dimens.TextSizeTitleHuge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingNormal))
            Text(
                text = stringResource(id = R.string.onboarding_p3_desc),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = Dimens.TextSizeNormal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.PaddingLarge)
            )
        }
    }
}

@Composable
fun OnboardingPage4() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(Dimens.PaddingMedium + Dimens.PaddingNormal),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(Dimens.PaddingMedium + Dimens.PaddingNormal)
                )
        ) {
            RatingCardContent()
        }
    }
}

@Composable
private fun RatingCardContent() {
    Column(
        modifier = Modifier.padding(Dimens.PaddingMedium + Dimens.PaddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_p4_card_title),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = Dimens.TextSizeExtraLarge
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        Text(
            text = stringResource(id = R.string.onboarding_p4_card_desc),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = Dimens.TextSizeNormal,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarGold,
                    modifier = Modifier.size(Dimens.StarSize)
                )
            }
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarGold,
                    modifier = Modifier.size(Dimens.StarSize)
                )
                Text(
                    text = "👆",
                    fontSize = Dimens.TextSizeExtraLarge,
                    modifier = Modifier.offset(x = 10.dp, y = 10.dp)
                )
            }
        }
    }
}

@Composable
fun SmoothSineWave(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sineWave")
    val phaseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f
        val amplitude = 12.dp.toPx()
        val frequency = (2f * Math.PI.toFloat()) / width * 1.5f

        val path = Path()
        for (x in 0..width.toInt()) {
            val y = midY + amplitude * kotlin.math.sin(x * frequency + phaseOffset)
            if (x == 0) {
                path.moveTo(x.toFloat(), y)
            } else {
                path.lineTo(x.toFloat(), y)
            }
        }

        drawPath(
            path = path,
            color = AccentLime,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        val path2 = Path()
        for (x in 0..width.toInt()) {
            val y =
                midY + (amplitude * 0.6f) * kotlin.math.sin(x * frequency - phaseOffset + (Math.PI / 2).toFloat())
            if (x == 0) {
                path2.moveTo(x.toFloat(), y)
            } else {
                path2.lineTo(x.toFloat(), y)
            }
        }

        drawPath(
            path = path2,
            color = AccentLime.copy(alpha = 0.4f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
