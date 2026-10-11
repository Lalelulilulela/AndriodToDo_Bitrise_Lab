package com.demo.todolist.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskRepositoryTest {
    @Test
    fun `adding a task trims the title and shows newest first`() {
        val repository = TaskRepository(MemoryTaskStore())

        val first = repository.addTask("  Buy milk  ")
        val second = repository.addTask("Walk the dog")

        assertEquals("Buy milk", first.title)
        assertFalse(first.isCompleted)
        assertNotEquals(first.id, second.id)
        assertEquals(listOf("Walk the dog", "Buy milk"), repository.getTasks().map { it.title })
    }

    @Test
    fun `blank titles are rejected`() {
        val repository = TaskRepository(MemoryTaskStore())

        assertThrows(IllegalArgumentException::class.java) {
            repository.addTask("   ")
        }
        assertTrue(repository.getTasks().isEmpty())
    }

    @Test
    fun `titles longer than the limit are rejected`() {
        val repository = TaskRepository(MemoryTaskStore())
        val tooLong = "a".repeat(TaskRepository.MAX_TITLE_LENGTH + 1)

        assertThrows(IllegalArgumentException::class.java) {
            repository.addTask(tooLong)
        }
    }

    @Test
    fun `tasks can be marked completed and incomplete`() {
        val repository = TaskRepository(MemoryTaskStore())
        val task = repository.addTask("Write tests")

        val completed = repository.setCompleted(task.id, true)
        assertEquals(true, completed?.isCompleted)
        assertTrue(repository.getTasks().single().isCompleted)

        val reopened = repository.setCompleted(task.id, false)
        assertEquals(false, reopened?.isCompleted)
        assertNull(repository.setCompleted("missing", true))
    }

    @Test
    fun `deleting a task removes only that task`() {
        val repository = TaskRepository(MemoryTaskStore())
        val keep = repository.addTask("Keep")
        val remove = repository.addTask("Remove")

        assertTrue(repository.deleteTask(remove.id))
        assertFalse(repository.deleteTask(remove.id))
        assertEquals(listOf(keep.id), repository.getTasks().map { it.id })
    }

    @Test
    fun `changes survive a new repository using the same store`() {
        val store = MemoryTaskStore()
        val original = TaskRepository(store)
        val task = original.addTask("Ship the app")
        original.setCompleted(task.id, true)

        val reloaded = TaskRepository(store)
        val saved = reloaded.getTasks().single()
        assertEquals(task.id, saved.id)
        assertEquals("Ship the app", saved.title)
        assertTrue(saved.isCompleted)
    }
}
