package com.demo.todolist.data

class MemoryTaskStore(
    initial: List<Task> = emptyList(),
) : TaskStore {
    var tasks: List<Task> = initial
        private set

    override fun load(): List<Task> = tasks

    override fun save(tasks: List<Task>) {
        this.tasks = tasks
    }
}
