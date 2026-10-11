package com.demo.todolist.data

/**
 * Local persistence for tasks. Implementations must not talk to a network
 * or a database server.
 */
interface TaskStore {
    fun load(): List<Task>

    fun save(tasks: List<Task>)
}
