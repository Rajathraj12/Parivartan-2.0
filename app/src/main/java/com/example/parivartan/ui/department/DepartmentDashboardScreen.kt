package com.example.parivartan.ui.department
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
private val Teal500 = Color(0xFF14B8A6)
private val Teal600 = Color(0xFF0D9488)
private val Slate50 = Color(0xFFF8FAFC)
private val Slate100 = Color(0xFFF1F5F9)
private val Slate200 = Color(0xFFE2E8F0)
private val Slate500 = Color(0xFF64748B)
private val Slate700 = Color(0xFF334155)
private val Slate800 = Color(0xFF1E293B)
private val StatusPending = Color(0xFFF59E0B)
private val StatusInProgress = Color(0xFF3B82F6)
private val StatusResolved = Color(0xFF10B981)
private val PriorityHigh = Color(0xFFEF4444)
private val PriorityMedium = Color(0xFFF59E0B)
private val PriorityLow = Color(0xFF10B981)
private data class Grievance(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val citizenName: String,
    val citizenPhone: String,
    var status: String,
    var priority: String,
    val location: String,
    var assignedTo: String? = null
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentDashboardScreen(
    onNavigateGrievances: (() -> Unit)? = null,
    departmentId: String? = null,
    onLogout: () -> Unit = {}
) {
    var grievances by remember {
        mutableStateOf(
            listOf(
                Grievance("2Ncm1GVQa6cHIH9Xr6aL", "Test", "App test", "14 Nov 2025, 11:38 am", "Rajath", "rajath@gmail.com", "Pending", "Medium", "Thapar University Area, Patiala"),
                Grievance("3ABcdEFGhIJKlMnOPQrs", "Broken Pipe", "Pipe leaking water", "15 Nov 2025, 09:12 am", "Aman", "aman@example.com", "In Progress", "High", "Model Town, Patiala"),
                Grievance("4xyZ0123LMnOPQRstuVW", "Pothole", "Huge pothole causing traffic issues", "16 Nov 2025, 02:45 pm", "Kiran", "kiran@example.com", "Resolved", "Low", "Civil Lines, Patiala")
            )
        )
    }
    var selectedGrievance by remember { mutableStateOf<Grievance?>(null) }
    // Filter & Sort States
    var statusFilter by remember { mutableStateOf("All Status") }
    var priorityFilter by remember { mutableStateOf("All Priorities") }
    var sortOrder by remember { mutableStateOf("Newest First") }
    val filteredGrievances = remember(grievances, statusFilter, priorityFilter, sortOrder) {
        var list = grievances
        if (statusFilter != "All Status") {
            list = list.filter { it.status == statusFilter }
        }
        if (priorityFilter != "All Priorities") {
            list = list.filter { "${it.priority} Priority" == priorityFilter || it.priority == priorityFilter }
        }
        if (sortOrder == "Oldest First") {
            list = list.reversed()
        }
        list
    }
    val departmentDisplayName = remember(departmentId) {
        val mapping = mapOf(
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
            "social-security" to "Social Security",
            "pollution-control" to "Pollution Control Board",
            "forest" to "Forest Department",
            "disaster-management" to "Disaster Management Authority"
        )
        mapping[departmentId ?: "pwd"] ?: "Department"
    }
    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(departmentDisplayName, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Welcome, Officer", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal600,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            // Filter & Sort Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text("Filter & Sort Grievances", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800)
                            Spacer(modifier = Modifier.height(8.dp))
                            Badge(text = "Showing ${filteredGrievances.size} of ${grievances.size} grievances", color = Slate500, backgroundColor = Slate100)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DropdownSelector(
                                label = "Status",
                                options = listOf("All Status", "Pending", "In Progress", "Resolved"),
                                selected = statusFilter,
                                onSelect = { statusFilter = it },
                                modifier = Modifier.weight(1f)
                            )
                            DropdownSelector(
                                label = "Priority",
                                options = listOf("All Priorities", "High", "Medium", "Low"),
                                selected = priorityFilter,
                                onSelect = { priorityFilter = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DropdownSelector(
                                label = "Order",
                                options = listOf("Newest First", "Oldest First"),
                                selected = sortOrder,
                                onSelect = { sortOrder = it },
                                modifier = Modifier.weight(1f)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(" ", fontSize = 12.sp) // Spacer
                                Button(
                                    onClick = {
                                        statusFilter = "All Status"
                                        priorityFilter = "All Priorities"
                                        sortOrder = "Newest First"
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)), // Better red
                                    shape = RoundedCornerShape(24.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Clear All Filters", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
            // Grievance List
            items(filteredGrievances) { grievance ->
                GrievanceCard(
                    grievance = grievance,
                    onUpdateStatus = { newStatus -> 
                        grievances = grievances.map {
                            if(it.id == grievance.id) it.copy(status = newStatus) else it    
                        }
                    },
                    onAssignStaff = { staff ->
                        grievances = grievances.map {
                            if(it.id == grievance.id) it.copy(assignedTo = staff) else it    
                        }
                    },
                    onViewDetails = { selectedGrievance = grievance }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
    selectedGrievance?.let { grievance ->
        GrievanceDetailDialog(
            grievance = grievance,
            onDismiss = { selectedGrievance = null },
            onUpdate = { newStatus, newPriority ->
                grievances = grievances.map {
                    if(it.id == grievance.id) it.copy(status = newStatus, priority = newPriority) else it
                }
                selectedGrievance = null
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(label, fontSize = 12.sp, color = Slate700, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFA5D6A7),
                    focusedBorderColor = Teal600
                ),
                shape = RoundedCornerShape(24.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Slate800),
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(opt, fontSize = 12.sp) }, onClick = { onSelect(opt); expanded = false })
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GrievanceCard(
    grievance: Grievance,
    onUpdateStatus: (String) -> Unit,
    onAssignStaff: (String) -> Unit,
    onViewDetails: () -> Unit
) {
    val priorityColor = when(grievance.priority) {
        "High" -> PriorityHigh
        "Medium" -> PriorityMedium
        else -> PriorityLow
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onViewDetails() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(priorityColor)
            )
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // ID Badge
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "#${grievance.id}",
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Badge(
                            text = grievance.priority.uppercase(),
                            color = Color.White,
                            backgroundColor = priorityColor
                        )
                        Badge(
                            text = grievance.status.uppercase(),
                            color = Color.White,
                            backgroundColor = when(grievance.status.lowercase()) {
                                "pending" -> StatusPending
                                "in progress" -> StatusInProgress
                                else -> StatusResolved
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Title & Description
                Text(grievance.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Spacer(modifier = Modifier.height(4.dp))
                Text(grievance.description, fontSize = 14.sp, color = Slate500, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(16.dp))
                // Details Block
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("SUBMITTED BY:", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
                            Text(grievance.citizenName, fontSize = 14.sp, color = Slate800, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                            Text("CONTACT:", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
                            Text(grievance.citizenPhone, fontSize = 14.sp, color = Slate800, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("LOCATION:", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
                            Text(grievance.location, fontSize = 14.sp, color = Slate800, textAlign = TextAlign.Start, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                            Text("DATE:", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
                            Text(grievance.date, fontSize = 14.sp, color = Slate800, textAlign = TextAlign.End, maxLines = 2)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Slate200)
                Spacer(modifier = Modifier.height(16.dp))
                // Action Buttons
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
                        // Status Action
                        var statusExpanded by remember { mutableStateOf(false) }
                        val currentStatusDisplay = if (grievance.status.length > 10) grievance.status.take(8) + ".." else grievance.status
                        Column(modifier = Modifier.weight(1f)) {
                            Text("UPDATE STATUS:", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                                OutlinedTextField(
                                    value = currentStatusDisplay,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedBorderColor = Teal600
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp)
                                )
                                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                                    listOf("Pending", "In Progress", "Resolved").forEach { option ->
                                        DropdownMenuItem(text = { Text(option, fontSize = 12.sp) }, onClick = { onUpdateStatus(option); statusExpanded = false })
                                    }
                                }
                            }
                        }
                        // Assign Staff Action
                        var staffExpanded by remember { mutableStateOf(false) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ASSIGN TO STAFF:", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                            ExposedDropdownMenuBox(expanded = staffExpanded, onExpandedChange = { staffExpanded = it }) {
                                OutlinedTextField(
                                    value = grievance.assignedTo?.let { if (it.length > 7) it.take(6) + ".." else it } ?: "Assign",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(staffExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFA5D6A7),
                                    focusedBorderColor = Teal600,
                                    unfocusedContainerColor = Color(0xFFF1F8E9)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp)
                                )
                                ExposedDropdownMenu(expanded = staffExpanded, onDismissRequest = { staffExpanded = false }) {
                                    listOf("Staff Member 1", "Staff Member 2", "Staff Member 3").forEach { option ->
                                        DropdownMenuItem(text = { Text(option, fontSize = 12.sp) }, onClick = { onAssignStaff(option); staffExpanded = false })
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        onClick = onViewDetails,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal600),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
private fun Badge(text: String, color: Color, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GrievanceDetailDialog(
    grievance: Grievance,
    onDismiss: () -> Unit,
    onUpdate: (status: String, priority: String) -> Unit
) {
    var status by remember { mutableStateOf(grievance.status) }
    var priority by remember { mutableStateOf(grievance.priority) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Grievance Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)
                Text(grievance.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Slate800)
                Text(grievance.description, style = MaterialTheme.typography.bodyMedium, color = Slate700, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
                Text("Citizen Info", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Slate500)
                Text("${grievance.citizenName} � ${grievance.citizenPhone}", style = MaterialTheme.typography.bodyMedium, color = Slate800)
                Text(grievance.location, style = MaterialTheme.typography.bodySmall, color = Slate500, modifier = Modifier.padding(bottom = 16.dp))
                Text("Update Status", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Slate500, modifier = Modifier.padding(bottom = 4.dp))
                // Note: Experimental API change in Jetpack Compose means FilterChip is commonly replacing this. Using OutlinedButton here for simplicity
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                    listOf("Pending", "In Progress", "Resolved").forEach { option ->
                        FilterChip(
                            selected = status == option,
                            onClick = { status = option },
                            label = { Text(option) },
                        )
                    }
                }
                Text("Update Priority", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Slate500, modifier = Modifier.padding(bottom = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Low").forEach { level ->
                        FilterChip(
                            selected = priority == level,
                            onClick = { priority = level },
                            label = { Text(level) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onUpdate(status, priority)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal600,
                        contentColor = Color.White
                    )
                ) {
                    Text("Update Grievance")
                }
            }
        }
    }
}
