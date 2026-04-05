package com.example.parivartan.ui.citizen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userFirstName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }

    var displayName by remember { mutableStateOf(userFirstName) }
    var email by remember { mutableStateOf(userEmail) }
    var phone by remember { mutableStateOf("") }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationSharing by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // slate-50
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488)) // teal-500 to teal-600
                    )
                )
                .padding(top = 60.dp, bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0891B2)) // cyan-600
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (displayName.isNotEmpty()) displayName.take(1).uppercase() else "U",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        placeholder = { Text("Your Name", color = Color.White.copy(alpha = 0.7f)) },
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.width(200.dp)
                    )
                } else {
                    Text(
                        text = displayName.ifEmpty { "User" },
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email.ifEmpty { "user@example.com" },
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isEditing && !showChangePassword) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        onClick = { isEditing = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Profile Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when {
                isEditing -> {
                    // Edit Profile Section
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Edit Profile", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 16.dp))

                            InputField("Full Name", displayName) { displayName = it }
                            InputField("Email", email) { email = it }
                            InputField("Phone Number (Optional)", phone) { phone = it }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { isEditing = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = { isEditing = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
                showChangePassword -> {
                    // Change Password Section
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Change Password", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 16.dp))

                            InputField("Current Password", currentPassword, isPassword = true) { currentPassword = it }
                            InputField("New Password", newPassword, isPassword = true) { newPassword = it }
                            InputField("Confirm New Password", confirmPassword, isPassword = true) { confirmPassword = it }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { showChangePassword = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = { showChangePassword = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Update Password", color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Settings View
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 8.dp))

                            SettingItem(Icons.Outlined.Lock, "Change Password") { showChangePassword = true }
                            SettingToggleItem(Icons.Outlined.Notifications, "Notifications", notificationsEnabled) { notificationsEnabled = it }
                            SettingToggleItem(Icons.Outlined.LocationOn, "Location Sharing", locationSharing) { locationSharing = it }

                            // For Moon/Dark mode
                            SettingToggleItem(Icons.Outlined.CheckCircle, "Dark Mode", darkMode) { darkMode = it }
                        }
                    }

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Help & Support", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 8.dp))

                            SettingItem(Icons.Outlined.Info, "Help Center") {}
                            SettingItem(Icons.Outlined.Email, "Contact Support") {}
                            SettingItem(Icons.Outlined.Lock, "Privacy Policy") {}
                            SettingItem(Icons.Outlined.Info, "About") {}
                        }
                    }

                    // Logout
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .padding(bottom = 32.dp),
                        onClick = onLogout
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Log Out", tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Out", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(label: String, value: String, isPassword: Boolean = false, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCBD5E1),
                unfocusedBorderColor = Color(0xFFCBD5E1)
            ),
        )
    }
}

@Composable
fun SettingItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0FDFA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun SettingToggleItem(icon: ImageVector, text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0FDFA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0D9488),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}