package com.kynv1.aiinsectidentifierpro.ui.screens.premium

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.ui.screens.home.HomeViewModel
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.StarGold
import kotlinx.coroutines.delay

@Composable
fun PaywallScreen(
    homeViewModel: HomeViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableIntStateOf(1) } // 0: Weekly, 1: Yearly, 2: Monthly

    val imageList = listOf(
        R.drawable.img_paywall_green_beetle,
        R.drawable.img_paywall_honey_bee,
        R.drawable.img_paywall_red_beetle,
        R.drawable.img_paywall_atlas_moth
    )

    val pagerState = rememberPagerState(pageCount = { imageList.size })

    // Auto-scroll the top images
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
        // Main scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top Section - Insect Image Pager (perfect height to avoid extreme zoom-in)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
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

                // Viewfinder Grid Brackets
                ViewfinderBrackets(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(140.dp)
                )
            }

            // 2. Bottom Section - Premium Subscription Info Card (wrap content size)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = Dimens.dp_32, topEnd = Dimens.dp_32),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp) // Overlap the bottom of the image slightly
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.dp_20)
                        .padding(top = Dimens.dp_24, bottom = Dimens.dp_32),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Title
                    Text(
                        text = stringResource(id = R.string.paywall_upgrade_premium),
                        color = NatureGreen,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(Dimens.dp_16))

                    // Premium Benefits List
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

                    // Testimonial Banner
                    TestimonialView()

                    Spacer(modifier = Modifier.height(Dimens.dp_16))

                    // Subscription Plan Selection Rows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.dp_8)
                    ) {
                        // Weekly Card
                        PlanCard(
                            title = stringResource(id = R.string.paywall_weekly),
                            price = stringResource(id = R.string.paywall_weekly_price),
                            subtext = stringResource(id = R.string.paywall_weekly_sub),
                            isSelected = selectedOption == 0,
                            onClick = { selectedOption = 0 },
                            modifier = Modifier.weight(1f)
                        )

                        // Yearly Card (Highlighted)
                        PlanCard(
                            title = stringResource(id = R.string.paywall_yearly),
                            price = stringResource(id = R.string.paywall_yearly_price),
                            subtext = stringResource(id = R.string.paywall_yearly_sub),
                            isSelected = selectedOption == 1,
                            isHighlighted = true,
                            badgeText = stringResource(id = R.string.paywall_discount_badge),
                            valueText = stringResource(id = R.string.paywall_best_value),
                            onClick = { selectedOption = 1 },
                            modifier = Modifier.weight(1.1f)
                        )

                        // Monthly Card
                        PlanCard(
                            title = stringResource(id = R.string.paywall_monthly),
                            price = stringResource(id = R.string.paywall_monthly_price),
                            subtext = stringResource(id = R.string.paywall_monthly_sub),
                            isSelected = selectedOption == 2,
                            onClick = { selectedOption = 2 },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.dp_20))

                    // CONTINUE Button
                    Button(
                        onClick = {
                            homeViewModel.purchasePremium()
                            Toast.makeText(context, "Premium Actived! Thank you!", Toast.LENGTH_LONG).show()
                            onNavigateToHome()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NatureGreen),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.dp_56)
                    ) {
                        Text(
                            text = stringResource(id = R.string.paywall_continue).uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.dp_12))

                    // Legal Subtext Disclaimer
                    Text(
                        text = stringResource(id = R.string.paywall_disclaimer),
                        color = Color.Gray,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(Dimens.dp_10))

                    // Terms and Privacy policy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.paywall_term_of_service),
                            color = Color.Gray,
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "Terms of Service Clicked", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = Dimens.dp_8)
                        )
                        Text(
                            text = "|",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = stringResource(id = R.string.paywall_privacy_policy),
                            color = Color.Gray,
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = Dimens.dp_8)
                        )
                    }

                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }

        // Close Button (X) stays floating at the Top Right of the screen
        IconButton(
            onClick = { onNavigateToHome() },
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopEnd)
                .padding(Dimens.dp_16)
                .size(Dimens.dp_36)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close paywall",
                tint = Color.White,
                modifier = Modifier.size(Dimens.dp_20)
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
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(Dimens.dp_12))
        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TestimonialView(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
        border = BorderStroke(1.dp, Color(0xFFECECEC)),
        shape = RoundedCornerShape(Dimens.dp_16),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.dp_12),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Star rating row
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.dp_2)
            ) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = StarGold,
                        modifier = Modifier.size(Dimens.dp_16)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.dp_6))

            // Testimonial Text
            Text(
                text = stringResource(id = R.string.paywall_testimonial_quote),
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(Dimens.dp_8))

            // Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.dp_4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = Dimens.dp_12, height = Dimens.dp_4)
                        .background(NatureGreen, RoundedCornerShape(Dimens.dp_2))
                )
                Box(
                    modifier = Modifier
                        .size(Dimens.dp_4)
                        .background(Color.LightGray, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(Dimens.dp_4)
                        .background(Color.LightGray, CircleShape)
                )
            }
        }
    }
}

@Composable
fun PlanCard(
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
    Box(
        modifier = modifier
            .clickable { onClick() }
    ) {
        // Main plan card layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.dp_16))
                .background(
                    if (isSelected) {
                        if (isHighlighted) Color(0xFFFFFDF3) else Color(0xFFF1F8E9)
                    } else {
                        Color.White
                    }
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) {
                        if (isHighlighted) Color(0xFFFFB300) else NatureGreen
                    } else {
                        Color(0xFFE0E0E0)
                    },
                    shape = RoundedCornerShape(Dimens.dp_16)
                )
                .padding(top = 16.dp, bottom = 16.dp)
                .padding(horizontal = Dimens.dp_4),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Plan Title
            Text(
                text = title,
                color = if (isSelected) Color.Black else Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Dimens.dp_8))

            // Plan Price
            Text(
                text = price,
                color = if (isSelected) Color.Black else Color.DarkGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(Dimens.dp_4))

            // Plan Subtext
            Text(
                text = subtext,
                color = Color.Gray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }

        // Overlay the Red "BEST" and Yellow "85% OFF" Badges side-by-side at the top center
        if (isHighlighted && (valueText != null || badgeText != null)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .offset(y = (-10).dp) // Sit exactly hanging over the top edge of the card
                    .align(Alignment.TopCenter)
            ) {
                valueText?.let {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE53935), RoundedCornerShape(Dimens.dp_4))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = it.uppercase(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                badgeText?.let {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFCA28), RoundedCornerShape(Dimens.dp_4))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = it,
                            color = Color.Black,
                            fontSize = 8.sp,
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
        val strokeWidth = 3.dp.toPx()
        val cornerLength = 20.dp.toPx()
        val color = ActiveGreen

        // Top Left Corner
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

        // Top Right Corner
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

        // Bottom Left Corner
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

        // Bottom Right Corner
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
