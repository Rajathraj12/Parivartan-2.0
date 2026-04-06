package com.example.parivartan.ui.department

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentLoginScreen(
    onLoginDemoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var captchaAnswer by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var departmentMenuExpanded by remember { mutableStateOf(false) }

    // Captcha Logic
    var num1 by remember { mutableStateOf((1..10).random()) }
    var num2 by remember { mutableStateOf((1..10).random()) }
    val refreshCaptcha = {
        num1 = (1..10).random()
        num2 = (1..10).random()
        captchaAnswer = ""
    }

    val coroutineScope = rememberCoroutineScope()

    val departments = listOf(
        "pwd" to "Public Works Department (PWD)",
        "municipal" to "Municipal Corporation",
        "traffic-police" to "Traffic Police",
        "water-sanitation" to "Water Supply & Sanitation",
        "pspcl" to "PSPCL",
        "health-welfare" to "Health & Family Welfare",
        "civil-surgeon" to "Civil Surgeon's Office",
        "punjab-police" to "Punjab Police",
        "education" to "School Education Department",
        "agriculture" to "Agriculture Department",
        "food-civil-supplies" to "Food & Civil Supplies",
        "roadways" to "Punjab Roadways / PRTC",
        "rto" to "Regional Transport Office (RTO)",
        "revenue" to "Revenue Department",
        "social-security" to "Social Security & Women & Child",
        "pollution-control" to "Punjab Pollution Control Board",
        "forest" to "Forest Department",
        "disaster-management" to "Disaster Management Authority"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 30.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Business,
                contentDescription = "Department",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Department Portal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Official Login",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Email Input
            Text(
                text = "Email Address",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email address", color = Color(0xFF9CA3AF)) },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, "Email", tint = Color(0xFF6B7280))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            )

            // Password Input
            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password", color = Color(0xFF9CA3AF)) },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, "Password", tint = Color(0xFF6B7280))
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF6B7280)
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            )

            // Department Dropdown
            Text(
                text = "Department",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = departmentMenuExpanded,
                onExpandedChange = { departmentMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = departments.find { it.first == selectedDepartment }?.second ?: "Select Department",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    ),
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth().padding(bottom = 20.dp)
                )
                ExposedDropdownMenu(
                    expanded = departmentMenuExpanded,
                    onDismissRequest = { departmentMenuExpanded = false }
                ) {
                    departments.forEach { (id, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedDepartment = id
                                departmentMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Captcha Section
            Text(
                text = "Security Verification",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E8), RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF66BB6A), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$num1 + $num2 = ?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = refreshCaptcha,
                    modifier = Modifier.background(Color(0xFFFF7043), CircleShape)
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh Captcha", tint = Color.White)
                }
            }

            OutlinedTextField(
                value = captchaAnswer,
                onValueChange = { captchaAnswer = it },
                placeholder = { Text("Enter your answer", color = Color(0xFF9CA3AF)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 20.sp, color = Color(0xFF1B5E20)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp)
            )

            // Submit Button
            Button(
                onClick = {
                    if (captchaAnswer.toIntOrNull() != (num1 + num2)) {
                        /* Add a way to show a toast or alert, or simply do nothing right now */
                        refreshCaptcha()
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            delay(800) // fake load (auth bypassed)
                            onLoginDemoClick("department:$selectedDepartment")
                        }
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && selectedDepartment.isNotBlank() && captchaAnswer.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    disabledContainerColor = Color(0xFF9CA3AF)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            TextButton(
                onClick = { /* TODO Placeholder */ },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp)
            ) {
                Text("Forgot Password?", color = Color(0xFF1565C0))
            }
        }
    }
}

