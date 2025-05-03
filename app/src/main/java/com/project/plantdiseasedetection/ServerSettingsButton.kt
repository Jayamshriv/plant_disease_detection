package com.project.plantdiseasedetection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun ServerSettingsButton(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    var showDialog by remember { mutableStateOf(false) }

    // Small icon button
    IconButton(
        onClick = { showDialog = true },
        modifier = modifier
            .size(40.dp)
            .shadow(4.dp, shape = CircleShape)
//            .background(color.copy(alpha = 0.2f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Server Settings",
            tint = color
        )
    }

    // Show dialog when button is clicked
    if (showDialog) {
        ServerSettingsDialog(
            onDismiss = { showDialog = false }
        )
    }
}

// The dialog UI for configuring server settings
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ServerSettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var ipAddress by remember { mutableStateOf(ServerConfig.getServerIp(context)) }
    var port by remember { mutableStateOf(ServerConfig.getServerPort(context)) }
    var isValidInput by remember { mutableStateOf(true) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    // Function to validate IP address format
    fun isValidIpAddress(ip: String): Boolean {
        val pattern = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"""
        return ip.matches(Regex(pattern))
    }

    // Function to validate port format
    fun isValidPort(port: String): Boolean {
        return port.toIntOrNull()?.let { it in 1..65535 } ?: false
    }

    // Function to validate both inputs
    fun validateInputs(): Boolean {
        return isValidIpAddress(ipAddress) && isValidPort(port)
    }

    // Function to save settings and show confirmation
    fun saveSettings() {
        if (validateInputs()) {
            isValidInput = true
            ServerConfig.saveServerConfig(context, ipAddress, port)
            // Update RetrofitInstance here
            RetrofitInstance.updateBaseUrl("http://$ipAddress:$port/")

            // Show success message briefly
            showSuccessMessage = true
            scope.launch {
                delay(1500)
                showSuccessMessage = false
                onDismiss()
            }
        } else {
            isValidInput = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Server Configuration",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // IP Address input
                Text(
                    text = "IP Address",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = {
                        ipAddress = it
                        isValidInput = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    placeholder = { Text("e.g. 192.168.0.106") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Port input
                Text(
                    text = "Port",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = port,
                    onValueChange = {
                        port = it
                        isValidInput = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            saveSettings()
                        }
                    ),
                    placeholder = { Text("e.g. 5000") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                if (!isValidInput) {
                    Text(
                        text = "Please enter a valid IP address and port",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color.Red
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { saveSettings() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text("Save")
                    }
                }

                // Success message animation
                AnimatedVisibility(
                    visible = showSuccessMessage,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(
                                color = Color(0xFF2E7D32).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings saved successfully",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Focus the IP field when dialog appears
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
}
