package com.example.manga2kindle2

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.manga2kindle2.models.Chapter
import com.example.manga2kindle2.models.File
import com.example.manga2kindle2.ui.theme.Manga2KindleV3Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val FOLDER_URI = stringPreferencesKey("folder_uri")
val KINDLE_EMAIL = stringPreferencesKey("kindle_email")


class MainActivity : ComponentActivity() {
    private var selectedChapter: Chapter = Chapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Manga2KindleV3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    M2k_main_view(modifier = Modifier.padding(innerPadding), selectedChapter)
                }
            }
        }
    }
}

//#region ui

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M2k_main_view(modifier: Modifier = Modifier, selectedChapter: Chapter) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    var selectedFolderUri by remember { mutableStateOf<Uri?>(null) }
    val fileList = remember { mutableStateListOf<File>() }
    var folderName by remember { mutableStateOf("No folder selected") }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showUploadBottomSheet by remember { mutableStateOf(false) }
    var sheetSelectedUri: Uri by remember { mutableStateOf(Uri.EMPTY) }


    // get folder uri and email from data store if any
    LaunchedEffect(key1 = Unit) {
        context.dataStore.data.collect { settings ->
            val folderUri = settings[FOLDER_URI]
            if (folderUri != null) {
                selectedFolderUri = Uri.parse(folderUri)
                folderName = getFolderName(selectedFolderUri!!)
            }

            val kindleEmail = settings[KINDLE_EMAIL]
            if (kindleEmail != null) {
                selectedChapter.email = kindleEmail
            }
        }
    }

    // launcher for permissions and folder
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        val contentResolver = context.contentResolver
        contentResolver.takePersistableUriPermission(
            uri ?: return@rememberLauncherForActivityResult, Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        selectedFolderUri = uri
        folderName = getFolderName(uri)

        // save folder uri to
        runBlocking {
            context.dataStore.edit { settings ->
                settings[FOLDER_URI] = uri.toString()
            }
        }
    }

    LaunchedEffect(key1 = selectedFolderUri) {
        val cbzFiles = getCBZFilesFromUri(context, selectedFolderUri ?: return@LaunchedEffect)
        val contentResolver = context.contentResolver

        // Do something with the list of CBZ file URIs
        fileList.clear()
        fileList.addAll(getFileList(cbzFiles, contentResolver))
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            for (file in fileList) {
                item {
                    List_item(
                        Modifier.padding(8.dp),
                        file.name,
                        file.uri.toString(),
                        buttonAction = {
                            showUploadBottomSheet = true
                            sheetSelectedUri = file.uri
                            Log.i("M2K", "M2k_main_view: ${file.uri}")
                        },
                    )
                }
                item {
                    HorizontalDivider(
                        thickness = 1.dp,
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(250.dp)) }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            Text(
                text = folderName,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

        // card when list is empty
        if (fileList.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "There is nothing here",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val helpString = buildAnnotatedString {
                        append("Select a folder with CBZ files by \nclicking the ")
                        pushStringAnnotation(
                            tag = "plus",
                            annotation = "plus",
                        )
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("+")
                        }
                        pop()
                        append(" button below.\n\n")
                        append("if you need need more help check ")
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = "https://m2k.wf/help",
                        )
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("m2k.wf/help")
                        }
                        pop()
                    }
                    ClickableText(
                        text = helpString,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            textAlign = TextAlign.Center

                        ),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { offset ->
                            helpString.getStringAnnotations(
                                tag = "URL",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let { annotation ->
                                val intent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(16.dp), launcher = launcher
        )
    }
    Box(modifier = modifier.fillMaxSize()) {
        if (showUploadBottomSheet) {
            Upload_sheet(
                uri = sheetSelectedUri,
                sheetState = sheetState,
                onDismissRequest = { showUploadBottomSheet = false },
                selectedChapter = selectedChapter
            )
        }
    }
}

@Composable
fun FloatingActionButton(
    modifier: Modifier = Modifier, launcher: ManagedActivityResultLauncher<Uri?, Uri?>
) {
    FloatingActionButton(
        onClick = { launcher.launch(null) }, modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add, contentDescription = "Add Folder"
        )
    }
}

