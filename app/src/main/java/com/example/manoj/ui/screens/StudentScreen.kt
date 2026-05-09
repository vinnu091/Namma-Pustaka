package com.example.vinnu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel

@Composable
fun StudentScreen(viewModel: LibraryViewModel) {
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    
    val students by viewModel.allStudents.collectAsState()

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
                    text = "Students",
                    style = Typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Manage your school's student database",
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
            // Register Section (SOP Card 6.1)
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(SurfaceColor, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Register New Student", style = Typography.titleMedium, color = TextPrimary)
                    
                    SopTextField(
                        value = name, 
                        onValueChange = { name = it }, 
                        label = "Full Name"
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            SopTextField(
                                value = studentId, 
                                onValueChange = { studentId = it }, 
                                label = "Student ID"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SopTextField(
                                value = className, 
                                onValueChange = { className = it }, 
                                label = "Grade"
                            )
                        }
                    }
                    
                    // SOP Primary Button (6.2)
                    Button(
                        onClick = {
                            if (name.isNotBlank() && studentId.isNotBlank()) {
                                viewModel.addStudent(name, studentId, className)
                                name = ""
                                studentId = ""
                                className = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Add Student", style = Typography.labelLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Registered Students", 
                style = Typography.headlineSmall, 
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            if (students.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No students registered yet.", style = Typography.bodyMedium, color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(students) { student ->
                        // SOP Card (6.1)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
                                .background(SurfaceColor, RoundedCornerShape(16.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = TagBgColor
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Person, 
                                            contentDescription = null,
                                            tint = AccentColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(student.name, style = Typography.titleMedium, color = TextPrimary)
                                    Text(
                                        "ID: ${student.studentId} • ${student.className}", 
                                        style = Typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
