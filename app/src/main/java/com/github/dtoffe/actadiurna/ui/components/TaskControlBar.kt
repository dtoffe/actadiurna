package com.github.dtoffe.actadiurna.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.dtoffe.actadiurna.R
import com.github.dtoffe.actadiurna.model.TodoItem
import com.github.dtoffe.actadiurna.ui.theme.TodoIcons

@Composable
fun TaskControlBar(
    selectedTasks: List<TodoItem>,
    availableContexts: List<String>,
    availableProjects: List<String>,
    onAddClick: () -> Unit,
    onEditClick: (TodoItem) -> Unit,
    onDeleteClick: (List<TodoItem>) -> Unit,
    onPriorityChange: (TodoItem, Char?) -> Unit,
    onContextToggle: (TodoItem, String) -> Unit,
    onProjectToggle: (TodoItem, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPriorityMenu by remember { mutableStateOf(value = false) }
    var showContextMenu by remember { mutableStateOf(value = false) }
    var showProjectMenu by remember { mutableStateOf(value = false) }

    val singleSelectedTask = if (selectedTasks.size == 1) selectedTasks.first() else null
    val hasSelection = selectedTasks.isNotEmpty()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Add / Edit Button
            FilterChip(
                modifier = Modifier.weight(if (hasSelection) 1f else 1.2f),
                selected = false,
                onClick = {
                    if (singleSelectedTask != null) onEditClick(singleSelectedTask) else onAddClick()
                },
                enabled = (!hasSelection) || (singleSelectedTask != null),
                label = {
                    Icon(
                        imageVector = if (hasSelection) Icons.Default.Edit else TodoIcons.AddBold,
                        contentDescription = stringResource(if (hasSelection) R.string.edit_content_desc else R.string.add_content_desc),
                        modifier = Modifier.fillMaxWidth(),
                        tint = if (hasSelection) {
                            if (singleSelectedTask != null) MaterialTheme.colorScheme.onSurfaceVariant 
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        } else Color(0xFF388E3C)
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )

            // 1b. Delete Button (only when selected)
            if (hasSelection) {
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = false,
                    onClick = { onDeleteClick(selectedTasks) },
                    label = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_content_desc),
                            modifier = Modifier.fillMaxWidth(),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // 2. Priority Button
            Box(modifier = Modifier.weight(1f)) {
                FilterChip(
                    selected = false,
                    onClick = { if (singleSelectedTask != null) showPriorityMenu = true },
                    enabled = singleSelectedTask != null,
                    label = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.priority_content_desc),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                if (singleSelectedTask != null) {
                    DropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = { showPriorityMenu = false }
                    ) {
                        listOf('A', 'B', 'C', 'D').forEach { p ->
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.priority_label, p)) },
                                onClick = {
                                    onPriorityChange(singleSelectedTask, p)
                                    showPriorityMenu = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.remove_priority_menu)) },
                            onClick = {
                                onPriorityChange(singleSelectedTask, null)
                                showPriorityMenu = false
                            }
                        )
                    }
                }
            }

            // 3. Project Button
            Box(modifier = Modifier.weight(1f)) {
                FilterChip(
                    selected = false,
                    onClick = { if (singleSelectedTask != null) showProjectMenu = true },
                    enabled = singleSelectedTask != null,
                    label = {
                        Icon(
                            imageVector = TodoIcons.Project,
                            contentDescription = stringResource(R.string.project_content_desc),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                if (singleSelectedTask != null) {
                    DropdownMenu(
                        expanded = showProjectMenu,
                        onDismissRequest = { showProjectMenu = false }
                    ) {
                        if (availableProjects.isEmpty()) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.no_projects_defined)) }, onClick = {}, enabled = false)
                        } else {
                            availableProjects.forEach { prj ->
                                val hasProject = singleSelectedTask.projects.contains(prj)
                                DropdownMenuItem(
                                    text = { Text("+$prj") },
                                    leadingIcon = {
                                        if (hasProject) Icon(Icons.Default.Add, contentDescription = null)
                                    },
                                    onClick = {
                                        onProjectToggle(singleSelectedTask, prj)
                                        showProjectMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Context Button
            Box(modifier = Modifier.weight(1f)) {
                FilterChip(
                    selected = false,
                    onClick = { if (singleSelectedTask != null) showContextMenu = true },
                    enabled = singleSelectedTask != null,
                    label = {
                        Icon(
                            imageVector = TodoIcons.Context,
                            contentDescription = stringResource(R.string.context_content_desc),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                if (singleSelectedTask != null) {
                    DropdownMenu(
                        expanded = showContextMenu,
                        onDismissRequest = { showContextMenu = false }
                    ) {
                        if (availableContexts.isEmpty()) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.no_contexts_defined)) }, onClick = {}, enabled = false)
                        } else {
                            availableContexts.forEach { ctx ->
                                val hasContext = singleSelectedTask.contexts.contains(ctx)
                                DropdownMenuItem(
                                    text = { Text("@$ctx") },
                                    leadingIcon = {
                                        if (hasContext) Icon(Icons.Default.Add, contentDescription = null)
                                    },
                                    onClick = {
                                        onContextToggle(singleSelectedTask, ctx)
                                        showContextMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
