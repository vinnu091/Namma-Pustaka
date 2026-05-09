package com.example.vinnu.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.vinnu.data.BookEntity
import com.example.vinnu.data.StudentEntity
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(viewModel: LibraryViewModel, navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var scannedCode by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    var detectedBook by remember { mutableStateOf<BookEntity?>(null) }
    var selectedStudent by remember { mutableStateOf<StudentEntity?>(null) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchedStudents by viewModel.searchedStudents.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(scannedCode) {
        if (scannedCode.isNotEmpty()) {
            detectedBook = viewModel.getBookByCode(scannedCode)
        } else {
            detectedBook = null
            selectedStudent = null
            viewModel.updateSearchQuery("")
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        if (!hasCameraPermission) {
            PermissionDeniedContent { launcher.launch(Manifest.permission.CAMERA) }
        } else if (scannedCode.isEmpty()) {
            ScannerCameraView(
                cameraExecutor = cameraExecutor,
                lifecycleOwner = lifecycleOwner,
                onCodeScanned = { scannedCode = it }
            )
        } else {
            ResultContent(
                detectedBook = detectedBook,
                selectedStudent = selectedStudent,
                searchQuery = searchQuery,
                searchedStudents = searchedStudents,
                isProcessing = isProcessing,
                onSearchChange = { viewModel.updateSearchQuery(it) },
                onStudentSelect = { selectedStudent = it },
                onConfirmIssue = {
                    if (detectedBook != null && selectedStudent != null) {
                        isProcessing = true
                        scope.launch {
                            if (viewModel.issueBook(detectedBook!!, selectedStudent!!)) {
                                scannedCode = ""
                                navController.navigate("history") { popUpTo("scan") { inclusive = true } }
                            } else {
                                isProcessing = false
                            }
                        }
                    }
                },
                onConfirmReturn = {
                    if (detectedBook != null) {
                        isProcessing = true
                        scope.launch {
                            if (viewModel.returnBook(detectedBook!!)) {
                                scannedCode = ""
                                navController.navigate("history") { popUpTo("scan") { inclusive = true } }
                            } else {
                                isProcessing = false
                            }
                        }
                    }
                },
                onScanAnother = { scannedCode = "" }
            )
        }
    }
}

@Composable
fun PermissionDeniedContent(onGrant: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Camera permission is required", style = Typography.headlineSmall, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onGrant,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
        ) {
            Text("Grant Permission", style = Typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerCameraView(
    cameraExecutor: java.util.concurrent.ExecutorService,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCodeScanned: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        BarcodeScanning.getClient().process(image)
                            .addOnSuccessListener { barcodes ->
                                if (barcodes.isNotEmpty()) {
                                    barcodes[0].rawValue?.let { onCodeScanned(it) }
                                }
                            }
                            .addOnCompleteListener { imageProxy.close() }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageAnalysis)
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                    } catch (e: Exception) {
                        Log.e("Scanner", "Camera binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Scan Book QR Code",
                    color = Color.White,
                    style = Typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun ResultContent(
    detectedBook: BookEntity?,
    selectedStudent: StudentEntity?,
    searchQuery: String,
    searchedStudents: List<StudentEntity>,
    isProcessing: Boolean,
    onSearchChange: (String) -> Unit,
    onStudentSelect: (StudentEntity?) -> Unit,
    onConfirmIssue: () -> Unit,
    onConfirmReturn: () -> Unit,
    onScanAnother: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(start = 0.dp, top = 48.dp, end = 0.dp, bottom = 32.dp)
    ) {
        item {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (detectedBook != null) "Book Identified" else "Searching...",
                style = Typography.headlineSmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (detectedBook != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0F000000))
                        .background(SurfaceColor, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(detectedBook.title, style = Typography.titleMedium, color = TextPrimary)
                        Text("Author: ${detectedBook.author}", style = Typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Surface(
                            color = if (detectedBook.isIssued) PinkColor.copy(alpha = 0.1f) else AccentColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (detectedBook.isIssued) "Currently Issued" else "Available to Issue",
                                color = if (detectedBook.isIssued) PinkColor else AccentColor,
                                style = Typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (!detectedBook.isIssued) {
                item {
                    Text(
                        "Issue to Student",
                        style = Typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                        placeholder = { Text("Search name or student ID", color = TextSecondary, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = BackgroundColor,
                            unfocusedContainerColor = BackgroundColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = AccentColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedStudent == null) {
                    items(searchedStudents) { student ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onStudentSelect(student) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(student.name, style = Typography.titleMedium, color = TextPrimary)
                                Text("ID: ${student.studentId} • ${student.className}", style = Typography.bodyMedium, color = TextSecondary)
                            }
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TagBgColor, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(selectedStudent.name, style = Typography.titleMedium, color = TextPrimary)
                                Text("ID: ${selectedStudent.studentId}", style = Typography.bodyMedium, color = TextSecondary)
                            }
                            IconButton(onClick = { onStudentSelect(null) }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Change", tint = AccentColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = onConfirmIssue,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                            enabled = !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Confirm & Issue", style = Typography.labelLarge)
                            }
                        }
                    }
                }
            } else {
                item {
                    Button(
                        onClick = onConfirmReturn,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkColor),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Confirm Return", style = Typography.labelLarge)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onScanAnother,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TagBgColor, contentColor = AccentColor),
                enabled = !isProcessing
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Another Book", style = Typography.labelLarge)
            }
        }
    }
}
