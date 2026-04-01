package com.example.parivartan.ui.intro

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.parivartan.R
import kotlinx.coroutines.launch

private val Teal = Color(0xFF0D9488)
private val Slate = Color(0xFF64748B)

private data class IntroSlide(
    val title: String,
    val description: String,
    val imageRes: Int,
)

private val introSlides = listOf(
    IntroSlide(
        title = "Welcome to Parivartan",
        description = "Report civic issues and help make your community better.",
        imageRes = R.drawable.icon,
    ),
    IntroSlide(
        title = "Report Issues Easily",
        description = "Take a photo, mark the location, and submit your complaint in seconds.",
        imageRes = R.drawable.icon,
    ),
    IntroSlide(
        title = "Track Progress",
        description = "Monitor the status of your reported issues and see them get resolved.",
        imageRes = R.drawable.icon,
    ),
)

/**
 * Compose version of the React Native IntroScreen.
 *
 * Contract:
 * - onSkip: user wants to skip intro (go to Signup)
 * - onGetStarted: pressed on last slide (go to Signup)
 */
@Composable
fun IntroScreen(
    onSkip: () -> Unit,
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { introSlides.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Skip button (top-right)
        Text(
            text = "Skip",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 50.dp, end = 20.dp)
                .clickable(onClick = onSkip),
            color = Teal,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 80.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                IntroSlidePage(slide = introSlides[page])
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Dots(
                pageCount = introSlides.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            val isLastPage = pagerState.currentPage == introSlides.lastIndex
            Button(
                onClick = {
                    if (!isLastPage) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onGetStarted()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(52.dp)
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Next",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun IntroSlidePage(slide: IntroSlide, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = slide.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 40.dp)
        )

        Text(
            text = slide.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Teal,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Text(
            text = slide.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Slate,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun Dots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val targetWidth = if (index == currentPage) 16.dp else 8.dp
            val width by animateDpAsState(targetValue = targetWidth, label = "dotWidth")
            val alpha = if (index == currentPage) 1f else 0.3f

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(Teal.copy(alpha = alpha))
            )

            if (index != pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
