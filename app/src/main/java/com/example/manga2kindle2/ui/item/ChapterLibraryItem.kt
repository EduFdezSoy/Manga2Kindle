package com.example.manga2kindle2.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.manga2kindle2.R

@Composable
fun ChapterLibraryItem(chapterName: String, isChecked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = {})
        Text(text = chapterName, modifier = Modifier.weight(1f))
        if (!isChecked) {
            Image(
                painter = painterResource(R.drawable.ic_upload),
                contentDescription = stringResource(R.string.upload),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview
@Composable
fun ChapterLibraryItemPreview() {
    ChapterLibraryItem("Nosequé del slime vol. 1 ch. 3", false)
}