package com.demo.todolist.ui

import com.demo.todolist.data.MemoryTaskStore
import com.demo.todolist.data.TaskRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoViewModelTest {
    @Test
    fun `blank titles are ignored`() {
        val viewModel = TodoViewModel(TaskRepository(MemoryTaskStore()))

        viewModel.addTask("   ")

        assertTrue(viewModel.tasks.value.isEmpty())
    }

    @Test
    fun `add complete and delete update the task list`() {
        val viewModel = TodoViewModel(TaskRepository(MemoryTaskStore()))

        viewModel.addTask("  Write tests  ")
        val added = viewModel.tasks.value.single()
        assertEquals("Write tests", added.title)

        viewModel.setCompleted(added.id, true)
        assertTrue(viewModel.tasks.value.single().isCompleted)

        viewModel.deleteTask(added.id)
        assertTrue(viewModel.tasks.value.isEmpty())
    }

    @Test
    fun `oversized titles are shortened before they are saved`() {
        val viewModel = TodoViewModel(TaskRepository(MemoryTaskStore()))
        val oversized = "b".repeat(TaskRepository.MAX_TITLE_LENGTH + 25)

        viewModel.addTask(oversized)

        assertEquals(TaskRepository.MAX_TITLE_LENGTH, viewModel.tasks.value.single().title.length)
    }

    @Test
    fun `existing tasks are loaded when the view model is created`() {
        val store = MemoryTaskStore()
        TaskRepository(store).addTask("Already there")

        val viewModel = TodoViewModel(TaskRepository(store))

        assertEquals(listOf("Already there"), viewModel.tasks.value.map { it.title })
    }
}