@Composable
fun List_item(
    modifier: Modifier = Modifier,
    title: String = "Title",
    description: String = "Description",
    buttonAction: () -> Unit = { /*TODO*/ },
    buttonDescription: String = "action"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        Column(
            modifier = Modifier.weight(8f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = getLastPartOfPath(description),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End

        ) {
            Button(onClick = buttonAction, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ), content = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = buttonDescription,
                    modifier = Modifier.rotate(270f),
                    tint = MaterialTheme.colorScheme.secondary
                )
            })
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun Upload_sheet(
    uri: Uri,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit = { /*TODO*/ },
    selectedChapter: Chapter
) {
    val context = LocalContext.current

    var showUploadingDialog by remember { mutableStateOf(false) }
    var mail by remember { mutableStateOf(selectedChapter.email ?: "") }

    selectedChapter.file = uri.toString()
    selectedChapter.manga = getFolderName(uri)
    selectedChapter.volume =
        getMangaVolume(getFileName(uri, LocalContext.current.contentResolver)).toIntOrNull()
    selectedChapter.chapter =
        getMangaChapter(getFileName(uri, LocalContext.current.contentResolver)).toFloatOrNull()
    selectedChapter.fileName = getFileName(uri, LocalContext.current.contentResolver)
    selectedChapter.title = getMangaChapterTitle(selectedChapter.fileName ?: "")

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        // Sheet content

        Column(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                style = MaterialTheme.typography.headlineLarge,
                text = "Upload",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = selectedChapter.manga ?: "",
                onValueChange = { /*TODO*/ },
                label = { Text("Manga") },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            )
            {
                OutlinedTextField(
                    value = selectedChapter.volume?.toString() ?: "",
                    onValueChange = { /*TODO*/ },
                    label = { Text("Vol.") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = selectedChapter.chapter?.toString() ?: "",
                    onValueChange = { /*TODO*/ },
                    label = { Text("Ch.") },
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = selectedChapter.title ?: "",
                onValueChange = { /*TODO*/ },
                label = { Text("Chapter Name") },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mail,
                placeholder = { Text("some_email@kindle.com") },
                onValueChange = {
                    mail = it
                    selectedChapter.email = it
                    runBlocking {
                        context.dataStore.edit { settings ->
                            settings[KINDLE_EMAIL] = it
                        }
                    }
                },
                label = { Text("Kindle Email") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = "Full name",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = getMangaFileTitle(uri, LocalContext.current.contentResolver),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "Send to",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedChapter.email ?: "No email set",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Close")
                }
                selectedChapter.contentResolver = LocalContext.current.contentResolver

                Button(onClick = {
                    showUploadingDialog = true
                    CoroutineScope(Dispatchers.IO).launch {
                        uploadFile(selectedChapter)
                        onDismissRequest()
                        showUploadingDialog = false
                    }
                }) {
                    Text("Upload")
                }
            }
        }
    }

    if (showUploadingDialog) {
        Dialog(
            onDismissRequest = { /* do nothing */ }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = "Uploading..."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(32.dp)
                            .width(64.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        modifier = Modifier
                            .padding(16.dp, 0.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium,
                        text = "You will receive your manga in\n" +
                                "~2 minute on your Kindle",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

//#endregion ui

//#region code

suspend fun getCBZFilesFromUri(context: Context, folderUri: Uri): List<Uri> {
    return withContext(Dispatchers.IO) {
        val cbzFiles = mutableListOf<Uri>()
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            folderUri, DocumentsContract.getTreeDocumentId(folderUri)
        )
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME
        )

        try {
            context.contentResolver.query(childrenUri, projection, null, null, null)
                ?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val fileName =
                            cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                        if (fileName.endsWith(".cbz")) {
                            val documentId =
                                cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID))
                            val fileUri =
                                DocumentsContract.buildDocumentUriUsingTree(folderUri, documentId)
                            cbzFiles.add(fileUri)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("M2K", "getCBZFilesFromUri: $e")
        }

        cbzFiles
    }
}

fun getFolderName(uri: Uri): String {
    return DocumentsContract.getTreeDocumentId(uri).substringAfterLast('/')

}

fun getLastPartOfPath(path: String): String {
    val length = 54
    var lastPart = "..."

    var pathFiltered = path.replace("%20", " ")
    pathFiltered = pathFiltered.replace("%2", "/")

    if (pathFiltered.length <= length)
        return pathFiltered

    lastPart += pathFiltered.substring(pathFiltered.length - length, pathFiltered.length)
    return lastPart
}

fun getFileList(cbzFiles: List<Uri>, contentResolver: ContentResolver): MutableList<File> {
    val fileList = mutableListOf<File>()

    for (uri in cbzFiles) {
        val fileName = getFileName(uri, contentResolver)
        fileList.add(File(uri = uri, name = fileName))
    }

    fileList.sortBy { it.name }

    return fileList
}

fun getFileName(uri: Uri, contentResolver: ContentResolver): String {
    var fileName = "Unknown"
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)

        // remove extension
        var fileNameWOExtension = fileName.substringBeforeLast('.')

        // replace " _ " with " - " if any
        fileNameWOExtension = fileNameWOExtension.replace(" _ ", " - ")

        // replace last _ with ? if any
        if (fileNameWOExtension.lastIndexOf('_') == fileNameWOExtension.length - 1) {
            fileNameWOExtension = fileNameWOExtension.replaceRange(
                fileNameWOExtension.length - 1, fileNameWOExtension.length, "?"
            )
        }

        // remove uploader/scanlator name
        fileName = fileNameWOExtension.substringAfterLast('_')
        fileName = fileName.ifEmpty {
            fileNameWOExtension
        }
    }

    return fileName
}

