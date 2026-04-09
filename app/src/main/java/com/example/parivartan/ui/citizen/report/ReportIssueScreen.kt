package com.example.parivartan.ui.citizen.report

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.util.Locale
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.parivartan.data.IssueModel
import com.example.parivartan.data.IssueRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit,
    onNavigateToMyComplaints: () -> Unit
) {
    // State for form
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // State for Dropdown
    var expanded by remember { mutableStateOf(false) }
    var departmentId by remember { mutableStateOf("") }
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

    // Location state mock
    var isLoadingLocation by remember { mutableStateOf(false) }
    var locationAddress by remember { mutableStateOf<String?>(null) }
    var locationCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Image state
    var mediaFiles by remember { mutableStateOf(listOf<Uri>()) }

    var isSubmitting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val issueRepository = remember { IssueRepository() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (mediaFiles.size < 5) {
                mediaFiles = mediaFiles + it
            } else {
                Toast.makeText(context, "Limit Reached: Maximum 5 images allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let {
                if (mediaFiles.size < 5) {
                    mediaFiles = mediaFiles + it
                } else {
                    Toast.makeText(context, "Limit Reached: Maximum 5 images allowed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            coroutineScope.launch {
                try {
                    val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if(hasPerm) {
                        @Suppress("MissingPermission")
                        val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                        if (location != null) {
                            try {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                @Suppress("DEPRECATION")
                                val addresses = withContext(Dispatchers.IO) {
                                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                }
                                if (!addresses.isNullOrEmpty()) {
                                    locationAddress = addresses[0].getAddressLine(0)
                                } else {
                                    locationAddress = "Location point selected"
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                locationAddress = "Could not resolve address"
                            }
                            locationCoords = Pair(location.latitude, location.longitude)
                        } else {
                            Toast.makeText(context, "Location not found, please turn on GPS", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isLoadingLocation = false
                }
            }
        } else {
            isLoadingLocation = false
            Toast.makeText(context, "Location permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    // Simplistic validation
    val isFormValid = title.isNotBlank() && description.isNotBlank() && departmentId.isNotBlank() && locationAddress != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // slate-50
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488))
                    )
                )
                .padding(top = 60.dp, bottom = 30.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Report an Issue",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Help improve your community by reporting issues",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title Input
            Text(
                text = "Title *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = title,
                onValueChange = { if (it.length <= 50) title = it },
                placeholder = { Text("Brief title of the issue") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedBorderColor = Color(0xFF0D9488)
                ),
                singleLine = true
            )
            Text(
                text = "${title.length}/50",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp, bottom = 16.dp)
            )

            // Department Input
            Text(
                text = "Department *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = departments.find { it.first == departmentId }?.second ?: "",
                    onValueChange = {},
                    placeholder = { Text("Select department") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedBorderColor = Color(0xFF0D9488)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    departments.forEach { (id, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                departmentId = id
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Description Input
            Text(
                text = "Description *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 500) description = it },
                placeholder = { Text("Detailed description of the issue") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedBorderColor = Color(0xFF0D9488)
                ),
                maxLines = 6
            )
            Text(
                text = "${description.length}/500",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp, bottom = 16.dp)
            )

            // Location Input
            Text(
                text = "Location *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoadingLocation) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color(0xFF0D9488), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Detecting your location...", fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                }
            } else if (locationAddress != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF0D9488))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(locationAddress ?: "", fontSize = 14.sp, color = Color(0xFF334155))
                            Text(
                                "${locationCoords?.first.toString().take(8)}, ${locationCoords?.second.toString().take(8)}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    IconButton(onClick = {
                        isLoadingLocation = true
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF0D9488))
                    }
                }
            } else {
                Button(
                    onClick = {
                        isLoadingLocation = true
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Detect My Location", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(20.dp))

            // Image Upload
            Text(
                text = "Upload Images (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Add up to 5 images to help describe the issue",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        cameraUri = uri
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D9488)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Take Photo", color = Color(0xFF334155))
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D9488)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery", color = Color(0xFF334155))
                }
            }

            if (mediaFiles.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(mediaFiles) { index, uri ->
                        Box(modifier = Modifier.size(100.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .clickable {
                                        val newList = mediaFiles.toMutableList()
                                        newList.removeAt(index)
                                        mediaFiles = newList
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isSubmitting = true
                        val newIssue = IssueModel(
                            title = title.trim(),
                            description = description.trim(),
                            department = departments.find { it.first == departmentId }?.first ?: "pwd",
                            locationAddress = locationAddress ?: "",
                            locationLat = locationCoords?.first ?: 0.0,
                            locationLng = locationCoords?.second ?: 0.0,
                            photos = mediaFiles.map { it.toString() }
                        )
                        val result = issueRepository.submitIssue(newIssue)
                        isSubmitting = false
                        if (result.isSuccess) {
                            Toast.makeText(context, "Issue reported successfully!", Toast.LENGTH_LONG).show()
                            val issueId = result.getOrNull() ?: newIssue.id.ifEmpty { "1" }
                            onNavigateToIssueDetail(issueId)
                        } else {
                            Toast.makeText(context, "Failed to submit issue: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = isFormValid && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D9488),
                    disabledContainerColor = Color(0xFF0D9488).copy(alpha = 0.7f)
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Report", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}