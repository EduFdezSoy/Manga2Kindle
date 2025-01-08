package com.example.manga2kindle2.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LibraryScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "LibraryScreen", modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun LibraryScreenPreview(){
    LibraryScreen()
}
