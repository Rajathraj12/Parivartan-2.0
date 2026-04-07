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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.parivartan.R

private val DeptColor = Color(0xFF14B8A6)
private val DarkDeptColor = Color(0xFF0D9488)
private val Slate50 = Color(0xFFF8FAFC)

private fun generateCaptcha(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..6).map { chars.random() }.joinToString("")
}

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
    var captchaText by remember { mutableStateOf(generateCaptcha()) }
    val refreshCaptcha = {
        captchaText = generateCaptcha()
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
            .background(Slate50)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Modern Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DeptColor, DarkDeptColor)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, bottom = 48.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Parivartan Logo",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Department Portal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Official Login",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-32).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Department Dropdown
                ExposedDropdownMenuBox(
                    expanded = departmentMenuExpanded,
                    onExpandedChange = { departmentMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = departments.find { it.first == selectedDepartment }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Department") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = DeptColor) },
                        leadingIcon = { Icon(Icons.Filled.Business, null, tint = DeptColor) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeptColor,
                            focusedLabelColor = DeptColor,
                            cursorColor = DeptColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = departmentMenuExpanded,
                        onDismissRequest = { departmentMenuExpanded = false }
                    ) {
                        departments.forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedDepartment = id
                                    departmentMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, "Email", tint = DeptColor)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeptColor,
                        focusedLabelColor = DeptColor,
                        cursorColor = DeptColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, "Password", tint = DeptColor)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeptColor,
                        focusedLabelColor = DeptColor,
                        cursorColor = DeptColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Captcha
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0FDFA), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFCCFBF1), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Solve Captcha",
                            fontSize = 12.sp,
                            color = Color(0xFF0F766E),
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = captchaText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF115E59),
                                modifier = Modifier.background(Color.White, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp).border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                            )
                            IconButton(onClick = refreshCaptcha) {
                                Icon(Icons.Outlined.Refresh, "Refresh", tint = Color(0xFF0F766E))
                            }
                        }
                    }
                    OutlinedTextField(
                        value = captchaAnswer,
                        onValueChange = { captchaAnswer = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeptColor,
                            unfocusedBorderColor = Color(0xFFCCFBF1)
                        ),
                        modifier = Modifier.width(100.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        placeholder = { Text("Text", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            delay(800) // fake load
                            onLoginDemoClick("department")
                        }
                    },
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && selectedDepartment.isNotBlank() && captchaAnswer.equals(captchaText, ignoreCase = true),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeptColor,
                        disabledContainerColor = DeptColor.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Secure Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = "Authorized Personnel Only",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )
            Text(
                text = "Tracked via IP & ID",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
