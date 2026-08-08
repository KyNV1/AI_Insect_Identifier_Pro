package com.kynv1.aiinsectidentifierpro.ui.screens.premium

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.common.AnalyticsHelper
import com.kynv1.aiinsectidentifierpro.ui.screens.home.HomeViewModel
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.AlertRed
import com.kynv1.aiinsectidentifierpro.ui.theme.ButtonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.GoldAmber
import com.kynv1.aiinsectidentifierpro.ui.theme.GoldYellow
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.PremiumBgLight
import com.kynv1.aiinsectidentifierpro.ui.theme.PremiumBorderLight
import com.kynv1.aiinsectidentifierpro.ui.theme.StarGold
import kotlinx.coroutines.delay

@Composable
fun PaywallScreen(
    homeViewModel: HomeViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableIntStateOf(1) }

    val imageList = listOf(
        R.drawable.img_onboarding_green_beetle,
        R.drawable.img_onboarding_honey_bee,
        R.drawable.img_onboarding_red_beetle,
        R.drawable.img_onboarding_atlas_moth
    )

    val pagerState = rememberPagerState(pageCount = { imageList.size })

    LaunchedEffect(Unit) {
        AnalyticsHelper.logPaywallView()
    }

    LaunchedEffect(key1 = pagerState) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % imageList.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.ImageSizeExtraLarge)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Image(
                            painter = painterResource(id = imageList[page]),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    ViewfinderBrackets(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(Dimens.dp_120)
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(topStart = Dimens.dp_24, topEnd = Dimens.dp_24),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -Dimens.dp_16)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.dp_16)
                            .padding(top = Dimens.dp_14, bottom = Dimens.dp_8),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.paywall_upgrade_premium),
                            color = ActiveGreen,
                            fontSize = Dimens.sp_22,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(Dimens.dp_10))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Dimens.dp_4)
                        ) {
                            BenefitItem(
                                icon = Icons.Default.LockOpen,
                                text = stringResource(id = R.string.paywall_benefit_1)
                            )
                            BenefitItem(
                                icon = Icons.Default.BugReport,
                                text = stringResource(id = R.string.paywall_benefit_2)
                            )
                            BenefitItem(
                                icon = Icons.AutoMirrored.Filled.Chat,
                                text = stringResource(id = R.string.paywall_benefit_3)
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.dp_10))

                        TestimonialView()

                        Spacer(modifier = Modifier.height(Dimens.dp_12))

                        // 3 Thẻ Giá Xếp Nằm Ngang Nối Tiếp Nhau (Side-by-side Horizontal Row)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.dp_8),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            HorizontalPlanCard(
                                title = stringResource(id = R.string.paywall_weekly),
                                price = stringResource(id = R.string.paywall_weekly_price),
                                subtext = stringResource(id = R.string.paywall_weekly_sub),
                                isSelected = selectedOption == 0,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedOption = 0 }
                            )

                            HorizontalPlanCard(
                                title = stringResource(id = R.string.paywall_yearly),
                                price = stringResource(id = R.string.paywall_yearly_price),
                                subtext = stringResource(id = R.string.paywall_yearly_sub),
                                isSelected = selectedOption == 1,
                                isHighlighted = true,
                                topBadgeText = stringResource(id = R.string.paywall_discount_badge),
                                bestValueText = stringResource(id = R.string.paywall_best_value),
                                modifier = Modifier.weight(1.08f),
                                onClick = { selectedOption = 1 }
                            )

                            HorizontalPlanCard(
                                title = stringResource(id = R.string.paywall_monthly),
                                price = stringResource(id = R.string.paywall_monthly_price),
                                subtext = stringResource(id = R.string.paywall_monthly_sub),
                                isSelected = selectedOption == 2,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedOption = 2 }
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.dp_16))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = Dimens.dp_20)
                    .padding(top = Dimens.dp_8, bottom = Dimens.dp_12)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.dp_56)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    ButtonGreen,
                                    NatureGreen
                                )
                            )
                        )
                        .clickable {
                            val planName = when (selectedOption) {
                                0 -> "weekly"
                                1 -> "yearly"
                                else -> "monthly"
                            }
                            AnalyticsHelper.logSubscriptionSuccess(planName)
                            homeViewModel.purchasePremium()
                            Toast.makeText(context, "Premium Active! Thank you!", Toast.LENGTH_LONG).show()
                            onNavigateToHome()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.paywall_continue).uppercase(),
                        color = Color.White,
                        fontSize = Dimens.sp_16,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.dp_8))

                Text(
                    text = stringResource(id = R.string.paywall_disclaimer),
                    color = Color.Gray,
                    fontSize = Dimens.sp_12,
                    lineHeight = Dimens.sp_14
                )

                Spacer(modifier = Modifier.height(Dimens.dp_6))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.paywall_term_of_service),
                        color = Color.Gray,
                        fontSize = Dimens.sp_11,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                //TODO: Open Terms of Service
                            }
                            .padding(horizontal = Dimens.dp_8)
                    )
                    Text(
                        text = "|",
                        color = Color.Gray,
                        fontSize = Dimens.sp_11
                    )
                    Text(
                        text = stringResource(id = R.string.paywall_privacy_policy),
                        color = Color.Gray,
                        fontSize = Dimens.sp_11,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                //TODO: Open Privacy Policy
                            }
                            .padding(horizontal = Dimens.dp_8)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.dp_16)
                .size(Dimens.dp_36)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onNavigateToHome() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Close paywall",
                tint = Color.White,
            )
        }
    }
}

