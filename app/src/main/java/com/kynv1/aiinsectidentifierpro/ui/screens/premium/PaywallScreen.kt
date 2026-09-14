package com.kynv1.aiinsectidentifierpro.ui.screens.premium

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.common.AnalyticsHelper
import com.kynv1.aiinsectidentifierpro.common.BillingManager
import com.kynv1.aiinsectidentifierpro.common.PurchaseResult
import com.kynv1.aiinsectidentifierpro.common.openUrl
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.ButtonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.GoldAmber
import com.kynv1.aiinsectidentifierpro.ui.theme.NatureGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.PremiumBgLight
import com.kynv1.aiinsectidentifierpro.ui.theme.PremiumBorderLight
import com.kynv1.aiinsectidentifierpro.ui.theme.TextCharcoal
import com.kynv1.aiinsectidentifierpro.ui.theme.TextMediumGrey
import kotlinx.coroutines.delay

/**
 * Subscription tiers. Carries its own analytics name so reordering the cards on screen
 * can never desync the reported plan from the selected one, plus the Play Console
 * product ID used to look up real pricing and launch the purchase flow.
 */
enum class PlanType(val analyticsName: String, val productId: String) {
    WEEKLY("weekly", BillingManager.PRODUCT_WEEKLY),
    MONTHLY("monthly", BillingManager.PRODUCT_MONTHLY),
    YEARLY("yearly", BillingManager.PRODUCT_YEARLY)
}

/** Google's real, region-priced string once loaded; the static fallback while it's still loading. */
private fun priceLabel(
    productDetails: Map<String, ProductDetails>,
    plan: PlanType,
    fallback: String
): String {
    return productDetails[plan.productId]
        ?.subscriptionOfferDetails
        ?.firstOrNull()
        ?.pricingPhases
        ?.pricingPhaseList
        ?.firstOrNull()
        ?.formattedPrice
        ?: fallback
}

