package com.example.vinnu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: LibraryViewModel, navController: NavController) {
    val books by viewModel.allBooks.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val categories = listOf("All", "Story", "Science", "History", "Literature")

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            // Template A - Zone 1: Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Digital Shelf",
                    style = Typography.headlineLarge,
                    color = TextPrimary
                )
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = TagBgColor
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = AccentColor
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_book") },
                containerColor = AccentColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Zone 3: Search & Filters
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // SOP Search Bar (6.6)
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                    placeholder = { Text("Search title or author...", color = TextSecondary, style = Typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AccentColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Zone 3: Filter Chips (6.3)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            modifier = Modifier
                                .height(36.dp)
                                .clickable { selectedCategory = category },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) AccentColor else SurfaceColor,
                            border = if (isSelected) null else BorderStroke(1.dp, BorderColor)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = category.lowercase(),
                                    color = if (isSelected) Color.White else TextSecondary,
                                    style = Typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Zone 4: Content List (6.1)
            val filteredBooks = books.filter {
                (selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)) &&
                (it.title.contains(searchQuery, true) || it.author.contains(searchQuery, true))
            }

            if (filteredBooks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No books found", color = TextSecondary, style = Typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredBooks) { book ->
                        BookListItem(book = book, onClick = {
                            navController.navigate("book_detail/${book.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun BookListItem(book: com.example.vinnu.data.BookEntity, onClick: () -> Unit) {
    // SOP Card (6.1)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
            .background(SurfaceColor, RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Image (left): 56x80px
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TagBgColor),
                contentAlignment = Alignment.Center
            ) {
                if (book.coverUrl.isNotEmpty()) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = book.title.take(1).uppercase(),
                        style = Typography.headlineSmall,
                        color = AccentColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = Typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = book.author,
                    style = Typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (book.isIssued) PinkColor.copy(alpha = 0.1f) else AccentColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (book.isIssued) "Issued" else "Available",
                            color = if (book.isIssued) PinkColor else AccentColor,
                            style = Typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = book.category,
                        style = Typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            
            // Action Button (right)
            Surface(
                onClick = onClick,
                modifier = Modifier.width(70.dp).height(32.dp),
                shape = RoundedCornerShape(12.dp),
                color = TagBgColor,
                contentColor = AccentColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("view", style = Typography.labelLarge, fontSize = 12.sp)
                }
            }
        }
    }
}
