package com.github.dtoffe.actadiurna.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.dtoffe.actadiurna.R

/**
 * Utility to export the Play Store Feature Graphic (1024x500).
 */
@Preview(widthDp = 1024, heightDp = 500, showBackground = false)
@Composable
fun ExportFeatureGraphicPreview() {
    // Colors from ic_logo.xml
    val backgroundColor = Color(0xFFF5F5DC) // Beige
    val textColor = Color(0xFF5D4037)       // Dark Brown
    
    Box(
        modifier = Modifier
            .size(width = 1024.dp, height = 500.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 64.dp)
        ) {
            // App Logo (Updated Scroll Design)
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(300.dp)
            )
            
            Spacer(modifier = Modifier.width(32.dp))
            
            // Text Branding
            Column {
                Text(
                    text = "Acta Diurna",
                    color = textColor,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Plan and build your day like a Roman architect",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}
