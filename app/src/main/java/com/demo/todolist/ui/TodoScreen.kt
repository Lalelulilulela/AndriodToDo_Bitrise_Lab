package com.demo.todolist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.todolist.R
import com.demo.todolist.data.Task
import com.demo.todolist.data.TaskRepository
import com.demo.todolist.ui.theme.TodoTheme

private enum class TaskFilter {
    All,
    Active,
    Done,
}

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    TodoScreen(
        tasks = tasks,
        onAdd = viewModel::addTask,
        onToggle = viewModel::setCompleted,
        onDelete = viewModel::deleteTask,
    )
}

@Composable
fun TodoScreen(
    tasks: List<Task>,
    onAdd: (String) -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onDelete: (String) -> Unit,
) {
    var draft by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(TaskFilter.All) }
    val doneCount = tasks.count { it.isCompleted }
    val visibleTasks = when (filter) {
        TaskFilter.All -> tasks
        TaskFilter.Active -> tasks.filterNot { it.isCompleted }
        TaskFilter.Done -> tasks.filter { it.isCompleted }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.union(WindowInsets.ime)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(
                text = stringResource(R.string.screen_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = summaryText(tasks.size, doneCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { doneCount.toFloat() / tasks.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(99.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskFilter.entries.forEach { option ->
                    FilterChip(
                        selected = filter == option,
                        onClick = { filter = option },
                        label = { Text(filterLabel(option)) },
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (visibleTasks.isEmpty()) {
                EmptyTasks(filter = filter, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 12.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(visibleTasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { completed -> onToggle(task.id, completed) },
                            onDelete = { onDelete(task.id) },
                        )
                    }
                }
            }
        }

        AddTaskBar(
            draft = draft,
            onDraftChange = { draft = it.take(TaskRepository.MAX_TITLE_LENGTH) },
            onAdd = {
                val title = draft.trim()
                if (title.isNotEmpty()) {
                    onAdd(title)
                    draft = ""
                }
            },
        )
    }
}

@Composable
private fun summaryText(total: Int, done: Int): String {
    return when {
        total == 0 -> stringResource(R.string.summary_none)
        done == 0 -> stringResource(R.string.summary_open, total)
        else -> stringResource(R.string.summary_progress, done, total)
    }
}

@Composable
private fun filterLabel(filter: TaskFilter): String {
    return when (filter) {
        TaskFilter.All -> stringResource(R.string.filter_all)
        TaskFilter.Active -> stringResource(R.string.filter_active)
        TaskFilter.Done -> stringResource(R.string.filter_done)
    }
}

@Composable
private fun EmptyTasks(filter: TaskFilter, modifier: Modifier = Modifier) {
    val title = when (filter) {
        TaskFilter.All -> stringResource(R.string.empty_all_title)
        TaskFilter.Active -> stringResource(R.string.empty_active_title)
        TaskFilter.Done -> stringResource(R.string.empty_done_title)
    }
    val hint = when (filter) {
        TaskFilter.All -> stringResource(R.string.empty_all_hint)
        TaskFilter.Active -> stringResource(R.string.empty_active_hint)
        TaskFilter.Done -> stringResource(R.string.empty_done_hint)
    }
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .toggleable(
                        value = task.isCompleted,
                        role = Role.Checkbox,
                        onValueChange = onToggle,
                    )
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = null,
                    modifier = Modifier.clearAndSetSemantics {},
                )
                Text(
                    text = task.title,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete_task, task.title),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun AddTaskBar(
    draft: String,
    onDraftChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.add_task_placeholder)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
            )
            Button(
                onClick = onAdd,
                enabled = draft.isNotBlank(),
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(stringResource(R.string.add))
            }
        }
    }
}

@Preview(showBackground = true, name = "Tasks")
@Composable
private fun TodoScreenPreview() {
    TodoTheme {
        TodoScreen(
            tasks = listOf(
                Task(id = "1", title = "Buy oat milk", isCompleted = false, createdAtEpochMillis = 2),
                Task(id = "2", title = "Walk the dog", isCompleted = true, createdAtEpochMillis = 1),
            ),
            onAdd = {},
            onToggle = { _, _ -> },
            onDelete = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun EmptyTodoScreenPreview() {
    TodoTheme {
        TodoScreen(
            tasks = emptyList(),
            onAdd = {},
            onToggle = { _, _ -> },
            onDelete = {},
        )
    }
}
