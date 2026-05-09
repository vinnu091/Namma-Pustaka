package com.example.vinnu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import com.example.vinnu.data.TransactionEntity
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: LibraryViewModel) {
    val transactions by viewModel.allTransactions.collectAsState()
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

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
                    text = "Lending History",
                    style = Typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Track all book borrows and returns",
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
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No transactions recorded yet.", 
                        style = Typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(transactions) { tx ->
                        TransactionListItem(tx = tx, sdf = sdf)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(tx: TransactionEntity, sdf: SimpleDateFormat) {
    val isOverdue = !tx.returned && (System.currentTimeMillis() - tx.borrowDate > 7L * 24 * 60 * 60 * 1000)
    
    // SOP Card (6.1)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .background(SurfaceColor, RoundedCornerShape(16.dp))
            .border(
                width = 1.dp, 
                color = if (isOverdue) PinkColor.copy(alpha = 0.5f) else BorderColor, 
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.bookTitle,
                    style = Typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Student: ${tx.studentName}",
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info, 
                        contentDescription = null, 
                        modifier = Modifier.size(14.dp),
                        tint = AccentColor
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Borrowed: ${sdf.format(Date(tx.borrowDate))}",
                        style = Typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (tx.returned) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle, 
                            contentDescription = null, 
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Returned: ${sdf.format(Date(tx.returnDate ?: 0))}",
                            style = Typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp
                        )
                    }
                } else if (isOverdue) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning, 
                            contentDescription = null, 
                            modifier = Modifier.size(14.dp),
                            tint = PinkColor
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "OVERDUE (Limit: 7 Days)",
                            style = Typography.labelSmall,
                            color = PinkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            
            // SOP Status Indicator
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (tx.returned) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                        else if (isOverdue) PinkColor.copy(alpha = 0.1f)
                        else TagBgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (tx.returned) Icons.Default.CheckCircle 
                                     else if (isOverdue) Icons.Default.Warning 
                                     else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (tx.returned) Color(0xFF2E7D32) 
                               else if (isOverdue) PinkColor
                               else AccentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
