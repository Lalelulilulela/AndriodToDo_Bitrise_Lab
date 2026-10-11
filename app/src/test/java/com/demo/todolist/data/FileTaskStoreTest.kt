package com.demo.todolist.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class FileTaskStoreTest {
    private lateinit var directory: File

    @Before
    fun setUp() {
        directory = createTempDirectory("tasks").toFile()
    }

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun `missing file loads as an empty list`() {
        val store = FileTaskStore(File(directory, "tasks.json"))

        assertTrue(store.load().isEmpty())
    }

    @Test
    fun `blank file loads as an empty list`() {
        val file = File(directory, "tasks.json")
        file.writeText("   ")
        val store = FileTaskStore(file)

        assertTrue(store.load().isEmpty())
    }

    @Test
    fun `corrupt file loads as an empty list`() {
        val file = File(directory, "tasks.json")
        file.writeText("{not json")
        val store = FileTaskStore(file)

        assertTrue(store.load().isEmpty())
    }

    @Test
    fun `save and load round trip including special characters`() {
        val file = File(directory, "tasks.json")
        val store = FileTaskStore(file)
        val tasks = listOf(
            Task(
                id = "task-1",
                title = "Call Sam \"tomorrow\" & pick up café",
                isCompleted = true,
                createdAtEpochMillis = 1_700_000_000_000,
            ),
            Task(
                id = "task-2",
                title = "行ってきます",
                isCompleted = false,
                createdAtEpochMillis = 1_700_000_000_100,
            ),
        )

        store.save(tasks)
        val loaded = FileTaskStore(file).load()

        assertEquals(tasks, loaded)
        assertTrue(file.readText().contains("café"))
    }
}
