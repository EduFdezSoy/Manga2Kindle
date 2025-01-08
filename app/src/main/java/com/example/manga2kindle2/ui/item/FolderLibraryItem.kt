package com.example.manga2kindle2.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.manga2kindle2.R

@Composable
fun FolderLibraryItem(folderName: String, chapterAmount: Int, isChecked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = {})
        Column(modifier = Modifier.weight(1f)) {
            Text(text = folderName, fontWeight = FontWeight.Bold)
            Text(
                text = if (chapterAmount > 0) stringResource(
                    R.string.chapter_amount,
                    chapterAmount
                ) else stringResource(R.string.no_chapters)
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_folder),
            contentDescription = stringResource(R.string.folder),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview
@Composable
fun FolderLibraryItemPreview() {
    FolderLibraryItem("Nosequé del slime", 3, true)
}