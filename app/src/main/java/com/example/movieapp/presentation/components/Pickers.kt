package com.example.movieapp.presentation.components

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.movieapp.presentation.screens.search.tmdbLanguageToCountryMap
import com.example.movieapp.ui.theme.background
import com.example.movieapp.ui.theme.component
import com.example.movieapp.ui.theme.componentLighter
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
    val years = (currentYear downTo 1900).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title ={ Text(text = "Select Year", fontFamily = netflixFamily, color = Color.White.copy(0.8f))},
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background, shape = RoundedCornerShape(10.dp))
            .border(1.dp, componentLighter),
        backgroundColor = background,
        contentColor = Color.White.copy(0.8f),
        text =  {
                Column {
                    TextButton(onClick = {
                        onYearSelected(null)
                        onDismiss()
                    }) {
                        Text(
                            "All Years",
                            color = if (currentSelectedYear == null) Color.White.copy(0.8f) else componentLighter
                        )
                    }
                    Divider(color = componentLighter.copy(alpha = 0.5f))
                    LazyColumn(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(years) {year ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onYearSelected(year)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 12.dp )
                                    .background(if(year == currentSelectedYear) component.copy(0.5f) else Color.Transparent)
                            ) {
                                Text(
                                    text = year.toString(),
                                    color = if (year == currentSelectedYear) Color.White.copy(0.8f) else componentLighter
                                )
                            }

                        }

                    }
                }
        },
        confirmButton = { /*TODO*/ })
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