package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@Composable
fun VerifyResetCodeScreen(
    email: String = "user@example.com",
    onVerifySuccess: () -> Unit,
    onBackClick: () -> Unit,
    onResendCode: () -> Unit
) {
    var digit1 by remember { mutableStateOf("") }
    var digit2 by remember { mutableStateOf("") }
    var digit3 by remember { mutableStateOf("") }
    var digit4 by remember { mutableStateOf("") }
    var digit5 by remember { mutableStateOf("") }
    var digit6 by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendTimer by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    val fullCode = "$digit1$digit2$digit3$digit4$digit5$digit6"
    val isCodeComplete = fullCode.length == 6

    // Countdown timer for resend
    LaunchedEffect(resendTimer, canResend) {
        if (resendTimer > 0 && !canResend) {
            delay(1000)
            resendTimer--
        } else if (resendTimer == 0) {
            canResend = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF0F4F8),
                        Color(0xFFFDF6F4),
                        White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NavyBlue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Key Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFF0ED))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Key,
                    contentDescription = null,
                    tint = Color(0xFFFF6B5B),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Enter Verification Code",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "We've sent a 6-digit code to",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = email,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // OTP Input Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResetOTPDigitField(
                    value = digit1,
                    onValueChange = { if (it.length <= 1 && it.all { c -> c.isDigit() }) digit1 = it },
                    modifier = Modifier.weight(1f)
                )
                ResetOTPDigitField(
                    value = digit2,
                    onValueChange = { if (it.length <= 1 && it.all { c -> c.isDigit() }) digit2 = it },
                    modifier = Modifier.weight(1f)
                )
                ResetOTPDigitField(
                    value = digit3,
                    onValueChange = { if (it.length <= 1 && it.all { c -> c.isDigit() }) digit3 = it },
                    modifier = Modifier.weight(1f)
                )
                ResetOTPDigitField(
                    value = digit4,
                    onValueChange = { if (it.length <= 1 && it.all { c -> c.isDigit() }) digit4 = it },
                    modifier = Modifier.weight(1f)
                )
                ResetOTPDigitField(
                    value = digit5,
                    onValueChange = { if (it.length <= 1 && it.all { c -> c.isDigit() }) digit5 = it },
                    modifier = Modifier.weight(1f)
                )
                ResetOTPDigitField(
                    value = digit6,
                    onValueChange = { if (it.length <= 1 && it.all { c -> c.isDigit() }) digit6 = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // Error Message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    fontSize = 14.sp,
                    color = CoralOrange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Verify Button
            Button(
                onClick = {
                    if (isCodeComplete) {
                        isLoading = true
                        errorMessage = null
                        onVerifySuccess()
                    } else {
                        errorMessage = "Please enter all 6 digits"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCodeComplete) NavyBlue else Color(0xFFB0B8C4),
                    disabledContainerColor = Color(0xFFB0B8C4)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Verify Code",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCodeComplete) White else Color(0xFFE8E8E8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resend Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Didn't receive the code? ",
                    fontSize = 14.sp,
                    color = TextGray
                )
                if (canResend) {
                    Text(
                        text = "Resend",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralOrange,
                        modifier = Modifier.clickable {
                            onResendCode()
                            resendTimer = 60
                            canResend = false
                        }
                    )
                } else {
                    Text(
                        text = "Resend in ${resendTimer}s",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGray
                    )
                }
            }
        }
    }
}

@Composable
fun ResetOTPDigitField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(60.dp),
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = NavyBlue
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = NavyBlue,
            unfocusedTextColor = NavyBlue,
            cursorColor = NavyBlue,
            focusedBorderColor = NavyBlue,
            unfocusedBorderColor = if (value.isNotEmpty()) Color(0xFF22C55E) else Color(0xFFE8E8E8),
            focusedContainerColor = White,
            unfocusedContainerColor = White
        )
    )
}