package com.github.dtoffe.actadiurna.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.dtoffe.actadiurna.model.SortBy
import com.github.dtoffe.actadiurna.model.TodoItem
import com.github.dtoffe.actadiurna.model.TodoParser

@Composable
fun TaskItemCard(
    item: TodoItem,
    onToggleCompletion: () -> Unit,
    onUpdatePriority: (Char?) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    sortBy: SortBy,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }

    val isOverdue = !item.isCompleted && item.dueDate != null && item.dueDate!! < TodoParser.todayDateString()

    val highlightColor = remember(sortBy, item) {
        if (item.isCompleted) return@remember null
        
        val tag = when (sortBy) {
            SortBy.PROJECT -> item.projects.firstOrNull()
            SortBy.CONTEXT -> item.contexts.firstOrNull()
            else -> null
        } ?: return@remember null
        
        val colors = listOf(
            Color(0xFFE3F2FD), // Blue 50
            Color(0xFFF1F8E9), // Green 50
            Color(0xFFFFF3E0), // Orange 50
            Color(0xFFF3E5F5), // Purple 50
            Color(0xFFE0F2F1), // Teal 50
            Color(0xFFFFFDE7), // Yellow 50
            Color(0xFFFFEBEE), // Red 50
            Color(0xFFEFEBE9), // Brown 50
        )
        colors[Math.abs(tag.hashCode()) % colors.size]
    }

    val cardBg by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else if (highlightColor != null) {
            highlightColor
        } else if (item.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardBg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_item_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else if (item.isCompleted) 0.dp else 0.5.dp),
        shape = RoundedCornerShape(4.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) 
                 else if (highlightColor != null) androidx.compose.foundation.BorderStroke(1.dp, highlightColor.copy(alpha = 0.8f))
                 else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Compact Checkbox
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggleCompletion() },
                modifier = Modifier
                    .size(24.dp)
                    .testTag("checkbox_${item.id}"),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Main Content Flow (One big Text block)
            val annotatedString = buildTaskAnnotatedString(item, isOverdue)
            
            Box(modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable { onSelect() }
            ) {
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Minimal Menu Button
            Box {
                IconButton(
                    onClick = { showOptionsMenu = true },
                    modifier = Modifier.size(24.dp).testTag("task_menu_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Task options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { showOptionsMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Task") },
                        onClick = {
                            showOptionsMenu = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Set Priority...") },
                        onClick = {
                            showOptionsMenu = false
                            showPriorityMenu = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showOptionsMenu = false
                            onDelete()
                        }
                    )
                }
                
                DropdownMenu(
                    expanded = showPriorityMenu,
                    onDismissRequest = { showPriorityMenu = false }
                ) {
                    listOf('A', 'B', 'C', 'D').forEach { p ->
                        DropdownMenuItem(
                            text = { Text("Priority ($p)") },
                            onClick = {
                                onUpdatePriority(p)
                                showPriorityMenu = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Remove priority") },
                        onClick = {
                            onUpdatePriority(null)
                            showPriorityMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun buildTaskAnnotatedString(item: TodoItem, isOverdue: Boolean): AnnotatedString {
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error
    val onSurfaceColor = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    return buildAnnotatedString {
        val globalDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
        val globalAlpha = if (item.isCompleted) 0.5f else 1.0f

        // 1. Priority
        if (item.priority != null) {
            val priColor = when (item.priority) {
                'A' -> Color(0xFFD32F2F)
                'B' -> Color(0xFFF57C00)
                'C' -> Color(0xFF388E3C)
                'D' -> Color(0xFF1976D2)
                else -> tertiaryColor
            }
            withStyle(
                SpanStyle(
                    color = priColor.copy(alpha = globalAlpha),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textDecoration = globalDecoration
                )
            ) {
                append("(${item.priority}) ")
            }
        }

        // 2. Body Text and Tags
        val words = item.text.split(Regex("\\s+"))
        words.forEachIndexed { index, word ->
            val isTag = word.startsWith("@") || word.startsWith("+") || word.startsWith("due:")
            val cleanWord = word.trimEnd('.', ',', '!', '?', ';', ':')
            val suffix = word.substring(cleanWord.length)

            if (isTag) {
                val tagColor = when {
                    word.startsWith("@") -> secondaryColor
                    word.startsWith("+") -> tertiaryColor
                    word.startsWith("due:") -> errorColor
                    else -> onSurfaceColor
                }
                
                withStyle(
                    SpanStyle(
                        color = tagColor.copy(alpha = globalAlpha),
                        fontWeight = FontWeight.Medium,
                        textDecoration = globalDecoration
                    )
                ) {
                    append(cleanWord)
                }
                if (suffix.isNotEmpty()) {
                    withStyle(SpanStyle(color = onSurfaceColor.copy(alpha = globalAlpha), textDecoration = globalDecoration)) {
                        append(suffix)
                    }
                }
            } else {
                withStyle(
                    SpanStyle(
                        color = onSurfaceColor.copy(alpha = globalAlpha),
                        fontWeight = if (item.priority == 'A' && !item.isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                        textDecoration = globalDecoration
                    )
                ) {
                    append(word)
                }
            }
            
            if (index < words.size - 1) {
                append(" ")
            }
        }

        // 3. Metadata
        val metaText = buildString {
            if (item.creationDate != null) append(" 🗓").append(item.creationDate)
            if (item.completionDate != null) append(" ✅").append(item.completionDate)
        }
        if (metaText.isNotBlank()) {
            withStyle(
                SpanStyle(
                    color = onSurfaceVariantColor.copy(alpha = 0.4f * globalAlpha),
                    fontSize = 11.sp
                )
            ) {
                append(metaText)
            }
        }
    }
}
