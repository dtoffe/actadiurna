package com.github.dtoffe.actadiurna.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.dtoffe.actadiurna.R
import com.github.dtoffe.actadiurna.model.SortBy
import com.github.dtoffe.actadiurna.model.StatusFilter
import com.github.dtoffe.actadiurna.ui.components.EditTaskDialog
import com.github.dtoffe.actadiurna.ui.components.TaskControlBar
import com.github.dtoffe.actadiurna.ui.components.TaskItemCard
import com.github.dtoffe.actadiurna.ui.theme.TodoIcons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Star

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TodoViewModel,
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when (currentScreen) {
        Screen.MAIN -> TodoListScreen(viewModel)
        Screen.DONE -> DoneScreen(viewModel, onBack = { viewModel.currentScreen.value = Screen.MAIN })
        Screen.SETTINGS -> SettingsScreen(onBack = { viewModel.currentScreen.value = viewModel.previousScreen.value })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val items by viewModel.filteredItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedContext by viewModel.selectedContext.collectAsState()
    val selectedProject by viewModel.selectedProject.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    val allContexts by viewModel.allContexts.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val dueTodayCount by viewModel.dueTodayCount.collectAsState()
    val overdueCount by viewModel.overdueCount.collectAsState()

    val editingTask by viewModel.editingTask.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val showArchiveConfirmation by viewModel.showArchiveConfirmation.collectAsState()
    var showClearTasksConfirmation by remember { mutableStateOf(false) }
    var showImportConfirmation by remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(false) }

    // File Import Launcher
    val fileImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importFromUri(it) }
    }

    // Snackbar listener
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.today_tasks_count, dueTodayCount),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (overdueCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.overdue_tasks_count, overdueCount),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                actions = {
                    // Options Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("overflow_menu_button")
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.share_todo_file)) },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    val shareIntent = viewModel.createShareIntent()
                                    context.startActivity(shareIntent)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.archive_completed_tasks)) },
                                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.showArchiveConfirmation.value = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.open_done_file)) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.currentScreen.value = Screen.DONE
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.import_todo_file)) },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    showImportConfirmation = true
                                },
                                modifier = Modifier.testTag("import_file_button"),
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.error,
                                    leadingIconColor = MaterialTheme.colorScheme.error
                                )
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.clear_all_tasks)) },
                                leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    showClearTasksConfirmation = true
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.error,
                                    leadingIconColor = MaterialTheme.colorScheme.error
                                )
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.settings_title)) },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.previousScreen.value = Screen.MAIN
                                    viewModel.currentScreen.value = Screen.SETTINGS
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Search Bar & Sort Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Search Box Area (~64dp height total)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("search_bar_input"),
                            interactionSource = interactionSource,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier
                                        .background(
                                            color = Color.Transparent,
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                stringResource(R.string.search_tasks_hint),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        innerTextField()
                                    }
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.searchQuery.value = "" },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = stringResource(R.string.clear_search),
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    // Sort Buttons Area (~64dp height total)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sortOptions = listOf(
                            SortBy.ALPHABETICAL to TodoIcons.SortAlpha,
                            SortBy.PRIORITY to Icons.Default.Star,
                            SortBy.PROJECT to TodoIcons.Project,
                            SortBy.CONTEXT to TodoIcons.Context
                        )

                        sortOptions.forEach { (option, icon) ->
                            val isSelected = sortBy == option
                            FilterChip(
                                modifier = Modifier.weight(1f),
                                selected = isSelected,
                                onClick = { viewModel.sortBy.value = option },
                                label = {
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = stringResource(option.labelRes),
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                                   else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // 2. Task List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (items.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty() || (selectedContext != null) || (selectedProject != null))
                                stringResource(R.string.no_tasks_match_filter)
                            else
                                stringResource(R.string.no_tasks_found),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.create_task_instruction),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items, key = { it.id }) { item ->
                            TaskItemCard(
                                item = item,
                                onToggleCompletion = { viewModel.toggleCompletion(item) },
                                sortBy = sortBy,
                                isSelected = selectedTask?.id == item.id,
                                onSelect = {
                                    viewModel.selectedTask.value = if (selectedTask?.id == item.id) null else item
                                }
                            )
                        }
                    }
                }
            }

            // 3. Task Control Bar at bottom
            TaskControlBar(
                selectedTask = selectedTask,
                availableContexts = allContexts,
                availableProjects = allProjects,
                onAddClick = {
                    // Open dialog with an empty template
                    viewModel.editingTask.value = com.github.dtoffe.actadiurna.model.TodoItem(
                        id = -1,
                        rawLine = ""
                    )
                },
                onEditClick = { task ->
                    viewModel.editingTask.value = task
                },
                onDeleteClick = { task ->
                    viewModel.deleteTask(task)
                },
                onPriorityChange = { task, pri ->
                    viewModel.updatePriority(task, pri)
                },
                onContextToggle = { task, ctx ->
                    viewModel.toggleContext(task, ctx)
                },
                onProjectToggle = { task, prj ->
                    viewModel.toggleProject(task, prj)
                },
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }

        // Edit Task Modal Dialog
        editingTask?.let { task ->
            EditTaskDialog(
                item = task,
                availableContexts = allContexts,
                availableProjects = allProjects,
                onDismiss = { viewModel.editingTask.value = null },
                onConfirm = { newRawLine ->
                    if (task.id == -1) {
                        viewModel.addRawTask(newRawLine)
                    } else {
                        viewModel.updateTaskText(task, newRawLine)
                    }
                    viewModel.editingTask.value = null
                }
            )
        }

        // Archive Confirmation Dialog
        if (showArchiveConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.showArchiveConfirmation.value = false },
                title = { Text(stringResource(R.string.archive_tasks_dialog_title)) },
                text = { Text(stringResource(R.string.archive_tasks_dialog_text)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.showArchiveConfirmation.value = false
                            viewModel.archiveCompleted()
                        }
                    ) {
                        Text(stringResource(R.string.archive_confirm_button), color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showArchiveConfirmation.value = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }

        // Clear All Tasks Confirmation Dialog
        if (showClearTasksConfirmation) {
            AlertDialog(
                onDismissRequest = { showClearTasksConfirmation = false },
                title = { Text(stringResource(R.string.clear_tasks_dialog_title)) },
                text = { Text(stringResource(R.string.clear_tasks_dialog_text)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearTasksConfirmation = false
                            viewModel.clearAllTasks()
                        }
                    ) {
                        Text(stringResource(R.string.clear_confirm_button), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearTasksConfirmation = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }

        // Import Confirmation Dialog
        if (showImportConfirmation) {
            AlertDialog(
                onDismissRequest = { showImportConfirmation = false },
                title = { Text(stringResource(R.string.import_todo_dialog_title)) },
                text = { Text(stringResource(R.string.import_todo_dialog_text)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showImportConfirmation = false
                            fileImportLauncher.launch("text/*")
                        }
                    ) {
                        Text(stringResource(R.string.import_confirm_button), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportConfirmation = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }
}