fun getMangaFileTitle(uri: Uri, contentResolver: ContentResolver): String {
    var title = getFolderName(uri)
    title += " - "
    title += getFileName(uri, contentResolver)

    return title
}

fun getMangaChapterTitle(fileName: String): String {
    val regex = Regex("(?:Vol\\.?\\s?\\d+\\s)?(?:Ch\\.|Chapter)\\s?\\d+(\\.\\d+)?\\s?-\\s(.*)")
    val matchResult = regex.find(fileName)
    val title = matchResult?.groups?.get(2)?.value ?: ""

    Log.i("M2K", "fileName: $fileName")
    Log.i("M2K", "title: $title")
    return title
}

fun getMangaChapter(fileName: String): String {
//    val regex = Regex("Ch\\.\\s?(\\d+(?:\\.\\d+)?)")
//    val regex = Regex("Ch\\.\\s?(\\d+(?:\\.\\d+)?)|Chapter\\s(\\d+)")
    val regex = Regex("(Ch\\.|Chapter|Capitulo|Capítulo)\\s?(\\d+(?:\\.\\d+)?)")
    val matchResult = regex.find(fileName)
    val chapter = matchResult?.groups?.get(1)?.value ?: matchResult?.groups?.get(2)?.value ?: ""

    Log.i("M2K", "fileName: $fileName")
    Log.i("M2K", "chapter: $chapter")
    return chapter
}

fun getMangaVolume(fileName: String): String {
    val regex = Regex("Vol\\.?\\s?(\\d+)")
    val matchResult = regex.find(fileName)
    return matchResult?.groups?.get(1)?.value ?: ""
}

suspend fun uploadFile(chapter: Chapter) {
    if (chapter.file.isNullOrBlank()) {
        Log.e("M2K", "uploadFile: uri is null")
        return
    }

    if (chapter.contentResolver == null) {
        Log.e("M2K", "uploadFile: contentResolver is null")
        return
    }

    val apiClient = ApiClient()
    val fileName = getFileName(chapter.getFile()!!, chapter.contentResolver!!)
    chapter.fileName = fileName
    val response = apiClient.createChapter(chapter)
    Log.i("M2K", "uploadFile: $response")
}

//#endregion code