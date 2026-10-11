package com.demo.todolist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.demo.todolist.data.Task
import com.demo.todolist.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodoViewModel(
    private val repository: TaskRepository,
) : ViewModel() {
    private val _tasks = MutableStateFlow(repository.getTasks())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun addTask(title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        repository.addTask(trimmed.take(TaskRepository.MAX_TITLE_LENGTH))
        refresh()
    }

    fun setCompleted(id: String, completed: Boolean) {
        repository.setCompleted(id, completed)
        refresh()
    }

    fun deleteTask(id: String) {
        repository.deleteTask(id)
        refresh()
    }

    private fun refresh() {
        _tasks.value = repository.getTasks()
    }

    companion object {
        fun factory(repository: TaskRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TodoViewModel(repository)
            }
        }
    }
}
