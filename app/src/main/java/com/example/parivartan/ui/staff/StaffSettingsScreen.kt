package com.example.parivartan.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateWorkHistory: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

    // User data
    val userName = user?.displayName?.takeIf { it.isNotBlank() } ?: "Staff Member"
    val userEmail = user?.email ?: "No email provided"
    val userStaffId = "EMP-${user?.uid?.take(4)?.uppercase() ?: "0000"}"
    val userDepartment = ""
    var contactNumber by remember { mutableStateOf("") }

    // Mock settings state
    var newAssignment by remember { mutableStateOf(true) }
    var deadlineReminder by remember { mutableStateOf(true) }
    var citizenComment by remember { mutableStateOf(true) }
    var statusUpdate by remember { mutableStateOf(false) }

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Settings Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Profile Settings", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Full Name", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = userName,
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Color(0xFFF9FAFB),
                            disabledTextColor = Color(0xFF6B7280),
                            disabledBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Mail, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Email", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Color(0xFFF9FAFB),
                            disabledTextColor = Color(0xFF6B7280),
                            disabledBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                    Text("Email cannot be changed", fontSize = 12.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(top = 4.dp))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Badge, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Staff ID", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = userStaffId,
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Color(0xFFF9FAFB),
                            disabledTextColor = Color(0xFF6B7280),
                            disabledBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (userDepartment.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Business, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Department", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = userDepartment,
                            onValueChange = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = Color(0xFFF9FAFB),
                                disabledTextColor = Color(0xFF6B7280),
                                disabledBorderColor = Color(0xFFD1D5DB)
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Call, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Contact Number", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = contactNumber,
                        onValueChange = { contactNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0D9488),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* TODO Save Profile */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Profile", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Notifications Preferences Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Notification Preferences", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    NotificationSettingRow(
                        title = "New Issue Assignment",
                        description = "Get notified when a new issue is assigned",
                        checked = newAssignment,
                        onCheckedChange = { newAssignment = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))

                    NotificationSettingRow(
                        title = "Deadline Reminders",
                        description = "Receive reminders about upcoming deadlines",
                        checked = deadlineReminder,
                        onCheckedChange = { deadlineReminder = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))

                    NotificationSettingRow(
                        title = "Citizen Comments",
                        description = "Get notified of citizen comments on your issues",
                        checked = citizenComment,
                        onCheckedChange = { citizenComment = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))

                    NotificationSettingRow(
                        title = "Status Updates",
                        description = "Receive updates on issue status changes",
                        checked = statusUpdate,
                        onCheckedChange = { statusUpdate = it }
                    )
                }
            }

            // Quick Actions Card (Optional but kept for parity)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Menu, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateWorkHistory() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.History, contentDescription = null, tint = Color(0xFF374151), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Work History", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151), modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Logout Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true }
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEF4444))
                    }
                }
            }

            // App Info
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Civic Issue Reporter v1.0.0", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(4.dp))
                Text("© 2025 City Government", fontSize = 12.sp, color = Color(0xFF9CA3AF))
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout", fontWeight = FontWeight.SemiBold) },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }) {
                        Text("Logout", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun NotificationSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
            Spacer(modifier = Modifier.height(2.dp))
            Text(description, fontSize = 12.sp, color = Color(0xFF6B7280), lineHeight = 16.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0D9488),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB)
            )
        )
    }
}
