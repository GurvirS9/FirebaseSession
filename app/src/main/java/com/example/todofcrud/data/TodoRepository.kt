package com.example.todofcrud.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TodoRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val todosCollection = firestore.collection("todos")

    // Fetch todos
    fun getTodos(): Flow<List<Todo>> = callbackFlow {
        // 1. Create a listener on the "todos" collection.
        //    orderBy("title", Query.Direction.ASCENDING) sorts the list alphabetically.
        val listenerRegistration = todosCollection
            .orderBy("title", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                // 2. Handle potential errors (e.g., permission denied, no internet).
                if (error != null) {
                    Log.e("TodoRepository", "Error fetching todos", error)
                    close(error) // Close the flow with the error
                    return@addSnapshotListener
                }

                // 3. If snapshot is not null, map the documents to our Todo data class.
                if (snapshot != null) {
                    val todos = snapshot.documents.mapNotNull { document ->
                        // toObject converts the Firestore document fields to our Todo Kotlin object.
                        // It matches fields by name (title, isCompleted).
                        val todo = document.toObject(Todo::class.java)
                        // We manually set the 'id' because it's the document's ID, not a field inside the document.
                        todo?.copy(id = document.id)
                    }
                    // 4. Emit the new list of todos to the Flow.
                    //    The ViewModel will receive this and update the UI.
                    trySend(todos)
                }
            }

        // 5. awaitClose is called when the Flow is cancelled (e.g., when the ViewModel is cleared).
        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun addTodo(title: String) {
        val todo = Todo(
            title = title,
            completed = false
        )
        // Add object to the "todos" collection.
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

            // Update the specific document using its ID.
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
        // Delete the document with the given ID.
        todosCollection.document(todoId).delete()
            .addOnSuccessListener {
                Log.d("TodoRepository", "Todo deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("TodoRepository", "Error deleting todo", e)
            }
    }
}
