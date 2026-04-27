package aur.diploma.kmp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconArrowBack(
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.width * 0.1f
        val centerY = this.size.height / 2
        val arrowWidth = this.size.width * 0.6f

        // Arrow line
        drawLine(
            color = tint,
            start = Offset(this.size.width * 0.8f, centerY),
            end = Offset(this.size.width * 0.2f, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Arrow head
        val path = Path().apply {
            moveTo(this@Canvas.size.width * 0.35f, centerY - this@Canvas.size.height * 0.25f)
            lineTo(this@Canvas.size.width * 0.2f, centerY)
            lineTo(this@Canvas.size.width * 0.35f, centerY + this@Canvas.size.height * 0.25f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun IconCheck(
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.width * 0.12f
        val path = Path().apply {
            moveTo(this@Canvas.size.width * 0.2f, this@Canvas.size.height * 0.5f)
            lineTo(this@Canvas.size.width * 0.4f, this@Canvas.size.height * 0.7f)
            lineTo(this@Canvas.size.width * 0.8f, this@Canvas.size.height * 0.3f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun IconClose(
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.width * 0.12f
        val padding = this.size.width * 0.25f

        // X shape
        drawLine(
            color = tint,
            start = Offset(padding, padding),
            end = Offset(this.size.width - padding, this.size.height - padding),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(this.size.width - padding, padding),
            end = Offset(padding, this.size.height - padding),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun IconExitToApp(
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.width * 0.1f
        val centerY = this.size.height / 2

        // Arrow pointing right
        drawLine(
            color = tint,
            start = Offset(this.size.width * 0.3f, centerY),
            end = Offset(this.size.width * 0.75f, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Arrow head
        val path = Path().apply {
            moveTo(this@Canvas.size.width * 0.6f, centerY - this@Canvas.size.height * 0.2f)
            lineTo(this@Canvas.size.width * 0.75f, centerY)
            lineTo(this@Canvas.size.width * 0.6f, centerY + this@Canvas.size.height * 0.2f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Door frame
        drawLine(
            color = tint,
            start = Offset(this.size.width * 0.2f, this.size.height * 0.2f),
            end = Offset(this.size.width * 0.2f, this.size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
