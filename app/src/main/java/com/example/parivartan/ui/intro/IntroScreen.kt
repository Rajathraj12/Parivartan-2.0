package com.example.parivartan.ui.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import com.example.parivartan.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

private val GreenPrimary = Color(0xFF00A86B)
private val GreenPrimaryDark = Color(0xFF008955)
private val Slate = Color(0xFF64748B)
private val LightBg1 = Color(0xFFF0FDF4)
private val LightBg2 = Color(0xFFF8FAFC)
private val LightBg3 = Color(0xFFE0F2FE)

/**
 * Compose version of the React Native IntroScreen.
 *
 * Contract:
 * - onSkip: user wants to skip intro (go to Signup)
 * - onGetStarted: pressed on last slide (go to Signup)
 */
@Composable
fun IntroScreen(
    onSkip: () -> Unit, // keeping for signature compatibility
    onGetStarted: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showRoleSelector by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Map-style or App branding background
        Image(
            painter = painterResource(id = R.drawable.icon), // Better if you had a map_bg, using icon for now and blurring it
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
                .blur(radius = 16.dp)
        )
        
        AnimatedVisibility(
            visible = !showRoleSelector,
            enter = fadeIn(),
            exit = fadeOut() + slideOutVertically()
        ) {
            OnboardingFlow(onFinish = { showRoleSelector = true })
        }
        
        AnimatedVisibility(
            visible = showRoleSelector,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut()
        ) {
            RoleSelectorFlow(onRoleSelected = onGetStarted)
        }
    }
}

@Composable
fun OnboardingFlow(onFinish: () -> Unit) {
    val items = listOf(
        Triple("Report Issues", "Easily report infrastructure issues in your locality.", Icons.Default.Build),
        Triple("Real-time Tracking", "Track the progress of your complaints until resolution.", Icons.Default.Timeline),
        Triple("Community Driven", "Join others to make your city a better place.", Icons.Default.People)
    )
    
    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()
    
    // Auto advance or manual swipe, simple manual here for clarity
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = items[page].third,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(120.dp).padding(bottom = 32.dp)
                )
                Text(
                    text = items[page].first,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = items[page].second,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Slate,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
        
        // Dots indicator
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(items.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) GreenPrimary else Slate.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
        
        Button(
            onClick = {
                if (pagerState.currentPage < items.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinish()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Text(if (pagerState.currentPage < items.size - 1) "Next" else "Get Started", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RoleSelectorFlow(onRoleSelected: (String) -> Unit) {
    var selectedRole by remember { mutableStateOf<String?>(null) }

    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimations = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bg_wave")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    // Background gradient overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(LightBg1, LightBg2, LightBg3),
                    start = Offset(0f, gradientOffset),
                    end = Offset(1000f, 1000f - gradientOffset)
                )
            )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val logoScale by animateFloatAsState(
            targetValue = if (startAnimations) 1f else 0.3f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "logoScale"
        )
        val logoAlpha by animateFloatAsState(
            targetValue = if (startAnimations) 1f else 0f,
            animationSpec = tween(800),
            label = "logoAlpha"
        )

        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = "Logo",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp)
                .graphicsLayer {
                    scaleX = logoScale
                    scaleY = logoScale
                    alpha = logoAlpha
                    shadowElevation = 0f
                    shape = CircleShape
                    clip = true
                }
        )

        AnimatedVisibility(
            visible = startAnimations,
            enter = fadeIn(tween(1000, delayMillis = 300)) + slideInVertically(tween(1000, delayMillis = 300)) { 20 }
        ) {
            Text(
                text = "Transforming Cities Together",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        AnimatedVisibility(
            visible = startAnimations,
            enter = fadeIn(tween(1000, delayMillis = 500)) + slideInVertically(tween(1000, delayMillis = 500)) { 40 }
        ) {
            Text(
                text = "How do you want to improve your city today?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 32.dp, top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            val roles = listOf(
                Triple("citizen", "Citizen" to "Report issues in your area", Icons.Default.Person),
                Triple("department", "Department" to "Manage city operations", Icons.Default.Home),
                Triple("staff", "Staff" to "Complete assigned work", Icons.Default.Build),
                Triple("admin", "Admin" to "Control & monitor system", Icons.Default.Settings)
            )

            roles.forEachIndexed { index, role ->
                AnimatedVisibility(
                    visible = startAnimations,
                    enter = fadeIn(tween(500, delayMillis = 700 + (index * 150))) +
                            slideInVertically(tween(500, delayMillis = 700 + (index * 150))) { 50 }
                ) {
                    RoleCard(
                        title = role.second.first,
                        subtitle = role.second.second,
                        icon = role.third,
                        isSelected = selectedRole == role.first,
                        index = index,
                        onClick = { selectedRole = role.first }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pulse animation for button
        val pulseTransition = rememberInfiniteTransition(label = "pulse")
        val btnScale by pulseTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (selectedRole != null) 1.04f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "buttonPulse"
        )

        AnimatedVisibility(
            visible = startAnimations,
            enter = fadeIn(tween(1000, delayMillis = 1400))
        ) {
            Button(
                onClick = { selectedRole?.let { onRoleSelected(it) } },
                enabled = selectedRole != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer {
                        scaleX = btnScale
                        scaleY = btnScale
                    },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (selectedRole != null)
                                Brush.horizontalGradient(listOf(GreenPrimary, GreenPrimaryDark))
                            else
                                Brush.horizontalGradient(listOf(Slate.copy(alpha = 0.3f), Slate.copy(alpha = 0.3f))),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    index: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else if (isSelected) 1.02f else 1f, label = "scale")

    // Subtle floating effect based on index
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + (index * 200), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 0.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = if (!isSelected && !isPressed) floatOffset.dp else 0.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) GreenPrimary else Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) GreenPrimary else Slate,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Slate
                )
            }
        }
    }
}