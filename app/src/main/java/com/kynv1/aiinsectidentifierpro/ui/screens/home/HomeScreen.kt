package com.kynv1.aiinsectidentifierpro.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import com.kynv1.aiinsectidentifierpro.data.model.HomeArticle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.data.model.InsectShort
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.AccentLime
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.MediumForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.StarGold

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToSoundScan: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreenContent(
        uiState = uiState,
        onNavigateToScan = onNavigateToScan,
        onNavigateToSoundScan = onNavigateToSoundScan,
        onNavigateToDetail = onNavigateToDetail,
        onGetPremiumClick = { viewModel.purchasePremium() },
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToScan: () -> Unit,
    onNavigateToSoundScan: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onGetPremiumClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        // 1. Header
        HomeHeader(onSettingsClick = {
            Toast.makeText(context, "Settings under development", Toast.LENGTH_SHORT).show()
        })

        // 2. Premium Banner
        if (!uiState.isPremium) {
            PremiumBanner(
                onGetPremiumClick = {
                    onGetPremiumClick()
                    Toast.makeText(context, "Premium Actived! Thank you!", Toast.LENGTH_LONG).show()
                }
            )
        }

        // 3. Identification Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_8),
            horizontalArrangement = Arrangement.spacedBy(Dimens.dp_16)
        ) {
            IdentificationCard(
                title = stringResource(id = R.string.home_photo_id),
                icon = {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = ActiveGreen,
                        modifier = Modifier.size(Dimens.dp_28)
                    )
                },
                onClick = onNavigateToScan,
                modifier = Modifier.weight(1f)
            )

            IdentificationCard(
                title = stringResource(id = R.string.home_sound_id),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Hearing,
                        contentDescription = null,
                        tint = AccentLime,
                        modifier = Modifier.size(Dimens.dp_28)
                    )
                },
                onClick = onNavigateToSoundScan,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.dp_16))

        // 4. Most Common List
        InsectSectionList(
            title = stringResource(id = R.string.home_most_common_title),
            subtitle = stringResource(id = R.string.home_most_common_desc),
            insects = uiState.mostCommonInsects,
            onInsectClick = onNavigateToDetail
        )

        Spacer(modifier = Modifier.height(Dimens.dp_24))

        // 5. Garden Insect List
        InsectSectionList(
            title = stringResource(id = R.string.home_garden_insect_title),
            subtitle = stringResource(id = R.string.home_garden_insect_desc),
            insects = uiState.gardenInsects,
            onInsectClick = onNavigateToDetail
        )

        Spacer(modifier = Modifier.height(Dimens.dp_24))

        // 6. Fun Bug Facts List
        ArticleSectionList(
            title = stringResource(id = R.string.home_fun_facts_title),
            subtitle = stringResource(id = R.string.home_fun_facts_desc),
            articles = uiState.funFactsArticles,
            onArticleClick = onNavigateToDetail
        )

        Spacer(modifier = Modifier.height(Dimens.dp_24))

        // 7. Pest Control List
        ArticleSectionList(
            title = stringResource(id = R.string.home_pest_control_title),
            subtitle = stringResource(id = R.string.home_pest_control_desc),
            articles = uiState.pestControlArticles,
            onArticleClick = onNavigateToDetail
        )

        Spacer(modifier = Modifier.height(Dimens.dp_24))

        // 8. Bug Bite Help List
        ArticleSectionList(
            title = stringResource(id = R.string.home_bug_bite_title),
            subtitle = stringResource(id = R.string.home_bug_bite_desc),
            articles = uiState.bugBiteArticles,
            onArticleClick = onNavigateToDetail
        )

        Spacer(modifier = Modifier.height(Dimens.dp_24))

        // 9. Remarkable Collection List
        ArticleSectionList(
            title = stringResource(id = R.string.home_remarkable_coll_title),
            subtitle = stringResource(id = R.string.home_remarkable_coll_desc),
            articles = uiState.remarkableCollArticles,
            onArticleClick = onNavigateToDetail
        )

        Spacer(modifier = Modifier.height(Dimens.dp_24))
    }
}