@Composable
fun BenefitItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp_8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.dp_24)
                .background(NatureGreen.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NatureGreen,
                modifier = Modifier.size(Dimens.dp_14)
            )
        }
        Spacer(modifier = Modifier.width(Dimens.dp_12))
        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = Dimens.sp_13,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TestimonialView(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PremiumBgLight),
        shape = RoundedCornerShape(Dimens.dp_16),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_14)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.dp_2)
                ) {
                    repeat(5) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_navigation_start),
                            contentDescription = "Star",
                            tint = StarGold,
                            modifier = Modifier.size(Dimens.dp_14)
                        )
                    }
                }

                Text(
                    text = "5.0 rating",
                    color = Color.Gray,
                    fontSize = Dimens.sp_10,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(Dimens.dp_8))

            Text(
                text = stringResource(id = R.string.paywall_testimonial_quote),
                color = Color.DarkGray,
                fontSize = Dimens.sp_12,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                lineHeight = Dimens.sp_16
            )
        }
    }
}

@Composable
fun HorizontalPlanCard(
    title: String,
    price: String,
    subtext: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    topBadgeText: String? = null,
    bestValueText: String? = null,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        if (isHighlighted) GoldAmber else NatureGreen
    } else {
        PremiumBorderLight
    }
    val borderWidth = if (isSelected) Dimens.dp_2 else Dimens.dp_1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (isHighlighted && topBadgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = Dimens.dp_8, topEnd = Dimens.dp_8))
                    .background(GoldYellow)
                    .fillMaxWidth()
                    .padding(vertical = Dimens.dp_2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = topBadgeText,
                    color = Color.Black,
                    fontSize = Dimens.sp_10,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        } else {
            Spacer(modifier = Modifier.height(Dimens.dp_12))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    if (isHighlighted && topBadgeText != null)
                        RoundedCornerShape(bottomStart = Dimens.dp_12, bottomEnd = Dimens.dp_12)
                    else
                        RoundedCornerShape(Dimens.dp_12)
                )
                .background(Color.White)
                .border(
                    borderWidth,
                    borderColor,
                    if (isHighlighted && topBadgeText != null)
                        RoundedCornerShape(bottomStart = Dimens.dp_12, bottomEnd = Dimens.dp_12)
                    else
                        RoundedCornerShape(Dimens.dp_12)
                )
                .clickable { onClick() }
                .padding(vertical = Dimens.dp_6, horizontal = Dimens.dp_4),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isHighlighted && bestValueText != null) {
                    Box(
                        modifier = Modifier
                            .background(AlertRed, RoundedCornerShape(Dimens.dp_4))
                            .padding(horizontal = Dimens.dp_4, vertical = Dimens.dp_1)
                    ) {
                        Text(
                            text = bestValueText,
                            color = Color.White,
                            fontSize = Dimens.sp_8,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.dp_2))
                }

                Text(
                    text = title,
                    color = Color.DarkGray,
                    fontSize = Dimens.sp_11,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(Dimens.dp_2))
                Text(
                    text = price,
                    color = Color.Black,
                    fontSize = Dimens.sp_13,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Dimens.dp_1))
                Text(
                    text = subtext,
                    color = Color.Gray,
                    fontSize = Dimens.sp_10
                )
            }
        }
    }
}

@Composable
fun ViewfinderBrackets(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = Dimens.dp_3.toPx()
        val cornerLength = Dimens.dp_20.toPx()
        val color = ActiveGreen

        val pathTopLeft = Path().apply {
            moveTo(0f, cornerLength)
            lineTo(0f, 0f)
            lineTo(cornerLength, 0f)
        }
        drawPath(
            path = pathTopLeft,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        val pathTopRight = Path().apply {
            moveTo(size.width - cornerLength, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, cornerLength)
        }
        drawPath(
            path = pathTopRight,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        val pathBottomLeft = Path().apply {
            moveTo(0f, size.height - cornerLength)
            lineTo(0f, size.height)
            lineTo(cornerLength, size.height)
        }
        drawPath(
            path = pathBottomLeft,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        val pathBottomRight = Path().apply {
            moveTo(size.width - cornerLength, size.height)
            lineTo(size.width, size.height)
            lineTo(size.width, size.height - cornerLength)
        }
        drawPath(
            path = pathBottomRight,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
