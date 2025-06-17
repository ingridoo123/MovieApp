package com.example.movieapp.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieapp.presentation.screens.search.tmdbLanguageToCountryMap
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
import com.example.movieapp.ui.theme.top_bar_component
import com.example.movieapp.ui.theme.whiteCopy
import com.example.movieapp.util.Constants.netflixFamily
import java.util.Calendar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CountryPickerDialog(
    currentSelectedCountry: String?,
    onCountrySelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {

    val countriesWithAll = listOf(null to "All Countries") + tmdbLanguageToCountryMap.toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Country", color = Color.White) },
        backgroundColor = background, // Use your app's theme
        contentColor = Color.White,
        buttons = {

        },
        text = {
            Column {
                TextButton(onClick = {
                    onCountrySelected(null)
                    onDismiss()
                }) {
                    Text(
                        "All Countries",
                        color = if (currentSelectedCountry == null) component else componentLighter
                    )
                }
                Divider(color = componentLighter.copy(alpha = 0.5f))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(tmdbLanguageToCountryMap.entries.toList()) { (code, name) ->
                        ListItem(
                            text = {
                                Text(
                                    name,
                                    color = if (code == currentSelectedCountry) Color.White.copy(0.8f) else componentLighter
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    onCountrySelected(code)
                                    onDismiss()
                                }
                                .background(if (code == currentSelectedCountry) component.copy(alpha = 0.5f) else Color.Transparent)
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun YearPicker(
    currentSelectedYear: Int?,
    onYearSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val years = (currentYear + 2 downTo currentYear - 100).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Year",
                fontFamily = netflixFamily,
                color = Color.White.copy(0.8f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        background,
                        top_bar_component
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                componentLighter.copy(0.3f),
                RoundedCornerShape(16.dp)
            ),
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        text = {
            Column(
                modifier = Modifier.height(300.dp).padding(top = 5.dp) // Ograniczamy wysokość
            ) {
                // "All Years" option z lepszym stylem
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onYearSelected(null)
                            onDismiss()
                        }
                        .padding(vertical = 8.dp),
                    backgroundColor = if (currentSelectedYear == null)
                        component else componentLighter.copy(0.1f),
                    elevation = if (currentSelectedYear == null) 4.dp else 0.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = if (currentSelectedYear == null)
                                whiteCopy else componentLighter,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "All Years",
                            color = if (currentSelectedYear == null)
                                whiteCopy else componentLighter,
                            fontSize = 16.sp,
                            fontWeight = if (currentSelectedYear == null)
                                FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lista lat z lepszym stylem
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(years) { year ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onYearSelected(year)
                                    onDismiss()
                                }
                                .animateContentSize(),
                            backgroundColor = if (year == currentSelectedYear)
                                component else componentLighter.copy(0.1f),
                            elevation = if (year == currentSelectedYear) 4.dp else 0.dp,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = year.toString(),
                                    color = if (year == currentSelectedYear)
                                        whiteCopy else componentLighter,
                                    fontSize = 16.sp,
                                    fontWeight = if (year == currentSelectedYear)
                                        FontWeight.Medium else FontWeight.Normal
                                )

                                if (year == currentSelectedYear) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = whiteCopy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
//            TextButton(
//                onClick = onDismiss,
//                modifier = Modifier
//                    .background(
//                        component.copy(0.8f),
//                        RoundedCornerShape(8.dp)
//                    )
//                    .padding(horizontal = 8.dp)
//            ) {
//                Text(
//                    "Close",
//                    color = Color.White,
//                    fontWeight = FontWeight.Medium
//                )
//            }
        }
    )
}

@Composable
fun CircleLoader(
    modifier: Modifier,
    isVisible: Boolean = true,
    color: Color,
    secondColor: Color? = color,
    tailLength: Float = 140f,
    smoothTransition: Boolean = true,
    strokeStyle: StrokeStyle = StrokeStyle(),
    cycleDuration: Int = 1400,
) {
    val tailToDisplay = remember { Animatable(0f) }

    LaunchedEffect(isVisible) {
        val targetTail = if (isVisible) tailLength else 0f
        when {
            smoothTransition -> tailToDisplay.animateTo(
                targetValue = targetTail,
                animationSpec = tween(cycleDuration, easing = LinearEasing)
            )
            else -> tailToDisplay.snapTo(targetTail)
        }
    }

    val transition = rememberInfiniteTransition()
    val spinAngel by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = cycleDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(
        modifier
            .rotate(spinAngel)
            .aspectRatio(1f)
    ) {
        listOfNotNull(color, secondColor).forEachIndexed { index, color ->
            rotate(if (index == 0) 0f else 180f) {
                val brush = Brush.sweepGradient(
                    0f to Color.Transparent,
                    tailToDisplay.value / 360f to color,
                    1f to Color.Transparent
                )
                val paint = setupPaint(strokeStyle, brush)

                drawIntoCanvas { canvas ->
                    canvas.drawArc(
                        rect = size.toRect(),
                        startAngle = 0f,
                        sweepAngle = tailToDisplay.value,
                        useCenter = false,
                        paint = paint
                    )
                }
            }
        }
    }
}

fun DrawScope.setupPaint(style: StrokeStyle, brush: Brush): Paint {
    val paint = Paint().apply paint@{
        this@paint.isAntiAlias = true
        this@paint.style = PaintingStyle.Stroke
        this@paint.strokeWidth = style.width.toPx()
        this@paint.strokeCap = style.strokeCap

        brush.applyTo(size, this@paint, 1f)
    }

    style.glowRadius?.let { radius ->
        paint.asFrameworkPaint().setShadowLayer(
            /* radius = */ radius.toPx(),
            /* dx = */ 0f,
            /* dy = */ 0f,
            /* shadowColor = */ android.graphics.Color.WHITE
        )
    }

    return paint
}

data class StrokeStyle(
    val width: Dp = 4.dp,
    val strokeCap: StrokeCap = StrokeCap.Round,
    val glowRadius: Dp? = 4.dp
)