@Composable
fun HomeHeader(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Dimens.dp_16,
                end = Dimens.dp_16,
                top = Dimens.dp_24,
                bottom = Dimens.dp_16
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.home_title),
                color = ActiveGreen,
                fontSize = Dimens.sp_28,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.home_subtitle),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = Dimens.sp_14
            )
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .background(MediumForestGreen, shape = CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PremiumBanner(
    onGetPremiumClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.dp_16),
        border = BorderStroke(Dimens.dp_1, Brush.horizontalGradient(listOf(CardBorder, StarGold))),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp_16, vertical = Dimens.dp_8),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CardBackground,
                            MediumForestGreen.copy(alpha = 0.5f),
                            DarkForestGreen
                        )
                    )
                )
                .padding(Dimens.dp_16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.dp_4))
                        Text(
                            text = stringResource(id = R.string.home_premium_title),
                            color = Color.White,
                            fontSize = Dimens.sp_16,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.dp_4))
                    Text(
                        text = stringResource(id = R.string.home_premium_desc),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = Dimens.sp_12
                    )
                }

                Button(
                    onClick = onGetPremiumClick,
                    shape = RoundedCornerShape(Dimens.dp_50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StarGold,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.dp_16,
                        vertical = Dimens.dp_6
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.home_premium_btn),
                        fontSize = Dimens.sp_12,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun IdentificationCard(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.dp_16),
        border = BorderStroke(Dimens.dp_1, CardBorder),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.dp_16),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            icon()
            Text(
                text = title,
                color = Color.White,
                fontSize = Dimens.sp_16,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun InsectSectionList(
    title: String,
    subtitle: String,
    insects: List<InsectShort>,
    onInsectClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = Dimens.dp_16)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = Dimens.sp_18,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = Dimens.sp_12
            )
        }

        Spacer(modifier = Modifier.height(Dimens.dp_16))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.dp_16),
            horizontalArrangement = Arrangement.spacedBy(Dimens.dp_16)
        ) {
            items(insects) { insect ->
                InsectListItem(
                    insect = insect,
                    onClick = { onInsectClick(insect.id) }
                )
            }
        }
    }
}

@Composable
fun InsectListItem(
    insect: InsectShort,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.dp_16),
        border = BorderStroke(Dimens.dp_1, CardBorder),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = insect.imageResId),
                contentDescription = insect.commonName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimens.dp_16.value.dp,
                            topEnd = Dimens.dp_16.value.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.dp_8)
            ) {
                Text(
                    text = insect.commonName,
                    color = Color.White,
                    fontSize = Dimens.sp_14,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = insect.scientificName,
                    color = ActiveGreen,
                    fontSize = Dimens.sp_12,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ArticleSectionList(
    title: String,
    subtitle: String,
    articles: List<HomeArticle>,
    onArticleClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = Dimens.dp_16)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = Dimens.sp_18,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = Dimens.sp_12
            )
        }

        Spacer(modifier = Modifier.height(Dimens.dp_16))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.dp_16),
            horizontalArrangement = Arrangement.spacedBy(Dimens.dp_16)
        ) {
            items(articles) { article ->
                ArticleListItem(
                    article = article,
                    onClick = { onArticleClick(article.id) }
                )
            }
        }
    }
}

@Composable
fun ArticleListItem(
    article: HomeArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.dp_16),
        border = BorderStroke(Dimens.dp_1, CardBorder),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = article.imageResId),
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimens.dp_16.value.dp,
                            topEnd = Dimens.dp_16.value.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.dp_8)
            ) {
                Text(
                    text = article.title,
                    color = Color.White,
                    fontSize = Dimens.sp_14,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreenContent(
        uiState = HomeUiState(),
        onNavigateToScan = {},
        onNavigateToSoundScan = {},
        onNavigateToDetail = {},
        onGetPremiumClick = {}
    )
}
