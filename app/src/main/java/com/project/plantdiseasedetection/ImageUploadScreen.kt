package com.project.plantdiseasedetection

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageUploadScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var predictionResponse by remember { mutableStateOf<PredictionResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            isLoading = true
            coroutineScope.launch {
                predictionResponse = uploadImageToServer(context, it)
                isLoading = false
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val file = File(context.cacheDir, "captured_image.jpg")
            file.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            val uri = Uri.fromFile(file)
            imageUri = uri
            isLoading = true
            coroutineScope.launch {
                predictionResponse = uploadImageToServer(context, uri)
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFE8F5E9),
        topBar = {
            TopAppBar(
                title = { Text("Plant Disease Detection", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal= 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Upload a leaf image to detect plant diseases",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1B5E20),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (imageUri != null) {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 220.dp)
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = "Upload Image",
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Upload a plant leaf image",
                                color = Color(0xFF388E3C),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { pickImageLauncher.launch("image/*") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF388E3C)),
                    border = BorderStroke(2.dp, Color(0xFF66BB6A)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Icon(Icons.Outlined.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }

//                Button(
//                    onClick = { takePictureLauncher.launch() },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
//                    shape = RoundedCornerShape(50),
//                    modifier = Modifier.weight(1f).padding(start = 8.dp)
//                ) {
//                    Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Camera", color = Color.White)
//                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Analyzing leaf image...", color = Color(0xFF2E7D32))
                        }
                    }
                }
            }

            predictionResponse?.let { result ->
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        // Disease Name with appropriate icon
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (result.disease_name == "Background Without Leaves")
                                    Icons.Outlined.ErrorOutline
                                else
                                    Icons.Outlined.LocalFlorist,
                                contentDescription = null,
                                tint = if (result.disease_name == "Background Without Leaves")
                                    Color(0xFFE53935)
                                else
                                    Color(0xFF2E7D32),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = result.disease_name,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (result.disease_name == "Background Without Leaves")
                                    Color(0xFFE53935)
                                else
                                    Color(0xFF2E7D32)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Description with icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = Color(0xFF5D4037),
                                modifier = Modifier.size(24.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = result.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Prevention with icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SecurityUpdate,
                                contentDescription = null,
                                tint = Color(0xFF5D4037),
                                modifier = Modifier.size(24.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = result.prevention,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Only show supplement if it's not a "Background Without Leaves" response
                        if (result.disease_name != "Background Without Leaves" && result.supplement?.name != null) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Supplement with icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Medication,
                                    contentDescription = null,
                                    tint = Color(0xFF5D4037),
                                    modifier = Modifier.size(24.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Recommended Supplement:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${result.supplement.name}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )


                                    if (result.supplement.image_url != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        AsyncImage(
                                            model = result.supplement.image_url,
                                            contentDescription = "Supplement Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                    }

                                    // Add Buy Link if available
                                    if (result.supplement.buy_link != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = {
                                                val intent = CustomTabsIntent.Builder()
                                                    .setShowTitle(true)
                                                    .setUrlBarHidingEnabled(true)
                                                    .build()
                                                    .launchUrl(context, Uri.parse(result.supplement.buy_link))
//                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.supplement.buy_link))
//                                                context.startActivity(intent)
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF388E3C)),
                                            border = BorderStroke(1.dp, Color(0xFF66BB6A)),
                                            shape = RoundedCornerShape(24.dp)
                                        ) {
                                            Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("View Product")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Disease image (if not in Background Without Leaves case)
                        if (result.disease_name != "Background Without Leaves") {
                            Text(
                                text = "Reference Image:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = result.image_url,
                                contentDescription = "Disease Reference Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            // Show a warning card for Background Without Leaves
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Please upload an image that clearly shows a plant leaf for proper disease detection.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFB71C1C)
                                    )
                                }
                            }
                        }

                        // Try Again button for Background Without Leaves case
                        if (result.disease_name == "Background Without Leaves") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { pickImageLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Outlined.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try Again with Different Image", color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


fun createTempFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


suspend fun uploadImageToServer(context: Context, uri: Uri): PredictionResponse? {
    return withContext(Dispatchers.IO) {
        try {
            val file = createTempFileFromUri(context, uri)!!

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = RetrofitInstance.getApi(context).uploadImage(body)
            Log.d("Response", response.body()?.toString() ?: "Response body is null")
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}


