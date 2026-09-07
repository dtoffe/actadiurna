package com.github.dtoffe.actadiurna.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.github.dtoffe.actadiurna.R
import com.github.dtoffe.actadiurna.model.TodoItem
import com.github.dtoffe.actadiurna.ui.theme.TodoIcons
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditTaskDialog(
    item: TodoItem,
    availableContexts: List<String>,
    availableProjects: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (newRawLine: String) -> Unit,
) {
    var rawTextState by remember(item) { mutableStateOf(item.toRawString()) }
    var showDatePicker by remember { mutableStateOf(value = false) }
    var showPriorityMenu by remember { mutableStateOf(value = false) }
    var showProjectMenu by remember { mutableStateOf(value = false) }
    var showContextMenu by remember { mutableStateOf(value = false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        val dateStr = sdf.format(Date(millis))
                        
                        // Add due:date to the text if not already there, or replace existing
                        val dueTag = "due:$dateStr"
                        rawTextState = if (rawTextState.contains("due:")) {
                            rawTextState.replace(Regex("""due:\d{4}-\d{2}-\d{2}"""), dueTag)
                        } else {
                            if (rawTextState.isBlank()) dueTag else "$rawTextState $dueTag"
                        }
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.date_picker_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (item.id == -1) R.string.add_task_title else R.string.edit_task_title)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.format_instruction),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = rawTextState,
                    onValueChange = { rawTextState = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_task_dialog_input"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                ) {
                    // Due Date Button
                    TextButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.testTag("due_date_picker_button")
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.due_date_content_desc), modifier = Modifier.size(20.dp))
                    }

                    // Priority Button
                    Box {
                        TextButton(onClick = { showPriorityMenu = true }) {
                            Icon(Icons.Default.Star, contentDescription = stringResource(R.string.priority_content_desc), modifier = Modifier.size(20.dp))
                        }
                        DropdownMenu(expanded = showPriorityMenu, onDismissRequest = { showPriorityMenu = false }) {
                            listOf('A', 'B', 'C', 'D').forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("($p)") },
                                    onClick = {
                                        // Replace or insert priority
                                        val priorityTag = "($p) "
                                        rawTextState = if (rawTextState.matches(Regex("""^\([A-Z]\)\s+.*"""))) {
                                            rawTextState.replaceFirst(Regex("""^\([A-Z]\)"""), "($p)")
                                        } else {
                                            "$priorityTag$rawTextState"
                                        }
                                        showPriorityMenu = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.priority_none)) },
                                onClick = {
                                    rawTextState = rawTextState.replaceFirst(Regex("""^\([A-Z]\)\s+"""), "")
                                    showPriorityMenu = false
                                }
                            )
                        }
                    }

                    // Project Button
                    Box {
                        TextButton(onClick = { showProjectMenu = true }) {
                            Icon(TodoIcons.Project, contentDescription = stringResource(R.string.project_content_desc), modifier = Modifier.size(20.dp))
                        }
                        DropdownMenu(expanded = showProjectMenu, onDismissRequest = { showProjectMenu = false }) {
                            if (availableProjects.isEmpty()) {
                                DropdownMenuItem(text = { Text(stringResource(R.string.no_projects)) }, onClick = {}, enabled = false)
                            } else {
                                availableProjects.forEach { prj ->
                                    DropdownMenuItem(
                                        text = { Text("+$prj") },
                                        onClick = {
                                            if (!rawTextState.contains("+$prj")) {
                                                rawTextState = if (rawTextState.isBlank()) "+$prj" else "$rawTextState +$prj"
                                            }
                                            showProjectMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Context Button
                    Box {
                        TextButton(onClick = { showContextMenu = true }) {
                            Icon(TodoIcons.Context, contentDescription = stringResource(R.string.context_content_desc), modifier = Modifier.size(20.dp))
                        }
                        DropdownMenu(expanded = showContextMenu, onDismissRequest = { showContextMenu = false }) {
                            if (availableContexts.isEmpty()) {
                                DropdownMenuItem(text = { Text(stringResource(R.string.no_contexts)) }, onClick = {}, enabled = false)
                            } else {
                                availableContexts.forEach { ctx ->
                                    DropdownMenuItem(
                                        text = { Text("@$ctx") },
                                        onClick = {
                                            if (!rawTextState.contains("@$ctx")) {
                                                rawTextState = if (rawTextState.isBlank()) "@$ctx" else "$rawTextState @$ctx"
                                            }
                                            showContextMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (rawTextState.isNotBlank()) {
                        onConfirm(rawTextState)
                    }
                },
                modifier = Modifier.testTag("edit_task_confirm_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save_content_desc), tint = Color(0xFF388E3C))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.cancel_content_desc), tint = MaterialTheme.colorScheme.error)
            }
        }
    )
}
