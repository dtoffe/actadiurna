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
        // Bolder 'A'
        moveTo(3f, 10f)
        lineTo(6f, 3f)
        lineTo(9f, 10f)
        lineTo(7.5f, 10f)
        lineTo(6.8f, 8.5f)
        lineTo(5.2f, 8.5f)
        lineTo(4.5f, 10f)
        close()
        moveTo(6f, 5.5f)
        lineTo(5.6f, 7.2f)
        lineTo(6.4f, 7.2f)
        close()

        // Bolder 'Z'
        moveTo(3f, 13f)
        lineTo(9f, 13f)
        lineTo(9f, 14.5f)
        lineTo(5.5f, 18.5f)
        lineTo(9f, 18.5f)
        lineTo(9f, 20f)
        lineTo(3f, 20f)
        lineTo(3f, 18.5f)
        lineTo(6.5f, 14.5f)
        lineTo(3f, 14.5f)
        close()

        // Bold Arrow
        moveTo(14f, 5f)
        lineTo(16f, 5f)
        lineTo(16f, 16f)
        lineTo(20f, 16f)
        lineTo(15f, 21f)
        lineTo(10f, 16f)
        lineTo(14f, 16f)
        close()
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

    val AddBold: ImageVector = ImageVector.Builder(
        name = "AddBold",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(fill = SolidColor(Color.Black)) {
        moveTo(19f, 13.5f)
        lineTo(13.5f, 13.5f)
        lineTo(13.5f, 19f)
        lineTo(10.5f, 19f)
        lineTo(10.5f, 13.5f)
        lineTo(5f, 13.5f)
        lineTo(5f, 10.5f)
        lineTo(10.5f, 10.5f)
        lineTo(10.5f, 5f)
        lineTo(13.5f, 5f)
        lineTo(13.5f, 10.5f)
        lineTo(19f, 10.5f)
        close()
    }.build()
}
