package com.demo.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.demo.todolist.data.FileTaskStore
import com.demo.todolist.data.TaskRepository
import com.demo.todolist.ui.TodoScreen
import com.demo.todolist.ui.TodoViewModel
import com.demo.todolist.ui.theme.TodoTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val viewModel: TodoViewModel by viewModels {
        val store = FileTaskStore(File(filesDir, "tasks.json"))
        TodoViewModel.factory(TaskRepository(store))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                TodoScreen(viewModel)
            }
        }
    }
}
