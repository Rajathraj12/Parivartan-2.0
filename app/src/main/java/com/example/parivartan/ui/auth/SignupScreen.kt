package com.example.parivartan.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignupScreen(
    navController: NavController,
    onSignup: (String, String, String, (String) -> Unit, () -> Unit) -> Unit = { _, _, _, _, _ -> }
) {
    var step by remember { mutableStateOf(1) } // 1 for form, 2 for success

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    val passwordsMatch = confirmPassword.isEmpty() || password == confirmPassword
    val isFormValid = fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
                      confirmPassword.isNotBlank() && password == confirmPassword

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488))
                    )
                )
                .padding(top = 60.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
        ) {
            IconButton(
                onClick = { if (step == 1) navController.popBackStack() else step = 1 },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (step == 1) "Create Account" else "Success",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (step == 1) "Sign up to get started" else "Your account has been created",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        if (step == 1) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 160.dp) // Push down below header
                    .verticalScroll(rememberScrollState())
            ) {
                // Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Full Name
                        InputField(
                            label = "Full Name",
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = "Enter your full name"
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Email
                        InputField(
                            label = "Email",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Enter your email"
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Phone Number
                        InputField(
                            label = "Phone Number (Optional)",
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = "Enter your phone number"
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Password
                        InputField(
                            label = "Password",
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Create a password",
                            isPassword = true,
                            showPassword = showPassword,
                            onTogglePassword = { showPassword = !showPassword }
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Confirm Password
                        InputField(
                            label = "Confirm Password",
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Confirm your password",
                            isPassword = true,
                            showPassword = showConfirmPassword,
                            onTogglePassword = { showConfirmPassword = !showConfirmPassword },
                            isError = !passwordsMatch,
                            errorMessage = "Passwords do not match"
                        )
                        Spacer(modifier = Modifier.height(30.dp))

                        // Signup Button
                        if (authError != null) {
                            Text(
                                text = authError ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Button(
                            onClick = {
                                isLoading = true
                                authError = null
                                onSignup(fullName.trim(), email.trim(), password, { error ->
                                    isLoading = false
                                    authError = error
                                }, {
                                    isLoading = false
                                    step = 2
                                })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0D9488),
                                disabledContainerColor = Color(0xFF0D9488).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = isFormValid && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }
                }

                // Login Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Already have an account? ", color = Color(0xFF64748B), fontSize = 14.sp)
                    Text(
                        "Sign In",
                        color = Color(0xFF0D9488),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }
            }
        } else {
            // Success Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 180.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(64.dp).padding(bottom = 16.dp)
                        )
                        Text(
                            "Account Created!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Your account has been created successfully. You can now sign in to access all features.",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = { navController.popBackStack() }, // Go back to login
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Continue to Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: () -> Unit = {},
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                unfocusedBorderColor = if (isError) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true,
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }
            } else null,
            isError = isError
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFEF4444),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}