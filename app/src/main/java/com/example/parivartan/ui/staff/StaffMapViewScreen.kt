package com.example.parivartan.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private data class MapIssueItem(
    val id: String,
    val title: String,
    val status: String,
    val locationAddress: String,
    val lat: Double,
    val lng: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMapViewScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatus by remember { mutableStateOf("all") }

    val mockIssues = remember {
        listOf(
            MapIssueItem("101", "Pothole on Main St", "pending", "Main St, City Center", 31.3300, 75.5844),
            MapIssueItem("102", "Street light not working", "in-progress", "Oak Avenue", 31.3350, 75.5880),
            MapIssueItem("103", "Garbage collection missed", "resolved", "Pine Street", 31.3250, 75.5800),
            MapIssueItem("104", "Water leakage", "pending", "Market Road", 31.3280, 75.5900)
        )
    }

    val filteredIssues = if (selectedStatus == "all") mockIssues else mockIssues.filter { it.status == selectedStatus }

    val getMarkerHue = { status: String ->
        // Convert compose color roughly to Hue for Google Maps marker
        when (status) {
            "pending" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE
            "acknowledged" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE
            "in-progress" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET
            "resolved" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
            "cannot-resolve" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            else -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN
        }
    }

    val statusFilters = listOf(
        Triple("all", "All", Icons.AutoMirrored.Filled.List),
        Triple("pending", "Pending", Icons.Default.Schedule),
        Triple("in-progress", "In Progress", Icons.Default.RocketLaunch),
        Triple("resolved", "Resolved", Icons.Default.CheckCircle)
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(31.3300, 75.5844), 13f)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Map View", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            // Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = com.google.maps.android.compose.MapUiSettings(zoomControlsEnabled = false)
            ) {
                filteredIssues.forEach { issue ->
                    Marker(
                        state = MarkerState(position = LatLng(issue.lat, issue.lng)),
                        title = issue.title,
                        snippet = "Tap to view details | Status: ${issue.status.replace("-", " ")}",
                        icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(getMarkerHue(issue.status)),
                        onInfoWindowClick = { onNavigateToIssueDetail(issue.id) }
                    )
                }
            }

            // Top Filter Container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFECFEFF)) // Cyan-50 equivalent but teal-tinted
                    .border(1.dp, Color(0xFF0D9488).copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                statusFilters.forEach { (value, label, icon) ->
                    val isActive = selectedStatus == value
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isActive) Color(0xFF0D9488) else Color.Transparent, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF0D9488), RoundedCornerShape(8.dp))
                            .clickable { selectedStatus = value }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = icon, contentDescription = label, tint = if (isActive) Color.White else Color(0xFF0D9488), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) Color.White else Color(0xFF0D9488),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Stats overlay
            Row(
                modifier = Modifier
                    .padding(top = 74.dp)
                    .align(Alignment.TopCenter)
                    .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Showing ${filteredIssues.size} of ${mockIssues.size} issues",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
            }

            // Legend at Bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .fillMaxWidth()
            ) {
                Text("Status Legend", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val legendItemsFirstRow = listOf(
                        "Pending" to Color(0xFFEAB308),
                        "Acknowledged" to Color(0xFF3B82F6)
                    )
                    val legendItemsSecondRow = listOf(
                        "In Progress" to Color(0xFF8B5CF6),
                        "Resolved" to Color(0xFF10B981)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        legendItemsFirstRow.forEach { (text, color) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text, fontSize = 11.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        legendItemsSecondRow.forEach { (text, color) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text, fontSize = 11.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
