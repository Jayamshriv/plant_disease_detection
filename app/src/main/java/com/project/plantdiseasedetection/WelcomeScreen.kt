package com.project.plantdiseasedetection

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onNextClick: () -> Unit) {
    // Create a richer gradient with multiple green shades
    val greenGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2E7D32), // Dark green
            Color(0xFF4CAF50), // Medium green
            Color(0xFF81C784)  // Light green
        )
    )

    // Animation for elements
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(50f) }

    // Get screen size to make the layout responsive
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

// Calculate responsive sizes
    val logoSize = (screenWidth.value * 0.3f).coerceAtMost(150f).dp
    val titleFontSize = (screenWidth.value * 0.07f).coerceIn(22f, 32f).sp
    val taglineFontSize = (screenWidth.value * 0.04f).coerceIn(14f, 18f).sp
    val descriptionFontSize = (screenWidth.value * 0.03f).coerceIn(12f, 14f).sp
    val buttonHeight = (screenHeight.value * 0.07f).coerceIn(48f, 56f).dp
    val iconSize = (screenWidth.value * 0.12f).coerceAtMost(64f).dp
    val featureIconSize = (screenWidth.value * 0.03f).coerceIn(12f, 16f).dp
    val featureFontSize = (screenWidth.value * 0.03f).coerceIn(12f, 14f).sp



    // Use responsive padding
    val horizontalPadding = (screenWidth * 0.06f).coerceIn(16.dp, 32.dp)
    val verticalPadding = (screenHeight * 0.04f).coerceIn(16.dp, 32.dp)

    LaunchedEffect(key1 = true) {
        // Start animations when screen is shown
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(greenGradient)
            .systemBarsPadding()
        ,
        contentAlignment = Alignment.BottomCenter
    ) {
        // Background leaf pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Draw some subtle leaf patterns
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = canvasWidth * 0.4f,
                center = Offset(canvasWidth * 0.8f, canvasHeight * 0.2f)
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = canvasWidth * 0.3f,
                center = Offset(canvasWidth * 0.1f, canvasHeight * 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main content column with fixed weight ratio to ensure fitting screen
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo and Title Section (top part)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .graphicsLayer(
                            alpha = alpha.value,
                            translationY = offsetY.value
                        )
                ) {
                    // App Icon/Logo
                    Box(
                        modifier = Modifier
                            .size(logoSize)
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                            .padding(logoSize * 0.1f)
                    ) {
                        // Plant icon with shadow effect
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color.White.copy(alpha = 0.9f),
                                    shape = CircleShape
                                )
                                .padding(logoSize * 0.1f),
                            contentAlignment = Alignment.Center
                        ) {
                            // Use the Eco icon from Material Icons Extended
                            Icon(
                                imageVector = Icons.Outlined.Eco,
                                contentDescription = "Plant Icon",
                                tint = Color(0xFF388E3C),
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height((screenHeight * 0.02f).coerceAtMost(32.dp)))

                    // App Title with better typography
                    Text(
                        text = "Plant Disease Detection",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 1.sp
                    )

                    Spacer(modifier = Modifier.height((screenHeight * 0.01f).coerceAtMost(16.dp)))

                    // App Tagline
                    Text(
                        text = "Detect • Diagnose • Protect",
                        fontSize = taglineFontSize,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height((screenHeight * 0.02f).coerceAtMost(24.dp)))

                    // App Description
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding * 0.25f),
                        shape = RoundedCornerShape((screenWidth * 0.04f).coerceAtMost(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding((screenWidth * 0.05f).coerceAtMost(20.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your plant's health at your fingertips",
                                fontSize = (taglineFontSize * 0.9f),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height((screenHeight * 0.01f).coerceAtMost(12.dp)))

                            Text(
                                text = "Instantly identify plant diseases using advanced AI technology. Take a photo and get diagnosis and treatment recommendations in seconds.",
                                fontSize = descriptionFontSize,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                lineHeight = (descriptionFontSize.value * 1.8f).sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(80.dp))

                // Bottom Section with Button (bottom part)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(
                            alpha = alpha.value,
                            translationY = offsetY.value * -1 // Move from bottom
                        ).padding(bottom = 32.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Feature bullets
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = (screenHeight * 0.01f).coerceAtMost(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(featureIconSize)
                        )
                        Spacer(modifier = Modifier.width((screenWidth * 0.02f).coerceAtMost(8.dp)))
                        Text(
                            text = "Instant disease identification",
                            fontSize = featureFontSize,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = (screenHeight * 0.01f).coerceAtMost(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(featureIconSize)
                        )
                        Spacer(modifier = Modifier.width((screenWidth * 0.02f).coerceAtMost(8.dp)))
                        Text(
                            text = "Treatment recommendations",
                            fontSize = featureFontSize,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = (screenHeight * 0.02f).coerceAtMost(24.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(featureIconSize)
                        )
                        Spacer(modifier = Modifier.width((screenWidth * 0.02f).coerceAtMost(8.dp)))
                        Text(
                            text = "Product suggestions for treatment",
                            fontSize = featureFontSize,
                            color = Color.White
                        )
                    }

                    // Get Started Button with animation
                    Button(
                        onClick = onNextClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape((screenWidth * 0.07f).coerceAtMost(28.dp)),
                        contentPadding = PaddingValues(
                            horizontal = (screenWidth * 0.08f).coerceIn(20.dp, 40.dp),
                            vertical = (screenHeight * 0.015f).coerceIn(12.dp, 16.dp)
                        ),
                        elevation = ButtonDefaults.buttonElevation((screenHeight * 0.01f).coerceAtMost(8.dp)),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(buttonHeight)
                            .shadow(8.dp, shape = RoundedCornerShape(50))
                            .border(
                                BorderStroke(2.dp, Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Text(
                            text = "Get Started",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = (buttonHeight.value * 0.3f).coerceIn(16f, 18f).sp
                        )
                        Spacer(modifier = Modifier.width((screenWidth * 0.02f).coerceAtMost(8.dp)))
                        Icon(
                            imageVector = Icons.Outlined.ArrowForward,
                            contentDescription = "Arrow Forward",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size((buttonHeight * 0.4f).coerceAtMost(24.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height((screenHeight * 0.02f).coerceAtMost(24.dp)))
                }
            }
        }
    }
}

