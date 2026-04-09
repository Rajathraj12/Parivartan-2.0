package com.example.parivartan.ui.citizen.profile

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userFirstName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }

    var displayName by remember { mutableStateOf(userFirstName) }
    var email by remember { mutableStateOf(userEmail) }
    var phone by remember { mutableStateOf("") }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationSharing by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // slate-50
    ) {
        // Profile Header Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Fixed height for overlap effect
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF14B8A6), Color(0xFF0F766E)) // teal-500 to teal-700
                        )
                    )
            )

            // Avatar & Info Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp), // slightly pushed down
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image with ring
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp) // inner border simulation
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF0EA5E9), Color(0xFF0369A1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (displayName.isNotEmpty()) displayName.take(1).uppercase() else "U",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Edit Profile Button (top right)
            if (!isEditing && !showChangePassword) {
                IconButton(
                    onClick = { isEditing = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 16.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Profile", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name & Email Info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0D9488),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    placeholder = { Text("Your Name", color = Color.Gray) },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                )
            } else {
                Text(
                    text = displayName.ifEmpty { "User" },
                    color = Color(0xFF1E293B),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email.ifEmpty { "user@example.com" },
                color = Color(0xFF64748B),
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            when {
                isEditing -> {
                    // Edit Profile Section
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 20.dp))

                            InputField("Full Name", displayName) { displayName = it }
                            InputField("Email", email) { email = it }
                            InputField("Phone Number (Optional)", phone) { phone = it }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isEditing = false
                                        // Reset fields
                                        displayName = userFirstName
                                        email = userEmail
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            try {
                                                val user = FirebaseAuth.getInstance().currentUser
                                                if (user != null) {
                                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                                        .setDisplayName(displayName)
                                                        .build()
                                                    user.updateProfile(profileUpdates).await()
                                                    if (email != user.email && email.isNotEmpty()) {
                                                        user.updateEmail(email).await()
                                                    }
                                                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                                    isEditing = false
                                                } else {
                                                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
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
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Text("Change Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 20.dp))

                            InputField("Current Password", currentPassword, isPassword = true) { currentPassword = it }
                            InputField("New Password", newPassword, isPassword = true) { newPassword = it }
                            InputField("Confirm New Password", confirmPassword, isPassword = true) { confirmPassword = it }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        showChangePassword = false
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                }
                                Button(
                                    onClick = {
                                        if (newPassword != confirmPassword) {
                                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (newPassword.length < 6) {
                                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (currentPassword.isEmpty()) {
                                            Toast.makeText(context, "Please enter current password", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        coroutineScope.launch {
                                            try {
                                                val user = FirebaseAuth.getInstance().currentUser
                                                if (user != null && user.email != null) {
                                                    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                                                    user.reauthenticate(credential).await()
                                                    user.updatePassword(newPassword).await()
                                                    Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                                    showChangePassword = false
                                                    currentPassword = ""
                                                    newPassword = ""
                                                    confirmPassword = ""
                                                } else {
                                                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Update", color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Settings View
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Account Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))

                            SettingItem(Icons.Outlined.Lock, "Change Password") { showChangePassword = true }
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                            SettingToggleItem(Icons.Outlined.Notifications, "Notifications", notificationsEnabled) {
                                notificationsEnabled = it
                                Toast.makeText(context, if (it) "Notifications Enabled" else "Notifications Disabled", Toast.LENGTH_SHORT).show()
                            }
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                            SettingToggleItem(Icons.Outlined.LocationOn, "Location Sharing", locationSharing) {
                                locationSharing = it
                                Toast.makeText(context, if (it) "Location Sharing Enabled" else "Location Sharing Disabled", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Help & Support", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 12.dp))

                            SettingItem(Icons.Outlined.Info, "Help Center") { Toast.makeText(context, "Help Center Opened", Toast.LENGTH_SHORT).show() }
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                            SettingItem(Icons.Outlined.Email, "Contact Support") { Toast.makeText(context, "Contact Support Opened", Toast.LENGTH_SHORT).show() }
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                            SettingItem(Icons.Outlined.Lock, "Privacy Policy") { Toast.makeText(context, "Privacy Policy Opened", Toast.LENGTH_SHORT).show() }
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                            SettingItem(Icons.Outlined.Info, "About") { Toast.makeText(context, "About Opened", Toast.LENGTH_SHORT).show() }
                        }
                    }

                    // Logout
                    Surface(
                        color = Color(0xFFFEF2F2), // subtle red bg
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33EF4444)),
                        onClick = onLogout
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Log Out", tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Out", color = Color(0xFFDC2626), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                uncheckedTrackColor = Color(0xFFE2E8F0),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}