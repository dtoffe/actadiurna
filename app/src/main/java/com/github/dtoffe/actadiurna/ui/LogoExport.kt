package com.github.dtoffe.actadiurna.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.dtoffe.actadiurna.R

/**
 * Temporary utility to export the ic_logo.xml as a 512x512 PNG.
 */
@Preview(showBackground = false, widthDp = 512, heightDp = 512)
@Composable
fun ExportLogoPreview() {
    Box(modifier = Modifier.size(512.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}
