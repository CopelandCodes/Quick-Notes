package quicknotes

/**
 * Main function: Displays a menu for users to manage their notes.
 * Offers options to create, view, search, edit, and delete notes.
 * Notes can be filtered by category or tag, searched by keyword, and identified by ID.
 */
fun main() {
    val manager = NoteManager() // Initializes the SQLite-backed NoteManager

    println("Welcome to QuickNotes with SQLite!")

    // Infinite loop for interactive CLI menu
    while (true) {
        // Display menu using raw string with margin trimming
        println(
            """
            |======= Quick Notes =======
            |1. Add Note
            |2. View All Notes
            |3. View Notes by Category
            |4. View Notes by Tag
            |5. View Note by ID
            |6. Edit Note
            |7. Delete Note
            |8. Search Notes by Keyword
            |0. Exit
            |===========================
            """.trimMargin()
        )

        // Process user input for menu selection
        when (readLine()?.trim()) {

            // Option 1: Add a new note
            "1" -> {
                // Prompt for note fields
                print("Title: ")
                val title = readLine() ?: ""
                print("Content: ")
                val content = readLine() ?: ""
                print("Category: ")
                val category = readLine() ?: ""
                print("Tags (comma-separated): ")
                val tags = readLine()?.split(",")?.map { it.trim() } ?: emptyList()

                // Create and save the new note
                val note = Note(
                    title = title,
                    content = content,
                    category = category,
                    tags = tags
                )
                manager.addNote(note)
                println("\nNote added!\n")
            }

            // Option 2: View all notes
            "2" -> {
                val notes = manager.getAllNotes()
                if (notes.isEmpty()) println("No notes found.\n")
                else notes.forEach { printNote(it) }
            }

            // Option 3: View notes by category
            "3" -> {
                print("Enter category to search: ")
                val category = readLine()?.trim() ?: ""
                val notes = manager.searchByCategory(category)
                if (notes.isEmpty()) println("No notes found in that category.\n")
                else notes.forEach { printNote(it) }
            }

            // Option 4: View notes by tag
            "4" -> {
                print("Enter tag to search: ")
                val tag = readLine()?.trim() ?: ""
                val notes = manager.searchByTag(tag)
                if (notes.isEmpty()) println("No notes found with that tag.\n")
                else notes.forEach { printNote(it) }
            }

            // Option 5: View a specific note by its database ID
            "5" -> {
                print("Enter note ID: ")
                val id = readLine()?.toIntOrNull()
                val note = manager.getNoteById(id ?: -1)
                if (note != null) printNote(note)
                else println("Note not found.\n")
            }

            // Option 6: Edit an existing note
            "6" -> {
                print("Enter note ID to edit: ")
                val id = readLine()?.toIntOrNull()
                val existing = manager.getNoteById(id ?: -1)

                if (existing != null) {
                    // Prompt for each field, allowing blank input to retain existing values
                    print("New title [${existing.title}]: ")
                    val newTitle = readLine()?.takeIf { it.isNotBlank() } ?: existing.title

                    print("New content [${existing.content}]: ")
                    val newContent = readLine()?.takeIf { it.isNotBlank() } ?: existing.content

                    print("New category [${existing.category}]: ")
                    val newCategory = readLine()?.takeIf { it.isNotBlank() } ?: existing.category

                    print("New tags (comma-separated) [${existing.tags.joinToString()}]: ")
                    val tagInput = readLine()
                    val newTags = if (!tagInput.isNullOrBlank())
                        tagInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    else existing.tags

                    // Create a modified note and update the database
                    val updatedNote = existing.copy(
                        title = newTitle,
                        content = newContent,
                        category = newCategory,
                        tags = newTags,
                        updatedAt = currentTimestamp()
                    )
                    manager.updateNote(id!!, updatedNote)
                    println("Note updated.\n")
                } else {
                    println("Note not found.\n")
                }
            }

            // Option 7: Delete a note
            "7" -> {
                print("Enter note ID to delete: ")
                val id = readLine()?.toIntOrNull()
                val note = manager.getNoteById(id ?: -1)
                if (note != null) {
                    print("Are you sure you want to delete '${note.title}'? (y/n): ")
                    if (readLine()?.lowercase() in listOf("y", "yes")) {
                        manager.deleteNote(id!!)
                        println("\n***Note deleted.***\n")
                    } else {
                        println("Delete canceled.\n")
                    }
                } else println("Note not found.\n")
            }

            // Option 8: Search notes by keyword (in title or content)
            "8" -> {
                print("Enter keyword to search in title/content: ")
                val keyword = readLine()?.trim() ?: ""
                val results = manager.searchByKeyword(keyword)
                if (results.isEmpty()) println("No notes matched.\n")
                else results.forEach { printNote(it) }
            }

            // Option 0: Exit the program
            "0" -> {
                manager.close() // Gracefully close DB connection
                println("Goodbye!\n")
                break
            }

            // Invalid input handler
            else -> println("Invalid option.\n")
        }
    }
}

