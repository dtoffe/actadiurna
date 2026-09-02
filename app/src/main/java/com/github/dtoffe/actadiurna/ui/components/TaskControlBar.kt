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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.dtoffe.actadiurna.model.TodoItem

@Composable
fun TaskControlBar(
    selectedTask: TodoItem?,
    availableContexts: List<String>,
    availableProjects: List<String>,
    onAddClick: () -> Unit,
    onEditClick: (TodoItem) -> Unit,
    onPriorityChange: (TodoItem, Char?) -> Unit,
    onContextToggle: (TodoItem, String) -> Unit,
    onProjectToggle: (TodoItem, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPriorityMenu by remember { mutableStateOf(value = false) }
    var showContextMenu by remember { mutableStateOf(value = false) }
    var showProjectMenu by remember { mutableStateOf(value = false) }

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
                modifier = Modifier.weight(1.2f),
                selected = false,
                onClick = {
                    if (selectedTask != null) onEditClick(selectedTask) else onAddClick()
                },
                label = {
                    Text(
                        text = if (selectedTask != null) "Edit" else "Add",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (selectedTask != null) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )

            // 2. Priority Button
            Box(modifier = Modifier.weight(1f)) {
                FilterChip(
                    selected = false,
                    onClick = { if (selectedTask != null) showPriorityMenu = true },
                    enabled = selectedTask != null,
                    label = { Text("Pri", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    shape = RoundedCornerShape(8.dp)
                )
                if (selectedTask != null) {
                    DropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = { showPriorityMenu = false }
                    ) {
                        listOf('A', 'B', 'C', 'D').forEach { p ->
                            DropdownMenuItem(
                                text = { Text("Priority ($p)") },
                                onClick = {
                                    onPriorityChange(selectedTask, p)
                                    showPriorityMenu = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Remove Priority") },
                            onClick = {
                                onPriorityChange(selectedTask, null)
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
                    onClick = { if (selectedTask != null) showProjectMenu = true },
                    enabled = selectedTask != null,
                    label = { Text("Prj", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    shape = RoundedCornerShape(8.dp)
                )
                if (selectedTask != null) {
                    DropdownMenu(
                        expanded = showProjectMenu,
                        onDismissRequest = { showProjectMenu = false }
                    ) {
                        if (availableProjects.isEmpty()) {
                            DropdownMenuItem(text = { Text("No projects defined") }, onClick = {}, enabled = false)
                        } else {
                            availableProjects.forEach { prj ->
                                val hasProject = selectedTask.projects.contains(prj)
                                DropdownMenuItem(
                                    text = { Text("+$prj") },
                                    leadingIcon = {
                                        if (hasProject) Icon(Icons.Default.Add, contentDescription = null)
                                    },
                                    onClick = {
                                        onProjectToggle(selectedTask, prj)
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
                    onClick = { if (selectedTask != null) showContextMenu = true },
                    enabled = selectedTask != null,
                    label = { Text("Ctx", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    shape = RoundedCornerShape(8.dp)
                )
                if (selectedTask != null) {
                    DropdownMenu(
                        expanded = showContextMenu,
                        onDismissRequest = { showContextMenu = false }
                    ) {
                        if (availableContexts.isEmpty()) {
                            DropdownMenuItem(text = { Text("No contexts defined") }, onClick = {}, enabled = false)
                        } else {
                            availableContexts.forEach { ctx ->
                                val hasContext = selectedTask.contexts.contains(ctx)
                                DropdownMenuItem(
                                    text = { Text("@$ctx") },
                                    leadingIcon = {
                                        if (hasContext) Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = null)
                                    },
                                    onClick = {
                                        onContextToggle(selectedTask, ctx)
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
