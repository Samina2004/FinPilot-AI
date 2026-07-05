package com.finpilotai.presentation.receipt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.finpilotai.presentation.components.FinPilotButton
import com.finpilotai.presentation.components.FinPilotOutlinedButton
import com.finpilotai.ui.theme.*
import java.io.File
import androidx.compose.ui.draw.clip
@Composable
fun ScanReceiptScreen(
    onBack: () -> Unit,
    onImageCaptured: (imagePath: String) -> Unit
) {
    val context = LocalContext.current
    var capturedImagePath by remember { mutableStateOf<String?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    // ── Gallery Picker ───────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = copyUriToFile(context, it)
            if (path != null) capturedImagePath = path
        }
    }

    // ── Camera Capture ───────────────────────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraUri != null) {
            val path = copyUriToFile(context, pendingCameraUri!!)
            if (path != null) capturedImagePath = path
        }
    }

    // ── Camera Permission ────────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val uri = createImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnBackground)
                }
                Text(
                    text  = "Scan Receipt",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnBackground
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandPrimary.copy(alpha = 0.08f), Background)
                    )
                )
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                if (capturedImagePath == null) {
                    // ── Initial State ─────────────────────────────────────
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🧾", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text  = "Attach Receipt",
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnBackground
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text  = "Take a photo or pick from gallery.\nThe image will be saved as proof with your expense.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))
                        FinPilotButton(
                            text    = "📷  Take Photo",
                            onClick = { launchCamera() }
                        )
                        Spacer(Modifier.height(12.dp))
                        FinPilotOutlinedButton(
                            text    = "🖼️  Choose from Gallery",
                            onClick = { galleryLauncher.launch("image/*") }
                        )
                    }
                } else {
                    // ── Image Preview ─────────────────────────────────────
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text  = "Receipt captured!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnBackground
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = "Fill in the expense details on the next screen. This image will be saved as proof.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    AsyncImage(
                        model = File(capturedImagePath!!),
                        contentDescription = "Receipt",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(24.dp))

                    FinPilotButton(
                        text    = "Continue — Fill Expense Details",
                        onClick = { onImageCaptured(capturedImagePath!!) }
                    )
                    Spacer(Modifier.height(12.dp))
                    FinPilotOutlinedButton(
                        text    = "Retake Photo",
                        onClick = { capturedImagePath = null }
                    )
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
private fun createImageUri(context: Context): Uri {
    val imageFile = File(context.cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

private fun copyUriToFile(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "receipt_copy_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output -> inputStream.copyTo(output) }
        inputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}