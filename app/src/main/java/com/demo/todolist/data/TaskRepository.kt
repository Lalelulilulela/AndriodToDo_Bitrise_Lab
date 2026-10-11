package com.demo.todolist.data

class TaskRepository(
    private val store: TaskStore,
) {
    private val tasks = store.load().toMutableList()

    fun getTasks(): List<Task> = tasks.toList()

    fun addTask(title: String): Task {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "Task title cannot be blank" }
        require(trimmed.length <= MAX_TITLE_LENGTH) {
            "Task title cannot exceed $MAX_TITLE_LENGTH characters"
        }
        val task = Task(title = trimmed)
        tasks.add(0, task)
        persist()
        return task
    }

    fun setCompleted(id: String, completed: Boolean): Task? {
        val index = tasks.indexOfFirst { it.id == id }
        if (index < 0) return null
        val updated = tasks[index].copy(isCompleted = completed)
        tasks[index] = updated
        persist()
        return updated
    }

    fun deleteTask(id: String): Boolean {
        val removed = tasks.removeAll { it.id == id }
        if (removed) persist()
        return removed
    }

    private fun persist() {
        store.save(tasks.toList())
    }

    companion object {
        const val MAX_TITLE_LENGTH = 200
    }
}
