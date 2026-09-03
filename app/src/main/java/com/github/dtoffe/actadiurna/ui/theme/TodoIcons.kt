package com.github.dtoffe.actadiurna.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object TodoIcons {
    val SortAlpha: ImageVector = ImageVector.Builder(
        name = "SortAlpha",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(fill = SolidColor(Color.Black)) {
        // 'A'
        moveTo(3f, 7f)
        lineTo(5f, 3f)
        lineTo(7f, 7f)
        moveTo(4f, 6f)
        lineTo(6f, 6f)
        // 'Z'
        moveTo(3f, 13f)
        lineTo(7f, 13f)
        lineTo(3f, 17f)
        lineTo(7f, 17f)
        // Down Arrow
        moveTo(15f, 5f)
        lineTo(15f, 19f)
        moveTo(15f, 19f)
        lineTo(11f, 15f)
        moveTo(15f, 19f)
        lineTo(19f, 15f)
    }.build()

    val Project: ImageVector = ImageVector.Builder(
        name = "Project",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(fill = SolidColor(Color.Black)) {
        moveTo(10f, 4f)
        lineTo(4f, 4f)
        curveTo(2.9f, 4f, 2.01f, 4.9f, 2.01f, 6f)
        lineTo(2f, 18f)
        curveTo(2f, 19.1f, 2.9f, 20f, 4f, 20f)
        lineTo(20f, 20f)
        curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f)
        lineTo(22f, 8f)
        curveTo(22f, 6.9f, 21.1f, 6f, 20f, 6f)
        lineTo(12f, 6f)
        lineTo(10f, 4f)
        close()
    }.build()

    val Context: ImageVector = ImageVector.Builder(
        name = "Context",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(fill = SolidColor(Color.Black)) {
        moveTo(21.41f, 11.58f)
        lineTo(12.41f, 2.58f)
        curveTo(12.05f, 2.22f, 11.55f, 2f, 11f, 2f)
        lineTo(4f, 2f)
        curveTo(2.9f, 2f, 2f, 2.9f, 2f, 4f)
        lineTo(2f, 11f)
        curveTo(2f, 11.55f, 2.22f, 12.05f, 2.59f, 12.42f)
        lineTo(11.59f, 21.42f)
        curveTo(11.95f, 21.78f, 12.45f, 22f, 13f, 22f)
        curveTo(13.55f, 22f, 14.05f, 21.78f, 14.41f, 21.41f)
        lineTo(21.41f, 14.41f)
        curveTo(21.78f, 14.05f, 22f, 13.55f, 22f, 13f)
        curveTo(22f, 12.45f, 21.77f, 11.94f, 21.41f, 11.58f)
        close()
        moveTo(6.5f, 8f)
        curveTo(5.67f, 8f, 5f, 7.33f, 5f, 6.5f)
        curveTo(5f, 5.67f, 5.67f, 5f, 6.5f, 5f)
        curveTo(7.33f, 5f, 8f, 5.67f, 8f, 6.5f)
        curveTo(8f, 7.33f, 7.33f, 8f, 6.5f, 8f)
        close()
    }.build()
}
