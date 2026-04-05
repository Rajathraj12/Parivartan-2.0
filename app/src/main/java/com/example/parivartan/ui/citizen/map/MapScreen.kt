package com.example.parivartan.ui.citizen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

data class MapIssue(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val location: String,
    val category: String,
    val date: String,
    val upvotes: Int,
    val latitude: Double,
    val longitude: Double
)

val mockMapIssues = listOf(
    MapIssue("1", "Broken Streetlight", "Streetlight on Main St is out.", "pending", "Main St", "Infrastructure", "2026-03-29", 15, 31.3300, 75.5844),
    MapIssue("2", "Pothole", "Large pothole on 5th Ave.", "in-progress", "5th Ave", "Roads", "2026-03-28", 22, 31.3320, 75.5810),
    MapIssue("3", "Trash Overflow", "Garbage bin overflowing.", "resolved", "Central Park", "Sanitation", "2026-03-25", 5, 31.3280, 75.5860)
)

data class MapFilters(
    val status: Set<String> = emptySet(),
    val category: Set<String> = emptySet(),
    val urgency: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToReport: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit
) {
    var issues by remember { mutableStateOf(mockMapIssues) }
    var filters by remember { mutableStateOf(MapFilters()) }
    var selectedIssue by remember { mutableStateOf<MapIssue?>(null) }
    var showFiltersModal by remember { mutableStateOf(false) }

    val filteredIssues = remember(issues, filters) {
        issues.filter { issue ->
            val matchStatus = filters.status.isEmpty() || filters.status.contains(issue.status)
            val matchCategory = filters.category.isEmpty() || filters.category.contains(issue.category)
            val matchUrgency = !filters.urgency || issue.upvotes > 10
            matchStatus && matchCategory && matchUrgency
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val jalandharPos = LatLng(31.3300, 75.5844)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(jalandharPos, 12f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)) // placeholder for Map background
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            filteredIssues.forEach { issue ->
                val issuePos = LatLng(issue.latitude, issue.longitude)
                Marker(
                    state = MarkerState(position = issuePos),
                    title = issue.title,
                    snippet = getStatusText(issue.status),
                    onClick = {
                        selectedIssue = issue
                        false
                    }
                )
            }
        }

        // Action Buttons overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Filter Button
            Surface(
                modifier = Modifier.shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                onClick = { showFiltersModal = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_sort_by_size),
                        contentDescription = "Filter",
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val hasFilter = filters.status.isNotEmpty() || filters.category.isNotEmpty() || filters.urgency
                    Text(
                        text = "Filters ${if (hasFilter) "ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¢" else ""}",
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }

            // Report Button
            Surface(
                modifier = Modifier.shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0D9488),
                onClick = onNavigateToReport
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.AddCircle, contentDescription = "Report", tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Report Issue", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }

        // Active Filter Chips List
        val activeChips = mutableListOf<FilterChipData>()
        filters.status.forEach { activeChips.add(FilterChipData.Status(it)) }
        filters.category.forEach { activeChips.add(FilterChipData.Category(it)) }
        if (filters.urgency) activeChips.add(FilterChipData.Urgency)

        if (activeChips.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeChips) { chip ->
                    Surface(
                        color = Color(0xFF0D9488),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            filters = when (chip) {
                                is FilterChipData.Status -> filters.copy(status = filters.status - chip.status)
                                is FilterChipData.Category -> filters.copy(category = filters.category - chip.category)
                                FilterChipData.Urgency -> filters.copy(urgency = false)
                            }
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (chip) {
                                    is FilterChipData.Status -> getStatusText(chip.status)
                                    is FilterChipData.Category -> chip.category
                                    FilterChipData.Urgency -> "Urgent Issues"
                                },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Outlined.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Issue Panel Overlay (Bottom)
        if (selectedIssue != null) {
            val issue = selectedIssue!!
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusColor = getStatusColor(issue.status)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(statusColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(getStatusText(issue.status), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                // simulate upvote locally
                                val updatedIssue = issue.copy(upvotes = issue.upvotes + 1)
                                issues = issues.map { if (it.id == issue.id) updatedIssue else it }
                                selectedIssue = updatedIssue
                            }
                        ) {
                            Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Upvotes", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${issue.upvotes}", color = Color(0xFF64748B), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(issue.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = "Location", tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(issue.location, fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(issue.description, fontSize = 14.sp, color = Color(0xFF334155))

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigateToIssueDetail(issue.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Details")
                    }
                }

                // Temporary Close Button overlaid on top right of issue panel
                IconButton(
                    onClick = { selectedIssue = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 0.dp, end = 0.dp).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                }
            }
        }
    }

    if (showFiltersModal) {
        ModalBottomSheet(
            onDismissRequest = { showFiltersModal = false },
            sheetState = bottomSheetState,
            containerColor = Color.White,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter Issues", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    IconButton(onClick = { coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion { showFiltersModal = false } }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color(0xFF1E293B))
                    }
                }

                Divider(color = Color(0xFFF1F5F9))

                // Filter content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Status Options
                    Text("Status", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 12.dp))
                    @Composable
                    fun FilterChipsRow(items: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items.forEach { item ->
                                val isSelected = selected.contains(item)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) Color(0xFF0D9488) else Color(0xFFF1F5F9),
                                    onClick = { onToggle(item) }
                                ) {
                                    Text(
                                        text = if(items.contains("pending") || items.contains("in-progress")) getStatusText(item) else item,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    FilterChipsRow(
                        listOf("pending", "in-progress", "resolved"),
                        filters.status,
                        onToggle = {
                            filters = if (filters.status.contains(it)) filters.copy(status = filters.status - it)
                            else filters.copy(status = filters.status + it)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Category Options
                    Text("Category", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 12.dp))
                    // Chunk into rows
                    val categoriesList = listOf("Roads", "Infrastructure", "Sanitation", "Vandalism", "Parks")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChipsRow(
                            categoriesList.take(3),
                            filters.category,
                            onToggle = {
                                filters = if (filters.category.contains(it)) filters.copy(category = filters.category - it)
                                else filters.copy(category = filters.category + it)
                            }
                        )
                        FilterChipsRow(
                            categoriesList.drop(3),
                            filters.category,
                            onToggle = {
                                filters = if (filters.category.contains(it)) filters.copy(category = filters.category - it)
                                else filters.copy(category = filters.category + it)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Urgency Filter
                    Text("Other", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), modifier = Modifier.padding(bottom = 12.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (filters.urgency) Color(0xFF0D9488) else Color(0xFFF1F5F9),
                        onClick = { filters = filters.copy(urgency = !filters.urgency) }
                    ) {
                        Text(
                            text = "Urgent Issues",
                            fontSize = 14.sp,
                            color = if (filters.urgency) Color.White else Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                // Footer Actions
                Divider(color = Color(0xFFF1F5F9))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { filters = MapFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset", color = Color(0xFF334155), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion { showFiltersModal = false } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

sealed class FilterChipData {
    data class Status(val status: String) : FilterChipData()
    data class Category(val category: String) : FilterChipData()
    object Urgency : FilterChipData()
}

private fun getStatusColor(status: String): Color {
    return when(status) {
        "pending" -> Color(0xFFEAB308) // amber-500
        "in-progress" -> Color(0xFF3B82F6) // blue-500
        "resolved" -> Color(0xFF10B981) // emerald-500
        else -> Color(0xFF94A3B8) // slate-400
    }
}

private fun getStatusText(status: String): String {
    return when(status) {
        "pending" -> "Pending"
        "in-progress" -> "In Progress"
        "resolved" -> "Resolved"
        else -> status
    }
}