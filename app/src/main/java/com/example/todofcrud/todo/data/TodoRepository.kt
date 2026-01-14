package com.example.todofcrud.todo.data

import android.util.Log
import com.example.todofcrud.todo.model.Todo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TodoRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Dynamically get the collection for the current user.
    // If no user is logged in, this will throw an exception, ensuring data security.
    private val todosCollection: CollectionReference
        get() {
            val uid = auth.currentUser?.uid ?: throw IllegalStateException("User must be logged in")
            return firestore.collection("users").document(uid).collection("todos")
        }

    // Fetch todos
    fun getTodos(): Flow<List<Todo>> = callbackFlow {
        // 1. Create a listener on the "todos" collection for the CURRENT USER.
        val listenerRegistration = todosCollection
            .orderBy("title", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("TodoRepository", "Error fetching todos", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val todos = snapshot.documents.mapNotNull { document ->
                        val todo = document.toObject(Todo::class.java)
                        todo?.copy(id = document.id)
                    }
                    trySend(todos)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun addTodo(title: String) {
        val todo = Todo(
            title = title,
            completed = false
        )
        // Add object to the user's specific collection.
        todosCollection.add(todo)
            .addOnSuccessListener { documentReference ->
                Log.d("TodoRepository", "Todo added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("TodoRepository", "Error adding todo", e)
            }
    }


    fun updateTodo(todo: Todo) {
        if (todo.id.isNotEmpty()) {
            Log.d("TodoRepository", "Updating todo: ${todo.id}, completed: ${todo.completed}")
            val updates = mapOf(
                "title" to todo.title,
                "completed" to todo.completed
            )

            todosCollection.document(todo.id).update(updates)
                .addOnSuccessListener {
                    Log.d("TodoRepository", "Todo updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("TodoRepository", "Error updating todo", e)
                }
        } else {
            Log.e("TodoRepository", "Cannot update todo: ID is empty!")
        }
    }

    fun deleteTodo(todoId: String) {
        todosCollection.document(todoId).delete()
            .addOnSuccessListener {
                Log.d("TodoRepository", "Todo deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("TodoRepository", "Error deleting todo", e)
            }
    }
}