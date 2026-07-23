package com.kynv1.aiinsectidentifierpro.ui.screens.premium

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.billing.BillingUiState
import com.kynv1.aiinsectidentifierpro.billing.BillingViewModel
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.ButtonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.StarGold
import kotlinx.coroutines.delay

@Composable
fun PaywallScreen(
    billingViewModel: BillingViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableIntStateOf(1) }

    val productDetailsList by billingViewModel.productDetailsList.collectAsStateWithLifecycle()
    val uiState by billingViewModel.uiState.collectAsStateWithLifecycle()

    val weeklyProduct = productDetailsList.find { it.productId == "weekly_premium" }
    val monthlyProduct = productDetailsList.find { it.productId == "monthly_premium" }
    val yearlyProduct = productDetailsList.find { it.productId == "yearly_premium" }

    val weeklyPrice = weeklyProduct?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
        ?: stringResource(id = R.string.paywall_weekly_price)

    val monthlyPrice = monthlyProduct?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
        ?: stringResource(id = R.string.paywall_monthly_price)

    val yearlyPrice = yearlyProduct?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
        ?: stringResource(id = R.string.paywall_yearly_price)

    LaunchedEffect(uiState) {
        when (uiState) {
            is BillingUiState.Success -> {
                Toast.makeText(context, "Premium active! Thank you!", Toast.LENGTH_LONG).show()
                onNavigateToHome()
            }
            is BillingUiState.Error -> {
                Toast.makeText(context, (uiState as BillingUiState.Error).message, Toast.LENGTH_LONG).show()
                billingViewModel.clearError()
            }
            else -> {}
        }
    }

    val imageList = listOf(
        R.drawable.img_onboarding_green_beetle,
        R.drawable.img_onboarding_honey_bee,
        R.drawable.img_onboarding_red_beetle,
        R.drawable.img_onboarding_atlas_moth
    )

    val pagerState = rememberPagerState(pageCount = { imageList.size })

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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp_280)
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
                        .size(Dimens.dp_140)
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = Dimens.dp_32, topEnd = Dimens.dp_32),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -Dimens.dp_24)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.dp_20)
                        .padding(top = Dimens.dp_24, bottom = Dimens.dp_32),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.paywall_upgrade_premium),
                        color = DarkForestGreen,
                        fontSize = Dimens.sp_22,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(Dimens.dp_16))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.dp_10)
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

                    Spacer(modifier = Modifier.height(Dimens.dp_16))

                    TestimonialView()

                    Spacer(modifier = Modifier.height(Dimens.dp_16))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.dp_10)
                    ) {
                        VerticalPlanCard(
                            title = stringResource(id = R.string.paywall_weekly),
                            price = weeklyPrice,
                            subtext = stringResource(id = R.string.paywall_weekly_sub),
                            isSelected = selectedOption == 0,
                            onClick = { selectedOption = 0 }
                        )

                        VerticalPlanCard(
                            title = stringResource(id = R.string.paywall_yearly),
                            price = yearlyPrice,
                            subtext = stringResource(id = R.string.paywall_yearly_sub),
                            isSelected = selectedOption == 1,
                            isHighlighted = true,
                            badgeText = stringResource(id = R.string.paywall_discount_badge),
                            valueText = stringResource(id = R.string.paywall_best_value),
                            onClick = { selectedOption = 1 }
                        )

                        VerticalPlanCard(
                            title = stringResource(id = R.string.paywall_monthly),
                            price = monthlyPrice,
                            subtext = stringResource(id = R.string.paywall_monthly_sub),
                            isSelected = selectedOption == 2,
                            onClick = { selectedOption = 2 }
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.dp_20))

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
                            .clickable(enabled = uiState !is BillingUiState.Loading) {
                                val activity = context as? Activity
                                if (activity != null) {
                                    val targetProductId = when (selectedOption) {
                                        0 -> "weekly_premium"
                                        2 -> "monthly_premium"
                                        else -> "yearly_premium"
                                    }
                                    billingViewModel.launchPurchaseFlow(activity, targetProductId)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState is BillingUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(Dimens.dp_24))
                        } else {
                            Text(
                                text = stringResource(id = R.string.paywall_continue).uppercase(),
                                color = Color.White,
                                fontSize = Dimens.sp_16,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.dp_12))

                    Text(
                        text = stringResource(id = R.string.paywall_disclaimer),
                        color = Color.Gray,
                        fontSize = Dimens.sp_13,
                        lineHeight = Dimens.sp_14
                    )

                    Spacer(modifier = Modifier.height(Dimens.dp_10))

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
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_terms)))
                                    context.startActivity(intent)
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
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_privacy)))
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = Dimens.dp_8)
                        )
                    }

                    Spacer(modifier = Modifier.navigationBarsPadding())
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAF7)),
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
fun VerticalPlanCard(
    title: String,
    price: String,
    subtext: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    badgeText: String? = null,
    valueText: String? = null,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        if (isHighlighted) Color(0xFFFFB300) else NatureGreen
    } else {
        Color(0xFFE5EBE6)
    }

    val borderWidth = if (isSelected) Dimens.dp_2 else Dimens.dp_1
    val backgroundColor = if (isSelected) {
        if (isHighlighted) Color(0xFFFFFDF3) else Color(0xFFF1F8E9)
    } else {
        Color.White
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.dp_16))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(Dimens.dp_16))
            .clickable { onClick() }
            .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_14)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.dp_6)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        fontSize = Dimens.sp_14,
                        fontWeight = FontWeight.Bold
                    )

                    if (isHighlighted && valueText != null) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE53935), RoundedCornerShape(Dimens.dp_4))
                                .padding(horizontal = Dimens.dp_6, vertical = Dimens.dp_2)
                        ) {
                            Text(
                                text = valueText.uppercase(),
                                color = Color.White,
                                fontSize = Dimens.sp_8,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.dp_4))

                Text(
                    text = subtext,
                    color = Color.Gray,
                    fontSize = Dimens.sp_11
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = price,
                    color = Color.Black,
                    fontSize = Dimens.sp_16,
                    fontWeight = FontWeight.Black
                )

                if (isHighlighted && badgeText != null) {
                    Spacer(modifier = Modifier.height(Dimens.dp_2))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFCA28), RoundedCornerShape(Dimens.dp_4))
                            .padding(horizontal = Dimens.dp_6, vertical = Dimens.dp_2)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.Black,
                            fontSize = Dimens.sp_8,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