@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf(PlanType.YEARLY) }
    val productDetails by viewModel.productDetails.collectAsState()
    val purchaseResult by viewModel.purchaseResult.collectAsState()

    LaunchedEffect(purchaseResult) {
        when (val result = purchaseResult) {
            is PurchaseResult.Success -> {
                if (!result.isRestore) {
                    AnalyticsHelper.logSubscriptionSuccess(selectedPlan.analyticsName)
                }
                Toast.makeText(
                    context,
                    context.getString(R.string.premium_activated_message),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.consumePurchaseResult()
                onNavigateToHome()
            }

            is PurchaseResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                viewModel.consumePurchaseResult()
            }

            PurchaseResult.Cancelled -> viewModel.consumePurchaseResult()
            PurchaseResult.Idle -> Unit
        }
    }

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

    // WCAG 2.2.2: auto-moving content must be stoppable. The first touch on the carousel
    // hands control to the user permanently.
    var autoAdvanceEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = pagerState, key2 = autoAdvanceEnabled) {
        if (!autoAdvanceEnabled) return@LaunchedEffect
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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.PaywallHeroHeight)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        // Initial pass sees the touch before the pager
                                        // consumes it for its own drag handling.
                                        awaitFirstDown(pass = PointerEventPass.Initial)
                                        autoAdvanceEnabled = false
                                    }
                                }
                        ) { page ->
                            Image(
                                painter = painterResource(id = imageList[page]),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
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

                            GeminiTrustBadge()

                            Spacer(modifier = Modifier.height(Dimens.dp_12))

                            // Plan cards stacked vertically, ordered by ascending duration.
                            // The Yearly plan stands out via badge + border, not position.
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectableGroup(),
                                verticalArrangement = Arrangement.spacedBy(Dimens.dp_8)
                            ) {
                                PlanRowCard(
                                    title = stringResource(id = R.string.paywall_weekly),
                                    price = priceLabel(
                                        productDetails,
                                        PlanType.WEEKLY,
                                        stringResource(id = R.string.paywall_weekly_price)
                                    ),
                                    perWeek = stringResource(id = R.string.paywall_weekly_sub),
                                    isSelected = selectedPlan == PlanType.WEEKLY,
                                    onClick = { selectedPlan = PlanType.WEEKLY }
                                )

                                PlanRowCard(
                                    title = stringResource(id = R.string.paywall_monthly),
                                    price = priceLabel(
                                        productDetails,
                                        PlanType.MONTHLY,
                                        stringResource(id = R.string.paywall_monthly_price)
                                    ),
                                    perWeek = stringResource(id = R.string.paywall_monthly_sub),
                                    isSelected = selectedPlan == PlanType.MONTHLY,
                                    onClick = { selectedPlan = PlanType.MONTHLY }
                                )

                                PlanRowCard(
                                    title = stringResource(id = R.string.paywall_yearly),
                                    price = priceLabel(
                                        productDetails,
                                        PlanType.YEARLY,
                                        stringResource(id = R.string.paywall_yearly_price)
                                    ),
                                    perWeek = stringResource(id = R.string.paywall_yearly_sub),
                                    isSelected = selectedPlan == PlanType.YEARLY,
                                    badgeText = stringResource(id = R.string.paywall_discount_badge),
                                    onClick = { selectedPlan = PlanType.YEARLY }
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimens.dp_16))

                            // Full billing terms + policy links live in the scroll area, not pinned.
                            // Pinning them cost ~150dp of fixed height and pushed the plan cards
                            // off screen. The one-line summary above the CTA carries the gist.
                            Text(
                                text = stringResource(id = R.string.paywall_disclaimer),
                                color = TextMediumGrey,
                                fontSize = Dimens.sp_12,
                                lineHeight = Dimens.sp_16
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                LegalLinkText(
                                    text = stringResource(id = R.string.paywall_term_of_service),
                                    url = stringResource(id = R.string.url_terms),
                                    context = context
                                )
                                Text(
                                    text = "|",
                                    color = TextMediumGrey,
                                    fontSize = Dimens.sp_12
                                )
                                LegalLinkText(
                                    text = stringResource(id = R.string.paywall_privacy_policy),
                                    url = stringResource(id = R.string.url_privacy),
                                    context = context
                                )
                            }
                        }
                    }
                }

                // Fades the scrolling text into the CTA block instead of letting the
                // pinned bar chop it off mid-sentence.
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(Dimens.dp_24)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.White)
                            )
                        )
                )
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
                // Material3 Button for ripple + role=Button semantics. The container is transparent
                // so the gradient painted underneath shows through while the ripple stays on top.
                Button(
                    onClick = {
                        val activity = context as? Activity
                        if (activity != null) {
                            viewModel.launchPurchase(activity, selectedPlan)
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.dp_56)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    ButtonGreen,
                                    NatureGreen
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.paywall_continue).uppercase(),
                        color = Color.White,
                        fontSize = Dimens.sp_16,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.dp_6))

                // Short billing summary stays pinned with the CTA so the key terms are visible
                // at the moment of purchase; the full text sits above in the scroll area.
                Text(
                    text = stringResource(id = R.string.paywall_billing_summary),
                    color = TextMediumGrey,
                    fontSize = Dimens.sp_12,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.dp_16)
                .size(Dimens.dp_48)
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

        TextButton(
            onClick = { viewModel.restorePurchases() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Dimens.dp_8)
        ) {
            Text(
                text = stringResource(id = R.string.paywall_restore_purchases),
                color = Color.White,
                fontSize = Dimens.sp_12,
                fontWeight = FontWeight.SemiBold
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
            color = TextCharcoal,
            fontSize = Dimens.sp_13,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LegalLinkText(text: String, url: String, context: Context) {
    Text(
        text = text,
        color = TextMediumGrey,
        fontSize = Dimens.sp_12,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable { openUrl(context, url) }
            .padding(horizontal = Dimens.dp_8, vertical = Dimens.dp_16)
    )
}

/**
 * Trust block. Carries only verifiable claims about the technology in use —
 * never testimonials or ratings the app cannot substantiate.
 */
@Composable
fun GeminiTrustBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.dp_16))
            .background(PremiumBgLight)
            .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_14)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.dp_36)
                .background(ActiveGreen.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ActiveGreen,
                modifier = Modifier.size(Dimens.dp_20)
            )
        }

        Spacer(modifier = Modifier.width(Dimens.dp_12))

        Column {
            Text(
                text = stringResource(id = R.string.paywall_trust_title),
                color = TextCharcoal,
                fontSize = Dimens.sp_13,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Dimens.dp_2))
            Text(
                text = stringResource(id = R.string.paywall_trust_subtitle),
                color = TextMediumGrey,
                fontSize = Dimens.sp_12,
                lineHeight = Dimens.sp_16
            )
        }
    }
}

/**
 * One subscription option as a full-width row: radio · name + per-week price · price · badge.
 *
 * Stacked vertically rather than side-by-side so every label clears the 12sp floor and the
 * whole row clears the 48dp touch minimum — three cards abreast cannot do either on a phone.
 */
@Composable
fun PlanRowCard(
    title: String,
    price: String,
    perWeek: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(Dimens.dp_12)
    val borderColor = if (isSelected) ActiveGreen else PremiumBorderLight
    val borderWidth = if (isSelected) Dimens.dp_2 else Dimens.dp_1
    val containerColor = if (isSelected) PremiumBgLight else Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.dp_56)
            .clip(shape)
            .background(containerColor)
            .border(borderWidth, borderColor, shape)
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = Dimens.dp_12, vertical = Dimens.dp_10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // onClick = null: the whole row already handles selection above.
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = ActiveGreen,
                unselectedColor = TextMediumGrey
            )
        )

        Spacer(modifier = Modifier.width(Dimens.dp_8))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextCharcoal,
                fontSize = Dimens.sp_14,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = perWeek,
                color = TextMediumGrey,
                fontSize = Dimens.sp_12
            )
        }

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .background(GoldAmber, RoundedCornerShape(Dimens.dp_8))
                    .padding(horizontal = Dimens.dp_8, vertical = Dimens.dp_4)
            ) {
                Text(
                    text = badgeText,
                    color = TextCharcoal,
                    fontSize = Dimens.sp_12,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(Dimens.dp_8))
        }

        Text(
            text = price,
            color = TextCharcoal,
            fontSize = Dimens.sp_16,
            fontWeight = FontWeight.Bold
        )
    }
}
