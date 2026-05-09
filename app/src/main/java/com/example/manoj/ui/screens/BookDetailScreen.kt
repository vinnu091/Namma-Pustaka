package com.example.vinnu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.navigation.NavController
import com.example.vinnu.data.BookEntity
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(bookId: Long, viewModel: LibraryViewModel, navController: NavController) {
    val books by viewModel.allBooks.collectAsState()
    val book = books.find { it.id == bookId }
    val reviews by viewModel.getReviews(bookId).collectAsState(initial = emptyList())
    val students by viewModel.allStudents.collectAsState()

    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var selectedStudentName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize().background(BackgroundColor), contentAlignment = Alignment.Center) {
            Text("Book not found", style = Typography.bodyMedium, color = TextPrimary)
        }
        return
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            // Template C - Zone 1: Back navigation + title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    "Book Details",
                    style = Typography.headlineSmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Zone 3: Metadata block (Using Card anatomy 6.1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
                    .background(SurfaceColor, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(book.title, style = Typography.headlineLarge, color = TextPrimary)
                    Text("by ${book.author}", style = Typography.titleMedium, color = AccentColor)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = TagBgColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = book.category,
                                color = AccentColor,
                                style = Typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = TagBgColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${book.pages} Pages",
                                color = TextSecondary,
                                style = Typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Surface(
                        color = if (book.isIssued) PinkColor.copy(alpha = 0.1f) else Color(0xFF4CAF50).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (book.isIssued) "Currently Issued" else "Available",
                            color = if (book.isIssued) PinkColor else Color(0xFF2E7D32),
                            style = Typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Review Section (Template C - Zone 5)
            Text("Review Corner", style = Typography.headlineSmall, color = TextPrimary)
            Text("Share your thoughts with other students", style = Typography.bodyMedium, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Review Input Card (6.1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceColor, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("Leave a Review", style = Typography.titleMedium, color = TextPrimary)
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Student Selector (SOP Search Bar style 6.6)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedStudentName,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .menuAnchor()
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                            label = { Text("Reviewer Name", color = TextSecondary, fontSize = 12.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = BackgroundColor,
                                unfocusedContainerColor = BackgroundColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(SurfaceColor)
                        ) {
                            students.forEach { student ->
                                DropdownMenuItem(
                                    text = { Text(student.name, style = Typography.bodyMedium) },
                                    onClick = {
                                        selectedStudentName = student.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Star Rating (6.7)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = index + 1 },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (index < rating) PinkColor else TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$rating.0", style = Typography.labelSmall, color = TextPrimary)
                    }

                    SopTextField(
                        value = comment,
                        onValueChange = { if (it.length <= 100) comment = it },
                        label = "Your one-sentence review"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // SOP Primary Button (6.2)
                    Button(
                        onClick = {
                            if (selectedStudentName.isNotBlank() && comment.isNotBlank()) {
                                viewModel.addReview(book.id, selectedStudentName, rating, comment)
                                comment = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                        enabled = selectedStudentName.isNotBlank() && comment.isNotBlank()
                    ) {
                        Text("Submit Review", style = Typography.labelLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Community Reviews
            Text("Community Reviews", style = Typography.headlineSmall, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            if (reviews.isEmpty()) {
                Text("No reviews yet. Be the first to review!", style = Typography.bodyMedium, color = TextSecondary)
            } else {
                reviews.forEach { review ->
                    // Review Card (6.1 variant)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .background(SurfaceColor, RoundedCornerShape(16.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(review.studentName, style = Typography.titleMedium, color = TextPrimary)
                                Spacer(modifier = Modifier.weight(1f))
                                Row {
                                    repeat(review.rating) {
                                        Icon(Icons.Default.Star, null, tint = PinkColor, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("\"${review.comment}\"", style = Typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
