package com.example.vinnu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel

@Composable
fun LeaderboardScreen(viewModel: LibraryViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            // Template B - Zone 1: Section Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Reading Leaderboard",
                    style = Typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Ranking based on total pages read",
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (leaderboard.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Star, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp),
                            tint = TagBgColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No reading data yet", style = Typography.bodyMedium, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp)
                ) {
                    itemsIndexed(leaderboard) { index, item ->
                        LeaderboardListItem(index = index, name = item.first, pages = item.second)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardListItem(index: Int, name: String, pages: Int) {
    val rankColor = when(index) {
        0 -> Color(0xFFFFD700) // Gold
        1 -> Color(0xFFC0C0C0) // Silver
        2 -> Color(0xFFCD7F32) // Bronze
        else -> AccentColor
    }

    // SOP Card (6.1)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .background(SurfaceColor, RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Circular Rank Index
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (index < 3) rankColor.copy(alpha = 0.2f) else TagBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = Typography.labelSmall,
                    color = if (index < 3) rankColor else TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = Typography.titleMedium,
                    color = TextPrimary
                )
                // Rating / Badge style (Section 4)
                Text(
                    text = "$pages Pages Read",
                    style = Typography.labelSmall,
                    color = PinkColor
                )
            }
            
            if (index < 3) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = rankColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
