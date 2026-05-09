package com.example.vinnu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(viewModel: LibraryViewModel, navController: NavController) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Story") }
    var code by remember { mutableStateOf("") }
    var pages by remember { mutableStateOf("100") }
    
    val categories = listOf("Story", "Science", "History", "Literature")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            // Template C - Zone 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    "Add New Book",
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Register a new book into the school library system.",
                style = Typography.bodyMedium,
                color = TextSecondary
            )

            // SOP Styled Input (6.6)
            SopTextField(
                value = title,
                onValueChange = { title = it },
                label = "Book Title"
            )

            SopTextField(
                value = author,
                onValueChange = { author = it },
                label = "Author Name"
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .menuAnchor()
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                        label = { Text("Category", color = TextSecondary, fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceColor,
                            unfocusedContainerColor = SurfaceColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = AccentColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SurfaceColor)
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption, style = Typography.bodyMedium) },
                                onClick = {
                                    category = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            SopTextField(
                value = pages,
                onValueChange = { pages = it },
                label = "Number of Pages",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            SopTextField(
                value = code,
                onValueChange = { code = it },
                label = "Book QR/Barcode Content",
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Scan", tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SOP Primary Button (6.2)
            Button(
                onClick = {
                    if (title.isNotBlank() && code.isNotBlank()) {
                        viewModel.addBook(title, author, category, code, pages.toIntOrNull() ?: 100)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
            ) {
                Text("Add to Collection", style = Typography.labelLarge)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SopTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
        label = { Text(label, color = TextSecondary, fontSize = 12.sp) },
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceColor,
            unfocusedContainerColor = SurfaceColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = AccentColor
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions
    )
}
