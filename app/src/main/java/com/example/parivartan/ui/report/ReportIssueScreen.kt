package com.example.parivartan.ui.report

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
    var department by remember { mutableStateOf("") }

    // State for Dropdown
    var expanded by remember { mutableStateOf(false) }
    val departments = listOf(
        "PWD (Public Works Department)",
        "Municipal Corporation",
        "Traffic Police",
        "Water & Sanitation",
        "PSPCL (Punjab State Power)",
        "Health & Welfare",
        "Civil Surgeon",
        "Punjab Police",
        "Education Department",
        "Agriculture Department",
        "Food & Civil Supplies",
        "Punjab Roadways",
        "RTO (Regional Transport)",
        "Revenue Department",
        "Social Security",
        "Pollution Control Board",
        "Forest Department",
        "Disaster Management"
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

    // Simplistic validation
    val isFormValid = title.isNotBlank() && description.isNotBlank() && department.isNotBlank() && locationAddress != null

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
                    value = department,
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
                    departments.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                department = selectionOption
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
                        coroutineScope.launch {
                            isLoadingLocation = true
                            delay(1000) // Mocking delay
                            locationAddress = "Model Town, Jalandhar"
                            locationCoords = Pair(31.3260, 75.5762)
                            isLoadingLocation = false
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF0D9488))
                    }
                }
            } else {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoadingLocation = true
                            delay(1000) // Mocking delay
                            locationAddress = "Model Town, Jalandhar"
                            locationCoords = Pair(31.3260, 75.5762)
                            isLoadingLocation = false
                        }
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
                    onClick = { Toast.makeText(context, "Camera intent would go here", Toast.LENGTH_SHORT).show() },
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
                        delay(2000) // Fake submission delay
                        Toast.makeText(context, "Issue reported successfully!", Toast.LENGTH_LONG).show()
                        onNavigateToMyComplaints()
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
