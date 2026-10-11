package com.demo.todolist.data

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Saves the task list as JSON in the app's private files directory.
 */
class FileTaskStore(
    private val file: File,
) : TaskStore {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun load(): List<Task> {
        if (!file.exists() || file.length() == 0L) return emptyList()
        return try {
            json.decodeFromString<List<Task>>(file.readText())
        } catch (_: SerializationException) {
            emptyList()
        } catch (_: IllegalArgumentException) {
            emptyList()
        }
    }

    override fun save(tasks: List<Task>) {
        val parent = file.parentFile ?: error("Task file has no parent directory")
        parent.mkdirs()
        val temp = File(parent, "${file.name}.tmp")
        temp.writeText(json.encodeToString(tasks))
        Files.move(
            temp.toPath(),
            file.toPath(),
            StandardCopyOption.REPLACE_EXISTING,
        )
    }
}
